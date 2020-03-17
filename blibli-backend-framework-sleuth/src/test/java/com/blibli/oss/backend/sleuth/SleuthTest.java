package com.blibli.oss.backend.sleuth;

import brave.Span;
import brave.Tracer;
import brave.propagation.ExtraFieldPropagation;
import com.blibli.oss.backend.sleuth.fields.SleuthExtraFields;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SleuthTest.Application.class)
public class SleuthTest {

  @Autowired
  private Tracer tracer;

  @Test
  void testExists() {
    Span span = tracer.newTrace();

    ExtraFieldPropagation.set(span.context(), "Eko", "Value");
    ExtraFieldPropagation.set(span.context(), "Kurniawan", "Value");
    ExtraFieldPropagation.set(span.context(), "Khannedy", "Value");
    ExtraFieldPropagation.set(span.context(), "Hello", "Value");
    ExtraFieldPropagation.set(span.context(), "World", "Value");

    assertEquals("Value", ExtraFieldPropagation.get(span.context(), "Eko"));
    assertEquals("Value", ExtraFieldPropagation.get(span.context(), "Kurniawan"));
    assertEquals("Value", ExtraFieldPropagation.get(span.context(), "Khannedy"));
    assertEquals("Value", ExtraFieldPropagation.get(span.context(), "Hello"));
    assertEquals("Value", ExtraFieldPropagation.get(span.context(), "World"));
  }

  @Test
  void testNotExists() {
    Span span = tracer.newTrace();

    ExtraFieldPropagation.set(span.context(), "NotExists", "Value");

    assertNull(ExtraFieldPropagation.get(span.context(), "NotExists"));
  }

  @SpringBootApplication
  public static class Application {

    @Component
    public static class HelloSleuthExtraFields implements SleuthExtraFields {

      @Override
      public List<String> getFields() {
        return Collections.singletonList("Hello");
      }
    }

    @Component
    public static class WorldSleuthExtraFields implements SleuthExtraFields {

      @Override
      public List<String> getFields() {
        return Collections.singletonList("World");
      }
    }

  }
}
