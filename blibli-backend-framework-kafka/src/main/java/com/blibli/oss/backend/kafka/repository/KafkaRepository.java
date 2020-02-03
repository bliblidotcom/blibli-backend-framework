package com.blibli.oss.backend.kafka.repository;

import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import org.springframework.kafka.support.SendResult;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

public interface KafkaRepository<T> extends KafkaKeyAnnotationAware<T>, KafkaTopicAnnotationAware<T> {

  KafkaProducer getKafkaProducer();

  default Mono<SendResult<String, String>> send(T data) {
    return send(getTopic(data), data);
  }

  default Mono<SendResult<String, String>> send(String topic, T data) {
    return getKafkaProducer().send(topic, getKey(data), data);
  }

  default Mono<SendResult<String, String>> sendOn(T data, Scheduler scheduler) {
    return getKafkaProducer().sendOn(getTopic(data), getKey(data), data, scheduler);
  }

  default Mono<SendResult<String, String>> sendOn(String topic, T data, Scheduler scheduler) {
    return getKafkaProducer().sendOn(topic, getKey(data), data, scheduler);
  }

  default Disposable sendAndSubscribe(T data, Scheduler scheduler) {
    return getKafkaProducer().sendAndSubscribe(getTopic(data), getKey(data), data, scheduler);
  }

  default Disposable sendAndSubscribe(String topic, T data, Scheduler scheduler) {
    return getKafkaProducer().sendAndSubscribe(topic, getKey(data), data, scheduler);
  }

}
