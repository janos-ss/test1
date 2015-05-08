/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

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
}
