package com.blibli.oss.backend.aggregate.query.model;

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
public class AggregateQueryHit {

  private String index;

  private String doc;

  private String id;

  private Double score;

  private Map<String, Object> source;

  public <T> T as(ObjectMapper objectMapper, Class<T> tClass) {
    return objectMapper.convertValue(source, tClass);
  }

}
