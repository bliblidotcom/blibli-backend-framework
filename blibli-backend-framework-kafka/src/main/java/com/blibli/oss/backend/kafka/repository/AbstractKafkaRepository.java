package com.blibli.oss.backend.kafka.repository;

import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractKafkaRepository<T> implements KafkaRepository<T>, KafkaProducerAware {

  @Setter
  @Getter
  private KafkaProducer kafkaProducer;

}
