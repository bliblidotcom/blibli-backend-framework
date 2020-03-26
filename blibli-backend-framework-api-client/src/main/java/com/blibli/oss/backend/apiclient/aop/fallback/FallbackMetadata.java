package com.blibli.oss.backend.apiclient.aop.fallback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FallbackMetadata {

  private Map<Method, Method> methods;

  private Map<Method, Method> exceptionMethods;

}
