package com.blibli.oss.backend.apiclient.controller;

import com.blibli.oss.backend.sleuth.fields.SleuthExtraFields;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ApiClientExtraFields implements SleuthExtraFields {

  @Override
  public List<String> getFields() {
    return Collections.singletonList("LastName");
  }
}
