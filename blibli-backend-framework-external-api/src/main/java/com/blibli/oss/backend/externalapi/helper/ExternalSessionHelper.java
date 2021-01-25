package com.blibli.oss.backend.externalapi.helper;

import brave.baggage.BaggageField;
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
      .userId(BaggageField.getByName(traceContext, ExternalSessionSleuth.USER_ID).getValue(traceContext))
      .sessionId(BaggageField.getByName(traceContext, ExternalSessionSleuth.SESSION_ID).getValue(traceContext))
      .member(Boolean.parseBoolean(BaggageField.getByName(traceContext, ExternalSessionSleuth.IS_MEMBER).getValue(traceContext)));

    String additionalParameters = BaggageField.getByName(traceContext, ExternalSessionSleuth.ADDITIONAL_PARAMETERS).getValue(traceContext);
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
    BaggageField.getByName(traceContext, ExternalSessionSleuth.USER_ID).updateValue(traceContext, externalSession.getUserId());
    BaggageField.getByName(traceContext, ExternalSessionSleuth.SESSION_ID).updateValue(traceContext, externalSession.getSessionId());
    BaggageField.getByName(traceContext, ExternalSessionSleuth.IS_MEMBER).updateValue(traceContext, String.valueOf(externalSession.isMember()));

    if (!externalSession.getAdditionalParameters().isEmpty()) {
      StringBuilder builder = new StringBuilder();
      externalSession.getAdditionalParameters().forEach((key, value) -> {
        builder.append(key).append("=").append(value).append("\n");
      });
      BaggageField.getByName(traceContext, ExternalSessionSleuth.ADDITIONAL_PARAMETERS).updateValue(traceContext, builder.toString());
    }

    return externalSession;
  }

}
