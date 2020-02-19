# Mandatory Parameter

Mandatory Parameter is required parameters for communication between backend service.

## Get Mandatory Parameter

Mandatory parameter represent with `MandatoryParameter` class. 
We also provide `MandatoryParameterHandlerMethodArgumentResolver` to automatically inject `MandatoryParameter` in controller.

```java
@RestController
public class CustomerController {

  @PostMapping(
    value = "/api/customers",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public Mono<Response<CreateCustomerWebResponse>> create(MandatoryParameter mandatoryParameter,
                                                          @RequestBody CreateCustomerWebRequest request) {
    return commandExecutor.execute(CreateCustomerCommand.class, toCreateCustomerCommandRequest(mandatoryParameter, request))
      .map(ResponseHelper::ok)
      .subscribeOn(commandScheduler);
  }
}
```

## Mandatory Parameter Properties



## Swagger Support 