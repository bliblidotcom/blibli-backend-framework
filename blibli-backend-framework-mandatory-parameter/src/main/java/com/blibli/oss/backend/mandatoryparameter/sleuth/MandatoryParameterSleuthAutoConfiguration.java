package com.blibli.oss.backend.mandatoryparameter.sleuth;

import brave.Tracing;
import com.blibli.oss.backend.mandatoryparameter.MandatoryParameterAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(WebFluxConfigurer.class)
@AutoConfigureAfter(MandatoryParameterAutoConfiguration.class)
@ConditionalOnBean(Tracing.class)
public class MandatoryParameterSleuthAutoConfiguration {



}
