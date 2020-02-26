# Blibli Backend Framework

Blibli Backend Framework is various spring boot 2 libraries for Blibli.com backend projects.

## Setup

To use blibli backend framework, we need to setup our pom.xml

```xml
<!-- Set parent project -->
<parent>
    <groupId>com.blibli.oss</groupId>
    <artifactId>blibli-backend-framework</artifactId>
    <version>...</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>

<!-- Set blibli framework version -->
<properties>
    <blibli-framework.version>...</blibli-framework.version>
</properties>

<!-- add blibli bintray repository --> 
<repositories>
    <repository>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
      <id>bintray-bliblidotcom-maven</id>
      <name>bintray</name>
      <url>https://dl.bintray.com/bliblidotcom/maven</url>
    </repository>
</repositories>
```

## List of Libraries 

- [Common](blibli-backend-framework-common/README.md) : standard request and response
- [Mandatory Parameter](blibli-backend-framework-mandatory-parameter/README.md) : mandatory parameter for backend to backend communication
- [Version](blibli-backend-framework-version/README.md) : standard application version information
- [Swagger](blibli-backend-framework-swagger/README.md) : open api and swagger generator
- [Reactor](blibli-backend-framework-reactor/README.md) : simplify project reactor usage 
- [API Client](blibli-backend-framework-api-client/README.md) : non blocking declarative restful api client
- [Kafka](blibli-backend-framework-kafka/README.md) : simplify spring kafka usage
- [Command](blibli-backend-framework-command/README.md) : command pattern implementation
- [Aggregate Query](blibli-backend-framework-aggregate-query/README.md) : api client for aggregate query 
- [Scheduler Platform](blibli-backend-framework-scheduler-platform/README.md) : api client for scheduler platform   

## Next Libraries

- JSON: simplify json manipulation usage
- Validation : reactive bean validation
- Internal API : helper for internal api app
- External API : helper for external api app
- Distributed Trace : simplify distributed tracing usage
- Zipkin : simplify zipkin usage
- New Relic : simplify new relic usage
- Log : api client for log platform