/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.DerivativeTaggableStandard;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.sonarsource.ruleapi.externalspecifications.Implementability.IMPLEMENTABLE;


public class OwaspTopTen extends AbstractMultiLanguageStandard {

  private static final Logger LOGGER = Logger.getLogger(OwaspTopTen.class.getName());

  private static final String REFERENCE_FIELD_NAME = "OWASP";

  private static final String SEE_SECTION_SEARCH = "OWASP Top Ten";

  private Language language = Language.JAVA;


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
  protected String generateReport(String instance, Map<String, List<Rule>> standardRules) {

    if (standardRules.isEmpty() || language == null) {
      return null;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<h2>").append(getReportHeader()).append("</h2>\n");
    sb.append("<table>\n");

    for (Map.Entry<String, List<Rule>> entry : standardRules.entrySet()) {

      StandardRule owasp = StandardRule.valueOf(entry.getKey());
      sb.append("<tr><td><a href='").append(owasp.getUrl()).append("' target='_blank'>")
              .append(owasp.name()).append(" ").append(owasp.getTitle())
              .append("</a></td>\n<td>");

      for (Rule rule : entry.getValue()) {
        sb.append(getLinkedRuleReference(instance, rule));
      }

      sb.append("</td></tr>\n");
    }
    sb.append("</table>");

    return sb.toString();
  }

  @Override
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

    String newline = String.format("%n");

    initCoverageResults(instance);

    StringBuilder sb = new StringBuilder();
    sb.append(getReportHeader());

    for (CodingStandardRule csr : getCodingStandardRules()) {

      StandardRule sr = (StandardRule)csr;
      CodingStandardRuleCoverage cov = getRulesCoverage().get(sr.getCodingStandardRuleId());

      sb.append(cov.getCodingStandardRuleId())
              .append("\tSpecified: ").append(cov.getSpecifiedBy().size())
              .append("\tImplemented: ").append(cov.getImplementedBy().size())
              .append(newline);
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

  public enum StandardRule implements CodingStandardRule, DerivativeTaggableStandard {
    A1 ("Injection", IMPLEMENTABLE),
    A2 ("Broken Authentication and Session Management", IMPLEMENTABLE),
    A3 ("Cross-Site Scripting (XSS)", IMPLEMENTABLE),
    A4 ("Insecure Direct Object References", IMPLEMENTABLE),
    A5 ("Security Misconfiguration", IMPLEMENTABLE),
    A6 ("Sensitive Data Exposure", IMPLEMENTABLE),
    A7 ("Missing Function Level Access Control", IMPLEMENTABLE),
    A8 ("Cross-Site Request Forgery (CSRF)", IMPLEMENTABLE),
    A9 ("Using Components with Known Vulnerabilities", IMPLEMENTABLE),
    A10 ("Unvalidated Redirects and Forwards", IMPLEMENTABLE);


    private static final String REFERENCE_PATTERN = "A\\d+";

    private String title;
    private Implementability implementability;


    StandardRule(String title, Implementability implementability) {
      this.title = title;
      this.implementability = implementability;
    }

    public String getTitle() {
      return title;
    }

    public String getUrl() {

      return "https://www.owasp.org/index.php/Top_10_2013-" + name() + "-" + title.replaceAll(" ", "_");
    }

    @Override
    public String getCodingStandardRuleId() {
      return name();
    }

    @Override
    public Implementability getImplementability() {
      return implementability;
    }

    @Override
    public void checkReferencesInSeeSection(Rule rule) {
      List<String> owasp = rule.getOwasp();

      String references = rule.getReferences().replaceAll("\n","");
      String regex = ".*" + name() + "\\<.*";
      boolean seeSectionHasReference = references.matches(regex);

      if (owasp.contains(name()) && !seeSectionHasReference) {
        LOGGER.info("Expected reference not found in " + rule.getKey() + ": " + name());
      } else if (!owasp.contains(name()) && seeSectionHasReference) {
        LOGGER.warning(name() + " found erroneously in See section for " + rule.getKey());
      }
    }

    @Override
    public void addTagIfMissing(Rule rule, Map<String, Object> updates) {

      if (Rule.Status.DEPRECATED.equals(rule.getStatus())) {
        return;
      }

      String tag = getTag();
      List tags = rule.getTags();

      boolean needsTag = getRspecReferenceFieldValues(rule).contains(name());
      boolean hasTag = tags.contains(tag);

      if (!hasTag && needsTag) {
        tags.add(tag);
        updates.put("Labels", tags);
      } else if (hasTag && !needsTag) {
        tags.remove(tag);
        updates.put("Labels", tags);
      }

    }

    @Override
    public boolean isTagShared() {

      return true;
    }

    @Override
    public String getTag() {

      return "owasp-"+name().toLowerCase();
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
    public boolean doesReferenceNeedUpdating(String ref, List<String> replacements, String ruleKey) {

      if (ref.matches(".+"+REFERENCE_PATTERN+".+")) {
        replacements.add(ref.replaceAll(".+("+REFERENCE_PATTERN+").+","$1"));
        return true;
      } else {
        if (!ref.matches(REFERENCE_PATTERN)) {
          LOGGER.info("Unrecognized OWASP reference pattern " + ref + " in " + ruleKey);
        }

        replacements.add(ref);
      }

      return false;
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

  }
}
