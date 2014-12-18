/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

public class RuleException extends Exception {

  public RuleException(String message) {
    super(message);
  }

  public RuleException(Exception e) {
    super(e);
  }
}
