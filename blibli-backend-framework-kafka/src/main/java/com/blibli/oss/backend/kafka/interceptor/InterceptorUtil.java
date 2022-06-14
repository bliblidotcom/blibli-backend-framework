package com.blibli.oss.backend.kafka.interceptor;

import com.blibli.oss.backend.kafka.model.ProducerEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.OrderComparator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public final class InterceptorUtil {

  public static ProducerEvent fireBeforeSend(ProducerEvent producerEvent, List<KafkaProducerInterceptor> producerInterceptors) {
    ProducerEvent result = producerEvent;
    for (KafkaProducerInterceptor interceptor : producerInterceptors) {
      result = interceptor.beforeSend(result);
    }
    return result;
  }

  public static boolean fireBeforeConsume(Object bean, Method method, List<KafkaConsumerInterceptor> kafkaConsumerInterceptors, ConsumerRecord<String, String> event) {
    for (KafkaConsumerInterceptor interceptor : kafkaConsumerInterceptors) {
      try {
        if (interceptor.isSupport(bean, method)) {
          if (interceptor.beforeConsume(event)) {
            return true;
          }
        }
      } catch (Throwable throwable) {
        log.error("Error while invoke interceptor", throwable);
      }
    }
    return false;
  }

  public static void fireAfterSuccessConsume(Object bean, Method method, List<KafkaConsumerInterceptor> kafkaConsumerInterceptors, ConsumerRecord<String, String> event) {
    for (KafkaConsumerInterceptor interceptor : kafkaConsumerInterceptors) {
      try {
        if (interceptor.isSupport(bean, method)) {
          interceptor.afterSuccessConsume(event);
        }
      } catch (Throwable throwable) {
        log.error("Error while invoke interceptor", throwable);
      }
    }
  }

  public static void fireAfterErrorConsume(Object bean, Method method, List<KafkaConsumerInterceptor> kafkaConsumerInterceptors, ConsumerRecord<String, String> event, Throwable throwable) {
    for (KafkaConsumerInterceptor interceptor : kafkaConsumerInterceptors) {
      try {
        if (interceptor.isSupport(bean, method)) {
          interceptor.afterFailedConsume(event, throwable);
        }
      } catch (Throwable e) {
        log.error("Error while invoke interceptor", e);
      }
    }
  }

  public static List<KafkaProducerInterceptor> getProducerInterceptors(ListableBeanFactory applicationContext) {
    Collection<KafkaProducerInterceptor> values = applicationContext.getBeansOfType(KafkaProducerInterceptor.class).values();
    List<KafkaProducerInterceptor> interceptors = new ArrayList<>(values);
    OrderComparator.sort(interceptors);
    return interceptors;
  }

  public static List<KafkaConsumerInterceptor> getConsumerInterceptors(ListableBeanFactory applicationContext) {
    Collection<KafkaConsumerInterceptor> values = applicationContext.getBeansOfType(KafkaConsumerInterceptor.class).values();
    List<KafkaConsumerInterceptor> interceptors = new ArrayList<>(values);
    OrderComparator.sort(interceptors);
    return interceptors;
  }

}
