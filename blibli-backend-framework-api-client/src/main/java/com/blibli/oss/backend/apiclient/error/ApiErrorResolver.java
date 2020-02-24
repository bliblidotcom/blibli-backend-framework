package com.blibli.oss.backend.apiclient.error;

import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

public interface ApiErrorResolver {

  Mono<Object> resolve(Throwable throwable, Class<?> type, Method method, Object[] arguments);

}
