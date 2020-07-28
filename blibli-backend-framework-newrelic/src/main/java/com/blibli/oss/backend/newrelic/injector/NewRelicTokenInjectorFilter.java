package com.blibli.oss.backend.newrelic.injector;

import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Optional;

public class NewRelicTokenInjectorFilter implements WebFilter {

  public static final String TRANSACTION_CONTEXT_KEY = "NEW_RELIC_TRANSACTION";
  public static final String TOKEN_CONTEXT_KEY = "NEW_RELIC_TOKEN";

  private Agent agent;

  public NewRelicTokenInjectorFilter(Agent agent) {
    this.agent = agent;
  }

  @Override
  @Trace(async = true)
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return chain.filter(exchange)
        .subscriberContext(context -> {
          Transaction transaction = agent.getTransaction();
          // TODO IDK exactly what is this
          // Just copas from newrelic-rx
          transaction.getTracedMethod().setMetricName("Reactor");

          Token token = transaction.getToken();
          token.link();
          Context updatedContext = context
              .put(TRANSACTION_CONTEXT_KEY, transaction)
              .put(TOKEN_CONTEXT_KEY, token);
          return updatedContext;
        });
  }

  public static Optional<Token> getToken(Context context) {
    return context.getOrEmpty(TOKEN_CONTEXT_KEY);
  }

  public static Optional<Transaction> getTransaction(Context context) {
    return context.getOrEmpty(TRANSACTION_CONTEXT_KEY);
  }
}
