package com.blibli.oss.backend.aggregate.query.apiclient;

import com.blibli.oss.backend.aggregate.query.model.AggregateQueryResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

public interface AggregateQueryApiClient<T> {

  @RequestMapping(
    value = "/api-native/{index}/_search",
    method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<AggregateQueryResponse<T>> search(@PathVariable("index") String index,
                                         @RequestBody String request);

  @RequestMapping(
    value = "/api-native/{index}/{id}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<AggregateQueryResponse<T>> get(@PathVariable("index") String index,
                                      @PathVariable("id") String id,
                                      @RequestBody String request);

  @RequestMapping(
    value = "/api-native/{index}/_scroll",
    method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<AggregateQueryResponse<T>> scroll(@PathVariable("index") String index,
                                         @RequestBody String request);

  @RequestMapping(
    value = "/api-native/{index}/_scroll/{scroll_id}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<AggregateQueryResponse<T>> nextScroll(@PathVariable("index") String index,
                                             @PathVariable("scroll_id") String scrollId);

}
