package com.blibli.oss.backend.swagger.factory;

import com.blibli.oss.backend.swagger.properties.SwaggerProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

public class OpenAPIFactoryBean implements FactoryBean<OpenAPI> {

  @Setter
  private SwaggerProperties swaggerProperties;

  @Setter
  private Components components;

  @Override
  public OpenAPI getObject() throws Exception {
    OpenAPI openAPI = new OpenAPI();
    openAPI.setInfo(getInfo());
    openAPI.setComponents(components);
    return openAPI;
  }

  private Info getInfo() {
    Info info = new Info();
    info.setTitle(swaggerProperties.getTitle());
    info.setDescription(swaggerProperties.getDescription());
    info.setTermsOfService(swaggerProperties.getTermsOfService());
    info.setVersion(swaggerProperties.getVersion());
    return info;
  }

  @Override
  public Class<?> getObjectType() {
    return OpenAPI.class;
  }
}
