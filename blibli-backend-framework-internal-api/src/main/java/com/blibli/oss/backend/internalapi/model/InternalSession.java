package com.blibli.oss.backend.internalapi.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternalSession {

  private String userId;

  private String userName;

  @Singular
  private List<String> roles = new ArrayList<>();

}
