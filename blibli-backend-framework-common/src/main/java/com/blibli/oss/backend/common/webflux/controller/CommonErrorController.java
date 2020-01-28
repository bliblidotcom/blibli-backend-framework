package com.blibli.oss.backend.common.webflux.controller;

import com.blibli.oss.backend.common.model.response.Response;
import org.slf4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.*;

import java.util.*;

public interface CommonErrorController {

  Logger getLogger();

  MessageSource getMessageSource();

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  default Response<Object> methodArgumentNotValidException(MethodArgumentNotValidException e) {
    getLogger().warn(MethodArgumentNotValidException.class.getName(), e);
    Response<Object> response = new Response<>();
    response.setCode(HttpStatus.BAD_REQUEST.value());
    response.setStatus(HttpStatus.BAD_REQUEST.name());
    response.setErrors(from(e.getBindingResult(), getMessageSource()));
    return response;
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BindException.class)
  default Response<Object> bindException(BindException e) {
    getLogger().warn(BindException.class.getName(), e);
    Response<Object> response = new Response<>();
    response.setCode(HttpStatus.BAD_REQUEST.value());
    response.setStatus(HttpStatus.BAD_REQUEST.name());
    response.setErrors(from(e.getBindingResult(), getMessageSource()));
    return response;
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  default Response<Object> httpMessageNotReadableException(HttpMessageNotReadableException e) {
    getLogger().warn(HttpMessageNotReadableException.class.getName(), e);
    Response<Object> response = new Response<>();
    response.setCode(HttpStatus.BAD_REQUEST.value());
    response.setStatus(HttpStatus.BAD_REQUEST.name());
    return response;
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Throwable.class)
  default Response<Object> throwable(Throwable e) {
    getLogger().error(e.getClass().getName(), e);
    Response<Object> response = new Response<>();
    response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
    return response;
  }

  @ExceptionHandler(ServerWebInputException.class)
  default ResponseEntity<Response<Object>> serverWebInputException(ServerWebInputException e) {
    getLogger().warn(ServerWebInputException.class.getName(), e);

    Map<String, List<String>> errors = new HashMap<>();
    if (e.getMethodParameter() != null) {
      errors.put(e.getMethodParameter().getParameterName(), Collections.singletonList(e.getReason()));
    }

    Response<Object> response = new Response<>();
    response.setCode(e.getStatus().value());
    response.setStatus(e.getStatus().name());
    response.setErrors(errors);

    return ResponseEntity.status(e.getStatus()).body(response);
  }

  @ExceptionHandler(WebExchangeBindException.class)
  default ResponseEntity<Response<Object>> serverWebInputException(WebExchangeBindException e) {
    getLogger().warn(WebExchangeBindException.class.getName(), e);

    Response<Object> response = new Response<>();
    response.setCode(e.getStatus().value());
    response.setStatus(e.getStatus().name());
    response.setErrors(from(e.getBindingResult(), getMessageSource()));

    return ResponseEntity.status(e.getStatus()).body(response);
  }

  @ExceptionHandler(ResponseStatusException.class)
  default ResponseEntity<Response<Object>> responseStatusException(ResponseStatusException e) {
    getLogger().warn(ResponseStatusException.class.getName(), e);
    Map<String, List<String>> errors = new HashMap<>();
    errors.put("reason", Collections.singletonList(e.getReason()));

    Response<Object> response = new Response<>();
    response.setCode(e.getStatus().value());
    response.setStatus(e.getStatus().name());
    response.setErrors(errors);

    return ResponseEntity.status(e.getStatus()).body(response);
  }

  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  @ExceptionHandler(MediaTypeNotSupportedStatusException.class)
  default Response<Object> mediaTypeNotSupportedException(MediaTypeNotSupportedStatusException e) {
    getLogger().warn(MediaTypeNotSupportedStatusException.class.getName(), e);
    Response<Object> response = new Response<>();
    response.setCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.name());
    return response;
  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(NotAcceptableStatusException.class)
  default Response<Object> notAcceptableStatusException(NotAcceptableStatusException e) {
    getLogger().warn(NotAcceptableStatusException.class.getName(), e);
    Response<Object> response = new Response<>();
    response.setCode(HttpStatus.NOT_ACCEPTABLE.value());
    response.setStatus(HttpStatus.NOT_ACCEPTABLE.name());
    return response;
  }

  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
  default Response<Object> unsupportedMediaTypeStatusException(UnsupportedMediaTypeStatusException e) {
    getLogger().warn(UnsupportedMediaTypeStatusException.class.getName(), e);
    Response<Object> response = new Response<>();
    response.setCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.name());
    return response;
  }

  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  @ExceptionHandler(MethodNotAllowedException.class)
  default Response<Object> methodNotAllowedException(MethodNotAllowedException e) {
    getLogger().warn(MethodNotAllowedException.class.getName(), e);
    Response<Object> response = new Response<>();
    response.setCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.name());
    return response;
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(ServerErrorException.class)
  default Response<Object> serverErrorException(ServerErrorException e) {
    getLogger().warn(ServerErrorException.class.getName(), e);
    Response<Object> response = new Response<>();
    response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
    return response;
  }

  static Map<String, List<String>> from(BindingResult result, MessageSource messageSource) {
    return from(result, messageSource, Locale.getDefault());
  }

  static Map<String, List<String>> from(BindingResult result, MessageSource messageSource, Locale locale) {
    if (result.hasFieldErrors()) {
      Map<String, List<String>> map = new HashMap<>();

      for (FieldError fieldError : result.getFieldErrors()) {
        String field = fieldError.getField();

        if (!map.containsKey(fieldError.getField())) {
          map.put(field, new ArrayList<>());
        }

        String errorMessage = messageSource.getMessage(fieldError.getCode(), fieldError.getArguments(), fieldError.getDefaultMessage(), locale);
        map.get(field).add(errorMessage);
      }

      return map;
    } else {
      return Collections.emptyMap();
    }
  }

}
