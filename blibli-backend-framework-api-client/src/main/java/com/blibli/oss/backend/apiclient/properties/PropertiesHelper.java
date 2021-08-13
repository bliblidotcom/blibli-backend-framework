package com.blibli.oss.backend.apiclient.properties;

import java.util.Objects;

public class PropertiesHelper {

  public static void copyConfigProperties(ApiClientProperties.ApiClientConfigProperties source,
                                          ApiClientProperties.ApiClientConfigProperties target) {
    if (source != null) {

      if (Objects.nonNull(source.getUrl())) {
        target.setUrl(source.getUrl());
      }

      if (Objects.nonNull(source.getFallback())) {
        target.setFallback(source.getFallback());
      }

      if (Objects.nonNull(source.getReadTimeout())) {
        target.setReadTimeout(source.getReadTimeout());
      }

      if (Objects.nonNull(source.getConnectTimeout())) {
        target.setConnectTimeout(source.getConnectTimeout());
      }

      if (Objects.nonNull(source.getWriteTimeout())) {
        target.setWriteTimeout(source.getWriteTimeout());
      }

      source.getParams().forEach((key, value) -> target.getParams().put(key, value));

      source.getHeaders().forEach((key, value) -> target.getHeaders().put(key, value));

      source.getInterceptors().forEach(aClass -> target.getInterceptors().add(aClass));

      source.getWebClientCustomizers().forEach(aClass -> target.getWebClientCustomizers().add(aClass));

      source.getCodecCustomizers().forEach(aClass -> target.getCodecCustomizers().add(aClass));

      source.getTcpClientCustomizers().forEach(aClass -> target.getTcpClientCustomizers().add(aClass));

      if (Objects.nonNull(source.getErrorResolver())) {
        target.setErrorResolver(source.getErrorResolver());
      }
    }
  }

}
