/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.google.common.base.Enums;
import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;
import com.sonarsource.ruleapi.services.ReportService;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SansTop25  extends AbstractMultiLanguageStandard {

  private static final Logger LOGGER = Logger.getLogger(SansTop25.class.getName());

  private static final String NAME = "SANS Top 25";
  private static final String TAG = "sans-top25";
  private static final String REFERENCE_PATTERN = String.format("%s|%s|%s", Category.INSECURE_INTERACTION.name, Category.RISKY_RESOURCE.name, Category.POROUS_DEFENSES.name);
  private static final String TITLE_AND_INTRO = "<h2>%1$s Coverage of SANS Top 25</h2>\n" +
          "<p>SANS Top 25 is a sub-set of the Common Weakness Enumeration (CWE). The following table lists the " +
          "CWE standard items in the SANS Top 25 that %1$s is able to detect, " +
          "and for each of them, the rules providing this coverage.</p>";

  private Language language = null;

  private int insecureInteractionCount = 0;
  private int insecureInteractionSpecified = 0;
  private int insecureInteractionImplemented = 0;
  private int insecureInteractionNa = 0;

  private int riskyResourceCount = 0;
  private int riskyResourceSpecified = 0;
  private int riskyResourceImplemented = 0;
  private int riskyResourceNa = 0;

  private int porousDefensesCount = 0;
  private int porousDefensesSpecified = 0;
  private int porousDefensesImplemented = 0;
  private int porousDefensesNa = 0;


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

    return rule.getSansTop25();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setCwe(ids);
  }

  @Override
  protected void initCoverageResults(String instance) {
    if (getRulesCoverage() == null && language == null) {

      populateRulesCoverageMap();
      findSpecifiedInRspec(getRSpecRulesReferencingStandard());
      findImplementedByPlugin(instance);

    } else {
      super.initCoverageResults(instance);
    }
  }

  @Override
  public String getReport(String instance) {

    initCoverageResults(instance);
    computeCoverage();

    StringBuilder sb = new StringBuilder();
    for (StandardRule sr : StandardRule.values()) {
      sb.append(formatLine(sr));
    }
    sb.append(getSummaryReport(instance));

    return sb.toString();
  }

  protected String formatLine(StandardRule sr) {

    CodingStandardRuleCoverage cov = getRulesCoverage().get(sr.getCodingStandardRuleId());
    if (Implementability.NOT_IMPLEMENTABLE.equals(sr.getImplementability())){
      return String.format("%2d) %-7s - %s%n      Not Implementable: %s%n%n",
              sr.rank,
              sr.getCodingStandardRuleId(),
              sr.category.getName(),
              sr.title);
    }

    return String.format("%2d) %-7s - %s - %s%n      Specifying:   %s%n      Implementing: %s%n%n",
            sr.rank,
            sr.getCodingStandardRuleId(),
            sr.category.getName(),
            sr.title,
            getSpecifiedByString(cov),
            getCoveredByString(cov));
  }

  protected String getSpecifiedByString(CodingStandardRuleCoverage cov) {

    StringBuilder sb = new StringBuilder();

    for (Rule rule : cov.getSpecifiedBy()) {
      List<String> languages = new ArrayList<>(rule.getTargetedLanguages());
      languages.addAll(rule.getCoveredLanguages());
      Collections.sort(languages);
      if (sb.length() > 0) {
        sb.append("; ");
      }
      sb.append(rule.getKey()).append(" (").append(Utilities.listToString(languages, true)).append(")");
    }

    return sb.toString();
  }

  protected String getCoveredByString(CodingStandardRuleCoverage cov) {

    StringBuilder sb = new StringBuilder();

    for (Rule rule : cov.getImplementedBy()) {
      if (sb.length() > 0) {
        sb.append("; ");
      }
      sb.append(rule.getKey()).append(" (").append(rule.getLanguage()).append(")");
    }

    return sb.toString();
  }


  @Override
  public String getSummaryReport(String instance) {

    initCoverageResults(instance);
    computeCoverage();

    String newline = System.lineSeparator();
    StringBuilder sb = new StringBuilder();
    sb.append(newline).append(getStandardName()).append(newline);

    sb.append(formatSummaryLine(Category.INSECURE_INTERACTION.getName(),
            insecureInteractionCount, insecureInteractionNa, insecureInteractionSpecified, insecureInteractionImplemented));
    sb.append(formatSummaryLine(Category.POROUS_DEFENSES.getName(),
            porousDefensesCount, porousDefensesNa, porousDefensesSpecified, porousDefensesImplemented));
    sb.append(formatSummaryLine(Category.RISKY_RESOURCE.getName(),
            riskyResourceCount, riskyResourceNa, riskyResourceSpecified, riskyResourceImplemented));
    sb.append(formatSummaryLine("Total", 25,
            insecureInteractionNa + riskyResourceNa + porousDefensesNa,
            insecureInteractionSpecified + riskyResourceSpecified + porousDefensesSpecified,
            insecureInteractionImplemented + riskyResourceImplemented + porousDefensesImplemented));

    return sb.toString();
  }

  protected String formatSummaryLine(String label, int total, int na, int specified, int implemented) {

    return String.format("%-39s %2d, unimplementable: %2d  specified: %2d,  implemented: %2d%n",
            label, total, na, specified, implemented);
  }

  @Override
  public Language getLanguage() {

    return language;
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
          countInsecureCoverage(sr, cov);
          break;

        case POROUS_DEFENSES:
          countPorousDefenses(sr, cov);
          break;

        case RISKY_RESOURCE:
          countRiskyResource(sr, cov);
          break;

        default:
      }
    }
  }

  private void countRiskyResource(StandardRule sr, CodingStandardRuleCoverage cov) {

    riskyResourceCount++;
    if (Implementability.NOT_IMPLEMENTABLE.equals(sr.getImplementability())) {
      riskyResourceNa ++;
    }
    if (!cov.getSpecifiedBy().isEmpty()) {
      riskyResourceSpecified++;
    }
    if (!cov.getImplementedBy().isEmpty()) {
      riskyResourceImplemented++;
    }
  }

  private void countPorousDefenses(StandardRule sr, CodingStandardRuleCoverage cov) {

    porousDefensesCount++;
    if (Implementability.NOT_IMPLEMENTABLE.equals(sr.getImplementability())) {
      porousDefensesNa++;
    }
    if (!cov.getSpecifiedBy().isEmpty()) {
      porousDefensesSpecified++;
    }
    if (!cov.getImplementedBy().isEmpty()) {
      porousDefensesImplemented++;
    }
  }

  private void countInsecureCoverage(StandardRule sr, CodingStandardRuleCoverage cov) {

    insecureInteractionCount++;
    if (Implementability.NOT_IMPLEMENTABLE.equals(sr.getImplementability())) {
      insecureInteractionNa++;
    }
    if (!cov.getSpecifiedBy().isEmpty()) {
      insecureInteractionSpecified++;
    }
    if (!cov.getImplementedBy().isEmpty()) {
      insecureInteractionImplemented++;
    }
  }

  @Override
  protected String generateReport(String instance) {

    if (getLanguage() == null || this.getRulesCoverage() == null) {
      return null;
    }

    Map<Category,Map<StandardRule, List<Rule>>> metaMap = new EnumMap<>(Category.class);
    for (Map.Entry<String, CodingStandardRuleCoverage> entry : this.getRulesCoverage().entrySet()) {
      if (!entry.getValue().getImplementedBy().isEmpty()) {

        StandardRule csr = StandardRule.fromString(entry.getValue().getCodingStandardRuleId());
        Map<StandardRule, List<Rule>> miniMap = metaMap.get(csr.category);
        if (miniMap == null) {
          miniMap = new EnumMap<>(StandardRule.class);
          metaMap.put(csr.category, miniMap);
        }
        miniMap.put(csr, entry.getValue().getImplementedBy());
      }
    }


    StringBuilder sb = new StringBuilder();
    sb.append(String.format(ReportService.HEADER_TEMPLATE, getLanguage().getReportName(), NAME + " Most Dangerous Software Errors"))
            .append(String.format(TITLE_AND_INTRO, getLanguage().getReportName()));


    for (Map.Entry<Category, Map<StandardRule, List<Rule>>> metaEntry : metaMap.entrySet()) {

      sb.append("<h3><a href='")
              .append(metaEntry.getKey().getUrl()).append("' target='_blank'>").append(metaEntry.getKey().getName())
              .append("</a></h3>")
              .append(ReportService.TABLE_OPEN)
              .append("<thead><tr><th>CWE ID</th><th>CWE Name</th><th>Implementing Rules</th></tr></thead>")
              .append("<tbody>");

      for (Map.Entry<StandardRule, List<Rule>> miniEntry : metaEntry.getValue().entrySet()) {

        String cweId = miniEntry.getKey().getCodingStandardRuleId();

        Integer id = Integer.valueOf(cweId.split("-")[1]);
        Cwe.CweRule cwe = Cwe.CweRule.fromString(cweId);

        sb.append("<tr><td><a href='http://cwe.mitre.org/data/definitions/").append(id)
                .append("' target='_blank'>").append(id).append("</a></td><td>")
                .append(cwe.getTitle()).append("</td><td>");

        for (Rule rule : miniEntry.getValue()) {
          sb.append(Utilities.getNemoLinkedRuleReference(instance, rule));
        }

        sb.append("</td></tr>\n");
      }
      sb.append("</table>");
    }

    sb.append(String.format(ReportService.FOOTER_TEMPLATE,Utilities.getFormattedDateString()));

    return sb.toString();
  }

  @Override
  public String getNameIfStandardApplies(Rule rule) {

    for (String id : rule.getCwe()) {
      if (Enums.getIfPresent(StandardRule.class, id.replace('-', '_') ).isPresent()) {
        return getStandardName();
      }
    }
    return null;
  }

  @Override
  public void setLanguage(Language language) {
    this.language = language;
  }

  public enum Category implements TaggableStandard {
    INSECURE_INTERACTION("Insecure Interaction Between Components", "http://www.sans.org/top25-software-errors/#cat1"),
    RISKY_RESOURCE("Risky Resource Management", "http://www.sans.org/top25-software-errors/#cat2"),
    POROUS_DEFENSES("Porous Defenses", "http://www.sans.org/top25-software-errors/#cat3");

    private String name;
    private String url;

    Category (String name, String url) {
      this.name = name;
      this.url = url;
    }

    public String getUrl() {
      return url;
    }

    public String getName(){
      return name;
    }

    @Override
    public boolean isTagShared() {

      return false;
    }

    @Override
    public String getTag() {
      return TAG + "-" + getName().split(" ")[0].toLowerCase(Locale.ENGLISH);
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
    public boolean doesReferenceNeedUpdating(String reference, List<String> updates, String ruleKey) {

      if (reference.matches(REFERENCE_PATTERN)) {
        updates.add(reference);
        return true;
      } else {
        if (!reference.matches(REFERENCE_PATTERN)) {
          LOGGER.log(Level.INFO, "Unrecognized SANS Top 25 reference {0} in {1}",
            new Object[] {reference, ruleKey});
        }

        updates.add(reference);
      }
      return false;
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

      return rule.getSansTop25();
    }

    @Override
    public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
      rule.setCwe(ids);
    }
  }

  public enum StandardRule implements CodingStandardRule {

    CWE_89  (1,  Category.INSECURE_INTERACTION, Implementability.IMPLEMENTABLE,     "Improper Neutralization of Special Elements used in an SQL Command ('SQL Injection')"),
    CWE_78  (2,  Category.INSECURE_INTERACTION, Implementability.IMPLEMENTABLE,     "Improper Neutralization of Special Elements used in an OS Command ('OS Command Injection')"),
    CWE_120 (3,  Category.RISKY_RESOURCE,       Implementability.IMPLEMENTABLE,     "Buffer Copy without Checking Size of Input ('Classic Buffer Overflow')"),
    CWE_79  (4,  Category.INSECURE_INTERACTION, Implementability.IMPLEMENTABLE,     "Improper Neutralization of Input During Web Page Generation ('Cross-site Scripting')"),
    CWE_306 (5,  Category.POROUS_DEFENSES,      Implementability.NOT_IMPLEMENTABLE, "Missing Authentication for Critical Function"),
    CWE_862 (6,  Category.POROUS_DEFENSES,      Implementability.IMPLEMENTABLE,     "Missing Authorization"),
    CWE_798 (7,  Category.POROUS_DEFENSES,      Implementability.IMPLEMENTABLE,     "Use of Hard-coded Credentials"),
    CWE_311 (8,  Category.POROUS_DEFENSES,      Implementability.IMPLEMENTABLE,     "Missing Encryption of Sensitive Data"),
    CWE_434 (9,  Category.INSECURE_INTERACTION, Implementability.IMPLEMENTABLE,     "Unrestricted Upload of File with Dangerous Type"),
    CWE_807 (10, Category.POROUS_DEFENSES,      Implementability.IMPLEMENTABLE,     "Reliance on Untrusted Inputs in a Security Decision"),
    CWE_250 (11, Category.POROUS_DEFENSES,      Implementability.IMPLEMENTABLE,     "Execution with Unnecessary Privileges"),
    CWE_352 (12, Category.INSECURE_INTERACTION, Implementability.IMPLEMENTABLE,     "Cross-Site Request Forgery (CSRF)"),
    CWE_22  (13, Category.RISKY_RESOURCE,       Implementability.IMPLEMENTABLE,     "Improper Limitation of a Pathname to a Restricted Directory ('Path Traversal')"),
    CWE_494 (14, Category.RISKY_RESOURCE,       Implementability.NOT_IMPLEMENTABLE, "Download of Code Without Integrity Check"),
    CWE_863 (15, Category.POROUS_DEFENSES,      Implementability.NOT_IMPLEMENTABLE, "Incorrect Authorization"),
    CWE_829 (16, Category.RISKY_RESOURCE,       Implementability.IMPLEMENTABLE,     "Inclusion of Functionality from Untrusted Control Sphere"),
    CWE_732 (17, Category.POROUS_DEFENSES,      Implementability.IMPLEMENTABLE,     "Incorrect Permission Assignment for Critical Resource"),
    CWE_676 (18, Category.RISKY_RESOURCE,       Implementability.IMPLEMENTABLE,     "Use of Potentially Dangerous Function"),
    CWE_327 (19, Category.POROUS_DEFENSES,      Implementability.IMPLEMENTABLE,     "Use of a Broken or Risky Cryptographic Algorithm"),
    CWE_131 (20, Category.RISKY_RESOURCE,       Implementability.IMPLEMENTABLE,     "Incorrect Calculation of Buffer Size"),
    CWE_307 (21, Category.POROUS_DEFENSES,      Implementability.IMPLEMENTABLE,     "Improper Restriction of Excessive Authentication Attempts"),
    CWE_601 (22, Category.INSECURE_INTERACTION, Implementability.IMPLEMENTABLE,     "URL Redirection to Untrusted Site ('Open Redirect')"),
    CWE_134 (23, Category.RISKY_RESOURCE,       Implementability.IMPLEMENTABLE,     "Uncontrolled Format String"),
    CWE_190 (24, Category.RISKY_RESOURCE,       Implementability.IMPLEMENTABLE,     "Integer Overflow or Wraparound"),
    CWE_759 (25, Category.POROUS_DEFENSES,      Implementability.IMPLEMENTABLE,     "Use of a One-Way Hash without a Salt");

    private int rank;
    private Category category;
    private String title;
    private Implementability implementability;

    StandardRule(int rank, Category category, Implementability implementability, String title) {
      this.rank = rank;
      this.category = category;
      this.implementability = implementability;
      this.title = title;
    }

    @Override
    public String getCodingStandardRuleId() {
      return this.name().replace('_','-');
    }

    @Override
    public Implementability getImplementability() {
      return this.implementability;
    }

    public static StandardRule fromString(String id) {

      String tmp = id.replace("-", "_");
      for (StandardRule sr : StandardRule.values()){
        if (sr.name().equals(tmp)) {
          return sr;
        }
      }
      throw new IllegalArgumentException("Could not find a SANS Top 25 rule for input string " + id);
    }

  }

}
