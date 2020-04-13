package com.blibli.oss.backend.kafka.interceptor;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.lang.reflect.Method;

public interface KafkaConsumerInterceptor {

  default boolean isSupport(Object bean, Method method) {
    return true;
  }

  default boolean beforeConsume(ConsumerRecord<String, String> consumerRecord) {
    return false;
  }

  default void afterSuccessConsume(ConsumerRecord<String, String> consumerRecord) {

  }

  default void afterFailedConsume(ConsumerRecord<String, String> consumerRecord, Throwable throwable) {

  }

}
