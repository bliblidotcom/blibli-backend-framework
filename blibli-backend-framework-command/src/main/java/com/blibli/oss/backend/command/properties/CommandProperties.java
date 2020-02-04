package com.blibli.oss.backend.command.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("blibli.backend.command")
public class CommandProperties {

  private CacheProperties cache = new CacheProperties();

  @Data
  public static class CacheProperties {

    private boolean enabled = false;

    private Duration timeout = Duration.ofSeconds(10);

  }

}
