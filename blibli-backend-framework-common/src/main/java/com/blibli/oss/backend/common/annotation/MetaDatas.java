package com.blibli.oss.backend.common.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MetaDatas {

  MetaData[] value() default {};

}
