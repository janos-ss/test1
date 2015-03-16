/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import java.util.*;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandard;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.get.RuleMaker;

import com.sonarsource.ruleapi.utilities.ComparisonUtilities;
import com.sonarsource.ruleapi.utilities.Language;
import org.fest.util.Strings;

public abstract class AbstractReportableStandard implements CodingStandard {

  public abstract String getReport(String instance);

  public abstract String getSummaryReport(String instance);

  public abstract Language getLanguage();

  public abstract CodingStandardRule[] getCodingStandardRules();


  private Map<String, CodingStandardRuleCoverage> rulesCoverage = null;
  private String lastInstance = null;


  public List<Rule> getRSpecRulesReferencingStandard() {
    String query = "'" + getRSpecReferenceFieldName() + "' is not EMPTY";

    Language language = getLanguage();
    String rspecLanguage = "";
    if (language != null) {
      rspecLanguage = language.getRspec();
    }
    List<Rule> rules = RuleMaker.getRulesByJql(query, rspecLanguage);

    for (Rule rule: rules) {
      List <String> expandedIdList = getExpandedStandardKeyList(getRspecReferenceFieldValues(rule));
      setRspecReferenceFieldValues(rule, expandedIdList);
    }

    return rules;
  }

  protected Map<String, CodingStandardRuleCoverage> getRulesCoverage(){
    return rulesCoverage;
  }

  protected void initCoverageResults(String instance) {

    if (rulesCoverage == null) {

      this.lastInstance = instance;

      populateRulesCoverageMap();
      findSpecifiedInRspec(getRSpecRulesReferencingStandard());
      findImplementedByPlugin(instance);

    } else if (!(Strings.isNullOrEmpty(instance) || instance.equals(this.lastInstance))) {

      this.lastInstance = instance;

      cleanRulesCoverageMap();
      findImplementedByPlugin(instance);

    }
  }

  protected void populateRulesCoverageMap() {

    rulesCoverage = new HashMap<String, CodingStandardRuleCoverage>();

    for (CodingStandardRule csr : getCodingStandardRules()) {
      CodingStandardRuleCoverage cov = new CodingStandardRuleCoverage();
      cov.setCodingStandardRuleId(csr.getCodingStandardRuleId());
      rulesCoverage.put(csr.getCodingStandardRuleId(), cov);
    }
  }

  protected void cleanRulesCoverageMap() {

    for (CodingStandardRuleCoverage csrc : rulesCoverage.values()) {
      csrc.getImplementedBy().clear();
    }
  }

  protected void resetRulesCoverageMap() {
    rulesCoverage = null;
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
          cov.addSpecifiedBy(rspecRule);
        }
      }
    }
  }

  protected void findImplementedByPlugin(String instance) {

    if (instance != null) {

      Language language = getLanguage();
      String sq = "";

      List<Rule> sqImplemented = null;
      if (language != null) {
        sq = language.getSq();

        sqImplemented = RuleMaker.getRulesFromSonarQubeForLanguage(getLanguage(), instance);

      } else {
        sqImplemented = RuleMaker.getRulesFromSonarQubeByQuery(instance, "repositories=" + getSqRepoList(),null);
      }

      for (Rule sqRule : sqImplemented) {
        String key = sqRule.getKey();

        Rule rspecRule = RuleMaker.getRuleByKey(key, sq);
        List<String> ids = getExpandedStandardKeyList(getRspecReferenceFieldValues(rspecRule));

        setCodingStandardRuleCoverageImplemented(ids, sqRule);
      }
    }
  }

  protected String getSqRepoList() {

    List<String> repos = new ArrayList<String>();
    for (Language language : Language.values()) {
      repos.add(language.getSq());
    }
    String tmp = ComparisonUtilities.listToString(repos, true);
    return tmp.replaceAll(" ", "");
  }

  protected void setCodingStandardRuleCoverageImplemented(List<String> ids, Rule rule) {

    if (ids != null && ! ids.isEmpty()) {
      for (String id : ids) {
        CodingStandardRuleCoverage cov = getRulesCoverage().get(id);
        if (cov != null) {
          cov.addImplementedBy(rule);
        }
      }
    }
  }

  protected String denormalizeRuleKey(String ruleKey) {
    if (RuleMaker.isKeyNormal(ruleKey)) {
      return ruleKey.replace("RSPEC-", "S");
    }
    return ruleKey;
  }

  protected String getLinkedRuleReference(String instance, Rule rule) {

    String ruleKey = denormalizeRuleKey(rule.getKey());

    StringBuilder sb = new StringBuilder();
    // http://nemo.sonarqube.org/coding_rules#rule_key=squid%3AS2066
    sb.append("<a href='").append(instance).append("/coding_rules#rule_key=")
            .append(getLanguage().getSq()).append("%3A").append(ruleKey).append("'>")
            .append(ruleKey).append("</a> ")
            .append(rule.getTitle()).append("<br/>\n");
    return sb.toString();
  }

}
