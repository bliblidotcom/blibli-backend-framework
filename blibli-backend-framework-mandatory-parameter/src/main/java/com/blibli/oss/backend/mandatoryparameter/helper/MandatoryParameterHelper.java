package com.blibli.oss.backend.mandatoryparameter.helper;

import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.sleuth.MandatoryParameterSleuth;

public class MandatoryParameterHelper {

  public static MandatoryParameter fromSleuth(TraceContext traceContext) {
    return MandatoryParameter.builder()
      .storeId(ExtraFieldPropagation.get(traceContext, MandatoryParameterSleuth.STORE_ID))
      .clientId(ExtraFieldPropagation.get(traceContext, MandatoryParameterSleuth.CLIENT_ID))
      .channelId(ExtraFieldPropagation.get(traceContext, MandatoryParameterSleuth.CHANNEL_ID))
      .requestId(ExtraFieldPropagation.get(traceContext, MandatoryParameterSleuth.REQUEST_ID))
      .username(ExtraFieldPropagation.get(traceContext, MandatoryParameterSleuth.USERNAME))
      .build();
  }

}
