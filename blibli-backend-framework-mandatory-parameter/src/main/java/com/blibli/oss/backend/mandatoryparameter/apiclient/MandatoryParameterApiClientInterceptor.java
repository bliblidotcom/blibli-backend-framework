package com.blibli.oss.backend.mandatoryparameter.apiclient;

import brave.Tracer;
import com.blibli.oss.backend.apiclient.interceptor.ApiClientInterceptor;
import com.blibli.oss.backend.mandatoryparameter.helper.MandatoryParameterHelper;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import lombok.AllArgsConstructor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@AllArgsConstructor
public class MandatoryParameterApiClientInterceptor implements ApiClientInterceptor {

  private MandatoryParameterProperties properties;

  private Tracer tracer;

  @Override
  public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
    return Mono.fromCallable(() -> {
      MandatoryParameter mandatoryParameter = MandatoryParameterHelper.fromSleuth(tracer.currentSpan().context());

      URI uri = UriComponentsBuilder.fromUri(request.url())
        .queryParam(properties.getQueryKey().getStoreId(), mandatoryParameter.getStoreId())
        .queryParam(properties.getQueryKey().getClientId(), mandatoryParameter.getClientId())
        .queryParam(properties.getQueryKey().getChannelId(), mandatoryParameter.getChannelId())
        .queryParam(properties.getQueryKey().getUsername(), mandatoryParameter.getUsername())
        .queryParam(properties.getQueryKey().getRequestId(), mandatoryParameter.getRequestId())
        .build().toUri();

      return ClientRequest.from(request)
        .url(uri)
        .header(properties.getHeaderKey().getStoreId(), mandatoryParameter.getStoreId())
        .header(properties.getHeaderKey().getClientId(), mandatoryParameter.getClientId())
        .header(properties.getHeaderKey().getChannelId(), mandatoryParameter.getChannelId())
        .header(properties.getHeaderKey().getUsername(), mandatoryParameter.getUsername())
        .header(properties.getHeaderKey().getRequestId(), mandatoryParameter.getRequestId())
        .build();

    }).flatMap(next::exchange);
  }
}
