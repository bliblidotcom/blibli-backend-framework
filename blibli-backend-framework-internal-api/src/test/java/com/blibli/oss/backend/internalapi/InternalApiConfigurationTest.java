package com.blibli.oss.backend.internalapi;

import com.blibli.oss.backend.common.helper.ResponseHelper;
import com.blibli.oss.backend.common.model.response.Response;
import com.blibli.oss.backend.common.webflux.controller.CommonErrorController;
import com.blibli.oss.backend.internalapi.model.InternalSession;
import com.blibli.oss.backend.internalapi.properties.InternalApiProperties;
import com.blibli.oss.backend.internalapi.swagger.annotation.InternalSessionAtHeader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = InternalApiConfigurationTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class InternalApiConfigurationTest {

  public static final String USER_ID = "userId";
  public static final String USER_NAME = "userName";
  public static final String ROLE_1 = "role1";
  public static final String ROLE_2 = "role2";

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private InternalApiProperties properties;

  @Test
  void testInternalSession() {
    webTestClient.get().uri("/internal-session")
      .header(properties.getHeader().getUserId(), USER_ID)
      .header(properties.getHeader().getUserName(), USER_NAME)
      .header(properties.getHeader().getRoles(), ROLE_1 + "," + ROLE_2)
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody()
      .jsonPath("$.code").isEqualTo(HttpStatus.OK.value())
      .jsonPath("$.status").isEqualTo(HttpStatus.OK.name())
      .jsonPath("$.data.userId").isEqualTo(USER_ID)
      .jsonPath("$.data.userName").isEqualTo(USER_NAME)
      .jsonPath("$.data.roles[0]").isEqualTo(ROLE_1)
      .jsonPath("$.data.roles[1]").isEqualTo(ROLE_2);
  }

  @SpringBootApplication
  public static class Application {

    @RestController
    public static class TestController {

      @InternalSessionAtHeader
      @GetMapping(path = "/internal-session", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<Response<InternalSession>> internalSession(InternalSession internalSession) {
        return Mono.just(ResponseHelper.ok(internalSession));
      }

    }

    @Slf4j
    @RestControllerAdvice
    public static class ErrorController implements CommonErrorController, MessageSourceAware {

      @Setter
      @Getter
      private MessageSource messageSource;

      @Override
      public Logger getLogger() {
        return log;
      }
    }

  }
}
