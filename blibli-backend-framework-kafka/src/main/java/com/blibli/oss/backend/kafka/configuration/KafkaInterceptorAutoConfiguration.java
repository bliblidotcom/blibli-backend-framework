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

import com.blibli.oss.backend.kafka.interceptor.IdentityInterceptor;
import com.blibli.oss.backend.kafka.interceptor.LogInterceptor;
import com.blibli.oss.backend.kafka.interceptor.RoutingInterceptor;
import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author Eko Kurniawan Khannedy
 */
@Configuration
@ConditionalOnClass({KafkaTemplate.class})
@AutoConfigureAfter({KafkaPropertiesAutoConfiguration.class})
public class KafkaInterceptorAutoConfiguration {

  @Bean
  public LogInterceptor logInterceptor(@Autowired KafkaProperties kafkaProperties) {
    return new LogInterceptor(kafkaProperties.getLog());
  }

  @Bean
  public IdentityInterceptor identityInterceptor(@Autowired KafkaProperties kafkaProperties) {
    return new IdentityInterceptor(kafkaProperties);
  }

  @Bean
  public RoutingInterceptor routingInterceptor(@Autowired KafkaProperties kafkaProperties) {
    return new RoutingInterceptor(kafkaProperties.getModel());
  }

}
