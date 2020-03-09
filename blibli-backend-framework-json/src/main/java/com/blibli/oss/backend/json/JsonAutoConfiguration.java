package com.blibli.oss.backend.json;

import com.blibli.oss.backend.json.helper.JsonHelper;
import com.blibli.oss.backend.json.processor.JsonAwareBeanPostProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@AutoConfigureAfter(JacksonAutoConfiguration.class)
@PropertySource("classpath:blibli-json.properties")
public class JsonAutoConfiguration {

  @Bean
  public JsonHelper jsonHelper(ObjectMapper objectMapper) {
    return new JsonHelper(objectMapper);
  }

  @Bean
  public JsonAwareBeanPostProcessor jsonAwareBeanPostProcessor(JsonHelper jsonHelper) {
    return new JsonAwareBeanPostProcessor(jsonHelper);
  }

}
