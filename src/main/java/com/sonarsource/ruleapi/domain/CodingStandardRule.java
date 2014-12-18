/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

public class CodingStandardRule {

  private String key;
  private boolean isMandatory = Boolean.TRUE;
  private boolean canBeCoveredByStaticAnalysis = Boolean.TRUE;

  public CodingStandardRule(Builder builder) {
    this.key = builder.key;
    this.isMandatory = builder.isMandatory;
    this.canBeCoveredByStaticAnalysis = builder.canBeCoveredByStaticAnalysis;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public boolean isMandatory() {
    return isMandatory;
  }

  public void setMandatory(boolean isMandatory) {
    this.isMandatory = isMandatory;
  }

  public boolean isCanBeCoveredByStaticAnalysis() {
    return canBeCoveredByStaticAnalysis;
  }

  public void setCanBeCoveredByStaticAnalysis(boolean canBeCoveredByStaticAnalysis) {
    this.canBeCoveredByStaticAnalysis = canBeCoveredByStaticAnalysis;
  }

  public static class Builder {

    private String key;
    private String description;
    private boolean isMandatory;
    private boolean canBeCoveredByStaticAnalysis;

    public Builder(String key) {
      this.key = key;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder isMandatory(boolean isMandatory) {
      this.isMandatory = isMandatory;
      return this;
    }

    public Builder canBeCoveredByStaticAnalysis(boolean canBeCoveredByStaticAnalysis) {
      this.canBeCoveredByStaticAnalysis = canBeCoveredByStaticAnalysis;
      return this;
    }

    public CodingStandardRule build() {
      return new CodingStandardRule(this);
    }

  }
}
