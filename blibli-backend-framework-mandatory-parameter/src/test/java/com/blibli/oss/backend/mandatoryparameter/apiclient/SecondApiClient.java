package com.blibli.oss.backend.mandatoryparameter.apiclient;

import com.blibli.oss.backend.apiclient.annotation.ApiClient;
import com.blibli.oss.backend.mandatoryparameter.apiclient.MandatoryParameterApiClientInterceptor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

import java.util.Map;

@ApiClient(
  name = "secondApiClient",
  interceptors = {
    MandatoryParameterApiClientInterceptor.class
  }
)
public interface SecondApiClient {

  @RequestMapping(
    value = "/second",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<Map<String, String>> second();

}
