/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blibli.oss.backend.kafka.sleuth;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Eko Kurniawan Khannedy
 */
public class SleuthHelper {

  public static void continueSpan(Tracer tracer, Map<String, String> map) {
    if (tracer != null && map != null && !map.isEmpty()) {
      tracer.continueSpan(fromMap(map));
    }
  }

  public static void joinSpan(Tracer tracer, Map<String, String> map) {
    if (tracer != null && map != null && !map.isEmpty()) {
      Span parent = fromMap(map);
      if (parent != null) {
        tracer.createSpan(parent.getName(), parent);
      }
    }
  }

  public static Map<String, String> toMap(Span span) {
    if (span == null) {
      return Collections.emptyMap();
    } else {
      return convertSpanToMap(span);
    }
  }

  private static Map<String, String> convertSpanToMap(Span span) {
    Map<String, String> map = new HashMap<>();

    List<String> parents = span.getParents().stream()
        .map(String::valueOf)
        .collect(Collectors.toList());

    map.put(Span.SPAN_NAME_NAME, span.getName());
    map.put(Span.PARENT_ID_NAME, String.join(",", parents));
    map.put(Span.TRACE_ID_NAME, String.valueOf(span.getTraceId()));
    map.put(Span.PROCESS_ID_NAME, span.getProcessId());
    map.put(Span.SPAN_ID_NAME, String.valueOf(span.getSpanId()));
    map.put(Span.SPAN_EXPORT_NAME, String.valueOf(span.isExportable()));

    span.getBaggage().forEach((key, value) ->
        map.put(Span.SPAN_BAGGAGE_HEADER_PREFIX + key, value)
    );

    return map;
  }

  public static Span fromMap(Map<String, String> map) {
    return Span.builder()
        .name(map.get(Span.SPAN_NAME_NAME))
        .parents(getParents(map.get(Span.PARENT_ID_NAME)))
        .traceId(Long.valueOf(map.get(Span.TRACE_ID_NAME)))
        .processId(map.get(Span.PROCESS_ID_NAME))
        .spanId(Long.valueOf(map.get(Span.SPAN_ID_NAME)))
        .exportable(Boolean.valueOf(map.get(Span.SPAN_EXPORT_NAME)))
        .baggage(getBaggage(map))
        .build();
  }

  private static Map<String, String> getBaggage(Map<String, String> map) {
    List<Map.Entry<String, String>> collect = map.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(Span.SPAN_BAGGAGE_HEADER_PREFIX))
        .collect(Collectors.toList());

    Map<String, String> baggage = new HashMap<>(collect.size());
    collect.forEach(entry -> {
      String key = entry.getKey().replaceFirst(Span.SPAN_BAGGAGE_HEADER_PREFIX, "");
      baggage.put(key, entry.getValue());
    });
    return baggage;
  }

  private static List<Long> getParents(String parents) {
    if (parents == null || parents.trim().isEmpty()) {
      return Collections.emptyList();
    } else {
      return Arrays.stream(parents.split(","))
          .map(Long::parseLong)
          .collect(Collectors.toList());
    }
  }

}

