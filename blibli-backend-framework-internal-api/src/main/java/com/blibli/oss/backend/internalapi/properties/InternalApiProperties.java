package com.blibli.oss.backend.internalapi.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("blibli.backend.internal.api")
public class InternalApiProperties {

  private Header header = new Header();

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Header {

    private String userId = "Baggage-Blibli-Internal-User-Id";

    private String userName = "Baggage-Blibli-Internal-User-Name";

    private String roles = "Baggage-Blibli-Internal-User-Roles";
  }

}
