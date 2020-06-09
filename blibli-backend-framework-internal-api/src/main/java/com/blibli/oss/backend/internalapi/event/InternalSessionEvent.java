package com.blibli.oss.backend.internalapi.event;

import com.blibli.oss.backend.internalapi.model.InternalSession;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class InternalSessionEvent extends ApplicationEvent {

  @Getter
  private InternalSession internalSession;

  public InternalSessionEvent(InternalSession internalSession) {
    super(internalSession);
    this.internalSession = internalSession;
  }
}
