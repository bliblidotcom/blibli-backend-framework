package com.blibli.oss.backend.swagger.factory;

import com.blibli.oss.backend.swagger.api.SwaggerIgnoredParameter;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springdoc.core.IgnoredParameterAnnotations;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Parameter;
import java.util.List;

public class IgnoredParameterAnnotationsFactoryBean implements FactoryBean<IgnoredParameterAnnotations> {

  @Setter
  private List<SwaggerIgnoredParameter> swaggerIgnoredParameters;

  @Override
  public IgnoredParameterAnnotations getObject() throws Exception {
    return new SwaggerIgnoredParameterAnnotations(swaggerIgnoredParameters);
  }

  @Override
  public Class<?> getObjectType() {
    return IgnoredParameterAnnotations.class;
  }

  @AllArgsConstructor
  public static class SwaggerIgnoredParameterAnnotations implements IgnoredParameterAnnotations {

    private List<SwaggerIgnoredParameter> swaggerIgnoredParameters;

    @Override
    public boolean isAnnotationToIgnore(Parameter parameter) {
      for (SwaggerIgnoredParameter swaggerIgnoredParameter : swaggerIgnoredParameters) {
        if (swaggerIgnoredParameter.isIgnored(parameter)) {
          return true;
        }
      }
      return false;
    }
  }
}
