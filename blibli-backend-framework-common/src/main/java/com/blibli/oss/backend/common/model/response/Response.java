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
public class Response<T> {

  @JsonProperty("code")
  private Integer code;

  @JsonProperty("status")
  private String status;

  @JsonProperty("data")
  private T data;

  @JsonProperty("paging")
  private Paging paging;

  @JsonProperty("errors")
  private Map<String, List<String>> errors;

}
