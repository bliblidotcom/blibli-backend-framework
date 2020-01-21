package com.blibli.oss.backend.swagger.api;

import java.lang.reflect.Parameter;

public interface SwaggerIgnoredParameter {

  boolean isIgnored(Parameter parameter);

}
