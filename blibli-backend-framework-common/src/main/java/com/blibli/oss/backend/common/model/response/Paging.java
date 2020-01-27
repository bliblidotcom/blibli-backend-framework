package com.blibli.oss.backend.common.model.response;

import com.blibli.oss.backend.common.model.request.SortBy;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Paging {

  @JsonProperty("page")
  private Integer page;

  @JsonProperty("total_page")
  private Integer totalPage;

  @JsonProperty("item_per_page")
  private Integer itemPerPage;

  @JsonProperty("total_item")
  private Integer totalItem;

  @JsonProperty("sort_by")
  private List<SortBy> sortBy;

}
