package com.blibli.oss.backend.aggregate.query.fallback;

import com.blibli.oss.backend.aggregate.query.apiclient.AggregateQueryApiClient;
import com.blibli.oss.backend.aggregate.query.model.AggregateQueryHit;
import com.blibli.oss.backend.aggregate.query.model.AggregateQueryHits;
import com.blibli.oss.backend.aggregate.query.model.AggregateQueryResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

public class AggregateQueryApiClientFallback implements AggregateQueryApiClient {

  private static final AggregateQueryResponse<Map<String, Object>> EMPTY_RESPONSES = AggregateQueryResponse.<Map<String, Object>>builder()
    .aggregations(Collections.emptyMap())
    .hits(AggregateQueryHits.<Map<String, Object>>builder()
      .maxScore(0.0)
      .total(0L)
      .hits(Collections.emptyList())
      .build())
    .timedOut(false)
    .took(0)
    .build();

  @Override
  public Mono<AggregateQueryResponse<Map<String, Object>>> search(String index, String request) {
    return Mono.just(EMPTY_RESPONSES);
  }

  @Override
  public Mono<AggregateQueryHit<Map<String, Object>>> get(String index, String id) {
    return Mono.just(AggregateQueryHit.<Map<String, Object>>builder()
      .found(false)
      .id(id)
      .index(index)
      .score(0.0)
      .version(0)
      .source(Collections.emptyMap())
      .build());
  }

  @Override
  public Mono<AggregateQueryResponse<Map<String, Object>>> scroll(String index, String request) {
    return Mono.just(EMPTY_RESPONSES);
  }

  @Override
  public Mono<AggregateQueryResponse<Map<String, Object>>> nextScroll(String index, String scrollId) {
    return Mono.just(EMPTY_RESPONSES);
  }
}
