package com.blibli.oss.backend.mandatoryparameter.sleuth;

import com.blibli.oss.backend.sleuth.fields.SleuthExtraFields;

import java.util.Arrays;
import java.util.List;

public class MandatoryParameterSleuthExtraFields implements SleuthExtraFields {

  @Override
  public List<String> getFields() {
    return Arrays.asList(
      MandatoryParameterSleuth.CHANNEL_ID,
      MandatoryParameterSleuth.CLIENT_ID,
      MandatoryParameterSleuth.REQUEST_ID,
      MandatoryParameterSleuth.STORE_ID,
      MandatoryParameterSleuth.USERNAME
    );
  }
}
