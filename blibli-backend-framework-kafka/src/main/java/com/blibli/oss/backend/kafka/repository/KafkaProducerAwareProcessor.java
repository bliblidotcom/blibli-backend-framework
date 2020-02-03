package com.blibli.oss.backend.kafka.repository;

import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@AllArgsConstructor
public class KafkaProducerAwareProcessor implements BeanPostProcessor {

  private KafkaProducer kafkaProducer;

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof KafkaProducerAware) {
      KafkaProducerAware kafkaProducerAware = (KafkaProducerAware) bean;
      kafkaProducerAware.setKafkaProducer(kafkaProducer);
    }

    return bean;
  }
}
