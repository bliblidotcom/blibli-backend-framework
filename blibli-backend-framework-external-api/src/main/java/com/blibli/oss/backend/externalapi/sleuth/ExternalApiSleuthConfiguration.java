package com.blibli.oss.backend.externalapi.sleuth;

import brave.Tracer;
import com.blibli.oss.backend.externalapi.ExternalApiConfiguration;
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
@AutoConfigureAfter({SleuthConfiguration.class, ExternalApiConfiguration.class})
public class ExternalApiSleuthConfiguration {

  @Bean
  public ExternalSessionExtraFields externalSessionExtraFields() {
    return new ExternalSessionExtraFields();
  }

  @Bean
  public ExternalSessionSleuthEventListener externalSessionSleuthEventListener(Tracer tracer) {
    return new ExternalSessionSleuthEventListener(tracer);
  }

}
