package com.blibli.oss.backend.internalapi.resolver;

import com.blibli.oss.backend.internalapi.model.InternalSession;
import com.blibli.oss.backend.internalapi.properties.InternalApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class InternalSessionArgumentResolver implements HandlerMethodArgumentResolver {

  private final InternalApiProperties properties;

  public InternalSessionArgumentResolver(InternalApiProperties properties) {
    this.properties = properties;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().isAssignableFrom(InternalSession.class);
  }

  @Override
  public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
    return Mono.fromCallable(() -> {
      HttpHeaders headers = exchange.getRequest().getHeaders();
      return InternalSession.builder()
        .userId(headers.getFirst(properties.getHeader().getUserId()))
        .userName(headers.getFirst(properties.getHeader().getUserName()))
        .roles(getRoles(headers.getFirst(properties.getHeader().getRoles())))
        .build();
    });
  }

  private List<String> getRoles(String roles) {
    if (StringUtils.hasText(roles)) {
      return Arrays.asList(roles.split(","));
    } else {
      return Collections.emptyList();
    }
  }
}
