package com.blibli.oss.backend.sleuth.baggage;

import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.baggage.BaggagePropagationCustomizer;
import com.blibli.oss.backend.sleuth.configuration.SleuthConfiguration;
import com.blibli.oss.backend.sleuth.fields.SleuthExtraFields;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class SleuthExtraFieldCustomizer implements BaggagePropagationCustomizer {

  private List<SleuthExtraFields> sleuthExtraFields;

  @Override
  public void customize(BaggagePropagation.FactoryBuilder factoryBuilder) {
    sleuthExtraFields.forEach(fields -> {
      fields.getFields().forEach(field -> {
        factoryBuilder.add(BaggagePropagationConfig.SingleBaggageField.newBuilder(BaggageField.create(field))
          .addKeyName(SleuthConfiguration.HTTP_BAGGAGE_PREFIX + field)
          .addKeyName(SleuthConfiguration.MESSAGING_BAGGAGE_PREFIX + field)
          .build());
      });
    });
  }
}
