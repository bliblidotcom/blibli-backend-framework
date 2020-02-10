package com.blibli.oss.backend.aggregate.query.interceptor;

import com.blibli.oss.backend.aggregate.query.constant.AggregateQueryConstant;
import com.blibli.oss.backend.aggregate.query.properties.AggregateQueryProperties;
import com.blibli.oss.backend.apiclient.interceptor.ApiClientInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class AggregateQueryApiClientInterceptor implements ApiClientInterceptor {

  private AggregateQueryProperties aggregateQueryProperties;

  @Override
  public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
    return Mono.just(request)
      .doOnNext(clientRequest -> {
        clientRequest.headers().add(AggregateQueryConstant.SERVICE_ID_HEADER, aggregateQueryProperties.getServiceId());
      }).flatMap(next::exchange);
  }
}
