package com.blibli.oss.backend.apiclient.controller;

import brave.Tracer;
import brave.propagation.ExtraFieldPropagation;
import com.blibli.oss.backend.apiclient.client.SleuthApiClient;
import com.blibli.oss.backend.apiclient.client.model.GenericResponse;
import com.blibli.oss.backend.sleuth.configuration.SleuthConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SleuthController {

  @Autowired
  private Tracer tracer;

  @Autowired
  private SleuthApiClient sleuthApiClient;

  @GetMapping(
    value = "/first",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Mono<Map<String, String>> first(@RequestParam("firstName") String firstName,
                                         @RequestParam("lastName") String lastName) {
    ExtraFieldPropagation.set(tracer.currentSpan().context(), "FirstName", firstName);
    ExtraFieldPropagation.set(tracer.currentSpan().context(), "LastName", lastName);
    return sleuthApiClient.second();
  }

  @GetMapping(
    value = "/second",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Mono<Map<String, String>> second(ServerWebExchange exchange) {
    Map<String, String> map = new HashMap<>();
    map.put("firstName", ExtraFieldPropagation.get(tracer.currentSpan().context(), "FirstName"));
    map.put("lastName", ExtraFieldPropagation.get(tracer.currentSpan().context(), "LastName"));
    map.put("headerFirstName", exchange.getRequest().getHeaders().getFirst(SleuthConfiguration.HTTP_BAGGAGE_PREFIX + "firstname"));
    map.put("headerLastName", exchange.getRequest().getHeaders().getFirst(SleuthConfiguration.HTTP_BAGGAGE_PREFIX + "lastname"));
    return Mono.just(map);
  }

  @GetMapping(
    value = "/list",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Mono<GenericResponse<List<String>>> list() {
    return sleuthApiClient.names(Arrays.asList("Eko", "Kurniawan", "Khannedy"));
  }

  @GetMapping(
    value = "/names",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Mono<GenericResponse<List<String>>> names(@RequestParam("names") List<String> names) {
    GenericResponse<List<String>> response = new GenericResponse<>(names);
    return Mono.just(response);
  }

  @GetMapping(
    value = "/param",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Mono<GenericResponse<Object>> param(ServerWebExchange exchange) {
    return Mono.just(new GenericResponse<>(exchange.getRequest().getQueryParams()));
  }

  @GetMapping(
    value = "/test-param",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Mono<GenericResponse<Object>> testParam() {
    return sleuthApiClient.param();
  }

}
