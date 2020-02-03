package com.blibli.oss.backend.kafka.interceptor.log;

import com.blibli.oss.backend.kafka.interceptor.KafkaProducerInterceptor;
import com.blibli.oss.backend.kafka.model.ProducerEvent;
import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;

@Slf4j
public class LogKafkaProducerInterceptor implements KafkaProducerInterceptor, Ordered {

  @Getter
  private final int order = Ordered.HIGHEST_PRECEDENCE;

  @Setter
  private KafkaProperties kafkaProperties;

  @Override
  public ProducerEvent beforeSend(ProducerEvent event) {
    if (kafkaProperties.getProducer().isEnabled()) {
      log.info("Send message to kafka : {}", event);
    }
    return event;
  }

}
