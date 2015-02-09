/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OwaspTopTen extends AbstractReportableStandard implements TaggableStandard {

  private static final String TAG = "owasp-top10";

  private static final String REFERENCE_FIELD_NAME = "OWASP";

  private static final String SEE_SECTION_SEARCH = "OWASP Top Ten";

  private static final String REFERENCE_PATTERN = "A\\d+";

  private Language language = Language.JAVA;

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

    return SEE_SECTION_SEARCH;
  }

  @Override
  public String getRSpecReferenceFieldName() {

    return REFERENCE_FIELD_NAME;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {

    return rule.getOwasp();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {

    rule.setOwasp(ids);
  }

  @Override
  public String getSeeSectionSearchString() {

    return SEE_SECTION_SEARCH;
  }

  @Override
  public String getReferencePattern() {

    return REFERENCE_PATTERN;
  }

  @Override
  public boolean isFieldEntryFormatNeedUpdating(Map<String, Object> updates, Rule rule) {

    List<String> references = getRspecReferenceFieldValues(rule);

    boolean needUpdating = false;
    List<String> replacements = new ArrayList<String>();
    for (int i = 0; i < references.size(); i++) {
      String ref = references.get(i);
      if (ref.matches(".+"+REFERENCE_PATTERN+".+")) {
        replacements.add(ref.replaceAll(".+("+REFERENCE_PATTERN+").+","$1"));
        needUpdating = true;
      } else {
        replacements.add(ref);
      }
    }

    if (needUpdating) {
      setRspecReferenceFieldValues(rule, replacements);
      updates.put(getRSpecReferenceFieldName(), replacements);
    }

    return needUpdating;
  }


  public void setLanguage(Language language) {
    this.language = language;
    resetRulesCoverageMap();
  }

  protected String getReportHeader() {

    StringBuilder sb = new StringBuilder();
    sb.append(getStandardName()).append(" for ").append(getLanguage().getRspec()).append(String.format("%n"));
    return sb.toString();
  }

  @Override
  public String getReport(String instance) {

    String newline = String.format("%n");

    initCoverageResults(instance);

    StringBuilder sb = new StringBuilder();
    sb.append(getReportHeader());

    for (CodingStandardRule csr : getCodingStandardRules()) {
      StandardRule sr = (StandardRule)csr;
      CodingStandardRuleCoverage csrc = getRulesCoverage().get(sr.getCodingStandardRuleId());

      sb.append(sr.getCodingStandardRuleId()).append(" - ").append(sr.getTitle()).append(newline);
      sb.append("\t").append("Specifying:   ").append(csrc.getSpecifiedByKeysAsCommaList()).append(newline);
      sb.append("\t").append("Implementing: ").append(csrc.getImplementedByKeysAsCommaList()).append(newline)
              .append(newline);
    }

    return sb.toString();
  }

  @Override
  public String getSummaryReport(String instance) {

    initCoverageResults(instance);

    StringBuilder sb = new StringBuilder();
    sb.append(getReportHeader());

    for (CodingStandardRule csr : getCodingStandardRules()) {

      StandardRule sr = (StandardRule)csr;
      CodingStandardRuleCoverage cov = getRulesCoverage().get(sr.getCodingStandardRuleId());

      sb.append(cov.getCodingStandardRuleId())
              .append("\tSpecified: ").append(cov.getSpecifiedBy().size())
              .append("\tImplemented: ").append(cov.getImplementedBy().size())
              .append("\n");
    }

    return sb.toString();
  }

  @Override
  public Language getLanguage() {

    return language;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return StandardRule.values();
  }

  public enum StandardRule implements CodingStandardRule {
    A1 ("Injection"),
    A2 ("Broken Authentication and Session Management"),
    A3 ("Cross-Site Scripting (XSS)"),
    A4 ("Insecure Direct Object References"),
    A5 ("Security Misconfiguration"),
    A6 ("Sensitive Data Exposure"),
    A7 ("Missing Function Level Access Control"),
    A8 ("Cross-Site Request Forgery (CSRF)"),
    A9 ("Using Components with Known Vulnerabilities"),
    A10 ("Unvalidated Redirects and Forwards");

    private String title;

    StandardRule(String title) {
      this.title = title;
    }

    public String getTitle() {
      return title;
    }

    @Override
    public String getCodingStandardRuleId() {
      return name();
    }

  }
}
