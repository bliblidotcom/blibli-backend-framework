package com.blibli.oss.backend.newrelic.newrelic.aspect.service.impl;

import com.blibli.oss.backend.newrelic.newrelic.aspect.service.AspectModifyService;
import com.blibli.oss.backend.newrelic.newrelic.aspect.service.util.AspectHelper;
import com.blibli.oss.backend.newrelic.newrelic.aspect.service.util.MongoUriParser;
import com.blibli.oss.backend.newrelic.newrelic.aspect.service.util.RetValType;
import com.blibli.oss.backend.newrelic.newrelic.aspect.service.util.SegmentType;
import com.blibli.oss.backend.newrelic.newrelic.injector.NewRelicTokenInjectorFilter;
import com.newrelic.api.agent.DatastoreParameters;
import com.newrelic.api.agent.Segment;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.blibli.oss.backend.newrelic.newrelic.aspect.service.util.AspectHelper.retValIsType;

@Slf4j
public class AspectModifyServiceImpl implements AspectModifyService {

  private Optional<MongoProperties> mongoProperties;

  private Optional<ReactiveMongoTemplate> mongoTemplate;

  public AspectModifyServiceImpl(
      Optional<MongoProperties> mongoProperties,
      Optional<ReactiveMongoTemplate> mongoTemplate
  ) {
    this.mongoProperties = mongoProperties;
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Object modifyRetValWithTiming(
      ProceedingJoinPoint proceedingJoinPoint, SegmentType segmentType
  ) throws Throwable {
    Object retVal = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    if (!retValIsType(retVal, segmentType.getReturnValueType())) {
      return retVal;
    }

    AtomicReference<Segment> segmentRef = new AtomicReference<>();

    if (retValIsType(retVal, RetValType.MONO)) {
      retVal = ((Mono<Object>) retVal)
          .subscriberContext(ctx -> this.startSegment(segmentType, ctx, segmentRef, proceedingJoinPoint))
          .doOnEach(signal -> this.stopSegment(signal, segmentRef));
    } else if (retValIsType(retVal, RetValType.FLUX)) {
      retVal = ((Flux<Object>) retVal)
          .subscriberContext(ctx -> this.startSegment(segmentType, ctx, segmentRef, proceedingJoinPoint))
          .doOnEach(signal -> this.stopSegment(signal, segmentRef));
    }
    return retVal;
  }

  Context startSegment(SegmentType segmentType, Context ctx, AtomicReference<Segment> segmentRef, JoinPoint jp) {
    // TODO starting segment through `subscriberContext` may make the timing longer than the actual execution block.
    // Especially if we have long/blocking Mono operation before Command layer.
    // Not likely though, because we discourage fat controller pattern.
    // This is a hack for this issue https://github.com/reactor/reactor-core/issues/1526
    NewRelicTokenInjectorFilter.getTransaction(ctx)
        .ifPresent(t -> {
          String segmentName = AspectHelper.constructSegmentName(jp, segmentType);
          Segment segment = t.startSegment(segmentName);
          segmentRef.set(segment);
          // TODO reportAsExternal maybe blocking? Move to stopSegment part is safer I guess
          if (segmentType == SegmentType.REACTIVE_MONGODB) {
            reportAsExternalDatabaseCall(segment, jp);
          }
        });
    return ctx;
  }

  void reportAsExternalDatabaseCall(Segment segment, JoinPoint jp) {
    ReactiveMongoTemplate mongoTemplate = this.mongoTemplate.orElseThrow(() ->
        new NoSuchBeanDefinitionException("mongoTemplate",
            "com.blibli.oss.backend.newrelic.newrelic-reactor plugin need MongoTemplate bean to instrument Reactive Mongo call"));
    MongoProperties mongoProperties = this.mongoProperties.orElseThrow(() ->
        new NoSuchBeanDefinitionException("mongoProperties",
            "com.blibli.oss.backend.newrelic.newrelic-reactor plugin need MongoProperties bean to instrument Reactive Mongo call"));


    Class entity = this.getMongoEntity(jp);
    String[] mongoHostPort = MongoUriParser.getHosts(mongoProperties.getUri())[0].split(":");
    segment.reportAsExternal(DatastoreParameters.product("ReactiveMongo")
        .collection(mongoTemplate.getCollectionName(entity))
        .operation(jp.getSignature().toShortString())
        .instance(mongoHostPort[0], mongoHostPort[1])
        .databaseName(mongoProperties.getDatabase())
        .noSlowQuery() // TODO report slow query
        .build()
    );
  }

  private Class getMongoEntity(JoinPoint jp) {
    Class[] thisInterface = AopProxyUtils.proxiedUserInterfaces(jp.getThis());
    assert(thisInterface.length >= 1); // TODO try catch this
    // TODO a comprehensive test suite that assert idx 0 IS our desired interface
    // we expect our app interface repo here
    // eg. com.gdn.app.repository.UserRepository
    Class repoInterface = thisInterface[0];
    return (Class) ((ParameterizedType)repoInterface.getGenericInterfaces()[0]).getActualTypeArguments()[0];
  }

  void stopSegment(Signal signal, AtomicReference<Segment> segmentRef) {
    if (signal.isOnComplete()) {
      if (segmentRef.get() != null) {
        segmentRef.get().end();
      } else {
        log.warn("New Relic segment does not exist!");
      }
    }
  }

}
