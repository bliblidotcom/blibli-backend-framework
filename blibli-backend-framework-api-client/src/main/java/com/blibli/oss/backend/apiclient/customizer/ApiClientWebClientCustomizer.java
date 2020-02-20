package com.blibli.oss.backend.apiclient.customizer;

import org.springframework.web.reactive.function.client.WebClient;

public interface ApiClientWebClientCustomizer {

  void customize(WebClient.Builder builder);

}
