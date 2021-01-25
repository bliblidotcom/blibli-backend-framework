package com.blibli.oss.backend.internalapi.helper;

import brave.baggage.BaggageField;
import brave.propagation.TraceContext;
import com.blibli.oss.backend.internalapi.model.InternalSession;
import com.blibli.oss.backend.internalapi.properties.InternalApiProperties;
import com.blibli.oss.backend.internalapi.sleuth.InternalSessionSleuth;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class InternalSessionHelper {

  public static InternalSession getInternalSession(HttpHeaders headers, InternalApiProperties properties) {
    return InternalSession.builder()
      .userId(headers.getFirst(properties.getHeader().getUserId()))
      .userName(headers.getFirst(properties.getHeader().getUserName()))
      .roles(getRoles(headers.getFirst(properties.getHeader().getRoles())))
      .build();
  }

  private static List<String> getRoles(String roles) {
    if (StringUtils.hasText(roles)) {
      return Arrays.asList(roles.split(","));
    } else {
      return Collections.emptyList();
    }
  }

  public static InternalSession fromSleuth(TraceContext traceContext) {
    return InternalSession.builder()
      .userId(BaggageField.getByName(traceContext, InternalSessionSleuth.USER_ID).getValue(traceContext))
      .userName(BaggageField.getByName(traceContext, InternalSessionSleuth.USER_NAME).getValue(traceContext))
      .roles(getRoles(BaggageField.getByName(traceContext, InternalSessionSleuth.ROLES).getValue(traceContext)))
      .build();
  }

  public static InternalSession toSleuth(TraceContext traceContext, InternalSession externalSession) {
    BaggageField.getByName(traceContext, InternalSessionSleuth.USER_ID).updateValue(traceContext, externalSession.getUserId());
    BaggageField.getByName(traceContext, InternalSessionSleuth.USER_NAME).updateValue(traceContext, externalSession.getUserName());

    StringJoiner stringJoiner = new StringJoiner(",");
    externalSession.getRoles().forEach(stringJoiner::add);
    BaggageField.getByName(traceContext, InternalSessionSleuth.ROLES).updateValue(traceContext, stringJoiner.toString());

    return externalSession;
  }
}
