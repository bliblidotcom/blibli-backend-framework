package com.blibli.oss.backend.externalapi.resolver;

import com.blibli.oss.backend.externalapi.annotation.MustMember;
import com.blibli.oss.backend.externalapi.exception.MustMemberException;
import com.blibli.oss.backend.externalapi.model.ExternalSession;
import com.blibli.oss.backend.externalapi.properties.ExternalApiProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
public class ExternalSessionArgumentResolver implements HandlerMethodArgumentResolver {

  private final ExternalApiProperties properties;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().isAssignableFrom(ExternalSession.class);
  }

  @Override
  public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
    return Mono.fromCallable(() -> {
      HttpHeaders headers = exchange.getRequest().getHeaders();
      ExternalSession session = getExternalSession(headers);
      validateMustMember(parameter, session);
      return session;
    });
  }

  private ExternalSession getExternalSession(HttpHeaders headers) {
    ExternalSession.ExternalSessionBuilder externalSessionBuilder = ExternalSession.builder()
      .userId(headers.getFirst(properties.getHeader().getUserId()))
      .sessionId(headers.getFirst(properties.getHeader().getSessionId()))
      .member(Boolean.parseBoolean(headers.getFirst(properties.getHeader().getIsMember())));

    headers.forEach((key, value) -> {
      if (key.startsWith(properties.getHeader().getAdditionalParameterPrefix())) {
        externalSessionBuilder.additionalParameter(key, value.get(0));
      }
    });

    return externalSessionBuilder.build();
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
