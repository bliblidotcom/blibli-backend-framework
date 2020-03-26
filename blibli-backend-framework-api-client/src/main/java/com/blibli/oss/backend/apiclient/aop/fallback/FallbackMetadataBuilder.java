package com.blibli.oss.backend.apiclient.aop.fallback;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FallbackMetadataBuilder {

  private Class<?> apiClient;

  private Class<?> fallback;

  private Map<Method, Method> exceptionMethods = new HashMap<>();

  private Map<Method, Method> methods = new HashMap<>();

  public FallbackMetadataBuilder(Class<?> apiClient, Class<?> fallback) {
    this.apiClient = apiClient;
    this.fallback = fallback;
  }

  private void prepareExceptionMethods() {
    Method[] apiClientMethods = ReflectionUtils.getAllDeclaredMethods(apiClient);
    Method[] fallbackMethods = ReflectionUtils.getAllDeclaredMethods(fallback);

    for (Method apiClientMethod : apiClientMethods) {
      for (Method fallbackMethod : fallbackMethods) {
        if (fallbackMethod.getName().equals(apiClientMethod.getName()) &&
          Arrays.equals(fallbackMethod.getParameterTypes(), getParameterClassesWithException(apiClientMethod))) {
          exceptionMethods.put(apiClientMethod, fallbackMethod);
        }
      }
    }
  }

  private Class<?>[] getParameterClassesWithException(Method method) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    Class<?>[] result = new Class<?>[parameterTypes.length + 1];
    System.arraycopy(parameterTypes, 0, result, 0, parameterTypes.length);
    result[result.length - 1] = Throwable.class;
    return result;
  }

  private void prepareMethod() {
    Method[] apiClientMethods = ReflectionUtils.getAllDeclaredMethods(apiClient);
    Method[] fallbackMethods = ReflectionUtils.getAllDeclaredMethods(fallback);

    for (Method apiClientMethod : apiClientMethods) {
      for (Method fallbackMethod : fallbackMethods) {
        if (fallbackMethod.getName().equals(apiClientMethod.getName()) &&
          Arrays.equals(fallbackMethod.getParameterTypes(), apiClientMethod.getParameterTypes())) {
          methods.put(apiClientMethod, fallbackMethod);
        }
      }
    }
  }

  public FallbackMetadata build() {
    prepareExceptionMethods();
    prepareMethod();

    return FallbackMetadata.builder()
      .methods(methods)
      .exceptionMethods(exceptionMethods)
      .build();
  }
}
