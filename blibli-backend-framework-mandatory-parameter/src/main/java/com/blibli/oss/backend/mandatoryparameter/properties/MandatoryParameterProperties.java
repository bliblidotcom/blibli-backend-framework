package com.blibli.oss.backend.mandatoryparameter.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("blibli.backend.mandatoryparameter")
public class MandatoryParameterProperties {

  private QueryKey queryKey = new QueryKey();

  private HeaderKey headerKey = new HeaderKey();

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class QueryKey {

    private String storeId = "storeId";

    private String clientId = "clientId";

    private String channelId = "channelId";

    private String username = "username";

    private String requestId = "requestId";

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class HeaderKey {

    private String storeId = "storeId";

    private String clientId = "clientId";

    private String channelId = "channelId";

    private String username = "username";

    private String requestId = "requestId";

  }

}
