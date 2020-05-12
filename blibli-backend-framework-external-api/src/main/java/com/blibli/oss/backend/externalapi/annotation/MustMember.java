package com.blibli.oss.backend.externalapi.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({PARAMETER, METHOD, TYPE})
@Retention(RUNTIME)
public @interface MustMember {

  /**
   * If true, ExternalSession must a member, if false, ExternalSession must not a member
   *
   * @return boolean
   */
  boolean value() default true;

}
