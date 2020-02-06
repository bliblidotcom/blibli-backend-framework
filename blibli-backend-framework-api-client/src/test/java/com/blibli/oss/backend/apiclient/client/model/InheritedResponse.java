package com.blibli.oss.backend.apiclient.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderMethodName = "inheritedBuilder")
@AllArgsConstructor
@NoArgsConstructor
public class InheritedResponse extends FirstResponse {

  private String name;

  private String detail;
}
