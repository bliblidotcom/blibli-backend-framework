package com.blibli.oss.backend.mandatoryparameter;

import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
  MandatoryParameterProperties.class
})
public class MandatoryParameterAutoConfiguration {

}
