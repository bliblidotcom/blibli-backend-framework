package com.blibli.oss.backend.apiclient.client;

import com.blibli.oss.backend.apiclient.TestApplication;
import com.blibli.oss.backend.apiclient.annotation.ApiClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

@ApiClient(
  name = "helloApiClient",
  tcpClientCustomizers = TestApplication.WireTrapTcpClientCustomizer.class
)
public interface HelloApiClient {

  @RequestMapping(value = "/hello", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
  Mono<String> hello();

}
