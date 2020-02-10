package com.blibli.oss.backend.aggregate.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregateQueryResponse<T> {

  private Integer took;

  private Boolean timedOut;

  private AggregateQueryHits<T> hits;

}
