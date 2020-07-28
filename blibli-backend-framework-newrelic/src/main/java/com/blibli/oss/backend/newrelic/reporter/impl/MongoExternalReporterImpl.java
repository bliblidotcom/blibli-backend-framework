package com.blibli.oss.backend.newrelic.reporter.impl;

import com.blibli.oss.backend.newrelic.aspect.service.util.MongoUriParser;
import com.blibli.oss.backend.newrelic.aspect.service.util.SegmentType;
import com.blibli.oss.backend.newrelic.reporter.ExternalReporter;
import com.newrelic.api.agent.DatastoreParameters;
import com.newrelic.api.agent.Segment;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

@Slf4j
public class MongoExternalReporterImpl implements ExternalReporter {

  private MongoProperties mongoProperties;

  private ReactiveMongoTemplate mongoTemplate;

  public MongoExternalReporterImpl(MongoProperties mongoProperties, ReactiveMongoTemplate mongoTemplate) {
    this.mongoProperties = mongoProperties;
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public SegmentType getSegmentType() {
    return SegmentType.REACTIVE_MONGODB;
  }

  @Override
  public void report(Segment segment, JoinPoint jp) {
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
}
