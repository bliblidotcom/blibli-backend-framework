package com.blibli.oss.backend.mandatoryparameter.sleuth;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

@AllArgsConstructor
public class MandatoryParameterSleuthWebFilter implements WebFilter {

  private MandatoryParameterProperties properties;

  private Tracer tracer;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return forwardMandatoryParamIfExists(exchange)
      .flatMap(chain::filter);
  }

  private Mono<ServerWebExchange> forwardMandatoryParamIfExists(ServerWebExchange exchange) {
    return Mono.fromCallable(() -> {
      Span span = tracer.currentSpan();
      if (Objects.nonNull(span)) {
        TraceContext traceContext = span.context();
        ServerHttpRequest httpRequest = exchange.getRequest();
        HttpHeaders httpHeaders = httpRequest.getHeaders();
        MultiValueMap<String, String> queryParams = httpRequest.getQueryParams();

//        ExtraFieldPropagation.set(traceContext, );
      }

      return exchange;
    });
  }

//  private void putExtraField(TraceContext traceContext, String key, String value){
//    if(Objects.nonNull())
//  }

  private String getValue(HttpHeaders httpHeaders, MultiValueMap<String, String> queryParams, String headerKey, String queryParamKey) {
    if (StringUtils.isEmpty(httpHeaders.getFirst(headerKey))) {
      return queryParams.getFirst(queryParamKey);
    } else {
      return httpHeaders.getFirst(headerKey);
    }
  }
}
