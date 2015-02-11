/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import com.google.common.base.Strings;
import com.sonarsource.ruleapi.utilities.ComparisonUtilities;

public class Parameter implements Comparable<Parameter> {
  private String key;
  private String description = "";
  private String defaultVal = "";
  private String type;

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

  public String getType() {
    return type;
  }

  public void setType(String type) {

    this.type = type;
  }

  @Override
  public int compareTo(Parameter parameter) {

    int result = key.compareTo(parameter.getKey());
    if (result != 0) {
      return result;
    }
    result = ComparisonUtilities.compareTextFunctionalEquivalence(description, parameter.getDescription());
    if (result != 0) {
      return result;
    }
    return ComparisonUtilities.compareStrings(defaultVal, parameter.getDefaultVal());
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("* key = ").append(key);
    if (!Strings.isNullOrEmpty(description)) {
      sb.append("\n* description = ").append(description);
    }
    if (!Strings.isNullOrEmpty(defaultVal)) {
      sb.append("\n* default = ").append(defaultVal);
    }
    if (!Strings.isNullOrEmpty(type)) {
      sb.append("\n* type = ").append(type);
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Parameter parameter = (Parameter) o;

    if (defaultVal != null ? !defaultVal.equals(parameter.defaultVal) : parameter.defaultVal != null) {
      return false;
    }
    if (!ComparisonUtilities.isTextFunctionallyEquivalent(description, parameter.description)) {
      return false;
    }
    if (!key.equals(parameter.key)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {

    int result = key.hashCode();
    result = 31 * result + description.hashCode();
    result = 31 * result + (defaultVal != null ? defaultVal.hashCode() : 0);
    return result;
  }
}
