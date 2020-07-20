# Common Module

Common module is standard request and response format for Web. Currently Common Module support :

- Standard web response
- Standard paging and sorting request
- Default error handler

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-common</artifactId>
</dependency>
```

## Standard Web Response

Blibli backend response will always use class `Response<T>`. 

```java
/**
 * Standard Blibli Web Response
 */
public class Response<T> {

  /**
   * Code , usually same as HTTP Code
   */
  private Integer code;

  /**
   * Status, usually same as HTTP status
   */
  private String status;

  /**
   * Response data
   */
  private T data;

  /**
   * Paging information, if response is paginate data
   */
  private Paging paging;

  /**
   * Error information, if request is not valid 
   */
  private Map<String, List<String>> errors;

  /**
   * Metadata information
   */
  private Map<String, Object> metadata;

}
```

We can use `ResponseHelper` to construct `Response<T>` object.

```java
@RestController
public class CustomerController {

  @GetMapping(value = "/api/customers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Response<Customer>> findById(@PathVariable("id") String id) {
    Mono<Customer> customerMono = service.findById(id);
    return customerMono.map(customer -> {
      return ResponseHelper.ok(customer);
    }).subscribeOn(Schedulers.elastic());
  }

}
```

## Standard Paging Request and Response 

For Paging, we can use `PagingRequest` class for web request, and `Paging` class for web response. 
We also already implement `PagingRequestArgumentResolver` to support argument injection on controller,
so we don't need to parse paging request manually.

```java
@RestController
public class CustomerController {
  
  @GetMapping(
    value = "/api/customers",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Mono<Response<List<Customer>>> list(PagingRequest pagingRequest) {
    return customerService.find(pagingRequest)
      .map(response -> {
        Paging paging = PagingHelper.toPaging(pagingRequest, response.getTotal());
        return ResponseHelper.ok(response.getCustomers(), paging);
      })
      .subscribeOn(commandScheduler);
  }


}
``` 

In the url we can use standard paging :

```
GET /api/customers?page=1&item_per_page=100

GET /api/customers?page=2&item_per_page=50

GET /api/customers?page=2
```

We can also configure default paging properties :

```properties
blibli.backend.common.paging.default-page=1
blibli.backend.common.paging.default-item-per-page=50
blibli.backend.common.paging.max-item-per-page=1000
```

## Standard Sorting Request

In the `PagingRequest` we also can give sorting information, and it will automatically injected to `PagingRequest`.

```
GET /api/customers?page=2&sort_by=id:asc,first_name:asc,created_at:desc

GET /api/customers?page=2&sort_by=id,first_name,created_at:desc
```

We can also configured default sorting properties

```properties
blibli.backend.common.paging.default-sort-direction=asc
```

## Default Error Handler

Common module also contain default error handler to create standard error response. 
We can use `CommonErrorController` class to create standard error controller.

```java
@Slf4j
@RestControllerAdvice
public class ErrorController implements CommonErrorController, MessageSourceAware {

  @Getter
  @Setter
  private MessageSource messageSource;

  @Override
  public Logger getLogger() {
    return log;
  }
}
``` 

## Swagger Support

If we include Swagger Module, we can also make Paging & Sorting feature to be included on Swagger OpenAPI Spec. 
We only need to add annotation `@PagingRequestInQuery`

```java
@RestController
public class CustomerController {
  
  @PagingRequestInQuery
  @GetMapping(
    value = "/api/customers",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Mono<Response<List<Customer>>> list(PagingRequest pagingRequest) {
    // some logic
  }

}
```

## Metadata Information for Validation

Sometimes we want to send metadata information for UI in validation message. We can do this with add @MetaDatas on validation field

```java
public class SampleRequest {

      @NotBlank(message = "NotBlank")
      private String name;

      @MetaDatas(
        @MetaData(key = "min", value = "1"),
        @MetaData(key = "max", value = "100")
      )
      @Min(1)
      @Max(100)
      private Integer age;
```

When get validation error, response will automatically contains metadata 

```json
{
  "code" : 400,
  "status" : "BAD_REQUEST",
  "errors" : {
    "age" : [
      "TooLarge"
    ] 
  },
  "metadata" : {
    "errors" : {
      "age" : {
        "min" : "1",
        "max" : "100"
      } 
    }
  }
}
```