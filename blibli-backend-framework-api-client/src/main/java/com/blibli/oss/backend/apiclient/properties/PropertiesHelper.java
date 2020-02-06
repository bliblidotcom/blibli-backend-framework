package com.blibli.oss.backend.apiclient.properties;

import java.util.Objects;

public class PropertiesHelper {

  public static void copyConfigProperties(ApiClientProperties.ApiClientConfigProperties source,
                                          ApiClientProperties.ApiClientConfigProperties target) {
    if (source != null) {

      if (Objects.nonNull(source.getUrl())) {
        target.setUrl(source.getUrl());
      }

      if (Objects.nonNull(source.getConnectTimeout())) {
        target.setConnectTimeout(source.getConnectTimeout());
      }

      if (Objects.nonNull(source.getReadTimeout())) {
        target.setReadTimeout(source.getReadTimeout());
      }

      source.getHeaders().forEach((key, value) -> target.getHeaders().put(key, value));
      source.getInterceptors().forEach(aClass -> target.getInterceptors().add(aClass));
    }
  }

}
