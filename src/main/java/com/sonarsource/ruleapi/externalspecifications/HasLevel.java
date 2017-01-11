/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

/**
 * Relevant to Tool reports (specifically ReSharper) where
 * there's a need to indicate Tthe "level" the rule has in
 * the original tool.
 */
@FunctionalInterface
public interface HasLevel {
  String getLevel();
}
