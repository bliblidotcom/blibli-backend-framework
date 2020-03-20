package com.blibli.oss.backend.apiclient.sleuth;

import brave.Tracer;
import com.blibli.oss.backend.apiclient.configuration.ApiClientConfiguration;
import com.blibli.oss.backend.apiclient.properties.ApiClientProperties;
import com.blibli.oss.backend.sleuth.configuration.SleuthConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({SleuthConfiguration.class})
@AutoConfigureAfter({SleuthConfiguration.class, ApiClientConfiguration.class})
public class ApiClientSleuthConfiguration {

  @Bean
  public SleuthGlobalApiClientInterceptor sleuthGlobalApiClientInterceptor(ApiClientProperties properties,
                                                                           Tracer tracer) {
    return new SleuthGlobalApiClientInterceptor(properties, tracer);
  }

}
