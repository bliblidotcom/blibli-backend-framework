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

  private boolean aspectj = true;

  private LoggingProperties logging = new LoggingProperties();

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class LoggingProperties {

    private boolean beforeSend = false;

    private boolean beforeConsume = false;

    private boolean afterSuccessConsume = false;

    private boolean afterFailedConsume = false;

    private boolean beforeSendExcludeEvent = false;

    private boolean beforeConsumeExcludeEvent = false;

    private boolean afterSuccessExcludeEvent = false;

    private boolean afterFailedExcludeEvent = false;

  }

}
