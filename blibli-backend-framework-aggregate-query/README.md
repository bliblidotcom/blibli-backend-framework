# Aggregate Query Module

Aggregate Query Module is api client for Aggregate Query platform at Blibli. 
Aggregate Query Module is based on API Client Module

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-aggregate-query</artifactId>
</dependency>  
```

## Configuration Properties

Because this module based on API Client Module, so to configure the url, timeout, 
we can use API Client configuration properties

```properties
blibli.backend.aggregate.query.service-id=YourServiceID

blibli.backend.apiclient.configs.aggregateQueryApiClient.url=http://aggregate-query-host
blibli.backend.apiclient.configs.aggregateQueryApiClient.connect-timeout=2s
blibli.backend.apiclient.configs.aggregateQueryApiClient.read-timeout=2s
blibli.backend.apiclient.configs.aggregateQueryApiClient.write-timeout=2s
```

And don't forget to register `AggregateQueryApiClient` package to api client packages

```properties
blibli.backend.apiclient.packages=com.blibli.oss.backend.aggregate.query.apiclient,com.yourcompany.project.apiclient
```

## Aggregate Query API Client

`AggregateQueryApiClient` support some operations, search, get, and scroll. And you can use elasticsearch JSON query to get data from aggregate query.

```java
@Autowired
private AggregateQueryApiClient aggregateQueryApiClient;

// elasticsearch query
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
  .query(QueryBuilders.termQuery("user", "kimchy"))
  .from(0)
  .size(5);
String jsonRequest = sourceBuilder.toString();

Flux<Customer> response = aggregateQueryApiClient.search("customer_index", jsonRequest)
  .map(value -> value.getHits().hitsAs(map -> objectMapper.convertValue(map, Customer.class)))
  .flatMapMany(Flux::fromIterable);
```