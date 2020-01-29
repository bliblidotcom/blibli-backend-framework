package com.blibli.oss.backend.kafka.producer.impl;

import com.blibli.oss.backend.kafka.producer.PlainKafkaProducer;
import com.blibli.oss.backend.kafka.producer.ReactorKafkaProducer;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;

public class ReactorKafkaProducerImpl implements ReactorKafkaProducer {

  private PlainKafkaProducer plainKafkaProducer;

  public ReactorKafkaProducerImpl(PlainKafkaProducer plainKafkaProducer) {
    this.plainKafkaProducer = plainKafkaProducer;
  }

  @Override
  public Mono<SendResult<String, String>> send(String topic, String key, Object message, Integer partition, Long timestamp) {
    return Mono.create(sink ->
      plainKafkaProducer.send(topic, key, message, partition, timestamp)
        .addCallback(sink::success, sink::error)
    );
  }
}
