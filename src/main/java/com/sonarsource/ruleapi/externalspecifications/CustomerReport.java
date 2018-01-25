/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

/**
 * A user-facing report. Hence the expection of an HTML format ({@link #getHtmlReport(String)})
 */
public interface CustomerReport extends Standard{

  String getHtmlReport(String instance);

}
