package com.blibli.oss.backend.common.helper;

import com.blibli.oss.backend.common.model.request.PagingRequest;
import com.blibli.oss.backend.common.model.response.Paging;

public class PagingHelper {

  public static Paging toPaging(PagingRequest request, Long totalItem) {
    Long totalPage = totalItem / request.getItemPerPage();
    if (totalItem % request.getItemPerPage() != 0) {
      totalPage++;
    }
    return toPaging(request, totalPage, totalItem);
  }

  public static Paging toPaging(PagingRequest request, Long totalPage, Long totalItem) {
    return Paging.builder()
      .page(request.getPage())
      .totalItem(totalItem)
      .totalPage(totalPage)
      .itemPerPage(request.getItemPerPage())
      .sortBy(request.getSortBy())
      .build();
  }

}
