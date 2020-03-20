package com.blibli.oss.backend.apiclient.client;

import com.blibli.oss.backend.apiclient.annotation.ApiClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

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

}
