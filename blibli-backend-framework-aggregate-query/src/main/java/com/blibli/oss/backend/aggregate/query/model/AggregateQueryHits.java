package com.blibli.oss.backend.aggregate.query.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
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

  public <R> List<R> hitsAs(ObjectMapper objectMapper, Class<R> tClass) {
    return hits.stream()
      .map(item -> item.sourceAs(objectMapper, tClass))
      .collect(Collectors.toList());
  }

}
