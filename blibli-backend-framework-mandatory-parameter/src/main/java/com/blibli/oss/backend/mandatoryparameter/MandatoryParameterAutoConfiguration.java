package com.blibli.oss.backend.mandatoryparameter;

import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterSwaggerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
  MandatoryParameterSwaggerProperties.class
})
public class MandatoryParameterAutoConfiguration {

}
