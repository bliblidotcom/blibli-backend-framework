package com.blibli.oss.backend.kafka.error;

public class KafkaException extends RuntimeException {

  public KafkaException(String message) {
    super(message);
  }
}
