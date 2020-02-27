package com.blibli.oss.backend.aggregate.query.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregateQueryHit<T> {

  @JsonProperty("_index")
  private String index;

  @JsonProperty("_type")
  private String type;

  @JsonProperty("_id")
  private String id;

  @JsonProperty("_score")
  private Double score;

  private Boolean found;

  @JsonProperty("_version")
  private Integer version;

  @JsonProperty("_source")
  private T source;

  public <R> R sourceAs(Function<T, R> function) {
    return function.apply(source);
  }

}
