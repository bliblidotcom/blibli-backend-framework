package com.blibli.oss.backend.common.properties;

import com.blibli.oss.backend.common.model.request.SortByDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("blibli.backend.common.paging")
public class PagingProperties {

  private Long defaultPage = 1L;

  private Long defaultItemPerPage = 50L;

  private SortByDirection defaultSortDirection = SortByDirection.ASC;

  private Long maxItemPerPage = 1000L;

  private Query query = new Query();

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Query {

    private String pageKey = "page";

    private String itemPerPageKey = "item_per_page";

    private String sortByKey = "sort_by";

  }

}
