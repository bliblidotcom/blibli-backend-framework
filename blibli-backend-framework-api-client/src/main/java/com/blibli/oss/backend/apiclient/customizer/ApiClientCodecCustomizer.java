package com.blibli.oss.backend.apiclient.customizer;

import org.springframework.http.codec.ClientCodecConfigurer;

public interface ApiClientCodecCustomizer {

  void customize(ClientCodecConfigurer configurer);

}
