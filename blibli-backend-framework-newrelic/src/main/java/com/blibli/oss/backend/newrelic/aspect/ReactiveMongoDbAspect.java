package com.blibli.oss.backend.newrelic.aspect;

import com.blibli.oss.backend.newrelic.aspect.service.AspectModifyService;
import com.blibli.oss.backend.newrelic.aspect.service.util.SegmentType;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * Aspect to target Spring Reactive MongoDB for timing with New Relic Segment.
 */
@Aspect
@Configuration
@ConditionalOnClass({
    ReactiveMongoRepository.class,
    ReactiveMongoTemplate.class,
    MongoProperties.class
})
@ConditionalOnProperty(
    prefix = "blibli.newrelic.reactive-mongodb",
    name = "enabled",
    matchIfMissing = true
)
@AllArgsConstructor
public class ReactiveMongoDbAspect {

  private AspectModifyService aspectModifyService;

  @Pointcut("execution(public * *.*(..)) && within(org.springframework.data.mongodb.repository.ReactiveMongoRepository+)")
  void mongoRepositoryInterface() {}

  @Around(value = "mongoRepositoryInterface()")
  public Object afterCommandExecute(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    return aspectModifyService.modifyRetValWithTiming(
        proceedingJoinPoint, SegmentType.REACTIVE_MONGODB
    );
  }
}
