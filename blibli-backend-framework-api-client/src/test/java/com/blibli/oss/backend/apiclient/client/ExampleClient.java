package com.blibli.oss.backend.apiclient.client;

import com.blibli.oss.backend.apiclient.annotation.ApiClient;
import com.blibli.oss.backend.apiclient.client.model.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@ApiClient(
  name = "exampleClient",
  fallback = ExampleClientFallback.class,
  interceptors = {
    ExampleInterceptor.class
  }
)
public interface ExampleClient {

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

  @RequestMapping(
    method = RequestMethod.GET,
    path = "/third/{userId}"
  )
  Mono<FirstResponse> third(@PathVariable("userId") String userId);

  @RequestMapping(
    method = RequestMethod.GET,
    path = "/forth/{userId}"
  )
  Mono<FirstResponse> forth(@PathVariable("userId") String userId,
                            @RequestParam("page") Integer page,
                            @RequestParam("size") Integer size,
                            @RequestHeader("X-API") String xApi);

  @RequestMapping(
    method = RequestMethod.POST,
    path = "/fifth",
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
  )
  Mono<FirstResponse> fifth(@RequestBody MultiValueMap<String, String> form);

  @RequestMapping(
    method = RequestMethod.POST,
    path = "/sixth",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  Mono<FirstResponse> sixth(@RequestPart("file") Resource file);

  @RequestMapping(
    method = RequestMethod.POST,
    path = "/generics",
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<GenericResponse<String>> generic(@RequestBody String test);

  @RequestMapping(
    method = RequestMethod.POST,
    path = "/generics-two",
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<List<String>> genericTwo(@RequestBody String test);

  @RequestMapping(
    method = RequestMethod.POST,
    path = "/inherited",
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<InheritedResponse> inherited(@RequestBody String test);

  @RequestMapping(
    method = RequestMethod.POST,
    path = "/generic-inherited",
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<GenericResponse<InheritedResponse>> genericInherited(@RequestBody String test);

}
