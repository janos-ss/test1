/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

/**
 * Describes whether or not we're interested in covering
 * any random rule from a standard.
 */
public enum Implementability {
  IMPLEMENTABLE, NOT_IMPLEMENTABLE, REJECTED;
}
