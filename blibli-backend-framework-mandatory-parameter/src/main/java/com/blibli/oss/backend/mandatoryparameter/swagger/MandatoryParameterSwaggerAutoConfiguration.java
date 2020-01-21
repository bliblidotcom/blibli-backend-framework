package com.blibli.oss.backend.mandatoryparameter.swagger;

import com.blibli.oss.backend.mandatoryparameter.swagger.bean.MandatoryParameterSwaggerIgnoredParameter;
import com.blibli.oss.backend.swagger.SwaggerAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({
  SwaggerAutoConfiguration.class
})
public class MandatoryParameterSwaggerAutoConfiguration {

  @Bean
  public MandatoryParameterSwaggerIgnoredParameter mandatoryParameterSwaggerIgnoredParameter() {
    return new MandatoryParameterSwaggerIgnoredParameter();
  }

}
