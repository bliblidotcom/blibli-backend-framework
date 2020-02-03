package com.blibli.oss.backend.kafka.interceptor;

import com.blibli.oss.backend.kafka.model.ProducerEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class InterceptorUtil {

  public static ProducerEvent intercepts(ProducerEvent producerEvent, List<KafkaProducerInterceptor> producerInterceptors) {
    ProducerEvent result = producerEvent;
    for (KafkaProducerInterceptor interceptor : producerInterceptors) {
      result = interceptor.beforeSend(result);
    }
    return result;
  }

  public static List<KafkaProducerInterceptor> getProducerInterceptors(ApplicationContext applicationContext) {
    Collection<KafkaProducerInterceptor> values = applicationContext.getBeansOfType(KafkaProducerInterceptor.class).values();
    List<KafkaProducerInterceptor> interceptors = new ArrayList<>(values);
    OrderComparator.sort(interceptors);
    return interceptors;
  }

}
