package com.blibli.oss.backend.common.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * Standard Blibli Web Response
 */
public class Response<T> {

  /**
   * Code , usually same as HTTP Code
   */
  @JsonProperty("code")
  private Integer code;

  /**
   * Status, usually same as HTTP status
   */
  @JsonProperty("status")
  private String status;

  /**
   * Response data
   */
  @JsonProperty("data")
  private T data;

  /**
   * Paging information, if response is paginate data
   */
  @JsonProperty("paging")
  private Paging paging;

  /**
   * Error information, if request is not valid
   */
  @JsonProperty("errors")
  private Map<String, List<String>> errors;

}
