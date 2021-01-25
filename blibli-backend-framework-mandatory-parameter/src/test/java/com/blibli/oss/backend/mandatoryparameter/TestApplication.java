package com.blibli.oss.backend.mandatoryparameter;

import brave.Tracer;
import com.blibli.oss.backend.mandatoryparameter.apiclient.FirstApiClient;
import com.blibli.oss.backend.mandatoryparameter.apiclient.SecondApiClient;
import com.blibli.oss.backend.mandatoryparameter.helper.MandatoryParameterHelper;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.swagger.annotation.MandatoryParameterAtHeader;
import com.blibli.oss.backend.mandatoryparameter.swagger.annotation.MandatoryParameterAtQuery;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class TestApplication {

  @MandatoryParameterAtQuery
  @RestController
  public static class QueryController {

    @GetMapping("/query")
    public Mono<MandatoryParameter> query(MandatoryParameter parameter) {
      return Mono.just(parameter);
    }

  }

  @MandatoryParameterAtHeader
  @RestController
  public static class HeaderController {

    @GetMapping("/header")
    public Mono<MandatoryParameter> header(MandatoryParameter parameter) {
      return Mono.just(parameter);
    }

  }

  @RestController
  public static class SleuthController {

    @Autowired
    private SleuthService sleuthService;

    @GetMapping(
      value = "/sleuth",
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<MandatoryParameter> mandatoryParameter(MandatoryParameter mandatoryParameter) {
      return sleuthService.getMandatoryParameter();
    }

  }

  @Service
  public static class SleuthService {

    @Autowired
    private Tracer tracer;

    public Mono<MandatoryParameter> getMandatoryParameter() {
      return Mono.fromCallable(() -> MandatoryParameterHelper.fromSleuth(tracer.currentSpan().context()));
    }

  }

  @RestController
  @Slf4j
  public static class ApiClientController {

    @Autowired
    private FirstApiClient firstApiClient;

    @Autowired
    private SecondApiClient secondApiClient;

    @Autowired
    private MandatoryParameterProperties properties;

    @GetMapping(value = "/first", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> first(ServerWebExchange exchange) {
      return Mono.fromCallable(() -> {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        Map<String, String> map = new HashMap<>();
        map.put(properties.getQueryKey().getStoreId(), queryParams.getFirst(properties.getQueryKey().getStoreId()));
        map.put(properties.getQueryKey().getClientId(), queryParams.getFirst(properties.getQueryKey().getClientId()));
        map.put(properties.getQueryKey().getChannelId(), queryParams.getFirst(properties.getQueryKey().getChannelId()));
        map.put(properties.getQueryKey().getUsername(), queryParams.getFirst(properties.getQueryKey().getUsername()));
        map.put(properties.getQueryKey().getRequestId(), queryParams.getFirst(properties.getQueryKey().getRequestId()));

        log.info("Receive : " + exchange.getRequest().getQueryParams().toSingleValueMap().toString());

        return map;
      });
    }

    @GetMapping(value = "/second", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> second(ServerWebExchange exchange) {
      return Mono.fromCallable(() -> {
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
        Map<String, String> map = new HashMap<>();
        map.put(properties.getHeaderKey().getStoreId(), httpHeaders.getFirst(properties.getHeaderKey().getStoreId()));
        map.put(properties.getHeaderKey().getClientId(), httpHeaders.getFirst(properties.getHeaderKey().getClientId()));
        map.put(properties.getHeaderKey().getChannelId(), httpHeaders.getFirst(properties.getHeaderKey().getChannelId()));
        map.put(properties.getHeaderKey().getUsername(), httpHeaders.getFirst(properties.getHeaderKey().getUsername()));
        map.put(properties.getHeaderKey().getRequestId(), httpHeaders.getFirst(properties.getHeaderKey().getRequestId()));

        log.info("Receive : " + exchange.getRequest().getQueryParams().toSingleValueMap().toString());

        return map;
      });
    }

    @GetMapping(value = "/test-first", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> testFirst(MandatoryParameter mandatoryParameter) {
      log.info("Controller : " + mandatoryParameter);
      return firstApiClient.first();
    }

    @GetMapping(value = "/test-second", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> testSecond(MandatoryParameter mandatoryParameter) {
      log.info("Controller : " + mandatoryParameter);
      return secondApiClient.second();
    }

  }

}
