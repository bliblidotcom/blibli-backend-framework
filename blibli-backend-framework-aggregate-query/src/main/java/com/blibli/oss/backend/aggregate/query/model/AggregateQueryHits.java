package com.blibli.oss.backend.aggregate.query.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregateQueryHits<T> {

  private Long total;

  @JsonProperty("max_score")
  private Double maxScore;

  private List<AggregateQueryHit<T>> hits;

  public List<T> hitsOnly() {
    return hits.stream().
      map(AggregateQueryHit::getSource).
      collect(Collectors.toList());
  }

  public <R> List<R> hitsAs(Function<T, R> function) {
    return hits.stream()
      .map(item -> item.sourceAs(function))
      .collect(Collectors.toList());
  }

}
