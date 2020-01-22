package com.blibli.oss.backend.mandatoryparameter.swagger.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Parameters({
  @Parameter(name = "storeId", ref = "queryParameterStoreId"),
  @Parameter(name = "channelId", ref = "queryParameterChannelId"),
  @Parameter(name = "clientId", ref = "queryParameterClientId"),
  @Parameter(name = "username", ref = "queryParameterUsername"),
  @Parameter(name = "requestId", ref = "queryParameterRequestId"),
})
@Target({
  ElementType.METHOD,
  ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface MandatoryParameterAtQuery {

}
