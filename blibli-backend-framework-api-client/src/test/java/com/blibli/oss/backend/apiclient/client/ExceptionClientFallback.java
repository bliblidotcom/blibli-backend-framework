package com.blibli.oss.backend.apiclient.client;

import com.blibli.oss.backend.apiclient.client.model.FirstRequest;
import com.blibli.oss.backend.apiclient.client.model.FirstResponse;
import com.blibli.oss.backend.apiclient.client.model.SecondResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ExceptionClientFallback {

  public Mono<FirstResponse> first(FirstRequest request, Throwable throwable) {
    log.error("Receive error", throwable);
    return Mono.just(FirstResponse.builder()
      .hello("Ups First")
      .build());
  }

  public Mono<SecondResponse> second() {
    return Mono.just(SecondResponse.builder()
      .hello("Ups Second")
      .build());
  }

}
