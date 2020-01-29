/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blibli.oss.backend.kafka;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author Eko Kurniawan Khannedy
 */
public class JsonTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testParsingJson() throws IOException {
    String json = "{\n" +
        "  \"span\": {\n" +
        "    \"key1\": \"value\",\n" +
        "    \"key2\": \"value\",\n" +
        "    \"key3\": \"value\",\n" +
        "    \"key4\": \"value\"\n" +
        "  }\n" +
        "}";


    JsonNode node = objectMapper.readTree(json);
    JsonNode span = node.get("span");
    JsonParser jsonParser = objectMapper.treeAsTokens(span);
    Map<String, String> map = objectMapper.readValue(jsonParser, new TypeReference<Map<String, String>>() {
    });

    Assert.assertEquals("value", span.get("key1").asText());
    Assert.assertEquals("value", map.get("key1"));
  }
}
