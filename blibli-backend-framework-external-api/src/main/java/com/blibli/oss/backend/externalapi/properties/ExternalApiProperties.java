package com.blibli.oss.backend.externalapi.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("blibli.backend.external.api")
public class ExternalApiProperties {

  private Header header = new Header();

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Header {

    private String isMember = "Blibli-Is-Member";

    private String userId = "Blibli-User-Id";

    private String sessionId = "Blibli-Session-Id";

    private String additionalParameterPrefix = "Blibli-Extras-";

  }
}
