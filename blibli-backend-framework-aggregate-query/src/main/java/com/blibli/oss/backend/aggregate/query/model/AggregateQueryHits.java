package com.blibli.oss.backend.aggregate.query.model;

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
public class AggregateQueryHits {

  private Long total;

  private Double maxScore;

  private List<AggregateQueryHit> hits;

  public <T> List<T> as(ObjectMapper objectMapper, Class<T> tClass) {
    return hits.stream()
      .map(item -> item.as(objectMapper, tClass))
      .collect(Collectors.toList());
  }

}
