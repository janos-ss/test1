/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.DerivativeTaggableStandard;
import com.sonarsource.ruleapi.utilities.ComparisonUtilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class SansTop25 extends Cwe implements DerivativeTaggableStandard {

  private static final Logger LOGGER = Logger.getLogger(SansTop25.class.getName());


  private static final String NAME = "SANS Top 25";
  private static final String TAG = "sans-top25";

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
  public String getStandardName() {

    return NAME;
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

  private boolean isSansRule(Rule rule) {

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
    CWE_79  (4,  Category.INSECURE_INTERACTION, "Improper Neutralization of Input During Web Page Generation ('Cross-site Scripting')"),
    CWE_434 (9,  Category.INSECURE_INTERACTION, "Unrestricted Upload of File with Dangerous Type"),
    CWE_352 (12, Category.INSECURE_INTERACTION, "Cross-Site Request Forgery (CSRF)"),
    CWE_601 (22, Category.INSECURE_INTERACTION, "URL Redirection to Untrusted Site ('Open Redirect')"),
    CWE_120 (3,  Category.RISKY_RESOURCE, "Buffer Copy without Checking Size of Input ('Classic Buffer Overflow')"),
    CWE_22  (13, Category.RISKY_RESOURCE, "Improper Limitation of a Pathname to a Restricted Directory ('Path Traversal')"),
    CWE_494 (14, Category.RISKY_RESOURCE, "Download of Code Without Integrity Check"),
    CWE_829 (16, Category.RISKY_RESOURCE, "Inclusion of Functionality from Untrusted Control Sphere"),
    CWE_676 (18, Category.RISKY_RESOURCE, "Use of Potentially Dangerous Function"),
    CWE_131 (20, Category.RISKY_RESOURCE, "Incorrect Calculation of Buffer Size"),
    CWE_134 (23, Category.RISKY_RESOURCE, "Uncontrolled Format String"),
    CWE_190 (24, Category.RISKY_RESOURCE, "Integer Overflow or Wraparound"),
    CWE_306 (5,  Category.POROUS_DEFENSES, "Missing Authentication for Critical Function"),
    CWE_862 (6,  Category.POROUS_DEFENSES, "Missing Authorization"),
    CWE_798 (7,  Category.POROUS_DEFENSES, "Use of Hard-coded Credentials"),
    CWE_311 (8,  Category.POROUS_DEFENSES, "Missing Encryption of Sensitive Data"),
    CWE_807 (10, Category.POROUS_DEFENSES, "Reliance on Untrusted Inputs in a Security Decision"),
    CWE_250 (11, Category.POROUS_DEFENSES, "Execution with Unnecessary Privileges"),
    CWE_863 (15, Category.POROUS_DEFENSES, "Incorrect Authorization"),
    CWE_732 (17, Category.POROUS_DEFENSES, "Incorrect Permission Assignment for Critical Resource"),
    CWE_327 (19, Category.POROUS_DEFENSES, "Use of a Broken or Risky Cryptographic Algorithm"),
    CWE_307 (21, Category.POROUS_DEFENSES, "Improper Restriction of Excessive Authentication Attempts"),
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
