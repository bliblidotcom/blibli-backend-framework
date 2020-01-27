package com.blibli.oss.backend.common;

import com.blibli.oss.backend.common.properties.PagingProperties;
import com.blibli.oss.backend.common.webflux.PagingRequestArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(WebFluxConfigurer.class)
@AutoConfigureAfter(CommonAutoConfiguration.class)
public class CommonWebFluxAutoConfiguration implements WebFluxConfigurer {

  @Autowired
  private PagingProperties pagingProperties;

  @Override
  public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
    configurer.addCustomResolver(new PagingRequestArgumentResolver(pagingProperties));
  }
}
