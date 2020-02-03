package com.blibli.oss.backend.kafka.repository;

import lombok.SneakyThrows;

import java.util.Objects;

public interface KafkaTopicAnnotationAware<T> {

  @SneakyThrows
  default String getTopic(T data) {
    String topic = KafkaHelper.getTopic(data.getClass());
    if (Objects.isNull(topic)) {
      throw new NullPointerException(String.format("No annotation @KafkaTopic on class %s", data.getClass().getName()));
    } else {
      return topic;
    }
  }

}
