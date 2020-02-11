package com.blibli.oss.backend.aggregate.query.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregateQueryResponse<T> {

  private Integer took;

  @JsonProperty("timed_out")
  private Boolean timedOut;

  private AggregateQueryHits<T> hits;

  private Map<String, Object> aggregations;

  public <R> R aggregationsAs(ObjectMapper objectMapper, Class<R> tClass) {
    return objectMapper.convertValue(aggregations, tClass);
  }

}
