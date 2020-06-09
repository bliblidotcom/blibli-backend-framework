package com.blibli.oss.backend.internalapi.sleuth;

import com.blibli.oss.backend.sleuth.fields.SleuthExtraFields;

import java.util.Arrays;
import java.util.List;

public class InternalSessionExtraFields implements SleuthExtraFields {

  @Override
  public List<String> getFields() {
    return Arrays.asList(
      InternalSessionSleuth.USER_ID,
      InternalSessionSleuth.USER_NAME,
      InternalSessionSleuth.ROLES
    );
  }
}
