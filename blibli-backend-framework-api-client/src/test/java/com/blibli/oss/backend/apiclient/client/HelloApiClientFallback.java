package com.blibli.oss.backend.apiclient.client;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class HelloApiClientFallback implements HelloApiClient {

  @Override
  public Mono<String> hello() {
    return Mono.just("Fallback");
  }
}
