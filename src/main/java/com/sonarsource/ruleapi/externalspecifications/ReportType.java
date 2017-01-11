/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;


/**
 * This enum exists primarily to provide the user the ability
 * to generate any random report from Main.
 */
public enum ReportType {
  INTERNAL_COVERAGE(true),
  INTERNAL_COVERAGE_SUMMARY(true),
  HTML(false),
  DEPRECATION(true),
  UNSPECIFIED(true);

  private boolean internal = true;

  ReportType(boolean internal) {
    this.internal = internal;
  }

  public boolean isInternal() {
    return internal;
  }

  public static ReportType fromString(String name) {

    for (ReportType rt : ReportType.values()) {
      if (rt.name().equalsIgnoreCase(name)) {
        return rt;
      }
    }
    return null;
  }

}
