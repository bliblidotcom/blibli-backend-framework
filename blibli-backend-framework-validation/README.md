# Validation Module

We are using bean validation as validation library. The problem with bean validation, it's not reactive. 
Some times we need reactive code to do validation, such as : call mongo repository to check is email unique or not.
Validation module is module helper to support reactive code on validation layer.

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-validation</artifactId>
</dependency>
```

## Create ConstraintValidator

When using bean validation, if we want to create custom validator, we will implement `ConstraintValidator` interface.
Now we only need to implement interface `ReactiveConstraintValidator` or extend class `AbstractReactiveConstraintValidator`.

```java
public class MustValidValidator extends AbstractReactiveConstraintValidator<MustValid, String> {

   @Override
   public Mono<Boolean> validate(String value, MustValid annotation, ConstraintValidatorContext context) {
     return Mono.fromCallable(() -> "EKO".equals(value));
   }
 }
```