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



## Caching