/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

public interface CustomerReport {

  String getHtmlReport(String instance);

  String getStandardName();

}
