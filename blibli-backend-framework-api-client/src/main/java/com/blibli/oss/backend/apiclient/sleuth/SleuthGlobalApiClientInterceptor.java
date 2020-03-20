package com.blibli.oss.backend.apiclient.sleuth;

import brave.Tracer;
import brave.propagation.ExtraFieldPropagation;
import com.blibli.oss.backend.apiclient.interceptor.GlobalApiClientInterceptor;
import com.blibli.oss.backend.apiclient.properties.ApiClientProperties;
import com.blibli.oss.backend.sleuth.configuration.SleuthConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class SleuthGlobalApiClientInterceptor implements GlobalApiClientInterceptor {

  private ApiClientProperties properties;

  private Tracer tracer;

  @Override
  public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
    if (properties.getSleuth().isEnabled()) {
      return Mono.fromCallable(() -> {
        ClientRequest.Builder builder = ClientRequest.from(request);
        ExtraFieldPropagation.getAll(tracer.currentSpan().context()).forEach((key, value) -> {
          builder.header(SleuthConfiguration.HTTP_BAGGAGE_PREFIX + key, value);
        });
        return builder.build();
      }).flatMap(next::exchange);
    } else {
      return next.exchange(request);
    }
  }
}
