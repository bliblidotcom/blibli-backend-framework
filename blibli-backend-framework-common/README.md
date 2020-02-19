# Common Module

Common module is standard request and response format for Web. Currently Common Module support :

- Standard web response
- Standard paging and sorting request
- Default error handler

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
  public Mono<Response<List<Customer>>> list(MandatoryParameter mandatoryParameter,
                                                           PagingRequest pagingRequest) {
    return customerService.find(mandatoryParameter, pagingRequest)
      .map(response -> {
        Paging paging = PagingHelper.toPaging(pagingRequest, response.getTotal());
        return ResponseHelper.ok(response.getCustomers(), paging);
      })
      .subscribeOn(commandScheduler);
  }


}
``` 

## Default Error Handler

## Swagger Support