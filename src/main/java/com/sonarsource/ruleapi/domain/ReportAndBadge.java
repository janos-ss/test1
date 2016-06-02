/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

public class ReportAndBadge {

  private String report;
  private String badge;

  public String getReport() {

    return report;
  }

  public void setReport(String report) {

    this.report = report;
  }

  public String getBadge() {

    return badge;
  }

  public void setBadge(String badge) {

    this.badge = badge;
  }
}
