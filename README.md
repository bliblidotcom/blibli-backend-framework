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

<properties>
    <!-- Set blibli framework version -->
    <blibli-framework.version>...</blibli-framework.version>
</properties>

```

## List of Libraries 

- [Common](blibli-backend-framework-common/README.md) : standard request and response
- [Mandatory Parameter](blibli-backend-framework-mandatory-parameter/README.md) : mandatory parameter for backend to backend communication
- [Version](blibli-backend-framework-version/README.md) : standard application version information
- [API Client](blibli-backend-framework-api-client/README.md) : non blocking declarative restful api client
- [Reactor](blibli-backend-framework-reactor/README.md) : simplify project reactor usage
- [Swagger](blibli-backend-framework-swagger/README.md) : open api and swagger generator 
- [Sleuth](blibli-backend-framework-sleuth/README.md) : simplify spring sleuth usage
- [Kafka](blibli-backend-framework-kafka/README.md) : simplify spring kafka usage
- [Command](blibli-backend-framework-command/README.md) : command pattern implementation
- [Aggregate Query](blibli-backend-framework-aggregate-query/README.md) : api client for aggregate query 
- [Scheduler Platform](blibli-backend-framework-scheduler-platform/README.md) : api client for scheduler platform   
