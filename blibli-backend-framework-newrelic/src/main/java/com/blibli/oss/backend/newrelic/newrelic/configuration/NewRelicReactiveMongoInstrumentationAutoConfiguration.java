package com.blibli.oss.backend.newrelic.newrelic.configuration;

import com.blibli.oss.backend.newrelic.newrelic.aspect.CommandAspect;
import com.blibli.oss.backend.newrelic.newrelic.aspect.ReactiveMongoDbAspect;
import com.blibli.oss.backend.newrelic.newrelic.aspect.service.AspectModifyService;
import com.blibli.oss.backend.newrelic.newrelic.aspect.service.impl.AspectModifyServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import java.util.Optional;

@Configuration
@ConditionalOnClass({EnableNewRelicReactorInstrumentation.class})
@Slf4j
@Import({CommandAspect.class, ReactiveMongoDbAspect.class})
public class NewRelicReactiveMongoInstrumentationAutoConfiguration {

  @Bean
  public AspectModifyService aspectHelper(
    Optional<MongoProperties> mongoProperties,
    Optional<ReactiveMongoTemplate> reactiveMongoTemplate
  ) {
    return new AspectModifyServiceImpl(mongoProperties, reactiveMongoTemplate);
  }
}
