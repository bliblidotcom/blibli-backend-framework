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

We can add more than 1 `ApiClientInterceptor`

## Web Client Customizer

## Codec Customizer 

## Supported Body

## Error Resolver