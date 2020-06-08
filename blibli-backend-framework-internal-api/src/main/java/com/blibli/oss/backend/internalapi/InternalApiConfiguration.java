package com.blibli.oss.backend.internalapi;

import com.blibli.oss.backend.internalapi.properties.InternalApiProperties;
import com.blibli.oss.backend.internalapi.resolver.InternalSessionArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
@EnableConfigurationProperties({
  InternalApiProperties.class
})
public class InternalApiConfiguration implements WebFluxConfigurer {

  @Autowired
  private InternalApiProperties internalApiProperties;

  @Bean
  public InternalSessionArgumentResolver internalSessionArgumentResolver() {
    return new InternalSessionArgumentResolver(internalApiProperties);
  }

  @Override
  public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
    configurer.addCustomResolver(internalSessionArgumentResolver());
  }
}
