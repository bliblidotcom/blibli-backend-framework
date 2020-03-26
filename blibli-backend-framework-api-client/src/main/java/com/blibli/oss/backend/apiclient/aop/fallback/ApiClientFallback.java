package com.blibli.oss.backend.apiclient.aop.fallback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiClientFallback {

  private Object fallback;

  private FallbackMetadata metadata;

  public boolean isAvailable() {
    return Objects.nonNull(fallback);
  }

  public Mono invoke(Method method, Object[] arguments, Throwable throwable) {
    return Mono.just(throwable)
      .flatMap(exception -> {
        if (method.getDeclaringClass().isAssignableFrom(fallback.getClass())) {
          return (Mono) ReflectionUtils.invokeMethod(method, fallback, arguments);
        }

        Method methodWithException = metadata.getExceptionMethods().get(method);
        if (Objects.nonNull(methodWithException)) {
          Object[] target = new Object[arguments.length + 1];
          System.arraycopy(arguments, 0, target, 0, arguments.length);
          target[target.length - 1] = throwable;
          return (Mono) ReflectionUtils.invokeMethod(methodWithException, fallback, target);
        }

        Method fallbackMethod = metadata.getMethods().get(method);
        if (Objects.nonNull(fallbackMethod)) {
          return (Mono) ReflectionUtils.invokeMethod(fallbackMethod, fallback, arguments);
        }

        return Mono.error(throwable);
      });
  }

}
