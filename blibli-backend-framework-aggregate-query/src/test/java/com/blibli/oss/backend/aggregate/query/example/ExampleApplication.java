package com.blibli.oss.backend.aggregate.query.example;

import com.blibli.oss.backend.apiclient.annotation.EnableApiClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableApiClient
public class ExampleApplication {

  @Bean
  public ExampleAggregateQueryApiClientFallback exampleAggregateQueryApiClientFallback() {
    return new ExampleAggregateQueryApiClientFallback();
  }
}
