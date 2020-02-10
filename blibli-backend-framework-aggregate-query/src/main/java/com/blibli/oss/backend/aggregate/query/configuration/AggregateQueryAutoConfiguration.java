package com.blibli.oss.backend.aggregate.query.configuration;

import com.blibli.oss.backend.aggregate.query.interceptor.AggregateQueryApiClientInterceptor;
import com.blibli.oss.backend.aggregate.query.properties.AggregateQueryProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
  AggregateQueryProperties.class
})
public class AggregateQueryAutoConfiguration {

  @Bean
  public AggregateQueryApiClientInterceptor aggregateQueryApiClientInterceptor(AggregateQueryProperties properties) {
    return new AggregateQueryApiClientInterceptor(properties);
  }

}
