package com.blibli.oss.backend.mandatoryparameter.swagger.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("blibli.backend.mandatoryparameter.swagger")
public class MandatoryParameterSwaggerProperties {

  private QueryKey queryKey = new QueryKey();

  private HeaderKey headerKey = new HeaderKey();

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class QueryKey {

    private String storeId = "storeId";

    private String storeIdDefaultValue = "storeId";

    private String clientId = "clientId";

    private String clientIdDefaultValue = "clientId";

    private String channelId = "channelId";

    private String channelIdDefaultValue = "channelId";

    private String username = "username";

    private String usernameDefaultValue = "username";

    private String requestId = "requestId";

    private String requestIdDefaultValue = "requestId";

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class HeaderKey {

    private String storeId = "storeId";

    private String storeIdDefaultValue = "storeId";

    private String clientId = "clientId";

    private String clientIdDefaultValue = "clientId";

    private String channelId = "channelId";

    private String channelIdDefaultValue = "channelId";

    private String username = "username";

    private String usernameDefaultValue = "username";

    private String requestId = "requestId";

    private String requestIdDefaultValue = "requestId";

  }

}
