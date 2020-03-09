package com.blibli.oss.backend.json.processor;

import com.blibli.oss.backend.json.aware.JsonAware;
import com.blibli.oss.backend.json.helper.JsonHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class JsonAwareBeanPostProcessor implements BeanPostProcessor {

  private JsonHelper jsonHelper;

  public JsonAwareBeanPostProcessor(JsonHelper jsonHelper) {
    this.jsonHelper = jsonHelper;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof JsonAware) {
      JsonAware jsonAware = (JsonAware) bean;
      jsonAware.setJsonHelper(jsonHelper);
    }

    return bean;
  }
}
