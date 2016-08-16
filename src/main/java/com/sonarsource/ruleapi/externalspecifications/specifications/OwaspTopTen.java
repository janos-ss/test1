/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.DerivativeTaggableStandard;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.services.ReportService;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static com.sonarsource.ruleapi.externalspecifications.Implementability.IMPLEMENTABLE;


public class OwaspTopTen extends AbstractMultiLanguageStandard {

  private static final Logger LOGGER = Logger.getLogger(OwaspTopTen.class.getName());

  private static final String REFERENCE_FIELD_NAME = "OWASP";
  private static final String SEE_SECTION_SEARCH = "OWASP Top Ten";
  private static final String TITLE_AND_INTRO = "<h2>SonarAnalyzer for %1$s Coverage of OWASP Top Ten</h2>\n" +
          "<p>The following table lists the OWASP Top Ten standard items the SonarAnalyzer for %1$s is able to detect, " +
          "and for each of them, the rules providing this coverage.</p>";


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
  protected String generateReport(String instance) {

    if (language == null || this.getRulesCoverage() == null) {
      return null;
    }

    StringBuilder sb = new StringBuilder();

    sb.append(String.format(ReportService.HEADER_TEMPLATE, getLanguage().getRspec(), SEE_SECTION_SEARCH))
            .append(String.format(TITLE_AND_INTRO, getLanguage().getRspec()))
            .append(ReportService.TABLE_OPEN)
            .append("<thead><tr><th>OWASP ID</th><th>OWASP Title</th><th>Implementing Rules</th></tr></thead>")
            .append("<tbody>");

    for (Map.Entry<String, CodingStandardRuleCoverage> entry : this.getRulesCoverage().entrySet()) {
      if (!entry.getValue().getImplementedBy().isEmpty()) {
        StandardRule owasp = StandardRule.valueOf(entry.getKey());
        sb.append("<tr><td><a href='").append(owasp.getUrl()).append("' target='_blank'>")
                .append(owasp.name()).append("</a></td><td>").append(owasp.getTitle())
                .append("</td>\n<td>");

        for (Rule rule : entry.getValue().getImplementedBy()) {
          sb.append(Utilities.getNemoLinkedRuleReference(instance, rule));
        }

        sb.append("</td></tr>\n");
      }
    }
    sb.append("</table>")
            .append(ReportService.FOOTER_TEMPLATE);

    return sb.toString();
  }

  @Override
  public void setLanguage(Language language) {
    this.language = language;
    resetRulesCoverageMap();
  }

  protected String getReportHeader() {

    StringBuilder sb = new StringBuilder();
    sb.append(getStandardName());
    if (language != null) {
      sb.append(" for ").append(getLanguage().getRspec());
    }
    sb.append(System.lineSeparator());

    return sb.toString();
  }

  @Override
  public String getReport(String instance) {

    language = null;

    String newline = System.lineSeparator();

    initCoverageResults(instance);

    StringBuilder sb = new StringBuilder();
    sb.append(getReportHeader());

    for (CodingStandardRule csr : getCodingStandardRules()) {
      StandardRule sr = (StandardRule)csr;
      CodingStandardRuleCoverage csrc = getRulesCoverage().get(sr.getCodingStandardRuleId());

      sb.append(sr.getCodingStandardRuleId()).append(" - ").append(sr.getTitle()).append(newline);
      sb.append("\t").append("Specifying:   ").append(csrc.getSpecifiedByKeysAsCommaList()).append(newline)
              .append(newline);
    }

    return sb.toString();
  }

  @Override
  protected void initCoverageResults(String instance) {
    if (getRulesCoverage() == null && language == null) {
      populateRulesCoverageMap();
      findSpecifiedInRspec(getRSpecRulesReferencingStandard());
    } else {
      super.initCoverageResults(instance);
    }
  }

  @Override
  public String getSummaryReport(String instance) {

    language = null;

    String newline = System.lineSeparator();

    initCoverageResults(instance);

    StringBuilder sb = new StringBuilder();
    sb.append(getReportHeader());

    for (CodingStandardRule csr : getCodingStandardRules()) {

      StandardRule sr = (StandardRule)csr;
      CodingStandardRuleCoverage cov = getRulesCoverage().get(sr.getCodingStandardRuleId());

      sb.append(cov.getCodingStandardRuleId())
              .append("\tSpecified: ").append(cov.getSpecifiedBy().size())
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
      Set<String> tags = rule.getTags();

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
