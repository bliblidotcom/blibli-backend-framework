# Mandatory Parameter

Mandatory Parameter is required parameters for communication between backend service.

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-mandatory-parameter</artifactId>
</dependency>
```

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

Mandatory parameter will be resolved from query param or http header. If we want, we can change the query param key or header key to resolve it.

```properties
blibli.backend.mandatoryparameter.header-key.channel-id=channelId
blibli.backend.mandatoryparameter.header-key.client-id=clientId
blibli.backend.mandatoryparameter.header-key.request-id=requestId
blibli.backend.mandatoryparameter.header-key.store-id=storeId
blibli.backend.mandatoryparameter.header-key.username=username

blibli.backend.mandatoryparameter.query-key.channel-id=channelId
blibli.backend.mandatoryparameter.query-key.client-id=clientId
blibli.backend.mandatoryparameter.query-key.request-id=requestId
blibli.backend.mandatoryparameter.query-key.store-id=storeId
blibli.backend.mandatoryparameter.query-key.username=username
```

## Swagger Support 

If we already include Swagger Module, we can also automatically register Mandatory Parameter to swagger api doc using annotation :

- `@MandatoryParameterAtHeader` if using http header
- `@MandatoryParameterAtQuery` if using query param

```java
@RestController
public class CustomerController {

  @MandatoryParameterAtHeader
  @PostMapping(
    value = "/api/customers",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public Mono<Response<CreateCustomerWebResponse>> create(MandatoryParameter mandatoryParameter,
                                                          @RequestBody CreateCustomerWebRequest request) {
    return commandExecutor.execute(CreateCustomerCommand.class, toCreateCustomerCommandRequest(request))
      .map(ResponseHelper::ok)
      .subscribeOn(commandScheduler);
  }
}
```

We can also change default value for mandatory parameter on swagger using properties 

```properties
blibli.backend.mandatoryparameter.header-key.channel-id-default-value=channelId
blibli.backend.mandatoryparameter.header-key.client-id-default-value=clientId
blibli.backend.mandatoryparameter.header-key.request-id-default-value=requestId
blibli.backend.mandatoryparameter.header-key.store-id-default-value=storeId
blibli.backend.mandatoryparameter.header-key.username-default-value=username

blibli.backend.mandatoryparameter.query-key.channel-id-default-value=channelId
blibli.backend.mandatoryparameter.query-key.client-id-default-value=clientId
blibli.backend.mandatoryparameter.query-key.request-id-default-value=requestId
blibli.backend.mandatoryparameter.query-key.store-id-default-value=storeId
blibli.backend.mandatoryparameter.query-key.username-default-value=username
```