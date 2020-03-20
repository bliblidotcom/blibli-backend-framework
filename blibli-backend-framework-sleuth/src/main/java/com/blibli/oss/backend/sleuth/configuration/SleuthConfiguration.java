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

  public static final String HTTP_BAGGAGE_PREFIX = "baggage-";
  public static final String MESSAGING_BAGGAGE_PREFIX = "baggage_";

  @Autowired(required = false)
  private ExtraFieldPropagation.FactoryBuilder extraFieldPropagationFactoryBuilder;

  @Autowired(required = false)
  private List<ExtraFieldCustomizer> extraFieldCustomizers = new ArrayList<>();

  /**
   * This bean is copied from TraceAutoConfiguration class,
   * with some modification to support extra fields that can integrated for http and messaging
   *
   * @param extraFieldConfiguration extra field configuration
   * @param sleuthProperties        sleuth properties
   * @return new bean
   * @see org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration
   * @see org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration#sleuthPropagation(SleuthProperties)
   */
  @Bean
  public Propagation.Factory sleuthPropagation(SleuthExtraFieldConfiguration extraFieldConfiguration, SleuthProperties sleuthProperties) {
    // modification to merge baggage from properties and from bean
    List<String> baggageKeys = extraFieldConfiguration.getExtraFields(sleuthProperties.getBaggageKeys());
    if (baggageKeys.isEmpty()
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
    if (!baggageKeys.isEmpty()) {
      factoryBuilder
        // for HTTP
        .addPrefixedFields(HTTP_BAGGAGE_PREFIX, baggageKeys)
        // for messaging
        .addPrefixedFields(MESSAGING_BAGGAGE_PREFIX, baggageKeys);
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
