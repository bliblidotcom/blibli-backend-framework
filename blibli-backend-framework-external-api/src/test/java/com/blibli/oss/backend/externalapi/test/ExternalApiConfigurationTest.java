package com.blibli.oss.backend.externalapi.test;

import com.blibli.oss.backend.common.helper.ResponseHelper;
import com.blibli.oss.backend.common.model.response.Response;
import com.blibli.oss.backend.externalapi.annotation.MustMember;
import com.blibli.oss.backend.externalapi.controller.ExternalApiErrorController;
import com.blibli.oss.backend.externalapi.model.ExternalSession;
import com.blibli.oss.backend.externalapi.properties.ExternalApiProperties;
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
  classes = ExternalApiConfigurationTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ExternalApiConfigurationTest {

  public static final String USER_ID = "memberId";
  public static final String SESSION_ID = "sessionId";
  public static final String IS_MEMBER = "true";
  public static final String IS_GUEST = "false";
  public static final String ADDITIONAL_A = "A";
  public static final String ADDITIONAL_B = "B";

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private ExternalApiProperties properties;

  @Test
  void testAdditionalParameter() {
    webTestClient.get().uri("/member")
      .header(properties.getHeader().getUserId(), USER_ID)
      .header(properties.getHeader().getSessionId(), SESSION_ID)
      .header(properties.getHeader().getIsMember(), IS_MEMBER)
      .header(properties.getHeader().getAdditionalParameterPrefix() + ADDITIONAL_A, ADDITIONAL_A)
      .header(properties.getHeader().getAdditionalParameterPrefix() + ADDITIONAL_B, ADDITIONAL_B)
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody()
      .jsonPath("$.code").isEqualTo(HttpStatus.OK.value())
      .jsonPath("$.status").isEqualTo(HttpStatus.OK.name())
      .jsonPath("$.data.userId").isEqualTo(USER_ID)
      .jsonPath("$.data.sessionId").isEqualTo(SESSION_ID)
      .jsonPath("$.data.member").isEqualTo(IS_MEMBER)
      .jsonPath("$.data.additionalParameters." + properties.getHeader().getAdditionalParameterPrefix() + ADDITIONAL_A).isEqualTo(ADDITIONAL_A)
      .jsonPath("$.data.additionalParameters." + properties.getHeader().getAdditionalParameterPrefix() + ADDITIONAL_B).isEqualTo(ADDITIONAL_B);
  }

  @Test
  void testMember() {
    webTestClient.get().uri("/member")
      .header(properties.getHeader().getUserId(), USER_ID)
      .header(properties.getHeader().getSessionId(), SESSION_ID)
      .header(properties.getHeader().getIsMember(), IS_MEMBER)
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody()
      .jsonPath("$.code").isEqualTo(HttpStatus.OK.value())
      .jsonPath("$.status").isEqualTo(HttpStatus.OK.name())
      .jsonPath("$.data.userId").isEqualTo(USER_ID)
      .jsonPath("$.data.sessionId").isEqualTo(SESSION_ID)
      .jsonPath("$.data.member").isEqualTo(IS_MEMBER);
  }

  @Test
  void testMemberButGuest() {
    webTestClient.get().uri("/member")
      .header(properties.getHeader().getUserId(), USER_ID)
      .header(properties.getHeader().getSessionId(), SESSION_ID)
      .header(properties.getHeader().getIsMember(), IS_GUEST)
      .exchange()
      .expectStatus().is4xxClientError()
      .expectBody()
      .jsonPath("$.code").isEqualTo(HttpStatus.UNAUTHORIZED.value())
      .jsonPath("$.status").isEqualTo(HttpStatus.UNAUTHORIZED.name());
  }

  @Test
  void testGuest() {
    webTestClient.get().uri("/guest")
      .header(properties.getHeader().getUserId(), USER_ID)
      .header(properties.getHeader().getSessionId(), SESSION_ID)
      .header(properties.getHeader().getIsMember(), IS_GUEST)
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody()
      .jsonPath("$.code").isEqualTo(HttpStatus.OK.value())
      .jsonPath("$.status").isEqualTo(HttpStatus.OK.name())
      .jsonPath("$.data.userId").isEqualTo(USER_ID)
      .jsonPath("$.data.sessionId").isEqualTo(SESSION_ID)
      .jsonPath("$.data.member").isEqualTo(IS_GUEST);
  }

  @Test
  void testGuestButMember() {
    webTestClient.get().uri("/guest")
      .header(properties.getHeader().getUserId(), USER_ID)
      .header(properties.getHeader().getSessionId(), SESSION_ID)
      .header(properties.getHeader().getIsMember(), IS_MEMBER)
      .exchange()
      .expectStatus().is4xxClientError()
      .expectBody()
      .jsonPath("$.code").isEqualTo(HttpStatus.UNAUTHORIZED.value())
      .jsonPath("$.status").isEqualTo(HttpStatus.UNAUTHORIZED.name());
  }

  @SpringBootApplication
  static class Application {

    @RestController
    static class ExampleController {


      @GetMapping(value = "/member", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<Response<ExternalSession>> member(@MustMember ExternalSession externalSession) {
        return Mono.just(ResponseHelper.ok(externalSession));
      }

      @GetMapping(value = "/guest", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<Response<ExternalSession>> guest(@MustMember(false) ExternalSession externalSession) {
        return Mono.just(ResponseHelper.ok(externalSession));
      }

    }

    @Slf4j
    @RestControllerAdvice
    static class ErrorController implements ExternalApiErrorController, MessageSourceAware {

      @Getter
      @Setter
      private MessageSource messageSource;

      @Override
      public Logger getLogger() {
        return log;
      }
    }

  }

}