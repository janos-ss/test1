/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

public interface CodingStandardRequirableRule extends CodingStandardRule {
  public boolean isRuleRequired();
}
