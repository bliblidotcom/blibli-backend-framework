package com.blibli.oss.backend.externalapi.helper;

import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.blibli.oss.backend.externalapi.model.ExternalSession;
import com.blibli.oss.backend.externalapi.properties.ExternalApiProperties;
import com.blibli.oss.backend.externalapi.sleuth.ExternalSessionSleuth;
import org.springframework.http.HttpHeaders;

import java.util.Objects;

public class ExternalSessionHelper {

  public static ExternalSession getExternalSession(HttpHeaders headers, ExternalApiProperties properties) {
    ExternalSession.ExternalSessionBuilder externalSessionBuilder = ExternalSession.builder()
      .userId(headers.getFirst(properties.getHeader().getUserId()))
      .sessionId(headers.getFirst(properties.getHeader().getSessionId()))
      .member(Boolean.parseBoolean(headers.getFirst(properties.getHeader().getIsMember())));

    headers.forEach((key, value) -> {
      if (key.startsWith(properties.getHeader().getAdditionalParameterPrefix())) {
        externalSessionBuilder.additionalParameter(key, value.get(0));
      }
    });

    return externalSessionBuilder.build();
  }

  public static ExternalSession fromSleuth(TraceContext traceContext) {
    ExternalSession.ExternalSessionBuilder builder = ExternalSession.builder()
      .userId(ExtraFieldPropagation.get(traceContext, ExternalSessionSleuth.USER_ID))
      .sessionId(ExtraFieldPropagation.get(traceContext, ExternalSessionSleuth.SESSION_ID))
      .member(Boolean.parseBoolean(ExtraFieldPropagation.get(traceContext, ExternalSessionSleuth.IS_MEMBER)));

    String additionalParameters = ExtraFieldPropagation.get(traceContext, ExternalSessionSleuth.ADDITIONAL_PARAMETERS);
    if (Objects.nonNull(additionalParameters)) {
      String[] split = additionalParameters.split("\n");
      for (String pair : split) {
        String[] strings = pair.split("=");
        builder.additionalParameter(strings[0], strings[1]);
      }
    }

    return builder.build();
  }

  public static ExternalSession toSleuth(TraceContext traceContext, ExternalSession externalSession) {
    ExtraFieldPropagation.set(traceContext, ExternalSessionSleuth.USER_ID, externalSession.getUserId());
    ExtraFieldPropagation.set(traceContext, ExternalSessionSleuth.SESSION_ID, externalSession.getSessionId());
    ExtraFieldPropagation.set(traceContext, ExternalSessionSleuth.IS_MEMBER, String.valueOf(externalSession.isMember()));

    if (!externalSession.getAdditionalParameters().isEmpty()) {
      StringBuilder builder = new StringBuilder();
      externalSession.getAdditionalParameters().forEach((key, value) -> {
        builder.append(key).append("=").append(value).append("\n");
      });
      ExtraFieldPropagation.set(traceContext, ExternalSessionSleuth.ADDITIONAL_PARAMETERS, builder.toString());
    }

    return externalSession;
  }

}
