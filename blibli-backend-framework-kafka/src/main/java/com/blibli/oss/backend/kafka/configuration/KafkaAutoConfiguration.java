/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blibli.oss.backend.kafka.configuration;

import com.blibli.oss.backend.kafka.aop.KafkaListenerAdvisor;
import com.blibli.oss.backend.kafka.aop.KafkaListenerInterceptor;
import com.blibli.oss.backend.kafka.aop.KafkaListenerPointcut;
import com.blibli.oss.backend.kafka.aspect.KafkaListenerAspect;
import com.blibli.oss.backend.kafka.producer.PlainKafkaProducer;
import com.blibli.oss.backend.kafka.producer.impl.PlainKafkaProducerImpl;
import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author Eko Kurniawan Khannedy
 */
@Configuration
@EnableKafka
@EnableAspectJAutoProxy
@ConditionalOnClass({ObjectMapper.class, KafkaTemplate.class})
@AutoConfigureAfter({KafkaPropertiesAutoConfiguration.class})
public class KafkaAutoConfiguration {

  @Bean
  @ConditionalOnProperty(value = "kafka.plugin.aspectj", havingValue = "true", matchIfMissing = true)
  public KafkaListenerAspect kafkaListenerAspect(@Autowired ObjectMapper objectMapper,
                                                 @Autowired KafkaProperties kafkaProperties) {
    return new KafkaListenerAspect(objectMapper, kafkaProperties);
  }

  @Bean
  @ConditionalOnProperty(value = "kafka.plugin.aspectj", havingValue = "false")
  public KafkaListenerPointcut kafkaListenerPointcut() {
    return new KafkaListenerPointcut();
  }

  @Bean
  @ConditionalOnProperty(value = "kafka.plugin.aspectj", havingValue = "false")
  public KafkaListenerInterceptor kafkaListenerInterceptor(@Autowired ObjectMapper objectMapper,
                                                           @Autowired KafkaProperties kafkaProperties) {
    return new KafkaListenerInterceptor(objectMapper, kafkaProperties);
  }

  @Bean
  @ConditionalOnProperty(value = "kafka.plugin.aspectj", havingValue = "false")
  public KafkaListenerAdvisor kafkaListenerAdvisor(KafkaListenerPointcut kafkaListenerPointcut,
                                                   KafkaListenerInterceptor kafkaListenerInterceptor) {
    return new KafkaListenerAdvisor(kafkaListenerPointcut, kafkaListenerInterceptor);
  }

  @Bean
  public PlainKafkaProducer plainKafkaProducer(@Autowired ObjectMapper objectMapper,
                                               @Autowired KafkaTemplate<String, String> kafkaTemplate) {
    return new PlainKafkaProducerImpl(objectMapper, kafkaTemplate);
  }

}
