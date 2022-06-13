package com.blibli.oss.backend.newrelic.aspect;

import com.blibli.oss.backend.command.Command;
import com.blibli.oss.backend.newrelic.TestApplication;
import com.blibli.oss.backend.newrelic.aspect.service.AspectModifyService;
import com.blibli.oss.backend.newrelic.injector.NewRelicTokenInjectorFilter;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestApplication.class)
@ExtendWith(MockitoExtension.class)
public class CommandAspectTest {

  @MockBean
  private Token token;

  @MockBean
  private Transaction transaction;

  @MockBean
  private Segment segment;

  @MockBean
  private MongoProperties mongoProperties;

  @MockBean
  private ReactiveMongoTemplate reactiveMongoTemplate;

  @Autowired
  private AspectModifyService aspectModifyService;

  private final String SEGMENT_NAME = "Command DoublingCommand.Command.execute(..)";
  private Context COMMAND_CONTEXT;


  @BeforeEach
  public void before() {
    COMMAND_CONTEXT = Context.of(
        NewRelicTokenInjectorFilter.TOKEN_CONTEXT_KEY, token,
        NewRelicTokenInjectorFilter.TRANSACTION_CONTEXT_KEY, transaction
    );
  }

  @Test
  public void afterCommandExecute() {
    // Create proxy, see https://stackoverflow.com/a/11436601/4504053
    Command<Integer, Integer> myCommand = new DoublingCommand();
    AspectJProxyFactory factory = new AspectJProxyFactory(myCommand);
    CommandAspect aspect = new CommandAspect(aspectModifyService);
    factory.addAspect(aspect);
    Command<Integer, Integer> myCommandProxy = factory.getProxy();

    when(transaction.startSegment(SEGMENT_NAME))
        .thenReturn(segment);

    Mono<Integer> source = myCommandProxy.execute(4)
        .subscriberContext(COMMAND_CONTEXT);
    StepVerifier.create(source)
        .expectNext(8)
        .expectAccessibleContext()
        .contains(NewRelicTokenInjectorFilter.TOKEN_CONTEXT_KEY, token)
        .contains(NewRelicTokenInjectorFilter.TRANSACTION_CONTEXT_KEY, transaction)
        .then()
        .verifyComplete();

    verify(transaction).startSegment(SEGMENT_NAME);
    verify(segment).end();
  }

  @AfterEach
  public void after() {
    verifyNoMoreInteractions(token, transaction, segment);
  }

  private static class DoublingCommand implements Command<Integer, Integer> {
    @Override
    public Mono<Integer> execute(Integer request) {
      return Mono.just(request)
          .map(x -> x*2);
    }
  }
}
