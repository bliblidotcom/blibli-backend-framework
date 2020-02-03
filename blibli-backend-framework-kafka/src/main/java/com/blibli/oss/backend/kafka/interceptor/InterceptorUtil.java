package com.blibli.oss.backend.kafka.interceptor;

import com.blibli.oss.backend.kafka.model.ProducerEvent;

import java.util.List;

public final class InterceptorUtil {

  public static ProducerEvent intercepts(ProducerEvent producerEvent, List<KafkaProducerInterceptor> producerInterceptors) {
    ProducerEvent result = producerEvent;
    for (KafkaProducerInterceptor interceptor : producerInterceptors) {
      result = interceptor.beforeSend(result);
    }
    return result;
  }

}
