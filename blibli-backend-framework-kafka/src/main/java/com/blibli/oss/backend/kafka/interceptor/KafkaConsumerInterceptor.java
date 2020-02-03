package com.blibli.oss.backend.kafka.interceptor;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaConsumerInterceptor {

  default boolean beforeConsume(ConsumerRecord<String, String> consumerRecord) {
    return false;
  }

  default void afterSuccessConsume(ConsumerRecord<String, String> consumerRecord) {

  }

  default void afterFailedConsume(ConsumerRecord<String, String> consumerRecord, Throwable throwable) {

  }

}
