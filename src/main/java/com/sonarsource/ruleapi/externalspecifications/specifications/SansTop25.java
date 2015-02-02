/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.DerivativeTaggableStandard;
import com.sonarsource.ruleapi.utilities.ComparisonUtilities;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class SansTop25  extends AbstractReportableStandard implements DerivativeTaggableStandard {

  private static final Logger LOGGER = Logger.getLogger(SansTop25.class.getName());

  private static final String NAME = "SANS Top 25";
  private static final String TAG = "sans-top25";
  private static final String REFERENCE_PATTERN = "CWE-\\d+";
  private static final String CWE = "CWE";

  private Language language = Language.JAVA;

  private int insecureInteractionCount = 0;
  private int insecureInteractionSpecified = 0;
  private int insecureInteractionImplemented = 0;

  private int riskyResourceCount = 0;
  private int riskyResourceSpecified = 0;
  private int riskyResourceImplemented = 0;

  private int porousDefensesCount = 0;
  private int porousDefensesSpecified = 0;
  private int porousDefensesImplemented = 0;

  private Map<String, CodingStandardRule> ruleMap = new HashMap<String, CodingStandardRule>();

  public SansTop25() {
    for (CodingStandardRule csr : StandardRule.values()) {
      ruleMap.put(csr.getCodingStandardRuleId(), csr);
    }
  }

  @Override
  public boolean isTagShared() {

    return false;
  }

  @Override
  public String getTag() {

    return TAG;
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
  public boolean isFieldEntryFormatNeedUpdating(Map<String, Object> updates, Rule rule) {

    return false;
  }

  @Override
  public String getStandardName() {

    return NAME;
  }

  @Override
  public String getRSpecReferenceFieldName() {

    return CWE;
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
  public void checkReferencesInSeeSection(Rule rule) {

    if (! isSansRule(rule) && rule.getReferences().contains(NAME)) {
      LOGGER.warning(NAME + " found erroneously in See section for " + rule.getKey());
      return;
    }

    String seeSection = ComparisonUtilities.stripHtml(rule.getReferences());

    for (String cwe : getRspecReferenceFieldValues(rule)) {
      StandardRule sr = (StandardRule) ruleMap.get(cwe);
      if (sr != null) {
        String expectedReference = NAME + " - " + sr.category.getName();
        if (!seeSection.contains(expectedReference)) {
          LOGGER.info("Expected reference not found in " + rule.getKey() + ": " + expectedReference);
        }
      }
    }
  }

  @Override
  public void addTagIfMissing(Rule rule, Map<String, Object> updates) {

    List tags = rule.getTags();
    boolean needsTag = isSansRule(rule);
    boolean hasTag = tags.contains(TAG);

    if (needsTag && !hasTag) {
      tags.add(TAG);
      updates.put("Labels", tags);
    } else if (!needsTag && hasTag) {
      tags.remove(TAG);
      updates.put("Labels", tags);
    }
  }


  @Override
  public String getReport(String instance) throws RuleException {

    initCoverageResults(instance);
    computeCoverage();

    StringBuilder sb = new StringBuilder();
    sb.append(String.format("%-14s %-12s %-12s%n", "Rule", "Spec.", "Impl."));
    for (StandardRule sr : StandardRule.values()) {
      sb.append(formatLine(sr));
    }
    sb.append(getSummaryReport(instance));

    return sb.toString();
  }

  protected String formatLine(StandardRule sr) {

    CodingStandardRuleCoverage cov = getRulesCoverage().get(sr.getCodingStandardRuleId());

    return String.format("%2d) %-10s %-12s %-12s%n",
            sr.rank,
            sr.getCodingStandardRuleId(),
            cov.getSpecifiedBy() == null ? "" : cov.getSpecifiedBy().getKey(),
            cov.getImplementedBy() == null ? "" : cov.getImplementedBy().getKey());
  }


  @Override
  public String getSummaryReport(String instance) throws RuleException {

    initCoverageResults(instance);
    computeCoverage();

    String newline = String.format("%n");
    StringBuilder sb = new StringBuilder();
    sb.append(newline).append(getStandardName()).append(" for ").append(language.getRspec()).append(newline);

    sb.append(formatSummaryLine(Category.INSECURE_INTERACTION.getName(),
            insecureInteractionCount, insecureInteractionSpecified, insecureInteractionImplemented));
    sb.append(formatSummaryLine(Category.POROUS_DEFENSES.getName(),
            porousDefensesCount, porousDefensesSpecified, porousDefensesImplemented));
    sb.append(formatSummaryLine(Category.RISKY_RESOURCE.getName(),
            riskyResourceCount, riskyResourceSpecified, riskyResourceImplemented));
    sb.append(formatSummaryLine("Total", 25,
            insecureInteractionSpecified + riskyResourceSpecified + porousDefensesSpecified,
            insecureInteractionImplemented + riskyResourceImplemented + porousDefensesImplemented));

    return sb.toString();
  }

  protected String formatSummaryLine(String label, int total, int specified, int implemented) {

    return String.format("%-39s %2d,  specified: %2d,  implemented: %2d%n",
            label, total, specified, implemented);
  }

  @Override
  public Language getLanguage() {

    return language;
  }

  public void resetLanguage(Language langauge) {
    this.language = langauge;
    ruleMap.clear();
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return StandardRule.values();
  }

  protected void computeCoverage() {

    if (insecureInteractionCount > 0) {
      return;
    }

    for (StandardRule sr : StandardRule.values()) {
      CodingStandardRuleCoverage cov = getRulesCoverage().get(sr.getCodingStandardRuleId());

      switch (sr.category) {
        case INSECURE_INTERACTION:

          insecureInteractionCount++;
          if (cov.getSpecifiedBy() != null) {
            insecureInteractionSpecified++;
          }
          if (cov.getImplementedBy() != null) {
            insecureInteractionImplemented++;
          }

          break;

        case POROUS_DEFENSES:

          porousDefensesCount++;
          if (cov.getSpecifiedBy() != null) {
            porousDefensesSpecified++;
          }
          if (cov.getImplementedBy() != null) {
            porousDefensesImplemented++;
          }
          break;

        case RISKY_RESOURCE:

          riskyResourceCount++;
          if (cov.getSpecifiedBy() != null) {
            riskyResourceSpecified++;
          }
          if (cov.getImplementedBy() != null) {
            riskyResourceImplemented++;
          }

          break;
      }
    }

  }

  protected boolean isSansRule(Rule rule) {

    List<String> refs = getRspecReferenceFieldValues(rule);
    for (String cwe : refs) {
      if (ruleMap.get(cwe) != null) {
        return true;
      }
    }
    return false;
  }

  public enum Category {
    INSECURE_INTERACTION("Insecure Interaction Between Components"),
    RISKY_RESOURCE("Risky Resource Management"),
    POROUS_DEFENSES("Porous Defenses");

    private String name;

    Category (String name) {
      this.name = name;
    }

    public String getName(){
      return name;
    }

  }

  public enum StandardRule implements CodingStandardRule {

    CWE_89  (1,  Category.INSECURE_INTERACTION, "Improper Neutralization of Special Elements used in an SQL Command ('SQL Injection')"),
    CWE_78  (2,  Category.INSECURE_INTERACTION, "Improper Neutralization of Special Elements used in an OS Command ('OS Command Injection')"),
    CWE_120 (3,  Category.RISKY_RESOURCE, "Buffer Copy without Checking Size of Input ('Classic Buffer Overflow')"),
    CWE_79  (4,  Category.INSECURE_INTERACTION, "Improper Neutralization of Input During Web Page Generation ('Cross-site Scripting')"),
    CWE_306 (5,  Category.POROUS_DEFENSES, "Missing Authentication for Critical Function"),
    CWE_862 (6,  Category.POROUS_DEFENSES, "Missing Authorization"),
    CWE_798 (7,  Category.POROUS_DEFENSES, "Use of Hard-coded Credentials"),
    CWE_311 (8,  Category.POROUS_DEFENSES, "Missing Encryption of Sensitive Data"),
    CWE_434 (9,  Category.INSECURE_INTERACTION, "Unrestricted Upload of File with Dangerous Type"),
    CWE_807 (10, Category.POROUS_DEFENSES, "Reliance on Untrusted Inputs in a Security Decision"),
    CWE_250 (11, Category.POROUS_DEFENSES, "Execution with Unnecessary Privileges"),
    CWE_352 (12, Category.INSECURE_INTERACTION, "Cross-Site Request Forgery (CSRF)"),
    CWE_22  (13, Category.RISKY_RESOURCE, "Improper Limitation of a Pathname to a Restricted Directory ('Path Traversal')"),
    CWE_494 (14, Category.RISKY_RESOURCE, "Download of Code Without Integrity Check"),
    CWE_863 (15, Category.POROUS_DEFENSES, "Incorrect Authorization"),
    CWE_829 (16, Category.RISKY_RESOURCE, "Inclusion of Functionality from Untrusted Control Sphere"),
    CWE_732 (17, Category.POROUS_DEFENSES, "Incorrect Permission Assignment for Critical Resource"),
    CWE_676 (18, Category.RISKY_RESOURCE, "Use of Potentially Dangerous Function"),
    CWE_327 (19, Category.POROUS_DEFENSES, "Use of a Broken or Risky Cryptographic Algorithm"),
    CWE_131 (20, Category.RISKY_RESOURCE, "Incorrect Calculation of Buffer Size"),
    CWE_307 (21, Category.POROUS_DEFENSES, "Improper Restriction of Excessive Authentication Attempts"),
    CWE_601 (22, Category.INSECURE_INTERACTION, "URL Redirection to Untrusted Site ('Open Redirect')"),
    CWE_134 (23, Category.RISKY_RESOURCE, "Uncontrolled Format String"),
    CWE_190 (24, Category.RISKY_RESOURCE, "Integer Overflow or Wraparound"),
    CWE_759 (25, Category.POROUS_DEFENSES, "Use of a One-Way Hash without a Salt");

    private int rank;
    private Category category;
    private String title;

    StandardRule(int rank, Category category, String title) {
      this.rank = rank;
      this.category = category;
      this.title = title;
    }

    @Override
    public String getCodingStandardRuleId() {
      return this.name().replace('_','-');
    }

  }

}
