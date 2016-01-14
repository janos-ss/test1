/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Language;
import org.junit.Test;

import java.util.*;

import static org.fest.assertions.Assertions.assertThat;


public class SansTop25Test {

  private SansTop25 sansTop25 = new SansTop25();

  @Test
  public void testAddTagIfMissingAddTag() {

    Rule rule = new Rule("");
    rule.setTags(new ArrayList<String>());

    rule.getCwe().add("CWE-89");

    Map<String, Object> updates = new HashMap<>();

    SansTop25.Category.INSECURE_INTERACTION.addTagIfMissing(rule, updates);
    assertThat(updates).hasSize(1);
    assertThat((Set<String>)updates.get("Labels")).hasSize(1);
    assertThat(rule.getTags()).hasSize(1);

  }

  @Test
  public void testAddTagIfMissingDeprecated() {

    Rule rule = new Rule("");
    rule.setStatus(Rule.Status.DEPRECATED);
    rule.setTags(new ArrayList<String>());

    rule.getCwe().add("CWE-89");

    Map<String, Object> updates = new HashMap<>();

    SansTop25.Category.INSECURE_INTERACTION.addTagIfMissing(rule, updates);
    assertThat(updates).hasSize(0);
    assertThat(rule.getTags()).hasSize(0);

  }

  @Test
  public void testAddTagIfMissingDoNothing(){

    Rule rule = new Rule("");
    rule.setTags(new ArrayList<String>());
    rule.getTags().add("sans-top25-insecure");

    rule.getCwe().add("CWE-89");

    Map<String, Object> updates = new HashMap<>();

    SansTop25.Category.INSECURE_INTERACTION.addTagIfMissing(rule, updates);
    assertThat(updates).isEmpty();

  }

  @Test
  public void testAddTagIfMissingRemoveTag(){

    Rule rule = new Rule("");
    rule.setTags(new ArrayList<String>());
    rule.getTags().add("sans-top25-porous");

    Map<String, Object> updates = new HashMap<>();

    SansTop25.Category.POROUS_DEFENSES.addTagIfMissing(rule, updates);
    assertThat(updates).hasSize(1);
    assertThat((Set<String>) updates.get("Labels")).isEmpty();
    assertThat(rule.getTags()).isEmpty();
  }

  @Test
  public void testIsSansRuleItsNot() {

    Rule rule = new Rule("");

    assertThat(SansTop25.Category.POROUS_DEFENSES.isSansCategoryRule(rule, null)).isFalse();

    rule.getCwe().add("CWE-1");
    assertThat(SansTop25.Category.RISKY_RESOURCE.isSansCategoryRule(rule,null)).isFalse();
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
            "Porous Defenses                         11, unimplementable:  2  specified:  0,  implemented:  0" + newline +
            "Risky Resource Management                8, unimplementable:  1  specified:  0,  implemented:  0" + newline +
            "Total                                   25, unimplementable:  3  specified:  0,  implemented:  0" + newline;
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

    SansTop25.Category.POROUS_DEFENSES.checkReferencesInSeeSection(rule);

    assertThat(rule.getCwe()).isEmpty();
    assertThat(rule.getReferences()).isEqualTo(seeSection);

    rule.getCwe().add("CWE-250");
    SansTop25.Category.POROUS_DEFENSES.checkReferencesInSeeSection(rule);

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
            "Porous Defenses                         11, unimplementable:  2  specified:  0,  implemented:  0" + newline +
            "Risky Resource Management                8, unimplementable:  1  specified:  1,  implemented:  1" + newline +
            "Total                                   25, unimplementable:  3  specified:  1,  implemented:  1" + newline;
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
    assertThat(SansTop25.Category.RISKY_RESOURCE.getReferencePattern()).isEqualTo("CWE-\\d+");
    assertThat(SansTop25.Category.INSECURE_INTERACTION.getTag()).isEqualTo("sans-top25-insecure");
    assertThat(SansTop25.Category.RISKY_RESOURCE.getTag()).isEqualTo("sans-top25-risky");
    assertThat(SansTop25.Category.POROUS_DEFENSES.getTag()).isEqualTo("sans-top25-porous");
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

  @Test
  public void testPopulateStandardMap() {
    SansTop25 sans = new SansTop25();

    String id = "CWE-120";

    Rule rule = new Rule("");
    List<String> list = sans.getRspecReferenceFieldValues(rule);
    list.add(id);
    list.add("CWE-234");
    list.add(id);

    Map<String, List<Rule>> standardRules = new TreeMap<>();
    sans.populateStandardMap(standardRules, rule, rule);

    assertThat(standardRules).hasSize(1);
    List<Rule> ruleList = standardRules.get(id);
    assertThat(ruleList).hasSize(2);
    assertThat(ruleList.contains(rule)).isTrue();

  }

  @Test
  public void testGenerateReport() {

    String instance = "http://localhost:9000";

    SansTop25 sans = new SansTop25();
    Map<String, List<Rule>> standardRules = new TreeMap<>();

    String id = "CWE-120";

    Rule rule = new Rule("");
    List<String> list = sans.getRspecReferenceFieldValues(rule);
    list.add(id);
    list.add("89");


    assertThat(sans.generateReport(instance, standardRules)).isNull();

    sans.setLanguage(Language.C);
    assertThat(sans.generateReport(instance, standardRules)).isNull();

    sans.populateStandardMap(standardRules, rule, rule);
    sans.setLanguage(null);
    assertThat(sans.generateReport(instance, standardRules)).isNull();

    sans.setLanguage(Language.C);
    String report = sans.generateReport(instance, standardRules);

    assertThat(report).isNotNull();
    assertThat(report).contains("http://www.sans.org/top25-software-errors/");
    assertThat(report).contains("http://cwe.mitre.org/data/definitions/120");
    assertThat(report).contains("http://www.sans.org/top25-software-errors/#cat2");

  }

}
