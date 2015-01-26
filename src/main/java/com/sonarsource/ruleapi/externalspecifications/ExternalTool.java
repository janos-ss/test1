/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.RuleException;

public interface ExternalTool {

  String getDeprecationReport(String instance) throws RuleException;

}
