package com.blibli.oss.backend.aggregate.query.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ExampleApplication {

  @Bean
  public ExampleAggregateQueryApiClientFallback exampleAggregateQueryApiClientFallback() {
    return new ExampleAggregateQueryApiClientFallback();
  }
}
