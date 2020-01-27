package com.blibli.oss.backend.common.model.request;

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

  private Integer page;

  private Integer itemPerPage;

  private List<SortBy> sortBy;
}
