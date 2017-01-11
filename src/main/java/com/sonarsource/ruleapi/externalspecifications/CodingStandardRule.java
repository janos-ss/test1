/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;


/**
 * A single rule from a standard.
 */
public interface CodingStandardRule {
  String getCodingStandardRuleId();
  Implementability getImplementability();
}
