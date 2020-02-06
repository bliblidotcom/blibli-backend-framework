package com.blibli.oss.backend.apiclient.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiClient {

  String name();

  Class<?> fallback() default Void.class;

  boolean primary() default true;

}
