/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class SansTop25Test {

  private SansTop25 sansTop25 = new SansTop25();

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
    assertThat(sansTop25.getRSpecReferenceFieldName()).isEqualTo("SANS Top 25");
    assertThat(sansTop25.getCodingStandardRules()).hasSize(25);
    assertThat(SansTop25.Category.INSECURE_INTERACTION.getReferencePattern()).isEqualTo("Insecure Interaction Between Components");
    assertThat(SansTop25.Category.RISKY_RESOURCE.getReferencePattern()).isEqualTo("Risky Resource Management");
    assertThat(SansTop25.Category.POROUS_DEFENSES.getReferencePattern()).isEqualTo("Porous Defenses");
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
    Set<String> covered = rule.getCoveredLanguages();
    covered.add("Java");
    covered.add("Swift");
    covered.add("HTML");
    Set<String> targeted = rule.getTargetedLanguages();
    targeted.add("C");
    targeted.add("C++");
    targeted.add("PL/SQL");

    Rule rule2 = new Rule("JavaScript");
    rule2.setKey("RSPEC-2345");
    rule2.getTargetedLanguages().add("ABAP");

    CodingStandardRuleCoverage cov = sans.getRulesCoverage().get("CWE-829");
    cov.addSpecifiedBy(rule);
    cov.addSpecifiedBy(rule2);

    String expected = "RSPEC-1234 (C, C++, HTML, Java, PL/SQL, Swift); RSPEC-2345 (ABAP)";
    assertThat(sans.getSpecifiedByString(cov)).isEqualTo(expected);

  }

  @Test
  public void testGetCoveredByString() {
    SansTop25 sans = new SansTop25();

    sans.populateRulesCoverageMap();

    Rule rule = new Rule("Java");
    rule.setKey("RSPEC-1234");
    Set<String> covered = rule.getCoveredLanguages();
    covered.add("Java");
    covered.add("Swift");
    covered.add("HTML");
    Set<String> targeted = rule.getTargetedLanguages();
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
  public void testGenerateReport() {

    String instance = "http://localhost:9000";

    SansTop25 sans = new SansTop25();

    String id = "CWE-120";

    Rule rule = new Rule("");
    List<String> list = sans.getRspecReferenceFieldValues(rule);
    list.add(id);
    list.add("89");


    assertThat(sans.generateReport(instance)).isNull();

    sans.setLanguage(Language.C);
    assertThat(sans.generateReport(instance)).isNull();

    sans.populateRulesCoverageMap();
    sans.setLanguage(null);

    assertThat(sans.generateReport(instance)).isNull();

    sans.setCodingStandardRuleCoverageImplemented(list, rule);

    sans.setLanguage(Language.C);
    String report = sans.generateReport(instance);

    assertThat(report).isNotNull();
    assertThat(report).contains("http://www.sans.org/top25-software-errors/");
    assertThat(report).contains("http://cwe.mitre.org/data/definitions/120");
    assertThat(report).contains("http://www.sans.org/top25-software-errors/#cat2");
    assertThat(report).contains(Utilities.getFormattedDateString());
  }

  @Test
  public void testGetNameIfStandardApplies(){
    Rule rule = new Rule("Java");

    SansTop25 sansTop25 = new SansTop25();

    rule.getCwe().add("CWE-1");
    assertThat(sansTop25.getNameIfStandardApplies(rule)).isNull();

    rule.getCwe().add("CWE-89");
    assertThat(sansTop25.getNameIfStandardApplies(rule)).isEqualTo(sansTop25.getStandardName());
  }

}
