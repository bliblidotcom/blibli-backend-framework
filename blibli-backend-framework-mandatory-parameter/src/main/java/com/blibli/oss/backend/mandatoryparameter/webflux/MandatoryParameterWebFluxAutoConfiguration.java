package com.blibli.oss.backend.mandatoryparameter.webflux;

import com.blibli.oss.backend.mandatoryparameter.MandatoryParameterAutoConfiguration;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterSwaggerProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@AllArgsConstructor
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(WebFluxConfigurer.class)
@AutoConfigureAfter(MandatoryParameterAutoConfiguration.class)
public class MandatoryParameterWebFluxAutoConfiguration implements WebFluxConfigurer {

  private final MandatoryParameterSwaggerProperties properties;

  @Override
  public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
    configurer.addCustomResolver(new MandatoryParameterHandlerMethodArgumentResolver(properties));
  }
}
