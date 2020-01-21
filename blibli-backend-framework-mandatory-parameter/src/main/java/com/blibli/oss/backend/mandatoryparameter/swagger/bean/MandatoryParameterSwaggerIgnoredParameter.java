package com.blibli.oss.backend.mandatoryparameter.swagger.bean;

import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.swagger.api.SwaggerIgnoredParameter;

import java.lang.reflect.Parameter;

public class MandatoryParameterSwaggerIgnoredParameter implements SwaggerIgnoredParameter {

  @Override
  public boolean isIgnored(Parameter parameter) {
    return MandatoryParameter.class.isAssignableFrom(parameter.getType());
  }
}
