package com.blibli.oss.backend.newrelic.aspect.service.util;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MongoUriParser {

  //    mongodb://[username:password@]host1[:port1][,...hostN[:portN]][/[defaultauthdb][?options]]
  private static final String MONGO_CREDENTIALS_PATTERN = "([^:]+:[^@]+@)?";
  private static final String MONGO_HOST_PORT_PATTERN = "[^@:,/]+(:[\\d]+)?";
  private static final String MONGO_HOST_PORT_OTHERS_PATTERN = "(,[^@:,/]+(:[\\d]+)?)*";
  private static final String MONGO_DB_OPTIONS_PATTERN = "(/.*)?";

  private static final Pattern MONGO_URI_PATTERN = Pattern.compile(String.format("^mongodb://%s(%s%s)%s$",
    MONGO_CREDENTIALS_PATTERN, MONGO_HOST_PORT_PATTERN, MONGO_HOST_PORT_OTHERS_PATTERN, MONGO_DB_OPTIONS_PATTERN));

  public static String[] getHosts(String mongoUri) {
    Matcher m = MONGO_URI_PATTERN.matcher(mongoUri);

    if (m.find()) {
      return Optional.ofNullable(m.group(2))
        .map(hosts -> hosts.split(","))
        .orElseThrow(() -> new RuntimeException("unsupported mongo uri"));
    } else {
      throw new RuntimeException("unsupported mongo uri");
    }
  }


}
