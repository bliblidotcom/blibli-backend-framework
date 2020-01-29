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

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Eko Kurniawan Khannedy
 */
public class SleuthHelperTest {

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private Tracer tracer;

  @Captor
  private ArgumentCaptor<Span> spanArgumentCaptor;

  @After
  public void tearDown() throws Exception {
    verifyNoMoreInteractions(tracer);
  }

  @Test
  public void testContinueSpan() throws Exception {

    SleuthHelper.continueSpan(null, null);
    SleuthHelper.continueSpan(null, Collections.emptyMap());
    SleuthHelper.continueSpan(tracer, null);

    verifyNoMoreInteractions(tracer);

    Map<String, String> map = new HashMap<>();

    map.put(Span.SPAN_NAME_NAME, "blibli");
    map.put(Span.PARENT_ID_NAME, "1,2");
    map.put(Span.TRACE_ID_NAME, "1");
    map.put(Span.PROCESS_ID_NAME, "process");
    map.put(Span.SPAN_ID_NAME, "1");
    map.put(Span.SPAN_BAGGAGE_HEADER_PREFIX + "1", "1");
    map.put(Span.SPAN_BAGGAGE_HEADER_PREFIX + "2", "2");

    SleuthHelper.continueSpan(tracer, map);

    verify(tracer).continueSpan(spanArgumentCaptor.capture());

    Span span = spanArgumentCaptor.getValue();
    assertEquals(map.get(Span.SPAN_NAME_NAME), span.getName());
    assertEquals(map.get(Span.TRACE_ID_NAME), String.valueOf(span.getTraceId()));
    assertEquals(map.get(Span.PROCESS_ID_NAME), span.getProcessId());
    assertEquals(map.get(Span.SPAN_ID_NAME), String.valueOf(span.getSpanId()));
    assertEquals(map.get(Span.SPAN_BAGGAGE_HEADER_PREFIX + "1"), span.getBaggageItem("1"));
    assertEquals(map.get(Span.SPAN_BAGGAGE_HEADER_PREFIX + "2"), span.getBaggageItem("2"));
  }

  @Test
  public void testJoinSpan() throws Exception {

    SleuthHelper.joinSpan(null, null);
    SleuthHelper.joinSpan(null, Collections.emptyMap());
    SleuthHelper.joinSpan(tracer, null);

    verifyNoMoreInteractions(tracer);

    Map<String, String> map = new HashMap<>();

    map.put(Span.SPAN_NAME_NAME, "blibli");
    map.put(Span.PARENT_ID_NAME, "1,2");
    map.put(Span.TRACE_ID_NAME, "1");
    map.put(Span.PROCESS_ID_NAME, "process");
    map.put(Span.SPAN_ID_NAME, "1");
    map.put(Span.SPAN_BAGGAGE_HEADER_PREFIX + "1", "1");
    map.put(Span.SPAN_BAGGAGE_HEADER_PREFIX + "2", "2");

    SleuthHelper.joinSpan(tracer, map);

    verify(tracer).createSpan(eq("blibli"), spanArgumentCaptor.capture());

    Span span = spanArgumentCaptor.getValue();
    assertEquals(map.get(Span.SPAN_NAME_NAME), span.getName());
    assertEquals(map.get(Span.TRACE_ID_NAME), String.valueOf(span.getTraceId()));
    assertEquals(map.get(Span.PROCESS_ID_NAME), span.getProcessId());
    assertEquals(map.get(Span.SPAN_ID_NAME), String.valueOf(span.getSpanId()));
    assertEquals(map.get(Span.SPAN_BAGGAGE_HEADER_PREFIX + "1"), span.getBaggageItem("1"));
    assertEquals(map.get(Span.SPAN_BAGGAGE_HEADER_PREFIX + "2"), span.getBaggageItem("2"));
  }

  @Test
  public void testNullSpan() throws Exception {
    assertEquals(Collections.EMPTY_MAP, SleuthHelper.toMap(null));
  }

  @Test
  public void testFromMap() throws Exception {
    Map<String, String> map = new HashMap<String, String>();

    map.put(Span.SPAN_NAME_NAME, "blibli");
    map.put(Span.PARENT_ID_NAME, "1,2");
    map.put(Span.TRACE_ID_NAME, "1");
    map.put(Span.PROCESS_ID_NAME, "process");
    map.put(Span.SPAN_ID_NAME, "1");
    map.put(Span.SPAN_BAGGAGE_HEADER_PREFIX + "1", "1");
    map.put(Span.SPAN_BAGGAGE_HEADER_PREFIX + "2", "2");

    Span span = SleuthHelper.fromMap(map);

    assertEquals("blibli", span.getName());
    assertEquals(Long.valueOf(1L), span.getParents().get(0));
    assertEquals(Long.valueOf(2L), span.getParents().get(1));
    assertEquals(1L, span.getTraceId());
    assertEquals(1L, span.getSpanId());
    assertEquals("process", span.getProcessId());
    assertEquals("1", span.getBaggageItem("1"));
    assertEquals("2", span.getBaggageItem("2"));
  }

  @Test
  public void testToMap() throws Exception {
    Span span = Span.builder()
        .name("blibli")
        .parent(1L)
        .parent(2L)
        .traceId(1L)
        .processId("process")
        .spanId(1L)
        .baggage("1", "1")
        .baggage("2", "2")
        .build();

    Map<String, String> map = SleuthHelper.toMap(span);

    assertEquals("blibli", map.get(Span.SPAN_NAME_NAME));
    assertEquals("1,2", map.get(Span.PARENT_ID_NAME));
    assertEquals("1", map.get(Span.TRACE_ID_NAME));
    assertEquals("1", map.get(Span.SPAN_ID_NAME));
    assertEquals("process", map.get(Span.PROCESS_ID_NAME));
    assertEquals("1", map.get(Span.SPAN_BAGGAGE_HEADER_PREFIX + "1"));
    assertEquals("2", map.get(Span.SPAN_BAGGAGE_HEADER_PREFIX + "2"));
  }

}