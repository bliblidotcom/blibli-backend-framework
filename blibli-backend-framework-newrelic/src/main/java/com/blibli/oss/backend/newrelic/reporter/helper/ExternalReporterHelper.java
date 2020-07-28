package com.blibli.oss.backend.newrelic.reporter.helper;

import com.blibli.oss.backend.newrelic.aspect.service.util.SegmentType;
import com.blibli.oss.backend.newrelic.reporter.ExternalReporter;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExternalReporterHelper {

  public static Map<SegmentType, List<ExternalReporter>> getExternalReporters(ApplicationContext applicationContext) {
    Map<String, ExternalReporter> beans = applicationContext.getBeansOfType(ExternalReporter.class);
    return beans.values()
      .stream()
      .collect(
        Collectors.groupingBy(ExternalReporter::getSegmentType)
      );
  }

}
