package com.blibli.oss.backend.newrelic.aspect;

import com.blibli.oss.backend.command.Command;
import com.blibli.oss.backend.newrelic.aspect.service.AspectModifyService;
import com.blibli.oss.backend.newrelic.aspect.service.util.SegmentType;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Aspect to target Blibli Command plugin for timing with New Relic Segment.
 */
@Aspect
@Configuration
@ConditionalOnClass(Command.class)
@ConditionalOnProperty(
    prefix = "blibli.newrelic.command",
    name = "enabled",
    matchIfMissing = true
)
@AllArgsConstructor
public class CommandAspect {

  private AspectModifyService aspectModifyService;

  @Pointcut("execution(* com.blibli.oss.backend.command.Command.execute(..))")
  private void commandExecute() {}

  @Around(value = "commandExecute()")
  public Object afterCommandExecute(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    return aspectModifyService.modifyRetValWithTiming(
        proceedingJoinPoint, SegmentType.COMMAND
    );
  }

}
