package com.blibli.oss.backend.kafka.repository;

import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import org.springframework.beans.factory.Aware;

public interface KafkaProducerAware extends Aware {

  void setKafkaProducer(KafkaProducer kafkaProducer);

}
