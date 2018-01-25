/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * seralization/deserialization with support of ISO8601 format
 */
public class JsonUtil {

  private JsonUtil() {
    /* keep this constructor secret */
  }

  public static String toJson(Object object) {
    // convert to IOS8601 2011-12-03T10:15:30Z
    Gson gson = new GsonBuilder().disableHtmlEscaping()
      .registerTypeAdapter(Instant.class,
        (JsonSerializer<Instant>) (instant, type, context) -> new JsonPrimitive(instant.toString()))
      .registerTypeAdapter(Language.class,
        (JsonSerializer<Language>) (language, type, jsonDeserializationContext) -> new JsonPrimitive(language.toString()))
      .setPrettyPrinting()
      .create();

    return gson.toJson(object);
  }

  public static <T> T fromJson(String string, Class<? extends T> expectedClass) {

    // register specific deserializer for Instant
    Gson gson = new GsonBuilder()
      .disableHtmlEscaping()
      .registerTypeAdapter(Instant.class,
        (JsonDeserializer<Instant>) (json, type, jsonDeserializationContext) -> Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(json.getAsJsonPrimitive().getAsString())))
      .registerTypeAdapter(Language.class,
        (JsonDeserializer<Language>) (json, type, jsonDeserializationContext) -> Language.fromString(json.getAsJsonPrimitive().getAsString()))
      .create();

    return gson.fromJson(string, expectedClass);
  }

}
