package com.blibli.oss.backend.sleuth.fields;

import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SleuthExtraFieldConfiguration implements ApplicationContextAware, InitializingBean {

  @Setter
  private ApplicationContext applicationContext;

  private Collection<SleuthExtraFields> extraFields;

  @Override
  public void afterPropertiesSet() throws Exception {
    extraFields = applicationContext.getBeansOfType(SleuthExtraFields.class).values();
  }

  public List<String> getExtraFields(List<String> otherFields) {
    List<String> fields = new ArrayList<>();
    extraFields.forEach(sleuthExtraFields -> fields.addAll(sleuthExtraFields.getFields()));
    fields.addAll(otherFields);
    return fields;
  }
}
