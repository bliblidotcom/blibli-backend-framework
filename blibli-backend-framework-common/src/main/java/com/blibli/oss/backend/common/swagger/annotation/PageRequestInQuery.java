package com.blibli.oss.backend.common.swagger.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Parameters({
  @Parameter(name = "page", ref = "queryPagingRequestPage"),
  @Parameter(name = "itemPerPage", ref = "queryPagingRequestItemPerPage"),
  @Parameter(name = "sortBy", ref = "queryPagingRequestSortBy")
})
@Target({
  ElementType.TYPE,
  ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
public @interface PageRequestInQuery {
}
