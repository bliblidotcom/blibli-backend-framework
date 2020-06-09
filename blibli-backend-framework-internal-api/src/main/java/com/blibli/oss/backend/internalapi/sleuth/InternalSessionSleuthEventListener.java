package com.blibli.oss.backend.internalapi.sleuth;

import brave.Tracer;
import com.blibli.oss.backend.internalapi.event.InternalSessionEvent;
import com.blibli.oss.backend.internalapi.helper.InternalSessionHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

@Slf4j
@AllArgsConstructor
public class InternalSessionSleuthEventListener implements ApplicationListener<InternalSessionEvent> {

  private Tracer tracer;

  @Override
  public void onApplicationEvent(InternalSessionEvent event) {
    InternalSessionHelper.toSleuth(tracer.currentSpan().context(), event.getInternalSession());
  }
}
