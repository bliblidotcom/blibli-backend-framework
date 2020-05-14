# External API

External API Module is helper for Blibli External API

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-external-api</artifactId>
</dependency>
```

## External Session

External Session is represented by `ExternalSession` class. There are some information on that class, 
like userId, sessionId, member, and additional parameter

```java
public class ExternalSession {

  private String userId;

  private String sessionId;

  private boolean member;

  private Map<String, String> additionalParameters;

}
```

## Get External Session

To Get External Session on Controller, we can add on method parameter. `ExternalSessionArgumentResolver` 
will automatically get the data from HTTP Header

```java
@RestController
public class ExampleController {

  @GetMapping(value = "/backend/your-api", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Response<SomeResponse>> member(ExternalSession externalSession) {
    // do something
  }
```

## Validate External Session

Sometimes we want to validate external session. For example, we want to create API only for member, and reject guest request.
We don't need to do it manually on controller, we can use annotation `@MustMember` for member, 
`@MustMember(value=false)` for guest

```java
@RestController
public class ExampleController {

  @GetMapping(value = "/backend/only-member", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Response<MemberData>> member(@MustMember ExternalSession externalSession) {
    // do something
  }

  @GetMapping(value = "/backend/only-guest", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Response<GuestData>> guest(@MustMember(false) ExternalSession externalSession) {
    // do something
  }
```

## Swagger Support

To add External Session to swagger, we can use `@ExternalSessionAtHeader` annotation in controller method.

```java
@RestController
public class ExampleController {
  
  @ExternalSessionAtHeader
  @GetMapping(value = "/backend/only-member", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Response<MemberData>> member(@MustMember ExternalSession externalSession) {
    // do something
  }

  @ExternalSessionAtHeader
  @GetMapping(value = "/backend/only-guest", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Response<GuestData>> guest(@MustMember(false) ExternalSession externalSession) {
    // do something
  }
```

## Sleuth Support

If we are using sleuth, we can also get `ExternalSession` from sleuth using `ExternalSessionHelper`.

```java
@Service
public class ExampleService {

  @Autowired
  private Tracer tracer;

  public ExternalSession getExternalSession() {
    return ExternalSessionHelper.fromSleuth(tracer.currentSpan().context());
  }

}
```

## Exception Handler

When we get invalid external session, this module will throw `ExternalApiException`, you need to create `RestControllerAdvice` 
to translate the exception to your custom response. Or we can use `ExternalApiErrorController`, this will automatic translate
`ExternalApiException` to 401 Unauthorized response

```java
@Slf4j
@RestControllerAdvice
static class ErrorController implements ExternalApiErrorController, MessageSourceAware {

  @Getter
  @Setter
  private MessageSource messageSource;

  @Override
  public Logger getLogger() {
    return log;
  }
}
```