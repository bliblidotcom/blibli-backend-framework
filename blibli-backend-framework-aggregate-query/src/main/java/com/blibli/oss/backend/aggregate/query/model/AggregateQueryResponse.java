package com.blibli.oss.backend.aggregate.query.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.function.Function;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregateQueryResponse<T> {

  private Integer took;

  @JsonProperty("_scroll_id")
  private String scrollId;

  @JsonProperty("timed_out")
  private Boolean timedOut;

  private AggregateQueryHits<T> hits;

  private Map<String, Object> aggregations;

  public <R> R aggregationsAs(Function<Map<String, Object>, R> function) {
    return function.apply(aggregations);
  }

}
