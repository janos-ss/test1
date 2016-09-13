/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.fest.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Basic reporting implementation
 */
public abstract class AbstractReportableStandard implements CodingStandard {

  private Map<String, CodingStandardRuleCoverage> rulesCoverage = null;
  private String lastInstance = null;


  public abstract String getReport(String instance);

  public abstract String getSummaryReport(String instance);

  public abstract Language getLanguage();

  public abstract CodingStandardRule[] getCodingStandardRules();

  public abstract ReportType[] getReportTypes();


  public String getNameIfStandardApplies(Rule rule) {
    return null;
  }

  /**
   * Retrieve RSpec rules for which the the relevant field on the References
   * tab is non-blank.
   * @return list of rules relevant to the standard
   */
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

  /**
   * Quick map from an id to the list of relevant rules/RSpecs
   *
   * @return map of id's for this standard to {@link CodingStandardRuleCoverage} records
   */
  public Map<String, CodingStandardRuleCoverage> getRulesCoverage(){
    return rulesCoverage;
  }

  /**
   * Make sure the {@link #rulesCoverage} map is fully populated.
   * @param instance the SonarQube instance to get rule coverage data from
   */
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

  /**
   * Create the rulesCoverage map and and an entry per id in the standard
   */
  public void populateRulesCoverageMap() {

    rulesCoverage = new HashMap<>();

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

  /**
   * Because there's a length limit on Jira fields, some mapping fields contain
   * entries like: "BC_NULL.*". This method expands the regex to get a full list
   * of the id's covered by the RSpec.
   * @param listFromRspec
   * @return
   */
  public List<String> getExpandedStandardKeyList(List<String> listFromRspec) {

    if (listFromRspec == null){
      return listFromRspec;
    }

    List<String> expandedKeyList = new ArrayList<>();

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
      if (! Rule.Status.DEPRECATED.equals(rspecRule.getStatus())) {
        List<String> ids = getRspecReferenceFieldValues(rspecRule);
        setCodingStandardRuleCoverageSpecifiedBy(rspecRule, ids);
      }
    }
  }

  public void setCodingStandardRuleCoverageSpecifiedBy(Rule rspecRule, List<String> ids) {

    if (getRulesCoverage() == null) {
      populateRulesCoverageMap();
    }

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

      List<Rule> sqImplemented;
      if (language != null) {
        sq = language.getSq();

        sqImplemented = RuleMaker.getRulesFromSonarQubeForLanguage(getLanguage(), instance);

      } else {
        sqImplemented = RuleMaker.getRulesFromSonarQubeByQuery(instance, "repositories=" + getSqRepoList());
      }

      for (Rule sqRule : sqImplemented) {
        if (! Rule.Status.DEPRECATED.equals(sqRule.getStatus())) {
          String key = sqRule.getKey();

          Rule rspecRule = RuleMaker.getRuleByKey(key, sq);
          List<String> ids = getExpandedStandardKeyList(getRspecReferenceFieldValues(rspecRule));

          setCodingStandardRuleCoverageImplemented(ids, sqRule);
        }
      }
    }
  }

  protected String getSqRepoList() {

    List<String> repos = new ArrayList<>();
    for (Language language : Language.values()) {
      repos.add(language.getSq());
    }
    String tmp = Utilities.listToString(repos, true);
    return tmp.replaceAll(" ", "");
  }

  public void setCodingStandardRuleCoverageImplemented(List<String> ids, Rule rule) {

    if (getRulesCoverage() == null) {
      populateRulesCoverageMap();
    }

    if (ids != null && ! ids.isEmpty()) {
      for (String id : ids) {
        CodingStandardRuleCoverage cov = getRulesCoverage().get(id);
        if (cov != null) {
          cov.addImplementedBy(rule);
        }
      }
    }
  }

}
