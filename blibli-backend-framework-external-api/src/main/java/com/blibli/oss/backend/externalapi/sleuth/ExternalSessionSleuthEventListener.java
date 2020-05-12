package com.blibli.oss.backend.externalapi.sleuth;

import brave.Tracer;
import com.blibli.oss.backend.externalapi.event.ExternalSessionEvent;
import com.blibli.oss.backend.externalapi.helper.ExternalSessionHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

@Slf4j
@AllArgsConstructor
public class ExternalSessionSleuthEventListener implements ApplicationListener<ExternalSessionEvent> {

  private Tracer tracer;

  @Override
  public void onApplicationEvent(ExternalSessionEvent event) {
    ExternalSessionHelper.toSleuth(tracer.currentSpan().context(), event.getExternalSession());
  }
}
