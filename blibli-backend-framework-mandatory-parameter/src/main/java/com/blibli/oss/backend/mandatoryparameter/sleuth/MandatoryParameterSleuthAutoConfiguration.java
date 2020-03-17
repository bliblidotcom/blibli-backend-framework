package com.blibli.oss.backend.mandatoryparameter.sleuth;

import brave.Tracer;
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
@ConditionalOnClass({WebFluxConfigurer.class, SleuthConfiguration.class})
@AutoConfigureAfter(SleuthConfiguration.class)
public class MandatoryParameterSleuthAutoConfiguration {

  @Bean
  public MandatoryParameterSleuthExtraFields mandatoryParameterSleuthExtraFields() {
    return new MandatoryParameterSleuthExtraFields();
  }

  @Bean
  public MandatoryParameterSleuthWebFilter mandatoryParameterSleuthWebFilter(MandatoryParameterProperties mandatoryParameterProperties,
                                                                             Tracer tracer) {
    return new MandatoryParameterSleuthWebFilter(mandatoryParameterProperties, tracer);
  }

}
