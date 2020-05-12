package com.blibli.oss.backend.externalapi.sleuth;

import com.blibli.oss.backend.sleuth.fields.SleuthExtraFields;

import java.util.Arrays;
import java.util.List;

public class ExternalSessionExtraFields implements SleuthExtraFields {

  @Override
  public List<String> getFields() {
    return Arrays.asList(
      ExternalSessionSleuth.USER_ID,
      ExternalSessionSleuth.SESSION_ID,
      ExternalSessionSleuth.IS_MEMBER,
      ExternalSessionSleuth.ADDITIONAL_PARAMETERS
    );
  }
}
