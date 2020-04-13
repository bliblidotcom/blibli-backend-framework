package com.blibli.oss.backend.mandatoryparameter.helper;

import brave.Span;
import brave.Tracer;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MandatoryParameterHelperTest.Application.class)
class MandatoryParameterHelperTest {

  public static final MandatoryParameter MANDATORY_PARAMETER = MandatoryParameter.builder()
    .storeId("storeId")
    .channelId("channelId")
    .clientId("clientId")
    .requestId("requestId")
    .username("username")
    .build();

  @Autowired
  private Tracer tracer;

  private Span span;

  @BeforeEach
  void setUp() {
    span = tracer.currentSpan();
    if (Objects.isNull(span)) {
      span = tracer.nextSpan().start();
    }
  }

  @Test
  void testToSleuth() {
    MandatoryParameterHelper.toSleuth(span.context(), MANDATORY_PARAMETER);
    MandatoryParameter mandatoryParameter = MandatoryParameterHelper.fromSleuth(span.context());

    assertEquals(MANDATORY_PARAMETER, mandatoryParameter);
  }

  @SpringBootApplication
  static class Application {

  }

}