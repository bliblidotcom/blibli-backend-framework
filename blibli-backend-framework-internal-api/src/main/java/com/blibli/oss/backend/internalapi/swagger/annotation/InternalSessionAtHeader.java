package com.blibli.oss.backend.internalapi.swagger.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Parameters({
  @Parameter(name = "userId", ref = "internalApiHeaderUserId"),
  @Parameter(name = "sessionId", ref = "internalApiHeaderUserName"),
  @Parameter(name = "member", ref = "internalApiHeaderRoles")
})
@Target({
  ElementType.METHOD,
  ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface InternalSessionAtHeader {

}
