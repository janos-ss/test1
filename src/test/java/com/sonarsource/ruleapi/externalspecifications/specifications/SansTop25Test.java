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
    assertThat((List<String>) updates.get("Labels")).isEmpty();
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
            "SANS Top 25" + newline +
            "Insecure Interaction Between Components  6, unimplementable:  0  specified:  0,  implemented:  0" + newline +
            "Porous Defenses                         11, unimplementable:  4  specified:  0,  implemented:  0" + newline +
            "Risky Resource Management                8, unimplementable:  1  specified:  0,  implemented:  0" + newline +
            "Total                                   25, unimplementable:  5  specified:  0,  implemented:  0" + newline;
    String summaryReport = sans.getSummaryReport("");

    assertThat(summaryReport).isEqualTo(expectedSummaryReport);

    String report = sans.getReport("");
    assertThat(report).contains(expectedSummaryReport);
    assertThat(report).contains("CWE-352");
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
            "SANS Top 25" + newline +
            "Insecure Interaction Between Components  6, unimplementable:  0  specified:  0,  implemented:  0" + newline +
            "Porous Defenses                         11, unimplementable:  4  specified:  0,  implemented:  0" + newline +
            "Risky Resource Management                8, unimplementable:  1  specified:  1,  implemented:  1" + newline +
            "Total                                   25, unimplementable:  5  specified:  1,  implemented:  1" + newline;
    String summaryReport = "";
    summaryReport = sans.getSummaryReport("");

    assertThat(summaryReport).isEqualTo(expectedSummaryReport);

  }

  @Test
  public void testGetters() {

    assertThat(sansTop25.getLanguage()).isNull();
    assertThat(sansTop25.getStandardName()).isEqualTo("SANS Top 25");
    assertThat(sansTop25.getRSpecReferenceFieldName()).isEqualTo("CWE");
    assertThat(sansTop25.getCodingStandardRules()).hasSize(25);
    assertThat(sansTop25.getReferencePattern()).isEqualTo("CWE-\\d+");
    assertThat(sansTop25.getTag()).isEqualTo("sans-top25");
  }

  @Test
  public void testGetSpecifiedByString() {
    SansTop25 sans = new SansTop25();

    sans.populateRulesCoverageMap();

    Rule rule = new Rule("Java");
    rule.setKey("RSPEC-1234");
    List<String> covered = rule.getCoveredLanguages();
    covered.add("Java");
    covered.add("Swift");
    covered.add("HTML");
    List<String> targeted = rule.getTargetedLanguages();
    targeted.add("C");
    targeted.add("C++");
    targeted.add("PL/SQL");

    Rule rule2 = new Rule("JavaScript");
    rule2.setKey("RSPEC-2345");
    rule2.getTargetedLanguages().add("ABAP");

    CodingStandardRuleCoverage cov = sans.getRulesCoverage().get("CWE-829");
    cov.addSpecifiedBy(rule);
    cov.addSpecifiedBy(rule2);

    String expected = "RSPEC-1234 (C, C++, PL/SQL, Java, Swift, HTML); RSPEC-2345 (ABAP)";
    assertThat(sans.getSpecifiedByString(cov)).isEqualTo(expected);

  }

  @Test
  public void testGetCoveredByString() {
    SansTop25 sans = new SansTop25();

    sans.populateRulesCoverageMap();

    Rule rule = new Rule("Java");
    rule.setKey("RSPEC-1234");
    List<String> covered = rule.getCoveredLanguages();
    covered.add("Java");
    covered.add("Swift");
    covered.add("HTML");
    List<String> targeted = rule.getTargetedLanguages();
    targeted.add("C");
    targeted.add("C++");
    targeted.add("PL/SQL");

    Rule rule2 = new Rule("JavaScript");
    rule2.setKey("RSPEC-2345");
    rule2.getTargetedLanguages().add("ABAP");

    CodingStandardRuleCoverage cov = sans.getRulesCoverage().get("CWE-829");
    cov.addImplementedBy(rule);
    cov.addImplementedBy(rule2);

    String expected = "RSPEC-1234 (Java); RSPEC-2345 (JavaScript)";
    assertThat(sans.getCoveredByString(cov)).isEqualTo(expected);

  }


}
