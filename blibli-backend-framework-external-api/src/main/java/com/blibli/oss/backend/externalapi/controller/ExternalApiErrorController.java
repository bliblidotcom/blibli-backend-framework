package com.blibli.oss.backend.externalapi.controller;

import com.blibli.oss.backend.common.model.response.Response;
import com.blibli.oss.backend.common.webflux.controller.CommonErrorController;
import com.blibli.oss.backend.externalapi.exception.ExternalApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public interface ExternalApiErrorController extends CommonErrorController {

  @ExceptionHandler(ExternalApiException.class)
  default ResponseEntity<Response<Object>> commandValidationException(ExternalApiException e) {
    getLogger().warn(ExternalApiException.class.getName(), e);

    Response<Object> response = new Response<>();
    response.setCode(HttpStatus.UNAUTHORIZED.value());
    response.setStatus(HttpStatus.UNAUTHORIZED.name());

    return ResponseEntity.badRequest().body(response);
  }

}
