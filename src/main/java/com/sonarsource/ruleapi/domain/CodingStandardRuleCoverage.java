/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import com.sonarsource.ruleapi.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;


public class CodingStandardRuleCoverage {

  private String codingStandardRuleId = null;
  private List<Rule> specifiedBy = new ArrayList<>();
  private List<Rule> implementedBy = new ArrayList<>();

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

    List<String> ids = new ArrayList<>(rules.size());
    for (Rule rule : rules) {
      if (! rule.getLegacyKeys().isEmpty()) {
        ids.addAll(rule.getLegacyKeys());
      } else {
        ids.add(rule.getKey());
      }
    }
    return Utilities.listToString(ids, true);

  }
}
