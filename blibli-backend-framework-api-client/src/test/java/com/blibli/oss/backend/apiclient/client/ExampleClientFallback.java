package com.blibli.oss.backend.apiclient.client;

import com.blibli.oss.backend.apiclient.client.model.*;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
public class ExampleClientFallback implements ExampleClient {

  @Override
  public Mono<FirstResponse> first(FirstRequest request) {
    return Mono.just(FirstResponse.builder()
      .hello("Ups First")
      .build());
  }

  @Override
  public Mono<SecondResponse> second() {
    return Mono.just(SecondResponse.builder()
      .hello("Ups Second")
      .build());
  }

  @Override
  public Mono<FirstResponse> third(String userId) {
    return Mono.just(FirstResponse.builder()
      .hello("Ups Third")
      .build());
  }

  @Override
  public Mono<FirstResponse> forth(String userId, Integer page, Integer size, String xApi) {
    return Mono.just(FirstResponse.builder()
      .hello("Ups Forth")
      .build());
  }

  @Override
  public Mono<FirstResponse> fifth(MultiValueMap<String, String> form) {
    return Mono.just(FirstResponse.builder()
      .hello("Ups Fifth")
      .build());
  }

  @Override
  public Mono<FirstResponse> sixth(Resource file) {
    return Mono.just(FirstResponse.builder()
      .hello("Ups Sixth")
      .build());
  }

  @Override
  public Mono<GenericResponse<String>> generic(String test) {
    return Mono.just(GenericResponse.<String>builder()
      .value("Ups Generic")
      .build());
  }

  @Override
  public Mono<List<String>> genericTwo(String test) {
    return Mono.just(Collections.singletonList("Ups Generic Two"));
  }

  @Override
  public Mono<InheritedResponse> inherited(String test) {
    return Mono.just(InheritedResponse.inheritedBuilder().name("fallback").detail("fallback").build());
  }

  @Override
  public Mono<GenericResponse<InheritedResponse>> genericInherited(String test) {
    return Mono.just(
      GenericResponse.<InheritedResponse>builder()
         .value(
           InheritedResponse.inheritedBuilder()
             .name("fallback")
             .detail("fallback")
             .build()
         )
        .build()
    );
  }
}
