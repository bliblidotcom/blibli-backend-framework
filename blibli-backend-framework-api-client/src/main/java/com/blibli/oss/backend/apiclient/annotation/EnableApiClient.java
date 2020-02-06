package com.blibli.oss.backend.apiclient.annotation;

import com.blibli.oss.backend.apiclient.configuration.ApiClientRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ApiClientRegistrar.class)
public @interface EnableApiClient {

  String[] basePackages() default {};

}
