package com.blibli.oss.backend.internalapi.sleuth;

import brave.Tracer;
import com.blibli.oss.backend.internalapi.InternalApiConfiguration;
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
@AutoConfigureAfter({SleuthConfiguration.class, InternalApiConfiguration.class})
public class InternalApiSleuthConfiguration {

  @Bean
  public InternalSessionExtraFields externalSessionExtraFields() {
    return new InternalSessionExtraFields();
  }

  @Bean
  public InternalSessionSleuthEventListener externalSessionSleuthEventListener(Tracer tracer) {
    return new InternalSessionSleuthEventListener(tracer);
  }

}
