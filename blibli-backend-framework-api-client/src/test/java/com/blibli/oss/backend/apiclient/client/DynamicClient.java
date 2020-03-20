package com.blibli.oss.backend.apiclient.client;

import com.blibli.oss.backend.apiclient.annotation.ApiClient;
import com.blibli.oss.backend.apiclient.annotation.ApiUrl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

@ApiClient(
  name = "dynamicClient"
)
public interface DynamicClient {

  @RequestMapping(
    method = RequestMethod.GET,
    value = "/dynamic",
    produces = MediaType.TEXT_PLAIN_VALUE
  )
  Mono<String> dynamic(@ApiUrl String baseUrl);

}
