# Version Module

Version module is library to generate standard version information on Blibli.com backend application.

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-version</artifactId>
</dependency>
```

## Setup Properties

Version Module will read `version.properties` on resources packages. And we can use maven resource plugin to replace value on `version.properties` file.

```properties
blibli.backend.version.group-id=@project.groupId@
blibli.backend.version.artifact-id=@project.artifactId@
blibli.backend.version.version=@project.version@
blibli.backend.version.build-time=@maven.build.timestamp@
```

And on the `pom.xml`, we can setup configuration

```xml
<properties>
    <maven.build.timestamp.format>yyyy-MM-dd HH:mm:ss</maven.build.timestamp.format>
</properties>

<build>
    <resources>
      <resource>
        <directory>src/main/resources</directory>
        <filtering>true</filtering>
      </resource>
    </resources>
</build>
```

## Version Controller

This module will automatically create controller with route `/version`.

```
GET /version

maven.groupId=com.blibli.oss.backend.example
maven.artifactId=example-app
maven.pom.version=0.0.1-SNAPSHOT
maven.build.time=2020-02-20 07:38:01
```
