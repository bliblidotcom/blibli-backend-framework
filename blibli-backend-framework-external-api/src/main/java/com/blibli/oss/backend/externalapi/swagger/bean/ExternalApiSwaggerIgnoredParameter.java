package com.blibli.oss.backend.externalapi.swagger.bean;

import com.blibli.oss.backend.externalapi.model.ExternalSession;
import com.blibli.oss.backend.swagger.api.SwaggerIgnoredParameter;

import java.lang.reflect.Parameter;

public class ExternalApiSwaggerIgnoredParameter implements SwaggerIgnoredParameter {

  @Override
  public boolean isIgnored(Parameter parameter) {
    return parameter.getType().isAssignableFrom(ExternalSession.class);
  }
}
