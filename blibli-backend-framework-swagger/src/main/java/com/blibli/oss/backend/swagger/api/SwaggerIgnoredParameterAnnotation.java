package com.blibli.oss.backend.swagger.api;

import java.lang.reflect.Parameter;
import java.util.Objects;

public class SwaggerIgnoredParameterAnnotation implements SwaggerIgnoredParameter {

  @Override
  public boolean isIgnored(Parameter parameter) {
    return Objects.nonNull(parameter.getAnnotation(SwaggerIgnored.class));
  }
}
