package com.blibli.oss.backend.mandatoryparameter.swagger;

import com.blibli.oss.backend.mandatoryparameter.properties.MandatoryParameterProperties;
import com.blibli.oss.backend.mandatoryparameter.swagger.bean.MandatoryParameterSwaggerIgnoredParameter;
import com.blibli.oss.backend.swagger.SwaggerAutoConfiguration;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({
  SwaggerAutoConfiguration.class
})
@EnableConfigurationProperties({
  MandatoryParameterProperties.class
})
public class MandatoryParameterSwaggerAutoConfiguration {

  @Bean
  public MandatoryParameterSwaggerIgnoredParameter mandatoryParameterSwaggerIgnoredParameter() {
    return new MandatoryParameterSwaggerIgnoredParameter();
  }

  @Bean
  public Parameter queryParameterStoreId(MandatoryParameterProperties properties) {
    return new QueryParameter()
      .required(true)
      .name(properties.getQueryKey().getStoreId())
      .example(properties.getQueryKey().getStoreIdDefaultValue());
  }

  @Bean
  public Parameter queryParameterChannelId(MandatoryParameterProperties properties) {
    return new QueryParameter()
      .required(true)
      .name(properties.getQueryKey().getChannelId())
      .example(properties.getQueryKey().getChannelIdDefaultValue());
  }

  @Bean
  public Parameter queryParameterClientId(MandatoryParameterProperties properties) {
    return new QueryParameter()
      .required(true)
      .name(properties.getQueryKey().getClientId())
      .example(properties.getQueryKey().getClientIdDefaultValue());
  }

  @Bean
  public Parameter queryParameterUsername(MandatoryParameterProperties properties) {
    return new QueryParameter()
      .required(true)
      .name(properties.getQueryKey().getUsername())
      .example(properties.getQueryKey().getUsernameDefaultValue());
  }

  @Bean
  public Parameter queryParameterRequestId(MandatoryParameterProperties properties) {
    return new QueryParameter()
      .required(true)
      .name(properties.getQueryKey().getRequestId())
      .example(properties.getQueryKey().getRequestIdDefaultValue());
  }

  @Bean
  public Parameter headerParameterStoreId(MandatoryParameterProperties properties) {
    return new HeaderParameter()
      .required(true)
      .name(properties.getHeaderKey().getStoreId())
      .example(properties.getHeaderKey().getStoreIdDefaultValue());
  }

  @Bean
  public Parameter headerParameterChannelId(MandatoryParameterProperties properties) {
    return new HeaderParameter()
      .required(true)
      .name(properties.getHeaderKey().getChannelId())
      .example(properties.getHeaderKey().getChannelIdDefaultValue());
  }

  @Bean
  public Parameter headerParameterClientId(MandatoryParameterProperties properties) {
    return new HeaderParameter()
      .required(true)
      .name(properties.getHeaderKey().getClientId())
      .example(properties.getHeaderKey().getClientIdDefaultValue());
  }

  @Bean
  public Parameter headerParameterUsername(MandatoryParameterProperties properties) {
    return new HeaderParameter()
      .required(true)
      .name(properties.getHeaderKey().getUsername())
      .example(properties.getHeaderKey().getUsernameDefaultValue());
  }

  @Bean
  public Parameter headerParameterRequestId(MandatoryParameterProperties properties) {
    return new HeaderParameter()
      .required(true)
      .name(properties.getHeaderKey().getRequestId())
      .example(properties.getHeaderKey().getRequestIdDefaultValue());
  }

}
