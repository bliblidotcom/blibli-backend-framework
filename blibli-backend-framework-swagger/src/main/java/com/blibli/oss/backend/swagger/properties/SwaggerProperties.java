package com.blibli.oss.backend.swagger.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("blibli.backend.swagger")
public class SwaggerProperties {

  private String title;

  private String description;

  private String termsOfService;

  private String version;

}
