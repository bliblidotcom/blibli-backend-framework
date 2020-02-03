package com.blibli.oss.backend.kafka.interceptor;

import com.blibli.oss.backend.kafka.model.ProducerEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.OrderComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class InterceptorUtil {

  public static ProducerEvent fireBeforeSend(ProducerEvent producerEvent, List<KafkaProducerInterceptor> producerInterceptors) {
    ProducerEvent result = producerEvent;
    for (KafkaProducerInterceptor interceptor : producerInterceptors) {
      result = interceptor.beforeSend(result);
    }
    return result;
  }

  public static boolean fireBeforeConsume(ConsumerRecord<String, String> producerEvent, List<KafkaConsumerInterceptor> producerInterceptors) {
    for (KafkaConsumerInterceptor interceptor : producerInterceptors) {
      if (interceptor.beforeConsume(producerEvent)) {
        return true;
      }
    }
    return false;
  }

  public static void fireAfterSuccessConsume(ConsumerRecord<String, String> producerEvent, List<KafkaConsumerInterceptor> producerInterceptors) {
    for (KafkaConsumerInterceptor interceptor : producerInterceptors) {
      interceptor.afterSuccessConsume(producerEvent);
    }
  }

  public static void fireAfterErrorConsume(ConsumerRecord<String, String> producerEvent, Throwable throwable, List<KafkaConsumerInterceptor> producerInterceptors) {
    for (KafkaConsumerInterceptor interceptor : producerInterceptors) {
      interceptor.afterFailedConsume(producerEvent, throwable);
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
