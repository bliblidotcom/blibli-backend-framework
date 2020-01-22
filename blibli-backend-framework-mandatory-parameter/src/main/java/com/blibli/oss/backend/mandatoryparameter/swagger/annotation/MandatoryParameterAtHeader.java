package com.blibli.oss.backend.mandatoryparameter.swagger.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Parameters({
  @Parameter(name = "storeId", ref = "headerParameterStoreId"),
  @Parameter(name = "channelId", ref = "headerParameterChannelId"),
  @Parameter(name = "clientId", ref = "headerParameterClientId"),
  @Parameter(name = "username", ref = "headerParameterUsername"),
  @Parameter(name = "requestId", ref = "headerParameterRequestId"),
})
@Target({
  ElementType.METHOD,
  ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface MandatoryParameterAtHeader {

}
