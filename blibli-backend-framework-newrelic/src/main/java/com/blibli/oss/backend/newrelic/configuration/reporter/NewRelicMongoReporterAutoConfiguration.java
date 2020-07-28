package com.blibli.oss.backend.newrelic.configuration.reporter;

import com.blibli.oss.backend.newrelic.aspect.ReactiveMongoDbAspect;
import com.blibli.oss.backend.newrelic.reporter.ExternalReporter;
import com.blibli.oss.backend.newrelic.reporter.impl.MongoExternalReporterImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Configuration
@ConditionalOnClass({MongoProperties.class, ReactiveMongoTemplate.class})
@Slf4j
@Import({ReactiveMongoDbAspect.class})
public class NewRelicMongoReporterAutoConfiguration {

  @Bean
  public ExternalReporter externalReporter(MongoProperties mongoProperties, ReactiveMongoTemplate reactiveMongoTemplate) {
    return new MongoExternalReporterImpl(mongoProperties, reactiveMongoTemplate);
  }
}
