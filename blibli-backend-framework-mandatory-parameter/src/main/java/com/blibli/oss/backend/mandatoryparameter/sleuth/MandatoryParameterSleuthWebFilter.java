package com.blibli.oss.backend.mandatoryparameter.sleuth;

import com.blibli.oss.backend.mandatoryparameter.helper.ServerHelper;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import com.blibli.oss.backend.sleuth.webflux.SleuthWebFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
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
    return Mono.fromCallable(() -> putMandatoryParameterToSleuth(currentSpan.context(), exchange))
      .flatMap(chain::filter);
  }

  private ServerWebExchange putMandatoryParameterToSleuth(TraceContext traceContext, ServerWebExchange exchange) {
    tracer.createBaggage(MandatoryParameterSleuth.STORE_ID).set(traceContext, ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getStoreId(), properties.getQueryKey().getStoreId()));
    tracer.createBaggage(MandatoryParameterSleuth.CLIENT_ID).set(traceContext, ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getClientId(), properties.getQueryKey().getClientId()));
    tracer.createBaggage(MandatoryParameterSleuth.CHANNEL_ID).set(traceContext, ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getChannelId(), properties.getQueryKey().getChannelId()));
    tracer.createBaggage(MandatoryParameterSleuth.REQUEST_ID).set(traceContext, ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getRequestId(), properties.getQueryKey().getRequestId()));
    tracer.createBaggage(MandatoryParameterSleuth.USERNAME).set(traceContext, ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getUsername(), properties.getQueryKey().getUsername()));
    return exchange;
  }

}
