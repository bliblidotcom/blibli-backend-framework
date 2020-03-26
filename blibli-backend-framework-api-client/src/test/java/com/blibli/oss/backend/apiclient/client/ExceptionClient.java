package com.blibli.oss.backend.apiclient.client;

import com.blibli.oss.backend.apiclient.annotation.ApiClient;
import com.blibli.oss.backend.apiclient.client.model.FirstRequest;
import com.blibli.oss.backend.apiclient.client.model.FirstResponse;
import com.blibli.oss.backend.apiclient.client.model.SecondResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

@ApiClient(
  name = "exceptionClient",
  fallback = ExceptionClientFallback.class
)
public interface ExceptionClient {

  @RequestMapping(
    method = RequestMethod.POST,
    path = "/first",
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<FirstResponse> first(@RequestBody FirstRequest request);

  @RequestMapping(
    method = RequestMethod.GET,
    path = "/second"
  )
  Mono<SecondResponse> second();

}
