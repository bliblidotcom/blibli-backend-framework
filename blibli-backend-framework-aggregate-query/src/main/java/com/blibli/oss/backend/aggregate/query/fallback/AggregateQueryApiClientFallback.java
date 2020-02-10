package com.blibli.oss.backend.aggregate.query.fallback;

import com.blibli.oss.backend.aggregate.query.apiclient.AggregateQueryApiClient;
import com.blibli.oss.backend.aggregate.query.model.AggregateQueryResponse;
import reactor.core.publisher.Mono;

public class AggregateQueryApiClientFallback<T> implements AggregateQueryApiClient<T> {

  @Override
  public Mono<AggregateQueryResponse<T>> search(String index, String request) {
    return Mono.just(AggregateQueryResponse.<T>builder().build());
  }

  @Override
  public Mono<AggregateQueryResponse<T>> get(String index, String id, String request) {
    return null;
  }

  @Override
  public Mono<AggregateQueryResponse<T>> scroll(String index, String request) {
    return null;
  }

  @Override
  public Mono<AggregateQueryResponse<T>> nextScroll(String index, String scrollId) {
    return null;
  }
}
