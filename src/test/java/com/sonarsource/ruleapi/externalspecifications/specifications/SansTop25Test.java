/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Language;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;


public class SansTop25Test {

  private SansTop25 sansTop25 = new SansTop25();

  @Test
  public void testAddTagIfMissingAddTag() {

    Rule rule = new Rule("");
    rule.setTags(new ArrayList<String>());

    rule.getCwe().add("CWE-89");

    Map<String, Object> updates = new HashMap<String, Object>();

    sansTop25.addTagIfMissing(rule, updates);
    assertThat(updates).hasSize(1);
    assertThat((List<String>)updates.get("Labels")).hasSize(1);
    assertThat(rule.getTags()).hasSize(1);


    updates.clear();
    rule.getCwe().clear();
    sansTop25.addTagIfMissing(rule, updates);

  }

  @Test
  public void testAddTagIfMissingDoNothing(){

    Rule rule = new Rule("");
    rule.setTags(new ArrayList<String>());
    rule.getTags().add("sans-top25");

    rule.getCwe().add("CWE-89");

    Map<String, Object> updates = new HashMap<String, Object>();

    sansTop25.addTagIfMissing(rule, updates);
    assertThat(updates).isEmpty();

  }

  @Test
  public void testAddTagIfMissingRemoveTag(){

    Rule rule = new Rule("");
    rule.setTags(new ArrayList<String>());
    rule.getTags().add("sans-top25");

    Map<String, Object> updates = new HashMap<String, Object>();

    sansTop25.addTagIfMissing(rule, updates);
    assertThat(updates).hasSize(1);
    assertThat((List<String>)updates.get("Labels")).isEmpty();
    assertThat(rule.getTags()).isEmpty();
  }

  @Test
  public void testIsSansRuleItsNot() {

    Rule rule = new Rule("");

    assertThat(sansTop25.isSansRule(rule)).isFalse();

    rule.getCwe().add("CWE-1");
    assertThat(sansTop25.isSansRule(rule)).isFalse();
  }

  @Test
  public void testSummaryReport(){
    SansTop25 sans = new SansTop25();

    sans.populateRulesCoverageMap();
    sans.computeCoverage();

    String newline = String.format("%n");
    String expectedSummaryReport = newline +
            "SANS Top 25 for Java" + newline +
            "Insecure Interaction Between Components  6,  specified:  0,  implemented:  0" + newline +
            "Porous Defenses                         11,  specified:  0,  implemented:  0" + newline +
            "Risky Resource Management                8,  specified:  0,  implemented:  0" + newline +
            "Total                                   25,  specified:  0,  implemented:  0" + newline;
    String summaryReport = sans.getSummaryReport("");

    assertThat(summaryReport).isEqualTo(expectedSummaryReport);
  }

  @Test
  public void testReport(){
    SansTop25 sans = new SansTop25();

    sans.populateRulesCoverageMap();
    sans.computeCoverage();

    String newline = String.format("%n");
    String expectedReport = "Rule           Spec.        Impl.       " + newline +
            " 1) CWE-89                              " + newline +
            " 2) CWE-78                              " + newline +
            " 3) CWE-120                             " + newline +
            " 4) CWE-79                              " + newline +
            " 5) CWE-306                             " + newline +
            " 6) CWE-862                             " + newline +
            " 7) CWE-798                             " + newline +
            " 8) CWE-311                             " + newline +
            " 9) CWE-434                             " + newline +
            "10) CWE-807                             " + newline +
            "11) CWE-250                             " + newline +
            "12) CWE-352                             " + newline +
            "13) CWE-22                              " + newline +
            "14) CWE-494                             " + newline +
            "15) CWE-863                             " + newline +
            "16) CWE-829                             " + newline +
            "17) CWE-732                             " + newline +
            "18) CWE-676                             " + newline +
            "19) CWE-327                             " + newline +
            "20) CWE-131                             " + newline +
            "21) CWE-307                             " + newline +
            "22) CWE-601                             " + newline +
            "23) CWE-134                             " + newline +
            "24) CWE-190                             " + newline +
            "25) CWE-759                             " + newline + newline +
            "SANS Top 25 for Java" + newline +
            "Insecure Interaction Between Components  6,  specified:  0,  implemented:  0" + newline +
            "Porous Defenses                         11,  specified:  0,  implemented:  0" + newline +
            "Risky Resource Management                8,  specified:  0,  implemented:  0" + newline +
            "Total                                   25,  specified:  0,  implemented:  0" + newline;
    String report = sans.getReport("");

    assertThat(report).isEqualTo(expectedReport);
  }

  @Test
  public void testCheckReferencesInSeeSection() {

    Rule rule = new Rule("");

    String seeSection="<li><a href=\"cwe.mitre.org/data/definitions/459\">MITRE, CWE-459</a> - Incomplete Cleanup</li>" +
            "<li><a href=\"http://www.sans.org/top25-software-errors/\">SANS Top 25</a> - Porous Defenses</li>";
    rule.setReferences(seeSection);

    sansTop25.checkReferencesInSeeSection(rule);

    assertThat(rule.getCwe()).isEmpty();
    assertThat(rule.getReferences()).isEqualTo(seeSection);

    rule.getCwe().add("CWE-250");
    sansTop25.checkReferencesInSeeSection(rule);

    assertThat(rule.getCwe()).hasSize(1);
    assertThat(rule.getReferences()).isEqualTo(seeSection);
  }

  @Test
  public void testComputeCoverageRulesFound() {
    SansTop25 sans = new SansTop25();

    sans.populateRulesCoverageMap();

    Rule rule = new Rule("");

    CodingStandardRuleCoverage cov = sans.getRulesCoverage().get("CWE-829");
    cov.addSpecifiedBy(rule);
    cov.addImplementedBy(rule);
    sans.computeCoverage();


    String newline = String.format("%n");
    String expectedSummaryReport = newline +
            "SANS Top 25 for Java" + newline +
            "Insecure Interaction Between Components  6,  specified:  0,  implemented:  0" + newline +
            "Porous Defenses                         11,  specified:  0,  implemented:  0" + newline +
            "Risky Resource Management                8,  specified:  1,  implemented:  1" + newline +
            "Total                                   25,  specified:  1,  implemented:  1" + newline;
    String summaryReport = "";
    summaryReport = sans.getSummaryReport("");

    assertThat(summaryReport).isEqualTo(expectedSummaryReport);

  }

  @Test
  public void testGetters() {

    assertThat(sansTop25.getLanguage()).isEqualTo(Language.JAVA);
    assertThat(sansTop25.getStandardName()).isEqualTo("SANS Top 25");
    assertThat(sansTop25.getRSpecReferenceFieldName()).isEqualTo("CWE");
    assertThat(sansTop25.getCodingStandardRules()).hasSize(25);
    assertThat(sansTop25.getReferencePattern()).isEqualTo("CWE-\\d+");
    assertThat(sansTop25.getTag()).isEqualTo("sans-top25");
  }

}
