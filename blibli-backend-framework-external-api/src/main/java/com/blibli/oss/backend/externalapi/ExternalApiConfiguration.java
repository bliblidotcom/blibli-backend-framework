package com.blibli.oss.backend.externalapi;

import com.blibli.oss.backend.externalapi.properties.ExternalApiProperties;
import com.blibli.oss.backend.externalapi.resolver.ExternalSessionArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
@EnableConfigurationProperties({
  ExternalApiProperties.class
})
public class ExternalApiConfiguration implements WebFluxConfigurer {

  @Autowired
  private ExternalApiProperties properties;

  @Bean
  public ExternalSessionArgumentResolver externalSessionArgumentResolver() {
    return new ExternalSessionArgumentResolver(properties);
  }

  @Override
  public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
    configurer.addCustomResolver(externalSessionArgumentResolver());
  }
}
