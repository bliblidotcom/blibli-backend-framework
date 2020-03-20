package com.blibli.oss.backend.apiclient.annotation;

import com.blibli.oss.backend.apiclient.customizer.ApiClientCodecCustomizer;
import com.blibli.oss.backend.apiclient.customizer.ApiClientTcpClientCustomizer;
import com.blibli.oss.backend.apiclient.customizer.ApiClientWebClientCustomizer;
import com.blibli.oss.backend.apiclient.error.ApiErrorResolver;
import com.blibli.oss.backend.apiclient.error.DefaultApiErrorResolver;
import com.blibli.oss.backend.apiclient.interceptor.ApiClientInterceptor;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiClient {

  String name();

  Class<?> fallback() default Void.class;

  boolean primary() default true;

  Class<? extends ApiClientInterceptor>[] interceptors() default {};

  Class<? extends ApiClientWebClientCustomizer>[] webClientCustomizers() default {};

  Class<? extends ApiClientCodecCustomizer>[] codecCustomizers() default {};

  Class<? extends ApiClientTcpClientCustomizer>[] tcpClientCustomizers() default {};

  Class<? extends ApiErrorResolver> errorResolver() default DefaultApiErrorResolver.class;

}
