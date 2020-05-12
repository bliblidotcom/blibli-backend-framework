package com.blibli.oss.backend.externalapi.swagger;

import com.blibli.oss.backend.externalapi.ExternalApiConfiguration;
import com.blibli.oss.backend.externalapi.properties.ExternalApiProperties;
import com.blibli.oss.backend.externalapi.swagger.bean.ExternalApiSwaggerIgnoredParameter;
import com.blibli.oss.backend.swagger.SwaggerAutoConfiguration;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(SwaggerAutoConfiguration.class)
@AutoConfigureAfter(ExternalApiConfiguration.class)
public class ExternalApiSwaggerConfiguration {

  @Bean
  public ExternalApiSwaggerIgnoredParameter externalApiSwaggerIgnoredParameter() {
    return new ExternalApiSwaggerIgnoredParameter();
  }

  @Bean
  public Parameter externalApiHeaderUserId(ExternalApiProperties properties) {
    return new HeaderParameter()
      .required(true)
      .name(properties.getHeader().getUserId())
      .example(properties.getHeader().getUserId());
  }

  @Bean
  public Parameter externalApiHeaderSessionId(ExternalApiProperties properties) {
    return new HeaderParameter()
      .required(true)
      .name(properties.getHeader().getSessionId())
      .example(properties.getHeader().getSessionId());
  }

  @Bean
  public Parameter externalApiHeaderMember(ExternalApiProperties properties) {
    return new HeaderParameter()
      .required(true)
      .name(properties.getHeader().getIsMember())
      .example("false");
  }
}
