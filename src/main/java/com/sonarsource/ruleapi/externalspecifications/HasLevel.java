/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

/**
 * Relevant to Tool reports (specifically ReSharper) where
 * there's a need to indicate teh "level" the rule has in
 * the original tool.
 */
public interface HasLevel {
  String getLevel();
}
