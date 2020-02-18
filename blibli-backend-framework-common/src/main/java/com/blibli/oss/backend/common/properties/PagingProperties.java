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

  private Integer defaultPage = 1;

  private Integer defaultItemPerPage = 50;

  private SortByDirection defaultSortDirection = SortByDirection.ASC;

  private Integer maxItemPerPage = Integer.MAX_VALUE;

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
