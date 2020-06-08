package com.blibli.oss.backend.internalapi.swagger.bean;

import com.blibli.oss.backend.internalapi.model.InternalSession;
import com.blibli.oss.backend.swagger.api.SwaggerIgnoredParameter;

import java.lang.reflect.Parameter;

public class InternalApiSwaggerIgnoredParameter implements SwaggerIgnoredParameter {

  @Override
  public boolean isIgnored(Parameter parameter) {
    return parameter.getType().isAssignableFrom(InternalSession.class);
  }
}
