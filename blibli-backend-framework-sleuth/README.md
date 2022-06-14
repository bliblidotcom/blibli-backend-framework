# Sleuth Module

Sleuth module is library to simplify sleuth integration 

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-sleuth</artifactId>
</dependency>
```

## Extra Fields

Sleuth support baggage or extra fields. This is field that we can store to sleuth context,
and can be accessed anywhere. Default spring boot already support extra fields using properties. 
But sometimes we want to add extra field using code or in multiple place. Sleuth module support
extra fields using spring bean. We only need to create bean of class `SleuthExtraFields`

```java
@Component
public static class HelloWorldSleuthExtraFields implements SleuthExtraFields {

  @Override
  public List<String> getFields() {
    return Arrays.asList("Hello", "World");
  }
}

@Component 
public static class MandatoryParamSleuthExtraFields implements SleuthExtraFields {

  @Override
  public List<String> getFields() {
    return Arrays.asList("StoreId", "ClientId");
  }
}
``` 

Sleuth module will automatically register all fields, so we can set and get extra field to sleuth context.

```java

@Autowired
private Tracer tracer;

// set extra field
BaggageField.getByName(span.context(), "Hello").updateValue(span.context(), "Value");
BaggageField.getByName(span.context(), "World").updateValue(span.context(), "Value");
BaggageField.getByName(span.context(), "ClientId").updateValue(span.context(), "Value");
BaggageField.getByName(span.context(), "StoreId").updateValue(span.context(), "Value");

// get extra field
String hello = BaggageField.getByName(span.context(), "Hello").getValue(span.context());
String world = BaggageField.getByName(span.context(), "World").getValue(span.context());
String clientId = BaggageField.getByName(span.context(), "ClientId").getValue(span.context());
String storeId = BaggageField.getByName(span.context(), "StoreId").getValue(span.context());

``` 

## Tracer on Web Filter

Using Tracer on WebFilter is not easy. By default current span is not active on web filter. 
To simplify this, we can use class `SleuthWebFilter` as base interface. The filter method will have `Span` parameter.

```java
@Component
public class ExampleSleuthWebFilter implements SleuthWebFilter {

  @Autowired
  @Getter
  private Tracer tracer;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain, Span currentSpan) {
    // do something with currentSpan
    BaggageField.getByName(currentSpan.context(), "Key").updateValue(currentSpan.context(), "Value");

    return chain.filter(exchange);
  }
}
```