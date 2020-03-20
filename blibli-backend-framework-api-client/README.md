# API Client Module

API Client Module is declarative http client. It is replacement of OpenFeign. API Client use Spring WebClient as http client, and use Project Reactor for reactive programming.

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-api-client</artifactId>
</dependency>
```

## Create API Client

To Create API Client, we can create interface with annotation `@ApiClient`.

```java
@ApiClient(
  name = "binListApiClient"
)
public interface BinListApiClient {

  @RequestMapping(
    value = "/{number}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<BinResponse> lookup(@PathVariable("number") String number);

}
```

And to automatically register it, we need to change configuration properties

```properties
blibli.backend.apiclient.packages=com.blibli.oss.backend.example.client
```

```properties
blibli.backend.apiclient.packages=com.blibli.oss.backend.aggregate.query.apiclient,\
  com.blibli.oss.backend.example.client
```

## API Client Configuration

To configure API Client, we can use properties with bean name. 

```properties
blibli.backend.apiclient.configs.binListApiClient.url=https://lookup.binlist.net
blibli.backend.apiclient.configs.binListApiClient.connect-timeout=2s
blibli.backend.apiclient.configs.binListApiClient.read-timeout=2s
blibli.backend.apiclient.configs.binListApiClient.write-timeout=2s
blibli.backend.apiclient.configs.binListApiClient.headers.Accept=application/json
```

`binListApiClient` is name of `@ApiClient(name)`

## Default Configuration

Sometimes we have multiple API Client with same configuration, like headers, or interceptor. API Client module support
default configuration, where we can share configuration for all API Client.

Without default configuration, we need to create properties like this :

```properties
blibli.backend.apiclient.configs.firstApiClient.url=https://firt-host:8080
blibli.backend.apiclient.configs.firstApiClient.connect-timeout=2s
blibli.backend.apiclient.configs.firstApiClient.read-timeout=2s
blibli.backend.apiclient.configs.firstApiClient.write-timeout=2s
blibli.backend.apiclient.configs.firstApiClient.headers.Accept=application/json
blibli.backend.apiclient.configs.firstApiClient.headers.Content-Type=application/json
blibli.backend.apiclient.configs.firstApiClient.interceptors[0]=com.company.project.apiclient.interceptor.YourGlobalInterceptor
blibli.backend.apiclient.configs.firstApiClient.interceptors[1]=com.company.project.apiclient.interceptor.YourFirstInterceptor

blibli.backend.apiclient.configs.secondApiClient.url=https://second-host:8080
blibli.backend.apiclient.configs.secondApiClient.connect-timeout=2s
blibli.backend.apiclient.configs.secondApiClient.read-timeout=2s
blibli.backend.apiclient.configs.secondApiClient.write-timeout=2s
blibli.backend.apiclient.configs.secondApiClient.headers.Accept=application/json
blibli.backend.apiclient.configs.secondApiClient.headers.Content-Type=application/json
blibli.backend.apiclient.configs.secondApiClient.interceptors[0]=com.company.project.apiclient.interceptor.YourGlobalInterceptor
blibli.backend.apiclient.configs.secondApiClient.interceptors[1]=com.company.project.apiclient.interceptor.YourSecondInterceptor

blibli.backend.apiclient.configs.thirdApiClient.url=https://third-host:8080
blibli.backend.apiclient.configs.thirdApiClient.connect-timeout=2s
blibli.backend.apiclient.configs.thirdApiClient.read-timeout=2s
blibli.backend.apiclient.configs.thirdApiClient.write-timeout=2s
blibli.backend.apiclient.configs.thirdApiClient.headers.Accept=application/json
blibli.backend.apiclient.configs.thirdApiClient.headers.Content-Type=application/json
blibli.backend.apiclient.configs.thirdApiClient.interceptors[0]=com.company.project.apiclient.interceptor.YourGlobalInterceptor
blibli.backend.apiclient.configs.thirdApiClient.interceptors[1]=com.company.project.apiclient.interceptor.YourThirdInterceptor
```

With default configuration, we can simplify properties file like this :

```properties
# default properties
blibli.backend.apiclient.configs.default.connect-timeout=2s
blibli.backend.apiclient.configs.default.read-timeout=2s
blibli.backend.apiclient.configs.default.write-timeout=2s
blibli.backend.apiclient.configs.default.headers.Accept=application/json
blibli.backend.apiclient.configs.default.headers.Content-Type=application/json
blibli.backend.apiclient.configs.default.interceptors[0]=com.company.project.apiclient.interceptor.YourGlobalInterceptor

blibli.backend.apiclient.configs.firstApiClient.url=https://firt-host:8080
blibli.backend.apiclient.configs.firstApiClient.interceptors[0]=com.company.project.apiclient.interceptor.YourFirstInterceptor

blibli.backend.apiclient.configs.secondApiClient.url=https://second-host:8080
blibli.backend.apiclient.configs.secondApiClient.interceptors[0]=com.company.project.apiclient.interceptor.YourSecondInterceptor

blibli.backend.apiclient.configs.thirdApiClient.url=https://third-host:8080
blibli.backend.apiclient.configs.thirdApiClient.interceptors[0]=com.company.project.apiclient.interceptor.YourThirdInterceptor
``` 

With default properties, all config from default properties will copies to our API Client properties. But the config will be copied only
if API Client properties is null, so it will not override existing properties.

## Fallback

Error is part of service integration and handling error manually is really annoying. API Client module already support
fallback if there is error (network error, response error, etc). 

We can use fallback parameter on @ApiClient annotation

```java
@Component
public class AggregateQueryApiClientFallback implements AggregateQueryApiClient {

  @Override
  public Mono<AggregateQueryHit<Map<String, Object>>> get(String index, String id) {
    return Mono.just(AggregateQueryHit.<Map<String, Object>>builder()
      .found(false)
      .id(id)
      .index(index)
      .score(0.0)
      .version(0)
      .source(Collections.emptyMap())
      .build());
  }

}

@ApiClient(
  name = "aggregateQueryApiClient",
  fallback = AggregateQueryApiClientFallback.class
)
public interface AggregateQueryApiClient {

  @RequestMapping(
    value = "/api-native/{index}/{id}",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<AggregateQueryHit<Map<String, Object>>> get(@PathVariable("index") String index,
                                                   @PathVariable("id") String id);
}
```
 
or using properties

```properties
blibli.backend.apiclient.configs.aggregateQueryApiClient.fallback=com.example.project.apiclient.fallback.ServiceApiClientFallback
``` 

## Interceptor

Some times we want to do something before or after http request using API Client. We can use `ApiClientInterceptor`.
`ApiClientInterceptor` is interceptor that extend spring web client `ExchangeFilterFunction`. 
We can add action before and after http request.

```java
@Component
public class AggregateQueryApiClientInterceptor implements ApiClientInterceptor {

  private AggregateQueryProperties aggregateQueryProperties;

  @Override
  public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
    return Mono.just(request)
      .map(clientRequest ->
        ClientRequest
          .from(clientRequest)
          .header(AggregateQueryConstant.SERVICE_ID_HEADER, aggregateQueryProperties.getServiceId())
          .build()
      ).flatMap(next::exchange);
  }
}
```  

We can register `ApiClientInterceptor` using annotation :

```java
@ApiClient(
  name = "aggregateQueryApiClient",
  interceptors = {
    AggregateQueryApiClientInterceptor.class
  }
)
public interface AggregateQueryApiClient {
  
}
```

or using properties :

```properties
blibli.backend.apiclient.configs.aggregateQueryApiClient.interceptors[0]=com.blibli.oss.backend.aggregate.query.interceptor.AggregateQueryApiClientInterceptor
blibli.backend.apiclient.configs.aggregateQueryApiClient.interceptors[1]=com.blibli.oss.backend.aggregate.query.interceptor.OtherInterceptor
```

We can add more than one `ApiClientInterceptor`

## Global Interceptor

By default, Interceptor only works per API Client. Sometimes we want to create global interceptor, what works on all 
API Client. To handle this problem, API Client module also has `GlobalApiClientInterceptor` interface. We only need to
create spring bean of this interface, and it will automatically registered to all API Client.

```java
@Component
public static class EchoGlobalApiClientInterceptor implements GlobalApiClientInterceptor {

@Override
public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
  return Mono.fromCallable(() ->
    ClientRequest.from(request)
      .header("POWERED-BY", "BLIBLI")
      .build()
  ).flatMap(next::exchange);
}
}
```

## Web Client Customizer

Api Client Module using Spring WebClient as http client. Sometimes we want to change configuration of WebClient. 
Api Client Module provide `ApiClientWebClientCustomizer` to customize WebClient creation.

```java
@Component
public class BinListWebClientCustomizer implements ApiClientWebClientCustomizer {

  @Override
  public void customize(WebClient.Builder builder) {
    builder.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    builder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
  }
}
```  

We can register `ApiClientWebClientCustomizer` using annotation :

```java
@ApiClient(
  name = "binListApiClient",
  webClientCustomizers = {
    BinListWebClientCustomizer.class
  }
)
public interface BinListApiClient {
  
}
```

Or using properties :

```properties
blibli.backend.apiclient.configs.binListApiClient.web-client-customizers[0]=com.blibli.oss.backend.example.client.customizer.BinListWebClientCustomizer
```

We can add more than one `ApiClientWebClientCustomizer`

## Codec Customizer 

In Spring WebClient, we can configure codec for HTTP reader and writer. In API Client module, we can also configure the codec using `ApiClientCodecCustomizer`

```java
@Component
public class ExampleCodecCustomizer implements ApiClientCodecCustomizer {

  @Override
  public void customize(ClientCodecConfigurer configurer) {
    configurer.defaultCodecs().enableLoggingRequestDetails(true);
    configurer.defaultCodecs().maxInMemorySize(1000 * 1024);
  }
}
```

And we can register `ApiClientCodecCustomizer` using annotation :

```java
@ApiClient(
  name = "exampleApiClient",
  codecCustomizers = {
    ExampleCodecCustomizer.class
  }
)
public interface ExampleApiClient {

}
```

or using properties :

```properties
blibli.backend.apiclient.configs.exampleApiClient.codec-customizers[0]=com.blibli.oss.backend.example.client.customizer.ExampleCodecCustomizer
```

## Supported Body

API Client Module is modular library, it support request body resolver to translate from `@RequestBody` parameter to low level http request.
The resolver implemented using `ApiBodyResolver`. We can create custom `ApiBodyResolver` and add as spring bean. 
API Client Module will automatically load it.

By default, API Client module support 3 body resolver :

- `multipart/form-data` using `MultipartBodyResolver`. This resolver will get all parameter with annotation `@RequestPart`
- `application/json` using `JsonBodyResolver`. This resolver will convert `@RequestBody` object to JSON
- `application/x-www-form-urlencoded` using `FormBodyResolver`. This resolver will convert `@RequestBody MultiValueMap<String, String>` to form body.

## Error Resolver

Sometimes we want to do something after we get error. Like connection error, parsing error, http response error, etc. 
API Client Module provide `ApiErrorResolver` class to handle this. We can translate from error to `Mono<OtherResult>` if we want, 
or if we want to continue to fallback, we can return `Mono.empty()`.

```java
@Slf4j
@Component
public class BinListErrorResolver implements ApiErrorResolver {

  @Override
  public Mono<Object> resolve(Throwable throwable, Class<?> type, Method method, Object[] arguments) {
    log.error(String.format("Ups error call ApiClient %s.%s", type, method), throwable);
    return Mono.empty(); // continue to fallback
  }
}
```

To register `ApiErrorResolver` we can use annotation :

```java
@ApiClient(
  name = "binListApiClient",
  errorResolver = BinListErrorResolver.class
)
public interface BinListApiClient {

}
```

or properties :

```properties
blibli.backend.apiclient.configs.binListApiClient.error-resolver=com.blibli.oss.backend.example.client.error.BinListErrorResolver
```

## ResponseEntity Support

API Client Module using Spring WebClient as http client. And by default, if response code is 4xx or 5xx, Spring WebClient will throw an exception.
But sometimes we want to get all the detail of server response, like http status, headers and body. 
To achieve this, API Client Module support Spring `ResponseEntity<T>`. If method return `Mono<ResponseEntity<T>>`, 
API Client will get all server response, even if server response is 4xx or 5xx.

```java
@ApiClient(
  name = "exampleClient"
)
public interface ExampleClient {

  @RequestMapping(
    method = RequestMethod.GET,
    path = "/response-entity-void"
  )
  Mono<ResponseEntity<Void>> responseEntityVoid();

  @RequestMapping(
    method = RequestMethod.GET,
    path = "/response-entity",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<ResponseEntity<FirstResponse>> responseEntity();

  @RequestMapping(
    method = RequestMethod.GET,
    path = "/response-entity-list",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Mono<ResponseEntity<List<FirstResponse>>> responseEntityList();

}
``` 

## Log API Client Request and Response

Spring WebClient already support log request and response. But this is not recommended on production, becuase can
make our app more slow. But it's good for debug our API Client. To log request and response for API Client, we can 
use configuration properties.

```properties
spring.http.log-request-details=true

logging.level.org.springframework.web.reactive.function.client.ExchangeFunctions=TRACE
```

This is example request and response log

```
2020-03-18 17:09:28.854 TRACE 98271 --- [           main] o.s.w.r.f.client.ExchangeFunctions       : [3fa21d49] HTTP POST http://localhost:8089/fifth, headers={masked}
2020-03-18 17:09:28.882 TRACE 98271 --- [ctor-http-nio-1] o.s.w.r.f.client.ExchangeFunctions       : [3fa21d49] Response 200 OK, headers={masked}

2020-03-18 17:09:28.895 TRACE 98271 --- [           main] o.s.w.r.f.client.ExchangeFunctions       : [2ed71727] HTTP POST http://localhost:8089/first, headers={masked}
2020-03-18 17:09:28.913 TRACE 98271 --- [ctor-http-nio-1] o.s.w.r.f.client.ExchangeFunctions       : [2ed71727] Response 200 OK, headers={masked}

2020-03-18 17:09:28.925 TRACE 98271 --- [           main] o.s.w.r.f.client.ExchangeFunctions       : [72a2312e] HTTP GET http://localhost:8089/forth/eko?size=100&page=1, headers={masked}
2020-03-18 17:09:28.974 TRACE 98271 --- [ctor-http-nio-1] o.s.w.r.f.client.ExchangeFunctions       : [72a2312e] Response 200 OK, headers={masked}

2020-03-18 17:09:28.998 TRACE 98271 --- [           main] o.s.w.r.f.client.ExchangeFunctions       : [7a3269f5] HTTP POST http://localhost:8089/sixth, headers={masked}
2020-03-18 17:09:29.109 TRACE 98271 --- [ctor-http-nio-1] o.s.w.r.f.client.ExchangeFunctions       : [7a3269f5] Response 200 OK, headers={masked}
```

If we want to create more details logs, for example log the body, cookie, etc. We also can create our logger interceptor
using `ApiClientInterceptor` or `GlobalApiClientInterceptor`.

## Tcp Client Customizer

API Client using Spring Web Client as http client. And Spring Web Client use Netty. Sometimes we want to customize Netty TCP Client.
API Client support this with interface `ApiClientTcpClientCustomizer`, we only need to create the bean of `ApiClientTcpClientCustomizer`
and API Client will automatically call `customize(TcpClient)` method.

```java
@Component
public class WireTrapTcpClientCustomizer implements ApiClientTcpClientCustomizer {

    @Override
    public TcpClient customize(TcpClient tcpClient) {
      return tcpClient.wiretap(true);
    }
}
``` 

And we can register to API Client using annotation :

```java
@ApiClient(
  name = "helloApiClient",
  tcpClientCustomizers = WireTrapTcpClientCustomizer.class
)
public interface HelloApiClient {
  
}
```

or using properties 

```properties
blibli.backend.apiclient.configs.exampleClient.tcp-client-customizers[0]=com.yourcompany.project.apiclient.customizer.WireTrapTcpClientCustomizer
```

## Reactor Scheduler Support

API Client already non blocking using Netty. So by default you don't need to limit the request. But maybe sometimes we want
to limit the request using Reactor Scheduler, for example because target server is slow, so we want to limit number of thread
for API Client.

To handle this, API Client already support `SchedulerHelper` of Reactor Module. We only need to create scheduler using properties
with same name with API Client.

```java
@ApiClient(
  name = "exampleClient"
)
public interface ExampleClient {
  
}
```

```properties
blibli.backend.reactor.scheduler.configs.exampleClient.type=thread_pool
blibli.backend.reactor.scheduler.configs.exampleClient.thread-pool.allow-core-thread-time-out=false
blibli.backend.reactor.scheduler.configs.exampleClient.thread-pool.core-pool-size=10
blibli.backend.reactor.scheduler.configs.exampleClient.thread-pool.maximum-pool-size=100
blibli.backend.reactor.scheduler.configs.exampleClient.thread-pool.queue-size=100
blibli.backend.reactor.scheduler.configs.exampleClient.thread-pool.queue-type=linked
```

## Sleuth Integration

By default, all Sleuth extra fields will me send to all API Client. So on target service, we can also get the Sleuth extra fields 
automatically. If you want to disabled API Client with sleuth integration, you can disabled using properties

```properties
# Enabled disabled api client x sleuth, default is true
blibli.backend.apiclient.sleuth.enabled=true
```