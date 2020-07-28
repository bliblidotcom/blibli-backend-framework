package com.blibli.oss.backend.newrelic.aspect.service.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MongoUriParserTest {

  @Test
  @DisplayName("getHosts from host only")
  public void getHostsTest() {
    String[] mongoHosts = MongoUriParser.getHosts("mongodb://mongo0.exmpl.com");

    assertEquals(1, mongoHosts.length);
    assertEquals("mongo0.exmpl.com", mongoHosts[0]);
  }

  @Test
  @DisplayName("getHosts from multiple host only")
  public void getHostsTest2() {
    String[] mongoHosts = MongoUriParser.getHosts("mongodb://mongo0.exmpl.com,mongo1.exmpl.com");

    assertEquals(2, mongoHosts.length);
    assertEquals("mongo0.exmpl.com", mongoHosts[0]);
    assertEquals("mongo1.exmpl.com", mongoHosts[1]);
  }

  @Test
  @DisplayName("getHosts from host and port")
  public void getHostsTest3() {
    String[] mongoHosts = MongoUriParser.getHosts("mongodb://mongo0.exmpl.com:27017");

    assertEquals(1, mongoHosts.length);
    assertEquals("mongo0.exmpl.com:27017", mongoHosts[0]);
  }

  @Test
  @DisplayName("getHosts from multiple host and port")
  public void getHostsTest4() {
    String[] mongoHosts = MongoUriParser.getHosts("mongodb://mongo0.exmpl.com:27017,mongo1.exmpl.com:27017");

    assertEquals(2, mongoHosts.length);
    assertEquals("mongo0.exmpl.com:27017", mongoHosts[0]);
    assertEquals("mongo1.exmpl.com:27017", mongoHosts[1]);
  }

  @Test
  @DisplayName("getHosts from username, pass, host, port")
  public void getHostsTest5() {
    String[] mongoHosts = MongoUriParser.getHosts("mongodb://us3r:P%40ssw0rd@mongo0.exmpl.com:27017");

    assertEquals(1, mongoHosts.length);
    assertEquals("mongo0.exmpl.com:27017", mongoHosts[0]);
  }

  @Test
  @DisplayName("getHosts from username, pass, host, port, dbname")
  public void getHostsTest6() {
    String[] mongoHosts = MongoUriParser.getHosts("mongodb://us3r:P%40ssw0rd@mongo0.exmpl.com:27017/dbName");

    assertEquals(1, mongoHosts.length);
    assertEquals("mongo0.exmpl.com:27017", mongoHosts[0]);
  }

  @Test
  @DisplayName("getHosts from username, pass, host, port, dbname, options")
  public void getHostsTest7() {
    String[] mongoHosts = MongoUriParser.getHosts("mongodb://us3r:P%40ssw0rd@mongo0.exmpl.com:27017/dbName?authSource=admin&authSource2=admin2");

    assertEquals(1, mongoHosts.length);
    assertEquals("mongo0.exmpl.com:27017", mongoHosts[0]);
  }

  @Test
  @DisplayName("getHosts from username, pass, multiple host + port, dbname, options")
  public void getHostsTest8() {
    String[] mongoHosts =
      MongoUriParser.getHosts(
        "mongodb://us3r:P%40ssw0rd@mongo0.exmpl.com:27017,mongo1.exmpl.com:27017/dbName?authSource=admin&authSource2=admin2");

    assertEquals(2, mongoHosts.length);
    assertEquals("mongo0.exmpl.com:27017", mongoHosts[0]);
    assertEquals("mongo1.exmpl.com:27017", mongoHosts[1]);
  }

  @Test
  public void getHostsUnsupportedUri() {
    assertThrows(
      RuntimeException.class,
      () -> MongoUriParser.getHosts("mongodb://us3r:P@ssw0rd@mongo0.exmpl.com:27017/dbName")
    );
  }
}
