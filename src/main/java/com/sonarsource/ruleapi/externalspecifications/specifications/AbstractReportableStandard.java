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

import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.domain.RuleException;
import org.fest.util.Strings;

public abstract class AbstractReportableStandard implements CodingStandard {

  public abstract String getReport() throws RuleException;

  public abstract String getReport(String instance) throws RuleException;

  public abstract String getSummaryReport() throws RuleException;

  public abstract String getSummaryReport(String instance) throws RuleException;

  public abstract Language getLanguage();

  public abstract CodingStandardRule[] getCodingStandardRules();


  private Map<String, CodingStandardRuleCoverage> rulesCoverage = null;
  private String lastInstance = null;


  public List<Rule> getRSpecRulesReferencingStandard() throws RuleException {
    String query = "'" + getRSpecReferenceFieldName() + "' is not EMPTY";

    List<Rule> rules =  RuleMaker.getRulesByJql(query, getLanguage().getRspec());

    for (Rule rule: rules) {
      List <String> expandedIdList = getExpandedStandardKeyList(getRspecReferenceFieldValues(rule));
      setRspecReferenceFieldValues(rule, expandedIdList);
    }

    return rules;
  }

  protected Map<String, CodingStandardRuleCoverage> getRulesCoverage(){
    return rulesCoverage;
  }

  protected void initCoverageResults(String instance) throws RuleException {
    if (rulesCoverage == null || !(Strings.isNullOrEmpty(instance) || instance.equals(this.lastInstance))) {
      this.lastInstance = instance;

      populateRulesCoverageMap();

      findSpecifiedInRspec(getRSpecRulesReferencingStandard());

      findImplementedByPlugin(instance);
    }
  }

  protected void populateRulesCoverageMap() {

    rulesCoverage = new HashMap<String, CodingStandardRuleCoverage>();

    for (CodingStandardRule csr : getCodingStandardRules()) {
      CodingStandardRuleCoverage cov = new CodingStandardRuleCoverage();
      cov.setRule(csr.getCodingStandardRuleId());
      rulesCoverage.put(csr.getCodingStandardRuleId(), cov);
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

  protected void findSpecifiedInRspec(List<Rule> rspecRules) {

    for (Rule rspecRule : rspecRules) {
      List<String> ids = getRspecReferenceFieldValues(rspecRule);
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

  protected void findImplementedByPlugin(String instance) throws RuleException {

    if (instance != null) {

      List<Rule> sqImplemented = RuleMaker.getRulesFromSonarQubeForLanguage(getLanguage(), instance);

      for (Rule sqRule : sqImplemented) {
        String key = sqRule.getKey();

        Rule rspecRule = RuleMaker.getRuleByKey(key, getLanguage().getSq());
        List<String> ids = getExpandedStandardKeyList(getRspecReferenceFieldValues(rspecRule));

        setCodingStandardRuleCoverageImplemented(ids, sqRule);
      }
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
