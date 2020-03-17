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
ExtraFieldPropagation.set(span.context(), "Hello", "Value");
ExtraFieldPropagation.set(span.context(), "World", "Value");
ExtraFieldPropagation.set(span.context(), "ClientId", "Value");
ExtraFieldPropagation.set(span.context(), "StoreId", "Value");

// get extra field
String hello = ExtraFieldPropagation.get(span.context(), "Hello");
String world = ExtraFieldPropagation.get(span.context(), "World");
String clientId = ExtraFieldPropagation.get(span.context(), "ClientId");
String storeId = ExtraFieldPropagation.get(span.context(), "StoreId");

``` 

