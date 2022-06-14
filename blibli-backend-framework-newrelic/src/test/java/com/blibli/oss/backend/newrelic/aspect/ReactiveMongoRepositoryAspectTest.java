package com.blibli.oss.backend.newrelic.aspect;

import com.blibli.oss.backend.newrelic.TestApplication;
import com.blibli.oss.backend.newrelic.aspect.service.AspectModifyService;
import com.newrelic.api.agent.DatastoreParameters;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Transaction;
import jdk.nashorn.internal.parser.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.FluentQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.util.function.Function;

import static com.blibli.oss.backend.newrelic.injector.NewRelicTokenInjectorFilter.TOKEN_CONTEXT_KEY;
import static com.blibli.oss.backend.newrelic.injector.NewRelicTokenInjectorFilter.TRANSACTION_CONTEXT_KEY;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestApplication.class)
@ExtendWith(MockitoExtension.class)
public class ReactiveMongoRepositoryAspectTest {

  @MockBean
  private Token token;

  @MockBean
  private Transaction transaction;

  @MockBean
  private Segment segment;

  @MockBean
  private ReactiveMongoTemplate reactiveMongoTemplate;

  @MockBean
  private MongoProperties mongoProperties;

  @Autowired
  private AspectModifyService aspectModifyService;

  private DummyRepository dummyRepositoryProxy;

  private static final String SEGMENT_NAME_FIND_ID = "ReactiveMongoRepository DummyRepositoryImpl.ReactiveCrudRepository.findById(..)";
  private static final String SEGMENT_NAME_FIND_ALL = "ReactiveMongoRepository DummyRepositoryImpl.ReactiveCrudRepository.findAll()";
  private static final String COLLECTION_NAME = "dummy_data";

  private static final DatastoreParameters EXPECTED_PARAM_FIND_ALL = DatastoreParameters.product("ReactiveMongo")
      .collection(COLLECTION_NAME)
      .operation("ReactiveCrudRepository.findAll()")
      .instance("blibli.com", "27017")
      .databaseName("testdb")
      .noSlowQuery()
      .build();

  private static final DatastoreParameters EXPECTED_PARAM_FIND_ID = DatastoreParameters.product("ReactiveMongo")
      .collection(COLLECTION_NAME)
      .operation("ReactiveCrudRepository.findById(..)")
      .instance("blibli.com", "27017")
      .databaseName("testdb")
      .noSlowQuery()
      .build();

  private Context COMMAND_CONTEXT;

  @BeforeEach
  public void before() {
    COMMAND_CONTEXT = Context.of(
        TOKEN_CONTEXT_KEY, token,
        TRANSACTION_CONTEXT_KEY, transaction
    );

    when(mongoProperties.getUri()).thenReturn("mongodb://blibli.com:27017/admin");
    when(mongoProperties.getDatabase()).thenReturn("testdb");

    // Create proxy, see https://stackoverflow.com/a/11436601/4504053
    DummyRepository dummyRepository = new DummyRepositoryImpl();
    AspectJProxyFactory factory = new AspectJProxyFactory(dummyRepository);
    ReactiveMongoDbAspect aspect = new ReactiveMongoDbAspect(aspectModifyService);
    factory.addAspect(aspect);
    dummyRepositoryProxy = factory.getProxy();
  }

  @Test
  public void afterRepositoryWhenReturnFlux() {
    mockGetCollectionName();
    mockStartSegment(SEGMENT_NAME_FIND_ALL);

    Flux<DummyData> source = dummyRepositoryProxy.findAll()
        .subscriberContext(COMMAND_CONTEXT);
    StepVerifier.create(source)
        .expectAccessibleContext()
        .contains(TOKEN_CONTEXT_KEY, token)
        .contains(TRANSACTION_CONTEXT_KEY, transaction)
        .then()
        .verifyComplete();

    verify(transaction).startSegment(SEGMENT_NAME_FIND_ALL);
    verify(segment).reportAsExternal(argThat(new DatastoreParametersMatcher(EXPECTED_PARAM_FIND_ALL)));
    verify(segment).end();
  }

  @Test
  public void afterRepositoryWhenReturnMono() {
    mockGetCollectionName();
    mockStartSegment(SEGMENT_NAME_FIND_ID);

    Mono<DummyData> source = dummyRepositoryProxy.findById("ID")
        .subscriberContext(COMMAND_CONTEXT);
    StepVerifier.create(source)
        .expectAccessibleContext()
        .contains(TOKEN_CONTEXT_KEY, token)
        .contains(TRANSACTION_CONTEXT_KEY, transaction)
        .then()
        .verifyComplete();

    verify(transaction).startSegment(SEGMENT_NAME_FIND_ID);
    verify(segment).reportAsExternal(argThat(new DatastoreParametersMatcher(EXPECTED_PARAM_FIND_ID)));
    verify(segment).end();
  }

  @Test
  public void pointcut() {
    ReactiveMongoDbAspect aspect = new ReactiveMongoDbAspect(null);
    aspect.mongoRepositoryInterface();
  }

  private void mockStartSegment(String name) {
    when(transaction.startSegment(name))
      .thenReturn(segment);
  }

  private void mockGetCollectionName() {
    when(reactiveMongoTemplate.getCollectionName(DummyData.class))
      .thenReturn(COLLECTION_NAME);
  }

  @AfterEach
  public void after() {
    verifyNoMoreInteractions(token, transaction, segment);
  }

  @AllArgsConstructor
  private class DatastoreParametersMatcher implements ArgumentMatcher<DatastoreParameters> {

    private DatastoreParameters expected;

    @Override
    public boolean matches(DatastoreParameters actual) {
      return expected.getHost().equals(actual.getHost()) &&
          expected.getCollection().equals(actual.getCollection()) &&
          expected.getDatabaseName().equals(actual.getDatabaseName()) &&
          expected.getOperation().equals(actual.getOperation()) &&
          expected.getPathOrId().equals(actual.getPathOrId()) &&
          expected.getProduct().equals(actual.getProduct());
    }
  }

  private interface DummyRepository extends ReactiveMongoRepository<DummyData, String> {}

  private class DummyRepositoryImpl implements DummyRepository {
    @Override
    public <S extends DummyData> Mono<S> insert(S entity) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public <S extends DummyData> Flux<S> insert(Iterable<S> entities) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public <S extends DummyData> Flux<S> insert(Publisher<S> entities) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public <S extends DummyData> Mono<S> findOne(Example<S> example) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public <S extends DummyData> Flux<S> findAll(Example<S> example) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public <S extends DummyData> Flux<S> findAll(Example<S> example, Sort sort) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public <S extends DummyData> Mono<Long> count(Example<S> example) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public <S extends DummyData> Mono<Boolean> exists(Example<S> example) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public <S extends DummyData, R, P extends Publisher<R>> P findBy(Example<S> example, Function<FluentQuery.ReactiveFluentQuery<S>, P> queryFunction) {
      return null;
    }

    @Override
    public Flux<DummyData> findAll(Sort sort) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public <S extends DummyData> Mono<S> save(S entity) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public <S extends DummyData> Flux<S> saveAll(Iterable<S> entities) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public <S extends DummyData> Flux<S> saveAll(Publisher<S> entityStream) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Mono<DummyData> findById(String s) {
      return Mono.empty();
    }

    @Override
    public Mono<DummyData> findById(Publisher<String> id) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Mono<Boolean> existsById(String s) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Mono<Boolean> existsById(Publisher<String> id) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Flux<DummyData> findAll() {
      return Flux.empty();
    }

    @Override
    public Flux<DummyData> findAllById(Iterable<String> strings) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Flux<DummyData> findAllById(Publisher<String> idStream) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Mono<Long> count() {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Mono<Void> deleteById(String s) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Mono<Void> deleteById(Publisher<String> id) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Mono<Void> delete(DummyData entity) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Mono<Void> deleteAllById(Iterable<? extends String> strings) {
      return null;
    }

    @Override
    public Mono<Void> deleteAll(Iterable<? extends DummyData> entities) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Mono<Void> deleteAll(Publisher<? extends DummyData> entityStream) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Mono<Void> deleteAll() {
      throw new RuntimeException("NOT IMPLEMENTED");
    }
  }

  @Data
  private class DummyData {
    private String id;
    private String data;
  }

}
