package com.blibli.oss.backend.swagger;

import com.blibli.oss.backend.swagger.api.SwaggerIgnoredParameter;
import com.blibli.oss.backend.swagger.api.SwaggerIgnoredParameterAnnotation;
import com.blibli.oss.backend.swagger.factory.ComponentsFactoryBean;
import com.blibli.oss.backend.swagger.factory.IgnoredParameterAnnotationsFactoryBean;
import com.blibli.oss.backend.swagger.factory.OpenAPIFactoryBean;
import com.blibli.oss.backend.swagger.properties.SwaggerProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({
  SwaggerProperties.class
})
public class SwaggerAutoConfiguration {

  @Bean
  public ComponentsFactoryBean components(ApplicationContext applicationContext) {
    Map<String, Parameter> parameters = applicationContext.getBeansOfType(Parameter.class);

    ComponentsFactoryBean componentsFactoryBean = new ComponentsFactoryBean();
    componentsFactoryBean.setParameters(parameters);
    return componentsFactoryBean;
  }

  @Bean
  public OpenAPIFactoryBean openAPI(@Autowired Components components,
                                    @Autowired SwaggerProperties swaggerProperties) {
    OpenAPIFactoryBean openAPIFactoryBean = new OpenAPIFactoryBean();
    openAPIFactoryBean.setComponents(components);
    openAPIFactoryBean.setSwaggerProperties(swaggerProperties);
    return openAPIFactoryBean;
  }

  @Bean
  public SwaggerIgnoredParameterAnnotation swaggerIgnoredParameterAnnotation() {
    return new SwaggerIgnoredParameterAnnotation();
  }

  @Bean
  @Primary
  public IgnoredParameterAnnotationsFactoryBean swaggerIgnoredParameterAnnotations(ApplicationContext applicationContext) {
    Collection<SwaggerIgnoredParameter> ignoredParameters = applicationContext.getBeansOfType(SwaggerIgnoredParameter.class).values();

    IgnoredParameterAnnotationsFactoryBean factoryBean = new IgnoredParameterAnnotationsFactoryBean();
    factoryBean.setSwaggerIgnoredParameters(new ArrayList<>(ignoredParameters));
    return factoryBean;
  }

}
