/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;


public class CodingStandardRuleCoverage {

  private String codingStandardRuleId = null;
  private Rule specifiedBy = null;
  private Rule implementedBy = null;

  public Rule getSpecifiedBy() {
    return specifiedBy;
  }

  public void setSpecifiedBy(Rule specifiedBy) {
    this.specifiedBy = specifiedBy;
  }

  public String getRule() {
    return codingStandardRuleId;
  }

  public void setRule(String rule) {
    this.codingStandardRuleId = rule;
  }

  public Rule getImplementedBy() {
    return implementedBy;
  }

  public void setImplementedBy(Rule implementedBy) {
    this.implementedBy = implementedBy;
  }

}
