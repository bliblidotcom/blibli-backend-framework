package com.blibli.oss.backend.sleuth.webflux;

import brave.Span;
import brave.Tracer;
import org.springframework.cloud.sleuth.instrument.web.TraceWebFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

public interface SleuthWebFilter extends WebFilter, Ordered {

  @Override
  default int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 10;
  }

  default Span getCurrentSpan(ServerWebExchange exchange) {
    Span span = getTracer().currentSpan();
    if (Objects.isNull(span)) {
      span = exchange.getAttribute(TraceWebFilter.class.getName() + ".TRACE");
    }
    return span;
  }

  @Override
  default Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return Mono.fromCallable(() -> getCurrentSpan(exchange))
      .flatMap(span -> filter(exchange, chain, span));
  }

  Tracer getTracer();

  Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain, Span currentSpan);
}
