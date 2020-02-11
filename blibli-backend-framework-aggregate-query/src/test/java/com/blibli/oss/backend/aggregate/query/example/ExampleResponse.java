package com.blibli.oss.backend.aggregate.query.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExampleResponse {

  private Integer id;

  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("middle_name")
  private String middleName;

  @JsonProperty("last_name")
  private String lastName;
}
