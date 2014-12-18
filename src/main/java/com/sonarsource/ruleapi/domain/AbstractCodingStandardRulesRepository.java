/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sonarsource.ruleapi.utilities.RuleException;

public abstract class AbstractCodingStandardRulesRepository {

  public abstract List<CodingStandardRule> getCodingStandardRules();

  public abstract List<Rule> getRSpectRulesCoveringLanguage() throws RuleException;

  public abstract List<Rule> getRSpectRules() throws RuleException;

  public abstract List<String> getFieldFromRSpecRule(Rule rule);

  public boolean isRuleKeyInCodingStandardRules(String ruleKey) {
    for (CodingStandardRule rule : getCodingStandardRules()) {
      if (StringUtils.equals(rule.getKey(), ruleKey)) {
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;
  }

  public boolean isRuleMandatory(String ruleKey) {
    for (CodingStandardRule rule : getCodingStandardRules()) {
      if (StringUtils.equals(rule.getKey(), ruleKey) && rule.isMandatory()) {
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;
  }

  public boolean isCanBeCoveredByStaticAnalysis(String ruleKey) {
    for (CodingStandardRule rule : getCodingStandardRules()) {
      if (StringUtils.equals(rule.getKey(), ruleKey) && rule.isCanBeCoveredByStaticAnalysis()) {
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;
  }
}
