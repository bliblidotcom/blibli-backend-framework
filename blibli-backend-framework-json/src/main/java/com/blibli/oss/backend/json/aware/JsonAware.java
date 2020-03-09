package com.blibli.oss.backend.json.aware;

import com.blibli.oss.backend.json.helper.JsonHelper;
import org.springframework.beans.factory.Aware;

public interface JsonAware extends Aware {

  void setJsonHelper(JsonHelper jsonHelper);

}
