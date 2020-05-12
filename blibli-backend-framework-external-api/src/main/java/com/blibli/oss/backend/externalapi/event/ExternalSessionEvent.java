package com.blibli.oss.backend.externalapi.event;

import com.blibli.oss.backend.externalapi.model.ExternalSession;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ExternalSessionEvent extends ApplicationEvent {

  @Getter
  private ExternalSession externalSession;

  public ExternalSessionEvent(ExternalSession externalSession) {
    super(externalSession);
    this.externalSession = externalSession;
  }
}
