package com.blibli.oss.backend.kafka.producer;

import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;

public interface ReactorKafkaProducer {

  Mono<SendResult<String, String>> send(String topic, String key, Object message, Integer partition, Long timestamp);

  default Mono<SendResult<String, String>> send(String topic, String key, Object message, Integer partition) {
    return send(topic, key, message, null, null);
  }

  default Mono<SendResult<String, String>> send(String topic, String key, Object message) {
    return send(topic, key, message, null);
  }

  default Mono<SendResult<String, String>> send(String topic, Object message) {
    return send(topic, null, message);
  }

}
