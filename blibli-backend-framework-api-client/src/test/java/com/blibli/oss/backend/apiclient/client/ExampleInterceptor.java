package com.blibli.oss.backend.apiclient.client;

import com.blibli.oss.backend.apiclient.interceptor.ApiClientInterceptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

public class ExampleInterceptor implements ApiClientInterceptor {

  @Override
  public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
    ClientRequest clientRequest = ClientRequest.from(request)
      .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
      .build();

    return next.exchange(clientRequest);
  }
}
