package com.blibli.oss.backend.apiclient.configuration;

import com.blibli.oss.backend.apiclient.annotation.EnableApiClient;
import com.blibli.oss.backend.apiclient.body.FormBodyResolver;
import com.blibli.oss.backend.apiclient.body.JsonBodyResolver;
import com.blibli.oss.backend.apiclient.body.MultipartBodyResolver;
import com.blibli.oss.backend.apiclient.properties.ApiClientProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
  ApiClientProperties.class
})
@EnableApiClient
public class ApiClientConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public FormBodyResolver formBodyResolver() {
    return new FormBodyResolver();
  }

  @Bean
  @ConditionalOnMissingBean
  public MultipartBodyResolver multipartBodyResolver() {
    return new MultipartBodyResolver();
  }

  @Bean
  @ConditionalOnMissingBean
  public JsonBodyResolver jsonBodyResolver(ObjectMapper objectMapper) {
    return new JsonBodyResolver(objectMapper);
  }

}
