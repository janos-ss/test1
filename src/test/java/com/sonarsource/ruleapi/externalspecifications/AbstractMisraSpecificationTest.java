/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.misra.MisraC2004;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class AbstractMisraSpecificationTest {

  @Test
  public void reportsEmpty() {

    MisraC2004 c4 = new MisraC2004();
    c4.populateRulesCoverageMap();

    String summaryReport = "";
    String report = "";
    summaryReport = c4.getSummaryReport(null);
    report = c4.getReport(null);

    String linebreak = String.format("%n");
    String expectedSummary = "MISRA C 2004" + linebreak +
            "Mandatory:\tSpecified: 110\tImplemented: 0\t=> 0.00%" + linebreak +
            "Optional:\tSpecified: 18\tImplemented: 0\t=> 0.00%" + linebreak +
            "Total:\tSpecified: 128\tImplemented: 0\t=> 0.00%" + linebreak;

    assertThat(summaryReport).isEqualTo(expectedSummary);
    assertThat(report).endsWith(summaryReport);
    assertThat(report).contains("1.1\tS: NA\t\tC: NA\t\tI: N");

  }

  @Test
  public void getBadgeValue() {

    MisraC2004 c4 = new MisraC2004();
    c4.populateRulesCoverageMap();

    assertThat(c4.getBadgeValue(null)).isEqualTo("0");
  }

    @Test
  public void testReportsNonEmpty() {

    MisraC2004 c4 = new MisraC2004();
    c4.populateRulesCoverageMap();

    Rule rule = new Rule("C");
    List<String> ids = new ArrayList<>();
    ids.add("2.1");  // implementable, required
    ids.add("2.4");  // implementable, optional

    c4.setCodingStandardRuleCoverageSpecifiedBy(rule, ids);
    c4.setCodingStandardRuleCoverageImplemented(ids,rule);

    c4.computeCoverage();

    String summaryReport = "";
    String report = "";
    summaryReport = c4.getSummaryReport(null);
    report = c4.getReport(null);

    String linebreak = String.format("%n");

    String expectedSummary = "MISRA C 2004" + linebreak +
            "Mandatory:\tSpecified: 110\tImplemented: 1\t=> 0.91%" + linebreak +
            "Optional:\tSpecified: 18\tImplemented: 1\t=> 5.56%" + linebreak +
            "Total:\tSpecified: 128\tImplemented: 2\t=> 1.56%" + linebreak;

    assertThat(summaryReport).isEqualTo(expectedSummary);
    assertThat(report).contains(expectedSummary);
    assertThat(report).contains("2.1\tS: NA\t\tC: NA\t\tI: N");

  }

  @Test
  public void testHtmlReport() {
    MisraC2004 misraC2004 = new MisraC2004();
    misraC2004.populateRulesCoverageMap();

    Rule rule = new Rule("C");
    rule.setRepo("c");
    rule.setKey("RSPEC-1234");
    rule.setTitle("Rule title...");
    misraC2004.getRulesCoverage().get("2.1").addImplementedBy(rule);

    misraC2004.computeCoverage();

    String report = misraC2004.getHtmlReport("");

    assertThat(report).contains(rule.getTitle());
  }

  @Test
  public void testIsFieldEntryFormatNeedUpdating() {

    MisraC2004 misra = new MisraC2004();

    Rule rule = new Rule("C");
    rule.setKey("RSPEC-1345");
    rule.getMisraC04().add("hello");

    List<String> updates = new ArrayList<>();

    assertThat(misra.doesReferenceNeedUpdating("hello",updates, rule.getKey())).isFalse();

  }

  @Test
  public void testGetCodingStandardRuleFromId() {

    MisraC2004 m4 = new MisraC2004();

    assertThat(m4.getCodingStandardRuleFromId(null)).isNull();

    assertThat(m4.getCodingStandardRuleFromId("1.1")).isEqualTo(MisraC2004.StandardRule.MISRAC2004_1POINT1);

  }

  @Test
  public void testSetRspecReferenceFieldValues() {
    MisraC2004 m4 = new MisraC2004();
    Rule rule = new Rule("C");
    List<String> ids = new ArrayList<>(3);
    ids.add("red");
    ids.add("green");
    ids.add("blue");

    m4.setRspecReferenceFieldValues(rule, ids);

    assertThat(rule.getMisraC04()).isEqualTo(ids);
  }

  @Test
  public void testGetReportTypes(){
    MisraC2004 mc = new MisraC2004();
    assertThat(mc.getReportTypes()).isNotEmpty();
  }

}
