package com.blibli.oss.backend.sleuth.configuration;

import com.blibli.oss.backend.sleuth.baggage.SleuthExtraFieldCustomizer;
import com.blibli.oss.backend.sleuth.fields.SleuthExtraFields;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
public class SleuthConfiguration {

  public static final String HTTP_BAGGAGE_PREFIX = "baggage-";
  public static final String MESSAGING_BAGGAGE_PREFIX = "baggage_";

  @Bean
  public SleuthExtraFieldCustomizer sleuthExtraFieldCustomizer(ObjectProvider<SleuthExtraFields> sleuthExtraFields) {
    return new SleuthExtraFieldCustomizer(sleuthExtraFields.stream().collect(Collectors.toList()));
  }

}
