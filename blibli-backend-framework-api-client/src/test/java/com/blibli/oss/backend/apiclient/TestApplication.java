package com.blibli.oss.backend.apiclient;

import com.blibli.oss.backend.apiclient.client.ExampleInterceptor;
import com.blibli.oss.backend.apiclient.interceptor.GlobalApiClientInterceptor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class TestApplication {

  @Bean
  public ExampleInterceptor exampleInterceptor() {
    return new ExampleInterceptor();
  }

  @Component
  public static class EchoGlobalApiClientInterceptor implements GlobalApiClientInterceptor {

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
      return Mono.fromCallable(() ->
        ClientRequest.from(request)
          .header("ECHO", "ECHO")
          .build()
      ).flatMap(next::exchange);
    }
  }

}
