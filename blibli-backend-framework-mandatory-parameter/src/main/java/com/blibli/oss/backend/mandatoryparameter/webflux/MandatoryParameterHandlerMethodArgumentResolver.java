package com.blibli.oss.backend.mandatoryparameter.webflux;

import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterSwaggerProperties;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class MandatoryParameterHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

  private MandatoryParameterSwaggerProperties properties;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return MandatoryParameter.class.isAssignableFrom(parameter.getParameterType());
  }

  @Override
  public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
    return Mono.fromCallable(() -> {
      ServerHttpRequest httpRequest = exchange.getRequest();
      HttpHeaders httpHeaders = httpRequest.getHeaders();
      MultiValueMap<String, String> queryParams = httpRequest.getQueryParams();

      return MandatoryParameter.builder()
        .storeId(getValue(httpHeaders, queryParams, properties.getHeaderKey().getStoreId(), properties.getQueryKey().getStoreId()))
        .channelId(getValue(httpHeaders, queryParams, properties.getHeaderKey().getChannelId(), properties.getQueryKey().getChannelId()))
        .clientId(getValue(httpHeaders, queryParams, properties.getHeaderKey().getClientId(), properties.getQueryKey().getClientId()))
        .username(getValue(httpHeaders, queryParams, properties.getHeaderKey().getUsername(), properties.getQueryKey().getUsername()))
        .requestId(getValue(httpHeaders, queryParams, properties.getHeaderKey().getRequestId(), properties.getQueryKey().getRequestId()))
        .build();
    });
  }

  private String getValue(HttpHeaders httpHeaders, MultiValueMap<String, String> queryParams, String headerKey, String queryParamKey) {
    if (StringUtils.isEmpty(httpHeaders.getFirst(headerKey))) {
      return queryParams.getFirst(queryParamKey);
    } else {
      return httpHeaders.getFirst(headerKey);
    }
  }
}
