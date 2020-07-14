package com.blibli.oss.backend.newrelic.newrelic.aspect.service.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SegmentType {
  COMMAND("Command %s.%s", RetValType.MONO),
  REACTIVE_MONGODB("ReactiveMongoRepository %s.%s", RetValType.MONO_OR_FLUX);

  @Getter
  private String stringFormat;

  @Getter
  private RetValType returnValueType;
}
