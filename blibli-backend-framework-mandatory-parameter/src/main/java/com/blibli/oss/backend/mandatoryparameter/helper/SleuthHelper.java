package com.blibli.oss.backend.mandatoryparameter.helper;

import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import org.springframework.util.StringUtils;

public class SleuthHelper {

  public static void putExtraField(TraceContext traceContext, String name, String value) {
    if (!StringUtils.isEmpty(value)) {
      ExtraFieldPropagation.set(traceContext, name, value);
    }
  }

}
