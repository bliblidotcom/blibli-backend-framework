package com.blibli.oss.backend.kafka.repository;

import com.blibli.oss.backend.kafka.producer.KafkaProducer;

public interface KafkaProducerAware {

  void setKafkaProducer(KafkaProducer kafkaProducer);

}
