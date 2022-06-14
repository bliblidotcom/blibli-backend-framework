package com.blibli.oss.backend.kafka.listener.aspectj;

import com.blibli.oss.backend.kafka.interceptor.InterceptorUtil;
import com.blibli.oss.backend.kafka.interceptor.KafkaConsumerInterceptor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Aspect
public class KafkaListenerAspect implements ApplicationContextAware, InitializingBean {

  private List<KafkaConsumerInterceptor> kafkaConsumerInterceptors;

  @Setter
  private ApplicationContext applicationContext;


  @Around(value = "@annotation(org.springframework.kafka.annotation.KafkaListener)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    if (isConsumerRecordArgument(joinPoint) && joinPoint.getSignature() instanceof MethodSignature) {

      ConsumerRecord<String, String> record = getConsumerRecord(joinPoint);

      Object bean = joinPoint.getTarget();
      Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

      try {
        if (InterceptorUtil.fireBeforeConsume(bean, method, kafkaConsumerInterceptors, record)) {
          return null; // cancel process
        } else {
          Object response = joinPoint.proceed(joinPoint.getArgs());
          InterceptorUtil.fireAfterSuccessConsume(bean, method, kafkaConsumerInterceptors, record);
          return response;
        }
      } catch (Throwable throwable) {
        InterceptorUtil.fireAfterErrorConsume(bean, method, kafkaConsumerInterceptors, record, throwable);
        throw throwable;
      }
    } else {
      return joinPoint.proceed(joinPoint.getArgs());
    }
  }

  private boolean isConsumerRecordArgument(ProceedingJoinPoint joinPoint) {
    Object[] arguments = joinPoint.getArgs();
    if (arguments == null || arguments.length == 0) {
      return false;
    }

    for (Object o : joinPoint.getArgs()) {
      if (o instanceof ConsumerRecord) {
        return true;
      }
    }

    return false;
  }

  @SuppressWarnings("unchecked")
  private ConsumerRecord<String, String> getConsumerRecord(ProceedingJoinPoint joinPoint) {
    for (Object arg : joinPoint.getArgs()) {
      if (arg instanceof ConsumerRecord) {
        return (ConsumerRecord<String, String>) arg;
      }
    }
    return null;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    this.kafkaConsumerInterceptors = getKafkaConsumerInterceptors();
  }

  public List<KafkaConsumerInterceptor> getKafkaConsumerInterceptors() {
    List<KafkaConsumerInterceptor> interceptors = Collections.emptyList();

    Map<String, KafkaConsumerInterceptor> beans = applicationContext.getBeansOfType(KafkaConsumerInterceptor.class);
    if (!beans.isEmpty()) {
      interceptors = new ArrayList<>(beans.values());
    }

    OrderComparator.sort(interceptors);
    return interceptors;
  }
}
