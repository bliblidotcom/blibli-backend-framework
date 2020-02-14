package com.blibli.oss.backend.apiclient;

import com.blibli.oss.backend.apiclient.client.ExampleInterceptor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplication {

  @Bean
  public ExampleInterceptor exampleInterceptor() {
    return new ExampleInterceptor();
  }

}
