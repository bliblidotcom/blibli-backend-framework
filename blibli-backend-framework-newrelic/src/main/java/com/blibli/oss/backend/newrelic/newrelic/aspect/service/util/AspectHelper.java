package com.blibli.oss.backend.newrelic.newrelic.aspect.service.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@UtilityClass
public class AspectHelper {

  public static String constructSegmentName(JoinPoint joinPoint, SegmentType segmentType) {
    return String.format(
        segmentType.getStringFormat(),
        joinPoint.getTarget().getClass().getSimpleName(),
        joinPoint.getSignature().toShortString()
    );
  }

  public static boolean retValIsType(Object retVal, RetValType type) {
    switch (type) {
      case FLUX:
        return retVal instanceof Flux;
      case MONO:
        return retVal instanceof Mono;
      case MONO_OR_FLUX:
        return retVal instanceof Flux || retVal instanceof Mono;
      default:
        throw new IllegalArgumentException("RetValType is unknown");
    }
  }

}
