package com.blibli.oss.backend.sleuth;

import brave.Span;
import brave.Tracer;
import brave.baggage.BaggageField;
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

    BaggageField.getByName(span.context(), "Eko").updateValue(span.context(), "Value");
    BaggageField.getByName(span.context(), "Kurniawan").updateValue(span.context(), "Value");
    BaggageField.getByName(span.context(), "Khannedy").updateValue(span.context(), "Value");
    BaggageField.getByName(span.context(), "Hello").updateValue(span.context(), "Value");
    BaggageField.getByName(span.context(), "World").updateValue(span.context(), "Value");

    assertEquals("Value", BaggageField.getByName(span.context(), "Eko").getValue(span.context()));
    assertEquals("Value", BaggageField.getByName(span.context(), "Kurniawan").getValue(span.context()));
    assertEquals("Value", BaggageField.getByName(span.context(), "Khannedy").getValue(span.context()));
    assertEquals("Value", BaggageField.getByName(span.context(), "Hello").getValue(span.context()));
    assertEquals("Value", BaggageField.getByName(span.context(), "World").getValue(span.context()));
  }

  @Test
  void testNotExists() {
    Span span = tracer.newTrace();

    assertNull(BaggageField.getByName(span.context(), "NotExists"));
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
