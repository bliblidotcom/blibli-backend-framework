package com.blibli.oss.backend.newrelic.injector;

import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewRelicTokenInjectorFilterTest {

  @Mock
  private Agent agent;

  @Mock
  private Transaction transaction;

  @Mock
  private TracedMethod tracedMethod;

  @Mock
  private Token token;

  @Mock
  private ServerWebExchange exchange;

  @Mock
  private WebFilterChain chain;

  @Test
  public void filter() {
    NewRelicTokenInjectorFilter uut = new NewRelicTokenInjectorFilter(agent);

    when(chain.filter(exchange)).thenReturn(Mono.just("Void").then());
    when(agent.getTransaction()).thenReturn(transaction);
    when(transaction.getToken()).thenReturn(token);
    when(transaction.getTracedMethod()).thenReturn(tracedMethod);

    Mono<Void> source = uut.filter(exchange, chain);
    StepVerifier.create(source)
        .expectAccessibleContext()
        .contains(NewRelicTokenInjectorFilter.TRANSACTION_CONTEXT_KEY, transaction)
        .contains(NewRelicTokenInjectorFilter.TOKEN_CONTEXT_KEY, token)
        .then()
        .expectComplete()
        .verify();

    verify(chain).filter(exchange);
    verify(agent).getTransaction();
    verify(transaction).getToken();
    verify(transaction).getTracedMethod();
    verify(tracedMethod).setMetricName("Reactor");
    verify(token).link();
  }

  @Test
  public void getToken() {
    Context ctx = Context.of(NewRelicTokenInjectorFilter.TRANSACTION_CONTEXT_KEY, transaction, NewRelicTokenInjectorFilter.TOKEN_CONTEXT_KEY, token);
    final Optional<Token> actual = NewRelicTokenInjectorFilter.getToken(ctx);
    assertTrue(actual.isPresent());
    assertEquals(token, actual.get());
  }

  @Test
  public void getTransaction() {
    Context ctx = Context.of(NewRelicTokenInjectorFilter.TRANSACTION_CONTEXT_KEY, transaction, NewRelicTokenInjectorFilter.TOKEN_CONTEXT_KEY, token);
    final Optional<Transaction> actual = NewRelicTokenInjectorFilter.getTransaction(ctx);
    assertTrue(actual.isPresent());
    assertEquals(transaction, actual.get());
  }

  @AfterEach
  public void after() {
    verifyNoMoreInteractions(agent, transaction, token, exchange, chain, tracedMethod);
  }
}
