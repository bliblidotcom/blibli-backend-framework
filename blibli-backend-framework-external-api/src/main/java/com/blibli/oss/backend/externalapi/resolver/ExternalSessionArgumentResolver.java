package com.blibli.oss.backend.externalapi.resolver;

import com.blibli.oss.backend.externalapi.annotation.MustMember;
import com.blibli.oss.backend.externalapi.event.ExternalSessionEvent;
import com.blibli.oss.backend.externalapi.exception.MustMemberException;
import com.blibli.oss.backend.externalapi.helper.ExternalSessionHelper;
import com.blibli.oss.backend.externalapi.model.ExternalSession;
import com.blibli.oss.backend.externalapi.properties.ExternalApiProperties;
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
public class ExternalSessionArgumentResolver implements HandlerMethodArgumentResolver, ApplicationEventPublisherAware {

  private final ExternalApiProperties properties;

  @Setter
  private ApplicationEventPublisher applicationEventPublisher;

  public ExternalSessionArgumentResolver(ExternalApiProperties properties) {
    this.properties = properties;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().isAssignableFrom(ExternalSession.class);
  }

  @Override
  public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
    return Mono.fromCallable(() -> {
      HttpHeaders headers = exchange.getRequest().getHeaders();
      ExternalSession session = ExternalSessionHelper.getExternalSession(headers, properties);
      validateMustMember(parameter, session);
      applicationEventPublisher.publishEvent(new ExternalSessionEvent(session));
      return session;
    });
  }

  private void validateMustMember(MethodParameter parameter, ExternalSession session) {
    MustMember annotation = getMustMemberAnnotation(parameter);
    if (annotation != null && annotation.value() != session.isMember()) {
      throw new MustMemberException(
        String.format("Required Member Is %s, But Get Member Is %s", annotation.value(), session.isMember())
      );
    }
  }

  private MustMember getMustMemberAnnotation(MethodParameter parameter) {
    MustMember annotation = parameter.getParameterAnnotation(MustMember.class);

    if (annotation == null) {
      annotation = parameter.getMethodAnnotation(MustMember.class);
    }

    if (annotation == null) {
      annotation = parameter.getMember().getDeclaringClass().getAnnotation(MustMember.class);
    }

    return annotation;
  }
}
