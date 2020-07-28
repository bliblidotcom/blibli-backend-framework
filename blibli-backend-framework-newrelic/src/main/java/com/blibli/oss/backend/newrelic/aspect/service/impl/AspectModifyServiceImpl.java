package com.blibli.oss.backend.newrelic.aspect.service.impl;

import com.blibli.oss.backend.newrelic.aspect.service.AspectModifyService;
import com.blibli.oss.backend.newrelic.aspect.service.util.AspectHelper;
import com.blibli.oss.backend.newrelic.aspect.service.util.RetValType;
import com.blibli.oss.backend.newrelic.aspect.service.util.SegmentType;
import com.blibli.oss.backend.newrelic.injector.NewRelicTokenInjectorFilter;
import com.blibli.oss.backend.newrelic.reporter.ExternalReporter;
import com.blibli.oss.backend.newrelic.reporter.helper.ExternalReporterHelper;
import com.newrelic.api.agent.Segment;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class AspectModifyServiceImpl implements AspectModifyService, ApplicationContextAware, InitializingBean {

  @Setter
  private ApplicationContext applicationContext;

  private Map<SegmentType, List<ExternalReporter>> externalReporters;

  @Override
  public void afterPropertiesSet() {
    externalReporters = ExternalReporterHelper.getExternalReporters(applicationContext);
  }

  @Override
  public Object modifyRetValWithTiming(
    ProceedingJoinPoint proceedingJoinPoint, SegmentType segmentType) throws Throwable {
    Object retVal = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    if (!AspectHelper.retValIsType(retVal, segmentType.getReturnValueType())) {
      return retVal;
    }

    AtomicReference<Segment> segmentRef = new AtomicReference<>();

    if (AspectHelper.retValIsType(retVal, RetValType.MONO)) {
      retVal = ((Mono<Object>) retVal)
        .subscriberContext(ctx -> this.startSegment(segmentType, ctx, segmentRef, proceedingJoinPoint))
        .doOnEach(signal -> this.stopSegment(signal, segmentRef));
    } else if (AspectHelper.retValIsType(retVal, RetValType.FLUX)) {
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

        startExternalReporters(segmentType, segment, jp);
      });
    return ctx;
  }

  private void startExternalReporters(SegmentType segmentType, Segment segment, JoinPoint jp) {
    List<ExternalReporter> reporters = externalReporters.getOrDefault(segmentType, Collections.EMPTY_LIST);

    reporters.forEach(
      reporter -> reporter.report(segment, jp)
    );
  }

  private void stopSegment(Signal signal, AtomicReference<Segment> segmentRef) {
    if (signal.isOnComplete()) {
      if (segmentRef.get() != null) {
        segmentRef.get().end();
      } else {
        log.warn("New Relic segment does not exist!");
      }
    }
  }
}
