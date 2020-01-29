package com.blibli.oss.backend.kafka.interceptor;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

/**
 * @author Eko Kurniawan Khannedy
 * @since 11/02/18
 */
public class InterceptorUtilTest {

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private ApplicationContext applicationContext;

  @Mock
  private KafkaProducerInterceptor kafkaProducerInterceptor1;

  @Mock
  private KafkaProducerInterceptor kafkaProducerInterceptor2;

  @Mock
  private KafkaProducerInterceptor kafkaProducerInterceptor3;

  @Mock
  private KafkaConsumerInterceptor kafkaConsumerInterceptor1;

  @Mock
  private KafkaConsumerInterceptor kafkaConsumerInterceptor2;

  @Mock
  private KafkaConsumerInterceptor kafkaConsumerInterceptor3;

  private Map<String, KafkaProducerInterceptor> producerInterceptorMap = new HashMap<>();

  private Map<String, KafkaConsumerInterceptor> consumerInterceptorMap = new HashMap<>();

  @Test
  public void testEmptyProducer() {
    List<KafkaProducerInterceptor> interceptors = InterceptorUtil.getKafkaProducerInterceptors(applicationContext);
    assertTrue(interceptors.isEmpty());
  }

  @Test
  public void testEmptyProducerSize() {
    mockApplicationContextGetProducer();
    List<KafkaProducerInterceptor> interceptors = InterceptorUtil.getKafkaProducerInterceptors(applicationContext);
    assertTrue(interceptors.isEmpty());
  }

  private void mockApplicationContextGetProducer() {
    when(applicationContext.getBeansOfType(KafkaProducerInterceptor.class))
        .thenReturn(producerInterceptorMap);
  }

  @Test
  public void testProducer() {
    mockApplicationContextGetProducer();
    mockAllProducers();

    List<KafkaProducerInterceptor> interceptors = InterceptorUtil.getKafkaProducerInterceptors(applicationContext);

    assertSame(kafkaProducerInterceptor3, interceptors.get(0));
    assertSame(kafkaProducerInterceptor2, interceptors.get(1));
    assertSame(kafkaProducerInterceptor1, interceptors.get(2));
  }

  private void mockAllProducers() {
    when(kafkaProducerInterceptor1.getOrder()).thenReturn(3);
    when(kafkaProducerInterceptor2.getOrder()).thenReturn(2);
    when(kafkaProducerInterceptor3.getOrder()).thenReturn(1);

    producerInterceptorMap.put("1", kafkaProducerInterceptor1);
    producerInterceptorMap.put("2", kafkaProducerInterceptor2);
    producerInterceptorMap.put("3", kafkaProducerInterceptor3);
  }

  @Test
  public void testEmptyConsumer() {
    List<KafkaConsumerInterceptor> interceptors = InterceptorUtil.getKafkaConsumerInterceptors(applicationContext);
    assertTrue(interceptors.isEmpty());
  }

  @Test
  public void testEmptyConsumerSize() {
    mockApplicationContextGetConsumer();
    List<KafkaConsumerInterceptor> interceptors = InterceptorUtil.getKafkaConsumerInterceptors(applicationContext);
    assertTrue(interceptors.isEmpty());
  }

  private void mockApplicationContextGetConsumer() {
    when(applicationContext.getBeansOfType(KafkaConsumerInterceptor.class))
        .thenReturn(consumerInterceptorMap);
  }

  @Test
  public void testConsumer() {
    mockApplicationContextGetConsumer();
    mockAllConsumers();

    List<KafkaConsumerInterceptor> interceptors = InterceptorUtil.getKafkaConsumerInterceptors(applicationContext);

    assertSame(kafkaConsumerInterceptor3, interceptors.get(0));
    assertSame(kafkaConsumerInterceptor2, interceptors.get(1));
    assertSame(kafkaConsumerInterceptor1, interceptors.get(2));
  }

  private void mockAllConsumers() {
    when(kafkaConsumerInterceptor1.getOrder()).thenReturn(3);
    when(kafkaConsumerInterceptor2.getOrder()).thenReturn(2);
    when(kafkaConsumerInterceptor3.getOrder()).thenReturn(1);

    consumerInterceptorMap.put("1", kafkaConsumerInterceptor1);
    consumerInterceptorMap.put("2", kafkaConsumerInterceptor2);
    consumerInterceptorMap.put("3", kafkaConsumerInterceptor3);
  }
}