package com.blibli.oss.backend.swagger.factory;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

import java.util.Map;

public class ComponentsFactoryBean implements FactoryBean<Components> {

  @Setter
  private Map<String, Parameter> parameters;

  @Override
  public Components getObject() throws Exception {
    Components components = new Components();
    components.setParameters(parameters);
    return components;
  }

  @Override
  public Class<?> getObjectType() {
    return Components.class;
  }
}
