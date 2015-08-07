/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Rule;

import java.util.List;

public class Utilities {

  private Utilities() {
    // private constructor
  }

  public static int findBefore(String line, int start, String str) {
    for (int i = start - 1; i >= 0; i--) {
      if (line.substring(i).startsWith(str)) {
        return i;
      }
    }

    return -1;
  }

  public static int findBefore(String line, int start, char ch) {
    for (int i = start - 1; i >= 0; i--) {
      if (line.charAt(i) == ch) {
        return i;
      }
    }
    return -1;
  }

  public static String normalizeKey(String key) {

    return key.replaceAll("^(.+:)?S0*(\\d+)$", "RSPEC-$2");
  }

  public static String denormalizeKey(String key) {

    return key.replaceAll("RSPEC-(\\d+)", "S$1");
  }

  public static boolean isKeyNormal(String key) {
    return key != null && key.matches("RSPEC-\\d+");
  }

  public static String listToString(List<String> list, boolean doCommas) {

    StringBuilder sb = new StringBuilder();
    for (String str : list) {
      if (sb.length() > 0) {
        if (doCommas) {
          sb.append(",");
        }
        sb.append(" ");

      }
      sb.append(str);
    }
    return sb.toString();
  }

  public static String denormalizeRuleKey(String ruleKey) {
    if (Utilities.isKeyNormal(ruleKey)) {
      return ruleKey.replace("RSPEC-", "S");
    }
    return ruleKey;
  }

  public static String getLinkedRuleReference(String instance, Rule rule) {

    String ruleKey = denormalizeRuleKey(rule.getKey());
    if (rule.getLegacyKeys() != null && ! rule.getLegacyKeys().isEmpty()) {
      ruleKey = rule.getLegacyKeys().get(0);
    }

    StringBuilder sb = new StringBuilder();
    // http://nemo.sonarqube.org/coding_rules#rule_key=squid%3AS2066
    sb.append("<a href='").append(instance).append("/coding_rules#rule_key=")
            .append(rule.getRepo()).append("%3A").append(ruleKey).append("'>")
            .append(ruleKey).append("</a> ")
            .append(MarkdownConverter.handleEntities(rule.getTitle())).append("<br/>\n");
    return sb.toString();
  }
}
