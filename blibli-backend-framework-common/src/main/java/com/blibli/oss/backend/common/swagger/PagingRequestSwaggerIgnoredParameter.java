package com.blibli.oss.backend.common.swagger;

import com.blibli.oss.backend.common.model.request.PagingRequest;
import com.blibli.oss.backend.swagger.api.SwaggerIgnoredParameter;

import java.lang.reflect.Parameter;

public class PagingRequestSwaggerIgnoredParameter implements SwaggerIgnoredParameter {

  @Override
  public boolean isIgnored(Parameter parameter) {
    return PagingRequest.class.isAssignableFrom(parameter.getType());
  }
}
