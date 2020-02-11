package com.blibli.oss.backend.aggregate.query.fallback;

import com.blibli.oss.backend.aggregate.query.apiclient.AggregateQueryApiClient;
import com.blibli.oss.backend.aggregate.query.model.AggregateQueryHit;
import com.blibli.oss.backend.aggregate.query.model.AggregateQueryResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public class AggregateQueryApiClientFallback implements AggregateQueryApiClient {

  @Override
  public Mono<AggregateQueryResponse<Map<String, Object>>> search(String index, String request) {
    return Mono.just(AggregateQueryResponse.<Map<String, Object>>builder().build());
  }

  @Override
  public Mono<AggregateQueryHit<Map<String, Object>>> get(String index, String id) {
    return Mono.just(AggregateQueryHit.<Map<String, Object>>builder().build());
  }

  @Override
  public Mono<AggregateQueryResponse<Map<String, Object>>> scroll(String index, String request) {
    return Mono.just(AggregateQueryResponse.<Map<String, Object>>builder().build());
  }

  @Override
  public Mono<AggregateQueryResponse<Map<String, Object>>> nextScroll(String index, String scrollId) {
    return Mono.just(AggregateQueryResponse.<Map<String, Object>>builder().build());
  }
}
