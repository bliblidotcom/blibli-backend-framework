package com.blibli.oss.backend.apiclient.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("blibli.backend.apiclient")
public class ApiClientProperties {

  public static final String DEFAULT = "default";

  private Map<String, ApiClientConfigProperties> configs = new HashMap<>();

  private String packages;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ApiClientConfigProperties {

    private String url;

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration readTimeout = Duration.ofMillis(2000L);

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration connectTimeout = Duration.ofMillis(2000L);

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration writeTimeout = Duration.ofMillis(2000L);

    private Map<String, String> headers = new HashMap<>();

    private List<Class<?>> interceptors = new ArrayList<>();

  }

}
