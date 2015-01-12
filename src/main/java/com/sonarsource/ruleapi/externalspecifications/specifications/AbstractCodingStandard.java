/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import java.util.*;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.services.RuleManager;

import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.domain.RuleException;

public abstract class AbstractCodingStandard {

  public abstract String getReport() throws RuleException;

  public abstract String getSummaryReport() throws RuleException;

  public abstract String getStandardName();

  public abstract Language getLanguage();

  public abstract String getRSpecReferenceFieldName();

  public abstract List<String> getStandardIdsFromRSpecRule(Rule rule);

  public abstract void setStandardIdsInRSpecRule(Rule rule, List<String> ids);

  public abstract CodingStandardRule[] getCodingStandardRules();


  private Map<String, CodingStandardRuleCoverage> rulesCoverage = null;


  public List<Rule> getRSpecRulesReferencingStandard() throws RuleException {
    String query = "'" + getRSpecReferenceFieldName() + "' is not EMPTY";

    List<Rule> rules =  RuleMaker.getRulesByJql(query, getLanguage().getRspec());

    for (Rule rule: rules) {
      List <String> expandedIdList = getExpandedStandardKeyList(getStandardIdsFromRSpecRule(rule));
      setStandardIdsInRSpecRule(rule, expandedIdList);
    }

    return rules;
  }

  protected Map<String, CodingStandardRuleCoverage> getRulesCoverage(){
    return rulesCoverage;
  }

  protected void initCoverageResults() throws RuleException {
    if (rulesCoverage == null) {

      populateRulesCoverageMap();

      findSpecifiedInRspec(getRSpecRulesReferencingStandard());

      findImplementedByPlugin();
    }
  }

  protected void populateRulesCoverageMap() {

    rulesCoverage = new HashMap<String, CodingStandardRuleCoverage>();

    for (CodingStandardRule rule : getCodingStandardRules()) {
      CodingStandardRuleCoverage cov = new CodingStandardRuleCoverage();
      cov.setRule(rule.getCodingStandardRuleId());
      rulesCoverage.put(rule.getCodingStandardRuleId(), cov);
    }
  }

  public List<String> getExpandedStandardKeyList(List<String> listFromRspec) {

    if (listFromRspec == null){
      return listFromRspec;
    }

    List<String> expandedKeyList = new ArrayList<String>();

    for (String key : listFromRspec) {
      if (!key.matches(".*[*+?]+.*")) {
        expandedKeyList.add(key);
        continue;
      }

      for (CodingStandardRule standard : getCodingStandardRules()) {
        if (standard.getCodingStandardRuleId().matches(key)) {
          expandedKeyList.add(standard.getCodingStandardRuleId());
        }
      }
    }

    return expandedKeyList;
  }

  protected void findSpecifiedInRspec(List<Rule> rspecRules) throws RuleException {

    for (Rule rspecRule : rspecRules) {
      List<String> ids = getStandardIdsFromRSpecRule(rspecRule);
      setCodingStandardRuleCoverageSpecifiedBy(rspecRule, ids);
    }
  }

  protected void setCodingStandardRuleCoverageSpecifiedBy(Rule rspecRule, List<String> ids) {

    if (ids != null && ! ids.isEmpty()) {
      for (String id : ids) {
        CodingStandardRuleCoverage cov = getRulesCoverage().get(id);
        if (cov != null) {
          cov.setSpecifiedBy(rspecRule);
        }
      }
    }
  }

  protected void findImplementedByPlugin() throws RuleException {

    List<Rule> sqImplemented = RuleMaker.getRulesFromSonarQubeForLanguage(getLanguage(), RuleManager.NEMO);

    for (Rule sqRule : sqImplemented) {
      String key = sqRule.getKey();

      Rule rspecRule = RuleMaker.getRuleByKey(key, getLanguage().getSq());
      List<String> ids = getExpandedStandardKeyList(getStandardIdsFromRSpecRule(rspecRule));

      setCodingStandardRuleCoverageImplemented(ids, sqRule);
    }
  }

  protected void setCodingStandardRuleCoverageImplemented(List<String> ids, Rule rule) {

    if (ids != null && ! ids.isEmpty()) {
      for (String id : ids) {
        CodingStandardRuleCoverage cov = getRulesCoverage().get(id);
        if (cov != null) {
          cov.setImplementedBy(rule);
        }
      }
    }
  }

}
