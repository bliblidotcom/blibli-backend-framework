package com.blibli.oss.backend.kafka.repository;

import lombok.SneakyThrows;

import java.util.Objects;

public interface KafkaKeyAnnotationAware<T> {

  @SneakyThrows
  default String getKey(T data) {
    String key = KafkaKeyHelper.getKafkaKey(data);
    if (Objects.isNull(key)) {
      throw new NullPointerException(String.format("No annotation @KafkaKey on class %s", data.getClass().getName()));
    } else {
      return key;
    }
  }

}
