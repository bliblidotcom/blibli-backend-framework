# Swagger Module

Swagger module is module to generate Open API Spec and also provide Swagger UI.

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-swagger</artifactId>
</dependency>
```

## Open API and Swagger UI

This module will automatically generate open api spec and swagger ui. This module will read all Controller class and automatically generate open api spec.

```
// Open API Spec
GET /v3/api-docs

// Swagger UI
GET /swagger-ui.html
``` 

## Ignored Parameter for Swagger

By default swagger will transform all parameter on controller to become body or query param. 
If we need to ignore it, we can use annotation `@SwaggerIgnore`.

```java
@RestController
public class CustomerController {
  
  @GetMapping("/customers/{customerId}")
  public Mono<Response<Customer>> get(@SwaggerIgnored MandatoryParameter mandatoryParameter,
                                      @PathVariable("customerId") String customerId){
    ...
  } 

}
```

Of if we want to create custom ignored logic, we can create bean of class `SwaggerIgnoredParameter` 

```java
@Component
public class MandatoryParameterSwaggerIgnoredParameter implements SwaggerIgnoredParameter {

  @Override
  public boolean isIgnored(Parameter parameter) {
    // this logic will ignored all MandatoryParameter parameters
    return MandatoryParameter.class.isAssignableFrom(parameter.getType());
  }
}
```

## Swagger Properties

We can configure swagger information on Open API Spec

```properties
blibli.backend.swagger.title=@project.artifactId@
blibli.backend.swagger.version=@project.version@
blibli.backend.swagger.description=@project.description@
blibli.backend.swagger.terms-of-service=https://www.blibli.com/
```