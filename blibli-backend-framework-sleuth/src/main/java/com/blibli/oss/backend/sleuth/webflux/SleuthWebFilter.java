package com.blibli.oss.backend.sleuth.webflux;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.instrument.web.SleuthWebProperties;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

public interface SleuthWebFilter extends WebFilter, Ordered {

  String TRACE_REQUEST_ATTR = org.springframework.cloud.sleuth.Span.class.getName();

  @Override
  default int getOrder() {
    return SleuthWebProperties.TRACING_FILTER_ORDER + 10;
  }

  default Span getCurrentSpan(ServerWebExchange exchange) {
    Span span = getTracer().currentSpan();
    if (Objects.isNull(span)) {
      span = exchange.getAttribute(TRACE_REQUEST_ATTR);
    }
    return span;
  }

  @Override
  default Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    Span currentSpan = getCurrentSpan(exchange);
    return Objects.nonNull(currentSpan) ? filter(exchange, chain, currentSpan) : chain.filter(exchange);
  }

  Tracer getTracer();

  Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain, Span currentSpan);
}
