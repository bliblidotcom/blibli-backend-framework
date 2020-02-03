package com.blibli.oss.backend.kafka;

import com.blibli.oss.backend.kafka.interceptor.InterceptorUtil;
import com.blibli.oss.backend.kafka.interceptor.log.LogKafkaProducerInterceptor;
import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import com.blibli.oss.backend.kafka.repository.KafkaProducerAwareProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableConfigurationProperties({
  KafkaProperties.class
})
public class KafkaAutoConfiguration implements ApplicationContextAware {

  @Setter
  private ApplicationContext applicationContext;

  @Bean
  public KafkaProducer kafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
    return new KafkaProducer(kafkaTemplate, objectMapper, InterceptorUtil.getProducerInterceptors(applicationContext));
  }

  @Bean
  public LogKafkaProducerInterceptor logKafkaProducerInterceptor(KafkaProperties kafkaProperties) {
    LogKafkaProducerInterceptor interceptor = new LogKafkaProducerInterceptor();
    interceptor.setKafkaProperties(kafkaProperties);
    return interceptor;
  }

  @Bean
  public KafkaProducerAwareProcessor kafkaProducerAwareProcessor(KafkaProducer kafkaProducer) {
    return new KafkaProducerAwareProcessor(kafkaProducer);
  }

}
