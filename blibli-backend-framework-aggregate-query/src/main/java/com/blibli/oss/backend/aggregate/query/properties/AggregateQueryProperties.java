package com.blibli.oss.backend.aggregate.query.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("blibli.backend.aggregate.query")
public class AggregateQueryProperties {

  private String serviceId;

}
