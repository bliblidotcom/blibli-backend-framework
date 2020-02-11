package com.blibli.oss.backend.aggregate.query.example;

import com.blibli.oss.backend.aggregate.query.apiclient.AggregateQueryApiClient;
import com.blibli.oss.backend.apiclient.annotation.ApiClient;

@ApiClient(
  name = "exampleAggregateQueryApiClient",
  fallback = ExampleAggregateQueryApiClientFallback.class
)
public interface ExampleAggregateQueryApiClient extends AggregateQueryApiClient {

}
