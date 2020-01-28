package com.blibli.oss.backend.common.helper;

import com.blibli.oss.backend.common.model.response.Paging;
import com.blibli.oss.backend.common.model.response.Response;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class ResponseHelper {

  public static <T> Response<T> ok() {
    return ResponseHelper.status(HttpStatus.OK);
  }

  public static <T> Response<T> ok(T data) {
    return ResponseHelper.status(HttpStatus.OK, data);
  }

  public static <T> Response<T> internalServerError() {
    return ResponseHelper.status(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public static <T> Response<T> unauthorized() {
    return ResponseHelper.status(HttpStatus.UNAUTHORIZED);
  }

  public static <T> Response<T> badRequest(Map<String, List<String>> errors) {
    return ResponseHelper.status(HttpStatus.BAD_REQUEST, null, null, errors);
  }

  public static <T> Response<T> status(HttpStatus status) {
    return ResponseHelper.status(status, null);
  }

  public static <T> Response<T> status(HttpStatus status, T data) {
    return ResponseHelper.status(status, data, null);
  }

  public static <T> Response<T> status(HttpStatus status, T data, Paging paging) {
    return ResponseHelper.status(status, data, paging, null);
  }

  public static <T> Response<T> status(HttpStatus status, T data, Paging paging, Map<String, List<String>> errors) {
    return Response.<T>builder()
      .code(status.value())
      .status(status.name())
      .paging(paging)
      .data(data)
      .errors(errors)
      .build();
  }

}
