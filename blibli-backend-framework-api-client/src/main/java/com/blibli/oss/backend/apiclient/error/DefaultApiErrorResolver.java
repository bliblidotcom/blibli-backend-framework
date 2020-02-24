package com.blibli.oss.backend.apiclient.error;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

@Slf4j
public class DefaultApiErrorResolver implements ApiErrorResolver {

  @Override
  public Mono<Object> resolve(Throwable throwable, Class<?> type, Method method, Object[] arguments) {
    log.error(throwable.getMessage(), throwable);
    return Mono.empty();
  }
}
