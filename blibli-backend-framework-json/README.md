# JSON Module

JSON Module is module to help simplify json library usage. In Spring, by default it use Jackson as json library.
The problem with Jackson is, most of jackson operation is checked exception, so we need to add try catch everywhere.
JSON module will simplify this, this can be used to help us do json manipulation, like transform from json to object or vice versa.

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-json</artifactId>
</dependency>
```

## Automatic Jackson Configuration

This module by default will configure some configuration. So we don't need to configure it manually on application properties.

```properties
spring.jackson.deserialization.fail-on-unknown-properties=false
spring.jackson.deserialization.fail-on-ignored-properties=false
spring.jackson.deserialization.read-unknown-enum-values-as-null=true
spring.jackson.serialization.fail-on-empty-beans=false
spring.jackson.serialization.write-dates-as-timestamps=true
spring.jackson.serialization.write-empty-json-arrays=true
spring.jackson.default-property-inclusion=non_null
spring.jackson.generator.ignore-unknown=true
spring.jackson.mapper.accept-case-insensitive-enums=true
```

## JsonHelper Class

We don't need to use ObjectMapper anymore to manipulate json data. We can use JsonHelper class.

```java

@Autowired
private JsonHelper jsonHelper;

YourModel model = jsonHelper.fromJson(stringJson, YourModel.class);
Map<String, Object> map = jsonHelper.fromJson(stringJson, new TypeReference<Map<String, Object>>(){});

String json = jsonHelper.toJson(model);
```

And we don't need to add try-catch manually anymore.