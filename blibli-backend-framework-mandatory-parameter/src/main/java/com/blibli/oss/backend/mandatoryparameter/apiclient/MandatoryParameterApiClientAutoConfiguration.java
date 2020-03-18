package com.blibli.oss.backend.mandatoryparameter.apiclient;

import brave.Tracer;
import com.blibli.oss.backend.apiclient.configuration.ApiClientConfiguration;
import com.blibli.oss.backend.mandatoryparameter.MandatoryParameterAutoConfiguration;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import com.blibli.oss.backend.sleuth.configuration.SleuthConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass({WebFluxConfigurer.class, SleuthConfiguration.class, ApiClientConfiguration.class})
@AutoConfigureAfter({MandatoryParameterAutoConfiguration.class, SleuthConfiguration.class, ApiClientConfiguration.class})
public class MandatoryParameterApiClientAutoConfiguration {

  @Bean
  public MandatoryParameterApiClientInterceptor mandatoryParameterApiClientInterceptor(MandatoryParameterProperties properties,
                                                                                       Tracer tracer) {
    return new MandatoryParameterApiClientInterceptor(properties, tracer);
  }

}
