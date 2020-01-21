package com.blibli.oss.backend.version.controller;

import com.blibli.oss.backend.version.properties.VersionProperties;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class VersionController implements InitializingBean {

  @Setter
  private VersionProperties properties;

  private String maven;

  @Override
  public void afterPropertiesSet() throws Exception {
    maven = "maven.groupId=" + properties.getGroupId() + "\n" +
      "maven.artifactId=" + properties.getArtifactId() + "\n" +
      "maven.pom.version=" + properties.getVersion() + "\n" +
      "maven.build.time=" + properties.getBuildTime() + "\n";
  }

  @RequestMapping(
    value = "/version",
    produces = MediaType.TEXT_PLAIN_VALUE
  )
  public Mono<String> version() {
    return Mono.fromCallable(() -> maven);
  }
}
