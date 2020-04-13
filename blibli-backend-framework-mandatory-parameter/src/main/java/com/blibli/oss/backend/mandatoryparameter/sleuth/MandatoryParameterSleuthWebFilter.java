package com.blibli.oss.backend.mandatoryparameter.sleuth;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import com.blibli.oss.backend.mandatoryparameter.helper.ServerHelper;
import com.blibli.oss.backend.mandatoryparameter.helper.SleuthHelper;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import com.blibli.oss.backend.sleuth.webflux.SleuthWebFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
    SleuthHelper.putExtraField(traceContext, MandatoryParameterSleuth.STORE_ID, ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getStoreId(), properties.getQueryKey().getStoreId()));
    SleuthHelper.putExtraField(traceContext, MandatoryParameterSleuth.CLIENT_ID, ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getClientId(), properties.getQueryKey().getClientId()));
    SleuthHelper.putExtraField(traceContext, MandatoryParameterSleuth.CHANNEL_ID, ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getChannelId(), properties.getQueryKey().getChannelId()));
    SleuthHelper.putExtraField(traceContext, MandatoryParameterSleuth.REQUEST_ID, ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getRequestId(), properties.getQueryKey().getRequestId()));
    SleuthHelper.putExtraField(traceContext, MandatoryParameterSleuth.USERNAME, ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getUsername(), properties.getQueryKey().getUsername()));
    return exchange;
  }

}
