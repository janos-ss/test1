/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;


public class CodingStandardRuleCoverage {

  private CodingStandardRule rule;
  private Rule specifiedBy;
  private Rule coveredBy;
  private boolean implementedInPlugin = Boolean.FALSE;

  public Rule getSpecifiedBy() {
    return specifiedBy;
  }

  public void setSpecifiedBy(Rule specifiedBy) {
    this.specifiedBy = specifiedBy;
  }

  public Boolean isCovered() {
    return coveredBy != null;
  }

  public boolean isImplemented() {
    return implementedInPlugin;
  }

  public void setImplemented(boolean implemented) {
    this.implementedInPlugin = implemented;
  }

  public CodingStandardRule getRule() {
    return rule;
  }

  public void setRule(CodingStandardRule rule) {
    this.rule = rule;
  }

  public Rule getCoveredBy() {
    return coveredBy;
  }

  public void setCoveredBy(Rule coveredBy) {
    this.coveredBy = coveredBy;
  }

}
