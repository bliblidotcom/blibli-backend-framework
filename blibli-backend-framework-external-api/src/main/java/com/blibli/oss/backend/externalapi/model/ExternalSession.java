package com.blibli.oss.backend.externalapi.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalSession {

  private String userId;

  private String sessionId;

  private boolean member;

  @Singular
  private Map<String, String> additionalParameters = new HashMap<>();

}
