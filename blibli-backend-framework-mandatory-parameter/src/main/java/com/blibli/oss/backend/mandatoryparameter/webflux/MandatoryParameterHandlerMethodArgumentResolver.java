package com.blibli.oss.backend.mandatoryparameter.webflux;

import com.blibli.oss.backend.mandatoryparameter.helper.ServerHelper;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class MandatoryParameterHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

  private MandatoryParameterProperties properties;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return MandatoryParameter.class.isAssignableFrom(parameter.getParameterType());
  }

  @Override
  public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
    return Mono.fromCallable(() ->
      MandatoryParameter.builder()
        .storeId(ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getStoreId(), properties.getQueryKey().getStoreId()))
        .channelId(ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getChannelId(), properties.getQueryKey().getChannelId()))
        .clientId(ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getClientId(), properties.getQueryKey().getClientId()))
        .username(ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getUsername(), properties.getQueryKey().getUsername()))
        .requestId(ServerHelper.getValueFromQueryOrHeader(exchange, properties.getHeaderKey().getRequestId(), properties.getQueryKey().getRequestId()))
        .build());
  }
}
