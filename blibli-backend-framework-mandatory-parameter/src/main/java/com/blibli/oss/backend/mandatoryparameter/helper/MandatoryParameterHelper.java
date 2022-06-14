package com.blibli.oss.backend.mandatoryparameter.helper;

import brave.baggage.BaggageField;
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
      .storeId(BaggageField.getByName(traceContext, MandatoryParameterSleuth.STORE_ID).getValue(traceContext))
      .clientId(BaggageField.getByName(traceContext, MandatoryParameterSleuth.CLIENT_ID).getValue(traceContext))
      .channelId(BaggageField.getByName(traceContext, MandatoryParameterSleuth.CHANNEL_ID).getValue(traceContext))
      .requestId(BaggageField.getByName(traceContext, MandatoryParameterSleuth.REQUEST_ID).getValue(traceContext))
      .username(BaggageField.getByName(traceContext, MandatoryParameterSleuth.USERNAME).getValue(traceContext))
      .build();
  }

}
