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

import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import com.blibli.oss.backend.kafka.sleuth.SleuthSpanConsumerInterceptor;
import com.blibli.oss.backend.kafka.sleuth.SleuthSpanProducerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.SleuthProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author Eko Kurniawan Khannedy
 */
@Configuration
@ConditionalOnClass({ObjectMapper.class, KafkaTemplate.class, Tracer.class})
@AutoConfigureAfter({KafkaAutoConfiguration.class})
public class KafkaSleuthAutoConfiguration {

  @Bean
  public SleuthSpanProducerInterceptor sleuthSpanProducerInterceptor(@Autowired KafkaProperties kafkaProperties,
                                                                     @Autowired Tracer tracer) {
    return new SleuthSpanProducerInterceptor(kafkaProperties.getModel(), tracer);
  }

  @Bean
  public SleuthSpanConsumerInterceptor sleuthSpanConsumerInterceptor(@Autowired KafkaProperties kafkaProperties,
                                                                     @Autowired ObjectMapper objectMapper,
                                                                     @Autowired Tracer tracer,
                                                                     @Autowired SleuthProperties sleuthProperties) {
    return new SleuthSpanConsumerInterceptor(kafkaProperties.getModel(), objectMapper, tracer, sleuthProperties);
  }

}
