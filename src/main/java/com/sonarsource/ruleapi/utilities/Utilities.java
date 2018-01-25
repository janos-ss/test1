/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Rule;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    String ret = key;
    String[] pieces = key.split(":");
    if (pieces.length > 1) {
      ret = pieces[1];
    }
    return ret.replaceAll("^S0*(\\d+)$", "RSPEC-$1");
  }

  public static String denormalizeKey(String key) {

    return key.replaceAll("RSPEC-(\\d+)", "S$1");
  }

  public static boolean isKeyNormal(String key) {
    return key != null && key.matches("RSPEC-\\d+");
  }

  public static String setToString(Set<String> set, boolean doCommas) {

    List<String> list = new ArrayList<>(set);
    java.util.Collections.sort(list);

    return listToString(list, doCommas);
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

  public static String getJiraLinkedRuleReference(Rule rule) {

    String ruleKey = normalizeKey(rule.getKey());

    StringBuilder sb = new StringBuilder();

    sb.append("<a href='https://jira.sonarsource.com/browse/").append(ruleKey).append("'>")
            .append(ruleKey).append("</a> ")
            .append(MarkdownConverter.handleEntities(rule.getTitle())).append("<br/>\n");
    return sb.toString();

  }

  public static String getNemoLinkedRuleReference(String instance, Rule rule) {

    StringBuilder sb = new StringBuilder();
    // https://sonarqube.com/coding_rules#rule_key=squid%3AS2066
    sb.append(getInstanceLinkedRuleKey(instance, rule, false))
            .append(" ")
            .append(MarkdownConverter.handleEntities(rule.getTitle()))
            .append("<br/>\n");
    return sb.toString();
  }

  public static String getInstanceLinkedRuleKey(String instance, Rule rule, boolean abbreviateLongKeys) {

    int maxAbbreviatedKeyLength = 8;
    String ruleKey = getDeployedKey(rule);
    String displayKey = ruleKey;

    if (abbreviateLongKeys && displayKey != null && displayKey.length() + 1 > maxAbbreviatedKeyLength) {
      displayKey = displayKey.substring(0, maxAbbreviatedKeyLength) + ".";
    }

    StringBuilder sb = new StringBuilder();
    // https://sonarqube.com/coding_rules#rule_key=squid%3AS2066
    sb.append("<a href='").append(instance).append("/coding_rules#rule_key=")
            .append(rule.getRepo()).append("%3A").append(ruleKey).append("' target='rule'>")
            .append(displayKey).append("</a>");
    return sb.toString();
  }

  public static String getDeployedKey(Rule rule) {
    String ruleKey = denormalizeRuleKey(rule.getKey());
    if (rule.getLegacyKeys() != null && ! rule.getLegacyKeys().isEmpty()) {
      ruleKey = rule.getLegacyKeys().get(0);
    }
    return ruleKey;
  }

  public static String getFormattedDateString(){
    LocalDateTime currentTime = LocalDateTime.now();
    return currentTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
  }

  public static File assertBaseDir(String baseDir) {
    if (baseDir == null) {
      throw new IllegalArgumentException("directory is required");
    } else {
      File baseDirFile = new File(baseDir);
      if (!baseDirFile.isDirectory()) {
        throw new IllegalArgumentException("directory does not exist");
      }
      return baseDirFile;
    }
  }
}
