package com.blibli.oss.backend.sleuth.configuration;

import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldCustomizer;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.Propagation;
import com.blibli.oss.backend.sleuth.fields.SleuthExtraFieldConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.autoconfig.SleuthProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SleuthConfiguration {

  @Autowired(required = false)
  ExtraFieldPropagation.FactoryBuilder extraFieldPropagationFactoryBuilder;

  @Autowired(required = false)
  List<ExtraFieldCustomizer> extraFieldCustomizers = new ArrayList<>();

  @Bean
  Propagation.Factory sleuthPropagation(SleuthExtraFieldConfiguration extraFieldConfiguration, SleuthProperties sleuthProperties) {
    if (sleuthProperties.getBaggageKeys().isEmpty()
      && sleuthProperties.getPropagationKeys().isEmpty()
      && extraFieldCustomizers.isEmpty()
      && this.extraFieldPropagationFactoryBuilder == null
      && sleuthProperties.getLocalKeys().isEmpty()) {
      return B3Propagation.FACTORY;
    }
    ExtraFieldPropagation.FactoryBuilder factoryBuilder;
    if (this.extraFieldPropagationFactoryBuilder != null) {
      factoryBuilder = this.extraFieldPropagationFactoryBuilder;
    } else {
      factoryBuilder = ExtraFieldPropagation
        .newFactoryBuilder(B3Propagation.FACTORY);
    }
    List<String> baggageKeys = extraFieldConfiguration.getExtraFields(sleuthProperties.getBaggageKeys());
    if (!baggageKeys.isEmpty()) {
      factoryBuilder
        // for HTTP
        .addPrefixedFields("baggage-", baggageKeys)
        // for messaging
        .addPrefixedFields("baggage_", baggageKeys);
    }
    if (!sleuthProperties.getPropagationKeys().isEmpty()) {
      for (String key : sleuthProperties.getPropagationKeys()) {
        factoryBuilder.addField(key);
      }
    }
    if (!sleuthProperties.getLocalKeys().isEmpty()) {
      for (String key : sleuthProperties.getLocalKeys()) {
        factoryBuilder.addRedactedField(key);
      }
    }
    for (ExtraFieldCustomizer customizer : this.extraFieldCustomizers) {
      customizer.customize(factoryBuilder);
    }
    return factoryBuilder.build();
  }

  @Bean
  public SleuthExtraFieldConfiguration sleuthExtraFieldConfiguration() {
    return new SleuthExtraFieldConfiguration();
  }

}
