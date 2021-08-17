package com.blibli.oss.backend.apiclient.client;

import com.blibli.oss.backend.apiclient.annotation.ApiClient;
import com.blibli.oss.backend.apiclient.client.model.GenericResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@ApiClient(
  name = "sleuthApiClient"
)
public interface SleuthApiClient {

  @RequestMapping(
    method = RequestMethod.GET,
    value = "/second",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<Map<String, String>> second();

  @RequestMapping(
    value = "/names",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<GenericResponse<List<String>>> names(@RequestParam("names") List<String> names);

  @RequestMapping(
    method = RequestMethod.GET,
    value = "/param",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<GenericResponse<Object>> param();

}
