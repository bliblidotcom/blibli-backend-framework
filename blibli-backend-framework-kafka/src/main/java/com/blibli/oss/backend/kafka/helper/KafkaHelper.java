package com.blibli.oss.backend.kafka.helper;

import com.blibli.oss.backend.kafka.interceptor.events.ConsumerEvent;
import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Optional;

/**
 * @author Eko Kurniawan Khannedy
 */
@Slf4j
public class KafkaHelper {

  public static ConsumerEvent toConsumerEvent(ConsumerRecord<String, String> record, String eventId) {
    return ConsumerEvent.builder()
      .eventId(eventId)
      .key(record.key())
      .partition(record.partition())
      .timestamp(record.timestamp())
      .topic(record.topic())
      .value(record.value())
      .build();
  }

  public static String getEventId(String message, ObjectMapper objectMapper, KafkaProperties kafkaProperties) {
    try {
      return Optional.ofNullable(objectMapper.readTree(message))
        .map(jsonNode -> jsonNode.get(kafkaProperties.getModel().getIdentity()))
        .map(JsonNode::asText)
        .orElse(null);
    } catch (Throwable throwable) {
      if (kafkaProperties.getLog().isWhenFailedGetEventId()) {
        log.warn("Error while get event id", throwable);
      }
      return null;
    }
  }

}
