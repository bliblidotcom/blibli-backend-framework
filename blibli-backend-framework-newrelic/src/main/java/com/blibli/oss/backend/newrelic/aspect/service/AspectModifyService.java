package com.blibli.oss.backend.newrelic.aspect.service;

import com.blibli.oss.backend.newrelic.aspect.service.util.SegmentType;
import org.aspectj.lang.ProceedingJoinPoint;

public interface AspectModifyService {

  Object modifyRetValWithTiming(
    ProceedingJoinPoint proceedingJoinPoint, SegmentType segmentType
  ) throws Throwable;

}
