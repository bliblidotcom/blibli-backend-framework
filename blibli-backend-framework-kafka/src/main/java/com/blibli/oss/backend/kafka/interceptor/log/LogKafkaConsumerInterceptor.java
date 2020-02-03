package com.blibli.oss.backend.kafka.interceptor.log;

import com.blibli.oss.backend.kafka.interceptor.KafkaConsumerInterceptor;
import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.core.Ordered;

@Slf4j
public class LogKafkaConsumerInterceptor implements KafkaConsumerInterceptor, Ordered {

  @Getter
  private final int order = Ordered.HIGHEST_PRECEDENCE;

  @Setter
  private KafkaProperties kafkaProperties;

  @Override
  public boolean beforeConsume(ConsumerRecord<String, String> consumerRecord) {
    if (kafkaProperties.getLogging().isBeforeConsume()) {
      log.info("Receive from topic {} with message {}:{}", consumerRecord.topic(), consumerRecord.key(), consumerRecord.value());
    }
    return false;
  }

  @Override
  public void afterSuccessConsume(ConsumerRecord<String, String> consumerRecord) {
    if (kafkaProperties.getLogging().isAfterSuccessConsume()) {
      log.info("Success consume from topic {} with message {}:{}", consumerRecord.topic(), consumerRecord.key(), consumerRecord.value());
    }
  }

  @Override
  public void afterFailedConsume(ConsumerRecord<String, String> consumerRecord, Throwable throwable) {
    if (kafkaProperties.getLogging().isAfterFailedConsume()) {
      log.error(String.format("Failed consume from topic %s with message %s:%s and exception %s", consumerRecord.topic(), consumerRecord.key(), consumerRecord.value(), throwable.getMessage()), throwable);
    }
  }
}
