package com.blibli.oss.backend.mandatoryparameter.helper;

import brave.baggage.BaggageField;
import brave.propagation.TraceContext;
import org.springframework.util.StringUtils;

public class SleuthHelper {

  public static void putExtraField(TraceContext traceContext, String name, String value) {
    if (StringUtils.hasText(value)) {
      BaggageField.getByName(traceContext, name).updateValue(traceContext, value);
    }
  }

}
