package com.blibli.oss.backend.mandatoryparameter.sleuth;

import brave.Span;
import brave.Tracer;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import com.blibli.oss.backend.sleuth.webflux.SleuthWebFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class MandatoryParameterSleuthWebFilter implements SleuthWebFilter {

  private MandatoryParameterProperties properties;

  @Getter
  private Tracer tracer;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain, Span currentSpan) {
    return Mono.fromCallable(() -> {
      TraceContext traceContext = currentSpan.context();
      putMandatoryParameterToSleuth(traceContext, exchange.getRequest());
      return exchange;
    }).flatMap(chain::filter);
  }

  private void putMandatoryParameterToSleuth(TraceContext traceContext, ServerHttpRequest httpRequest) {
    HttpHeaders httpHeaders = httpRequest.getHeaders();
    MultiValueMap<String, String> queryParams = httpRequest.getQueryParams();

    putExtraField(traceContext, MandatoryParamSleuth.STORE_ID, getValue(httpHeaders, queryParams, properties.getHeaderKey().getStoreId(), properties.getQueryKey().getStoreId()));
    putExtraField(traceContext, MandatoryParamSleuth.CLIENT_ID, getValue(httpHeaders, queryParams, properties.getHeaderKey().getClientId(), properties.getQueryKey().getClientId()));
    putExtraField(traceContext, MandatoryParamSleuth.CHANNEL_ID, getValue(httpHeaders, queryParams, properties.getHeaderKey().getChannelId(), properties.getQueryKey().getChannelId()));
    putExtraField(traceContext, MandatoryParamSleuth.REQUEST_ID, getValue(httpHeaders, queryParams, properties.getHeaderKey().getRequestId(), properties.getQueryKey().getRequestId()));
    putExtraField(traceContext, MandatoryParamSleuth.USERNAME, getValue(httpHeaders, queryParams, properties.getHeaderKey().getUsername(), properties.getQueryKey().getUsername()));
  }

  private void putExtraField(TraceContext traceContext, String name, String value) {
    if (!StringUtils.isEmpty(value)) {
      ExtraFieldPropagation.set(traceContext, name, value);
    }
  }

  private String getValue(HttpHeaders httpHeaders, MultiValueMap<String, String> queryParams, String headerKey, String queryParamKey) {
    if (StringUtils.isEmpty(httpHeaders.getFirst(headerKey))) {
      return queryParams.getFirst(queryParamKey);
    } else {
      return httpHeaders.getFirst(headerKey);
    }
  }
}
