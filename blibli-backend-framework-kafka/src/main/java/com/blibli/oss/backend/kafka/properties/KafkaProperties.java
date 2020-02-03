package com.blibli.oss.backend.kafka.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("blibli.backend.kafka")
public class KafkaProperties {

  private KafkaProducerProperties producer = new KafkaProducerProperties();

  private KafkaConsumerProperties consumer = new KafkaConsumerProperties();

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class KafkaProducerProperties {

    private boolean logging;

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class KafkaConsumerProperties {

    private boolean logging;

  }

}
