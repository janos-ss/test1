/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class Cwe extends AbstractMultiLanguageStandard implements TaggableStandard {

  private static final Logger LOGGER = Logger.getLogger(Cwe.class.getName());

  private static final String TAG = "cwe";
  private static final String REFERENCE_PATTERN = "CWE-\\d+";
  private static final String NAME = "CWE";
  private Language language = null;

  @Override
  public boolean isTagShared() {

    return false;
  }

  @Override
  public String getTag() {

    return TAG;
  }

  @Override
  public String getStandardName() {

    return NAME;
  }

  @Override
  public String getRSpecReferenceFieldName() {

    return NAME;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {

    return rule.getCwe();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {

    rule.setCwe(ids);
  }

  @Override
  public String getSeeSectionSearchString() {

    return NAME;
  }

  @Override
  public String getReferencePattern() {

    return REFERENCE_PATTERN;
  }

  @Override
  public boolean doesReferenceNeedUpdating(String reference, List<String> replacements, String ruleKey){

    if (reference.matches("\\d+")) {
      replacements.add(NAME + "-" + reference);
      return true;
    } else {
      if (!reference.matches(REFERENCE_PATTERN)) {
        LOGGER.info("Unrecognized CWE reference pattern " + reference + " in " + ruleKey);
      }

      replacements.add(reference);
    }

    return false;
  }

  @Override
  public String getReport(String instance) {

    if (language == null) {
      return null;
    }

    initCoverageResults(instance);
    return generateReport(instance);
  }

  @Override
  protected String generateReport(String instance) {

    if (language == null || this.getRulesCoverage() == null) {
      return null;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<h2>").append(language.getRspec()).append(" coverage of CWE</h2>\n");
    sb.append("<table>\n");

    List<String> sortedKeys = new ArrayList(getRulesCoverage().keySet());
    Collections.sort(sortedKeys);

    for (String key : sortedKeys) {
      CodingStandardRuleCoverage csrc = getRulesCoverage().get(key);
      if (!csrc.getImplementedBy().isEmpty()) {

        Integer ikey = Integer.valueOf(key.split("-")[1]);
        sb.append("<tr><td><a href='http://cwe.mitre.org/data/definitions/").append(ikey)
                .append("' target='_blank'>CWE-").append(ikey).append("</a></td>\n<td>");

        for (Rule rule : csrc.getImplementedBy()) {
          sb.append(Utilities.getNemoLinkedRuleReference(instance, rule));
        }
        sb.append("</td></tr>\n");
      }
    }
    sb.append("</table>");

    return sb.toString();
  }

  /**
   * This override required by the fact that we don't hold the list of CWE id's
   * in this class. As a result the {{rulesCoverage}} map starts out non-null, but empty.
   *
   * We must simply assume that each passed id is valid and store the related data.
   *
   * @param rspecRule
   * @param ids list of CWE ids covered by rspecRule
   */
  @Override
  public void setCodingStandardRuleCoverageSpecifiedBy(Rule rspecRule, List<String> ids) {

    if (getRulesCoverage() == null) {
      populateRulesCoverageMap();
    }

    if (ids != null && ! ids.isEmpty()) {
      for (String id : ids) {
        CodingStandardRuleCoverage cov = getRulesCoverage().get(id);
        if (cov == null) {
          cov = new CodingStandardRuleCoverage();
          cov.setCodingStandardRuleId(id);
          getRulesCoverage().put(id, cov);
        }
        cov.addSpecifiedBy(rspecRule);
      }
    }
  }

  /**
   /**
   * This override required by the fact that we don't hold the list of CWE id's
   * in this class. As a result the {{rulesCoverage}} map starts out non-null, but empty.
   *
   * We must simply assume that each passed id is valid and store the related data.
   *
   * @param ids list of CWE ids implemented by rspecRule
   * @param rule
   */
  @Override
  public void setCodingStandardRuleCoverageImplemented(List<String> ids, Rule rule) {

    if (getRulesCoverage() == null) {
      populateRulesCoverageMap();
    }

    if (ids != null && ! ids.isEmpty()) {
      for (String id : ids) {
        CodingStandardRuleCoverage cov = getRulesCoverage().get(id);
        if (cov == null) {
          cov = new CodingStandardRuleCoverage();
          cov.setCodingStandardRuleId(id);
          getRulesCoverage().put(id, cov);
        }
        cov.addImplementedBy(rule);
      }
    }
  }

  @Override
  public String getSummaryReport(String instance) {

    return getReport(instance);
  }

  @Override
  public Language getLanguage() {

    return language;
  }

  @Override
  public void setLanguage(Language language) {
    this.language = language;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return new CodingStandardRule[0];
  }
}
