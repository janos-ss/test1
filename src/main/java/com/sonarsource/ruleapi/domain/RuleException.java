/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

public class RuleException extends RuntimeException {

  public RuleException(String message) {
    super(message);
  }

  public RuleException(Exception e) {
    super(e);
  }
}
