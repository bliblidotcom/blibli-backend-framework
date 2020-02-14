package com.blibli.oss.backend.aggregate.query.apiclient;

import com.blibli.oss.backend.aggregate.query.fallback.AggregateQueryApiClientFallback;
import com.blibli.oss.backend.aggregate.query.interceptor.AggregateQueryApiClientInterceptor;
import com.blibli.oss.backend.aggregate.query.model.AggregateQueryHit;
import com.blibli.oss.backend.aggregate.query.model.AggregateQueryResponse;
import com.blibli.oss.backend.apiclient.annotation.ApiClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

import java.util.Map;

@ApiClient(
  name = AggregateQueryApiClient.NAME,
  fallback = AggregateQueryApiClientFallback.class,
  interceptors = {
    AggregateQueryApiClientInterceptor.class
  }
)
public interface AggregateQueryApiClient {

  String NAME = "aggregateQueryApiClient";

  @RequestMapping(
    value = "/api-native/{index}/_search",
    method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<AggregateQueryResponse<Map<String, Object>>> search(@PathVariable("index") String index,
                                                           @RequestBody String request);

  @RequestMapping(
    value = "/api-native/{index}/{id}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<AggregateQueryHit<Map<String, Object>>> get(@PathVariable("index") String index,
                                                   @PathVariable("id") String id);

  @RequestMapping(
    value = "/api-native/{index}/_scroll",
    method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<AggregateQueryResponse<Map<String, Object>>> scroll(@PathVariable("index") String index,
                                                           @RequestBody String request);

  @RequestMapping(
    value = "/api-native/{index}/_scroll/{scroll_id}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<AggregateQueryResponse<Map<String, Object>>> nextScroll(@PathVariable("index") String index,
                                                               @PathVariable("scroll_id") String scrollId);

}
