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

package com.blibli.oss.backend.kafka.aspect;

import com.blibli.oss.backend.kafka.helper.KafkaHelper;
import com.blibli.oss.backend.kafka.interceptor.InterceptorUtil;
import com.blibli.oss.backend.kafka.interceptor.KafkaConsumerInterceptor;
import com.blibli.oss.backend.kafka.interceptor.events.ConsumerEvent;
import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.List;

/**
 * @author Eko Kurniawan Khannedy
 */
@Slf4j
@Aspect
public class KafkaListenerAspect implements ApplicationContextAware, InitializingBean {

  private ApplicationContext applicationContext;

  private ObjectMapper objectMapper;

  private KafkaProperties kafkaProperties;

  private List<KafkaConsumerInterceptor> kafkaConsumerInterceptors;

  public KafkaListenerAspect(ObjectMapper objectMapper, KafkaProperties kafkaProperties) {
    this.objectMapper = objectMapper;
    this.kafkaProperties = kafkaProperties;
  }

  @Override
  public void afterPropertiesSet() {
    kafkaConsumerInterceptors = InterceptorUtil.getKafkaConsumerInterceptors(applicationContext);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Around(value = "@annotation(org.springframework.kafka.annotation.KafkaListener)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    if (isConsumerRecordArgument(joinPoint)) {
      ConsumerRecord<String, String> record = getConsumerRecord(joinPoint);
      ConsumerEvent event = KafkaHelper.toConsumerEvent(record, getEventId(record));
      try {
        if (InterceptorUtil.fireBeforeConsume(kafkaConsumerInterceptors, event)) {
          return null; // cancel process
        } else {
          Object response = joinPoint.proceed(joinPoint.getArgs());
          InterceptorUtil.fireAfterSuccessConsume(kafkaConsumerInterceptors, event);
          return response;
        }
      } catch (Throwable throwable) {
        InterceptorUtil.fireAfterErrorConsume(kafkaConsumerInterceptors, event, throwable);
        throw throwable;
      }
    } else {
      return joinPoint.proceed(joinPoint.getArgs());
    }
  }

  private boolean isConsumerRecordArgument(ProceedingJoinPoint joinPoint) {
    Object[] arguments = joinPoint.getArgs();
    if (arguments == null || arguments.length == 0) {
      return false;
    }

    return Arrays.stream(joinPoint.getArgs())
        .anyMatch(o -> o instanceof ConsumerRecord);
  }

  @SuppressWarnings("unchecked")
  private ConsumerRecord<String, String> getConsumerRecord(ProceedingJoinPoint joinPoint) {
    return (ConsumerRecord<String, String>) Arrays.stream(joinPoint.getArgs())
        .filter(o -> o instanceof ConsumerRecord)
        .findFirst()
        .orElse(null);
  }

  private String getEventId(ConsumerRecord<String, String> record) {
    return KafkaHelper.getEventId(record.value(), objectMapper, kafkaProperties);
  }

}