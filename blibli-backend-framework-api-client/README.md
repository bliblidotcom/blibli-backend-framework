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