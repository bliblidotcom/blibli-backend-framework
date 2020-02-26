# Command Module

Command Module is library to simplify Command Pattern implementation. 
Blibli backend project should move from Facade Pattern (Service Layer) to Command Pattern.
Command Module is reactive framework based on Project Reactor. It also support validation using Bean Validation.

 ## Setup Dependency
 
 ```xml
 <dependency>
   <groupId>com.blibli.oss</groupId>
   <artifactId>blibli-backend-framework-command</artifactId>
 </dependency>
 ```

## Create Command

Command need request and response class. We recommend using POJO class, not primitive data type or java collection.
To create command, we can create command using interface `Command<R,T>`.

```java
public interface GetBinListCommand extends Command<GetBinListCommandRequest, GetBinListWebResponse> {

}
```

All logic implementation of the command is in `Mono<T> execute(R)` method.

```java
@Service
public class GetBinListCommandImpl implements GetBinListCommand {

  @Autowired
  private BinListApiClient binListApiClient;

  @Override
  public Mono<GetBinListWebResponse> execute(GetBinListCommandRequest request) {
    return binListApiClient.lookup(request.getNumber())
      .map(this::toWebResponse);
  }

  private GetBinListWebResponse toWebResponse(BinResponse response) {
    GetBinListWebResponse webResponse = new GetBinListWebResponse();
    BeanUtils.copyProperties(response, webResponse);
    return webResponse;
  }
}
```

## Execute Command

After we create command, we can use `CommandExecutor` to execute the command.

```java
@RestController
public class BinListController {

  @Autowired
  private CommandExecutor commandExecutor;

  @GetMapping(
    value = "/binlist/{number}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Mono<Response<GetBinListWebResponse>> lookup(@PathVariable("number") String number) {
    return commandExecutor.execute(GetBinListCommand.class, toGetBinListCommandRequest(number))
      .map(ResponseHelper::ok)
      .subscribeOn(scheduler);
  }

}
```

## Validation

By default, all before `execute()` method is executed by `CommandExecutor`. `CommandExecutor` will validate the request using bean validation.

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetBinListCommandRequest {

  @NotBlank(message = "NotBlank")
  @Digits(message = "MustDigit", integer = 8, fraction = 0)
  private String number;
}
```

If we want to skip validation, we can override method `validateRequest` on our `Command`

```java
@Service
public class GetBinListCommandImpl implements GetBinListCommand {

  @Override
  public boolean validateRequest() {
    return false; // skip validation process
  }
```

## Command Interceptor

Command Module support command interceptor. With command interceptor we can manipulate data before and after command executed.
We only need to create spring bean of `CommandInterceptor`

```java
public interface CommandInterceptor {

  // executed before execute command
  default <R, T> Mono<T> before(Command<R, T> command, R request) {
    // empty mean we will execute the command
    // non empty mean we will return the data without execute the command
    return Mono.empty();
  }
  
  // executed after success execute command
  default <R, T> Mono<Void> afterSuccess(Command<R, T> command, R request, T response) {
    return Mono.empty();
  }

  // executed after failed execute command
  default <R, T> Mono<Void> afterFailed(Command<R, T> command, R request, Throwable throwable) {
    return Mono.empty();
  }
}
```

## Caching

`CommandInterceptor` is powerfull feature. We provide command caching using this feature. 
Caching is feature for cache command result on redis after finish execute command. 
With this feature, command will execute one time, and next time when we execute command again,
it will directly return from redis. This can make slow command process become faster.

```java
@Service
public class GetBinListCommandImpl implements GetBinListCommand {

  @Override
  public String cacheKey(GetBinListCommandRequest request) {
    return request.getNumber(); // return cache key for redis
  }

  @Override
  public Class<GetBinListWebResponse> responseClass() {
    return GetBinListWebResponse.class; // return class for json mapper 
  }
```

Command Module will cache the data using `ReactiveStringRedisTemplate`. So we need to make sure we have 
`ReactiveStringRedisTemplate` bean on our spring application.

By default, caching is disabled. You need to enabled it using configuration. 
We can also change timeout cache using configuration properties.

```properties
blibli.backend.command.cache.enabled=true
blibli.backend.command.cache.timeout=10m
``` 