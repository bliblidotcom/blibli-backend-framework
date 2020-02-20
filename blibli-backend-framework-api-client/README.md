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

## Web Client Customizer

## Codec Customizer 

## Supported Body