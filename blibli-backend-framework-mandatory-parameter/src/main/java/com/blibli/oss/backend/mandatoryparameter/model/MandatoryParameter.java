package com.blibli.oss.backend.mandatoryparameter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MandatoryParameter {

  private String storeId;

  private String clientId;

  private String channelId;

  private String username;

  private String requestId;
}
