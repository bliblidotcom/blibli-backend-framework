package com.blibli.oss.backend.kafka.configuration;

import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import com.blibli.oss.backend.kafka.producer.PlainKafkaProducer;
import com.blibli.oss.backend.kafka.producer.impl.KafkaProducerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rx.Observable;
import rx.Single;

@ConditionalOnClass({Single.class, Observable.class})
@Configuration
@AutoConfigureAfter({KafkaAutoConfiguration.class})
public class KafkaRxJavaAutoConfiguration {

  @Bean
  public KafkaProducer kafkaProducer(@Autowired PlainKafkaProducer plainKafkaProducer) {
    return new KafkaProducerImpl(plainKafkaProducer);
  }

}
