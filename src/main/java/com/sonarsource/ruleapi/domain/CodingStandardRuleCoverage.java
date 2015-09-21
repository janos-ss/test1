/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import com.sonarsource.ruleapi.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;


public class CodingStandardRuleCoverage {

  private String codingStandardRuleId = null;
  private List<Rule> specifiedBy = new ArrayList<Rule>();
  private List<Rule> implementedBy = new ArrayList<Rule>();

  public List<Rule> getSpecifiedBy() {
    return specifiedBy;
  }

  public String getSpecifiedByKeysAsCommaList() {

    return getRuleKeysAsString(specifiedBy);
  }

  public void addSpecifiedBy(Rule rule) {

    this.specifiedBy.add(rule);
  }

  public String getCodingStandardRuleId() {
    return codingStandardRuleId;
  }

  public void setCodingStandardRuleId(String rule) {
    this.codingStandardRuleId = rule;
  }

  public List<Rule> getImplementedBy() {
    return implementedBy;
  }

  public String getImplementedByKeysAsCommaList() {

    return getRuleKeysAsString(implementedBy);
  }

  public void addImplementedBy(Rule rule) {

    implementedBy.add(rule);
  }

  protected String getRuleKeysAsString(List<Rule> rules) {

    List<String> ids = new ArrayList<String>(rules.size());
    for (Rule rule : rules) {
      if (rule.getLegacyKeys() != null) {
        ids.addAll(rule.getLegacyKeys());
      }
    }
    return Utilities.listToString(ids, true);

  }
}
