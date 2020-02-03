package com.blibli.oss.backend.kafka.listener;

import com.blibli.oss.backend.kafka.interceptor.InterceptorUtil;
import com.blibli.oss.backend.kafka.interceptor.KafkaConsumerInterceptor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.adapter.RecordMessagingMessageListenerAdapter;
import org.springframework.kafka.support.Acknowledgment;

import java.lang.reflect.Method;
import java.util.List;

public class KafkaMessageListener extends RecordMessagingMessageListenerAdapter<String, String> {

  private final List<KafkaConsumerInterceptor> interceptors;

  public KafkaMessageListener(Object bean, Method method, KafkaListenerErrorHandler errorHandler, List<KafkaConsumerInterceptor> interceptors) {
    super(bean, method, errorHandler);
    this.interceptors = interceptors;
  }

  @Override
  public void onMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
    try {
      boolean skip = InterceptorUtil.fireBeforeConsume(record, interceptors);
      if (!skip) super.onMessage(record, acknowledgment, consumer);
      InterceptorUtil.fireAfterSuccessConsume(record, interceptors);
    } catch (Throwable throwable) {
      InterceptorUtil.fireAfterErrorConsume(record, throwable, interceptors);
      throw throwable;
    }
  }
}
