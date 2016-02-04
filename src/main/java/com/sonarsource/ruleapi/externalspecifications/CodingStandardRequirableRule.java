/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;


/**
 * Provides the ability to distinguish between optional and required rules.
 * See {@link AbstractMisraSpecification}
 *
 * Can/should be combined with {@link HasLevel}?
 */
public interface CodingStandardRequirableRule extends CodingStandardRule {
  boolean isRuleRequired();
}
