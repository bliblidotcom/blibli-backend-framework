package com.blibli.oss.backend.mandatoryparameter.sleuth;

import com.blibli.oss.backend.sleuth.fields.SleuthExtraFields;

import java.util.Arrays;
import java.util.List;

public class MandatoryParameterSleuthExtraFields implements SleuthExtraFields {

  @Override
  public List<String> getFields() {
    return Arrays.asList(
      MandatoryParamSleuth.CHANNEL_ID,
      MandatoryParamSleuth.CLIENT_ID,
      MandatoryParamSleuth.REQUEST_ID,
      MandatoryParamSleuth.STORE_ID,
      MandatoryParamSleuth.USERNAME
    );
  }
}
