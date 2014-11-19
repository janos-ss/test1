/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

public class FetchException extends Exception {
  public FetchException (String message, Throwable cause) {
    super  (message, cause);
  }

  public FetchException (String message) {

    super(message);
  }
}
