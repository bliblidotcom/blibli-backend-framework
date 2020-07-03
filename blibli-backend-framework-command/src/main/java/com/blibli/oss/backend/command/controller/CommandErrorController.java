package com.blibli.oss.backend.command.controller;

import com.blibli.oss.backend.command.exception.CommandValidationException;
import com.blibli.oss.backend.common.model.response.Response;
import com.blibli.oss.backend.common.webflux.controller.CommonErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

public interface CommandErrorController extends CommonErrorController {

  @ExceptionHandler(CommandValidationException.class)
  default ResponseEntity<Response<Object>> commandValidationException(CommandValidationException e) {
    getLogger().warn(CommandValidationException.class.getName(), e);

    Response<Object> response = new Response<>();
    response.setCode(HttpStatus.BAD_REQUEST.value());
    response.setStatus(HttpStatus.BAD_REQUEST.name());
    response.setErrors(CommonErrorController.from(e.getConstraintViolations()));
    response.setMetadata(Collections.singletonMap("errors", getMetaData(e.getConstraintViolations())));

    return ResponseEntity.badRequest().body(response);
  }

}
