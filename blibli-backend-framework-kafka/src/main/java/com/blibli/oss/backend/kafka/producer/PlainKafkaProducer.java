package com.blibli.oss.backend.kafka.producer;

import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public interface PlainKafkaProducer {

  ListenableFuture<SendResult<String, String>> send(String topic, String key, Object message, Integer partition, Long timestamp);

  default ListenableFuture<SendResult<String, String>> send(String topic, String key, Object message, Integer partition) {
    return send(topic, key, message, null, null);
  }

  default ListenableFuture<SendResult<String, String>> send(String topic, String key, Object message) {
    return send(topic, key, message, null);
  }

  default ListenableFuture<SendResult<String, String>> send(String topic, Object message) {
    return send(topic, null, message);
  }

}
