package com.blibli.oss.backend.mandatoryparameter.sleuth;

import brave.Tracer;
import com.blibli.oss.backend.mandatoryparameter.helper.MandatoryParameterHelper;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.swagger.annotation.MandatoryParameterAtQuery;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = MandatoryParameterSleuthTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class MandatoryParameterSleuthTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private MandatoryParameterProperties properties;

  @Test
  void testExtraFieldsSleuth() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/sleuth")
        .queryParam(properties.getQueryKey().getStoreId(), "storeId")
        .queryParam(properties.getQueryKey().getChannelId(), "channelId")
        .queryParam(properties.getQueryKey().getClientId(), "clientId")
        .queryParam(properties.getQueryKey().getUsername(), "username")
        .queryParam(properties.getQueryKey().getRequestId(), "requestId")
        .build())
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody()
      .jsonPath("$.storeId").isEqualTo("storeId")
      .jsonPath("$.channelId").isEqualTo("channelId")
      .jsonPath("$.clientId").isEqualTo("clientId")
      .jsonPath("$.username").isEqualTo("username")
      .jsonPath("$.requestId").isEqualTo("requestId");
  }

  @SpringBootApplication
  public static class Application {

    @RestController
    public static class SleuthController {

      @Autowired
      private SleuthService sleuthService;

      @MandatoryParameterAtQuery
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

  }
}
