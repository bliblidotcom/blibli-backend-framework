package com.blibli.oss.backend.kafka;

import com.blibli.oss.backend.kafka.interceptor.KafkaProducerInterceptor;
import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class KafkaAutoConfiguration implements ApplicationContextAware {

  @Setter
  private ApplicationContext applicationContext;

  @Bean
  public KafkaProducer kafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
    List<KafkaProducerInterceptor> interceptors = new ArrayList<>(applicationContext.getBeansOfType(KafkaProducerInterceptor.class).values());
    return new KafkaProducer(kafkaTemplate, objectMapper, interceptors);
  }
}
