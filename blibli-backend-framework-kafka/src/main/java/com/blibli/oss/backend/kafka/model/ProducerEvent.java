package com.blibli.oss.backend.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.header.Headers;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProducerEvent {

  private String topic;

  private Integer partition;

  private Headers headers;

  private String key;

  private Object value;

  private Long timestamp;

}
