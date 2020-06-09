package com.blibli.oss.backend.internalapi.swagger;

import com.blibli.oss.backend.internalapi.InternalApiConfiguration;
import com.blibli.oss.backend.internalapi.properties.InternalApiProperties;
import com.blibli.oss.backend.internalapi.swagger.bean.InternalApiSwaggerIgnoredParameter;
import com.blibli.oss.backend.swagger.SwaggerAutoConfiguration;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(SwaggerAutoConfiguration.class)
@AutoConfigureAfter(InternalApiConfiguration.class)
public class InternalApiSwaggerConfiguration {

  @Bean
  public InternalApiSwaggerIgnoredParameter internalApiSwaggerIgnoredParameter() {
    return new InternalApiSwaggerIgnoredParameter();
  }

  @Bean
  public Parameter internalApiHeaderUserId(InternalApiProperties properties) {
    return new HeaderParameter()
      .required(true)
      .name(properties.getHeader().getUserId())
      .example(properties.getHeader().getUserId());
  }

  @Bean
  public Parameter internalApiHeaderUserName(InternalApiProperties properties) {
    return new HeaderParameter()
      .required(true)
      .name(properties.getHeader().getUserName())
      .example(properties.getHeader().getUserId());
  }

  @Bean
  public Parameter internalApiHeaderRoles(InternalApiProperties properties) {
    return new HeaderParameter()
      .required(true)
      .name(properties.getHeader().getRoles())
      .example(properties.getHeader().getRoles());
  }
}
