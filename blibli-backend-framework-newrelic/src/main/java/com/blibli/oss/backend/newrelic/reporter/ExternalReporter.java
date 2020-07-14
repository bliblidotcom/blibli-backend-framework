package com.blibli.oss.backend.newrelic.reporter;

import com.blibli.oss.backend.newrelic.aspect.service.util.SegmentType;
import com.newrelic.api.agent.Segment;
import org.aspectj.lang.JoinPoint;

public interface ExternalReporter {

  SegmentType getSegmentType();

  void report(Segment segment, JoinPoint jp);

}
