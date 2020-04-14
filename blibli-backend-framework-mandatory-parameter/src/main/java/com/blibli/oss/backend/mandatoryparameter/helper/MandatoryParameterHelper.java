package com.blibli.oss.backend.mandatoryparameter.helper;

import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.sleuth.MandatoryParameterSleuth;

public class MandatoryParameterHelper {

  public static MandatoryParameter toSleuth(TraceContext traceContext, MandatoryParameter mandatoryParameter) {
    SleuthHelper.putExtraField(traceContext, MandatoryParameterSleuth.STORE_ID, mandatoryParameter.getStoreId());
    SleuthHelper.putExtraField(traceContext, MandatoryParameterSleuth.CLIENT_ID, mandatoryParameter.getClientId());
    SleuthHelper.putExtraField(traceContext, MandatoryParameterSleuth.CHANNEL_ID, mandatoryParameter.getChannelId());
    SleuthHelper.putExtraField(traceContext, MandatoryParameterSleuth.REQUEST_ID, mandatoryParameter.getRequestId());
    SleuthHelper.putExtraField(traceContext, MandatoryParameterSleuth.USERNAME, mandatoryParameter.getUsername());
    return mandatoryParameter;
  }

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
