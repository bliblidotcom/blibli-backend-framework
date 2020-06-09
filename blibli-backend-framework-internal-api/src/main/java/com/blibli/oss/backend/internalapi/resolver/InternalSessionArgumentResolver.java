package com.blibli.oss.backend.internalapi.resolver;

import com.blibli.oss.backend.internalapi.event.InternalSessionEvent;
import com.blibli.oss.backend.internalapi.helper.InternalSessionHelper;
import com.blibli.oss.backend.internalapi.model.InternalSession;
import com.blibli.oss.backend.internalapi.properties.InternalApiProperties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class InternalSessionArgumentResolver implements HandlerMethodArgumentResolver, ApplicationEventPublisherAware {

  private final InternalApiProperties properties;

  @Setter
  private ApplicationEventPublisher applicationEventPublisher;

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
      InternalSession internalSession = InternalSessionHelper.getInternalSession(headers, properties);
      applicationEventPublisher.publishEvent(new InternalSessionEvent(internalSession));
      return internalSession;
    });
  }
}
