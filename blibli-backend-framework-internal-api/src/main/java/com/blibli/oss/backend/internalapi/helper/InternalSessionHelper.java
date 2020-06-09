package com.blibli.oss.backend.internalapi.helper;

import brave.propagation.ExtraFieldPropagation;
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
      .userId(ExtraFieldPropagation.get(traceContext, InternalSessionSleuth.USER_ID))
      .userName(ExtraFieldPropagation.get(traceContext, InternalSessionSleuth.USER_NAME))
      .roles(getRoles(ExtraFieldPropagation.get(traceContext, InternalSessionSleuth.ROLES)))
      .build();
  }

  public static InternalSession toSleuth(TraceContext traceContext, InternalSession externalSession) {
    ExtraFieldPropagation.set(traceContext, InternalSessionSleuth.USER_ID, externalSession.getUserId());
    ExtraFieldPropagation.set(traceContext, InternalSessionSleuth.USER_NAME, externalSession.getUserName());

    StringJoiner stringJoiner = new StringJoiner(",");
    externalSession.getRoles().forEach(stringJoiner::add);
    ExtraFieldPropagation.set(traceContext, InternalSessionSleuth.USER_NAME, stringJoiner.toString());

    return externalSession;
  }
}
