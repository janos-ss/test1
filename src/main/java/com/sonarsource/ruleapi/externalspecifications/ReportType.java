/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;


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
