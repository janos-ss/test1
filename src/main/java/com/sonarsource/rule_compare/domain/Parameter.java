/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.rule_compare.domain;

/**
 * Created by ganncamp on 9/19/14.
 */
public class Parameter {
  private String key;
  private String description;
  private String defaultVal;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDefaultVal() {
    return defaultVal;
  }

  public void setDefaultVal(String defaultVal) {
    this.defaultVal = defaultVal;
  }
}
