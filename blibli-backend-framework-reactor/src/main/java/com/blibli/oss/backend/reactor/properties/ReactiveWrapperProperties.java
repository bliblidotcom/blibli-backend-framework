package com.blibli.oss.backend.reactor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties("blibli.backend.reactor.wrapper")
public class ReactiveWrapperProperties {

  private String defaultSubscriber;

  private String defaultPublisher;

  private Map<String, ReactiveWrapperItemProperties> configs = new HashMap<>();

  @Data
  public static class ReactiveWrapperItemProperties {

    private String subscriber;

    private String publisher;

  }

}
