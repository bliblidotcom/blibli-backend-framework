package com.blibli.oss.backend.common.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagingRequest {

  @JsonProperty("page")
  private Integer page;

  @JsonProperty("item_per_page")
  private Integer itemPerPage;

  @JsonProperty("sort_by")
  private List<SortBy> sortBy;
}
