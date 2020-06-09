# Internal API

Internal API Module is helper for Blibli Internal API

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-internal-api</artifactId>
</dependency>
```

## Internal Session

External Session is represented by `InternalSession` class. There are some information on that class, 
like userId, userName and roles.

```java
public class InternalSession {

  private String userId;

  private String userName;

  private List<String> roles;

}
```

## Get Internal Session

To Get Internal Session on Controller, we can add on method parameter. `InternalSessionArgumentResolver` 
will automatically get the data from HTTP Header

```java
@RestController
public class ExampleController {

  @GetMapping(value = "/backend-internal/your-api", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Response<SomeResponse>> member(InternalSession internalSession) {
    // do something
  }
```

## Swagger Support

To add Internal Session to swagger, we can use `@InternalSessionAtHeader` annotation in controller method.

```java
@RestController
public class ExampleController {
  
  @InternalSessionAtHeader
  @GetMapping(value = "/backend-internal/only-member", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Response<MemberData>> member(InternalSession internalSession) {
    // do something
  }
```

## Sleuth Support

If we are using sleuth, we can also get `InternalSession` from sleuth using `InternalSessionHelper`.

```java
@Service
public class ExampleService {

  @Autowired
  private Tracer tracer;

  public InternalSession getInternalSession() {
    return InternalSessionHelper.fromSleuth(tracer.currentSpan().context());
  }

}
```
