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
      if (kafkaProperties.getLogging().isBeforeConsumeExcludeEvent()) {
        log.info("Receive from topic {} with message key: {}", consumerRecord.topic(), consumerRecord.key());
      } else {
        log.info("Receive from topic {} with message {}:{}", consumerRecord.topic(), consumerRecord.key(), consumerRecord.value());
      }
    }
    return false;
  }

  @Override
  public void afterSuccessConsume(ConsumerRecord<String, String> consumerRecord) {
    if (kafkaProperties.getLogging().isAfterSuccessConsume()) {
      if (kafkaProperties.getLogging().isAfterSuccessExcludeEvent()) {
        log.info("Success consume from topic {} with message key: {}", consumerRecord.topic(), consumerRecord.key());
      } else {
        log.info("Success consume from topic {} with message {}:{}", consumerRecord.topic(), consumerRecord.key(), consumerRecord.value());
      }
    }
  }

  @Override
  public void afterFailedConsume(ConsumerRecord<String, String> consumerRecord, Throwable throwable) {
    if (kafkaProperties.getLogging().isAfterFailedConsume()) {
      if (kafkaProperties.getLogging().isAfterFailedExcludeEvent()) {
        log.error(String.format("Failed consume from topic %s with message key: %s and exception %s", consumerRecord.topic(), consumerRecord.key(), throwable.getMessage()), throwable);
      } else {
        log.error(String.format("Failed consume from topic %s with message %s:%s and exception %s", consumerRecord.topic(), consumerRecord.key(), consumerRecord.value(), throwable.getMessage()), throwable);
      }
    }
  }
}
