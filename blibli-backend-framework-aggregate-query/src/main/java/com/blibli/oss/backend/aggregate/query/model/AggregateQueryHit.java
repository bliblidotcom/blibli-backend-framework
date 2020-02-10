package com.blibli.oss.backend.aggregate.query.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregateQueryHit<T> {

  private String index;

  private String doc;

  private String id;

  private Double score;

  private T source;

  public <R> R sourceAs(ObjectMapper objectMapper, Class<R> tClass) {
    return objectMapper.convertValue(source, tClass);
  }

}
