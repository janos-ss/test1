/*
 * Copyright (C) 2014 SonarSource SA
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
  public void testReportsEmpty() {

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
  public void testReportsNonEmpty() {

    MisraC2004 c4 = new MisraC2004();
    c4.populateRulesCoverageMap();

    Rule rule = new Rule("C");
    List<String> ids = new ArrayList<>();
    ids.add("1.4");
    ids.add("1.5");

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

    misraC2004.computeCoverage();

    Rule rule = new Rule("C");
    rule.setRepo("c");
    rule.setKey("RSPEC-1234");
    rule.setTitle("Rule title...");
    misraC2004.getRulesCoverage().get("2.1").addImplementedBy(rule);

    String report = misraC2004.getHtmlReport("");
    String expectedReport = "<h2>SonarQube C Plugin coverage of MISRA C 2004</h2>" +
            "These are the MISRA C 2004 rules covered for C by the " +
            "<a href='http://sonarsource.com'>SonarSource</a> " +
            "<a href='http://www.sonarsource.com/products/plugins/languages/cpp/'>C/C++ plugin</a>." +
            "<table>" +
            "<tr><td>1.1</td><td>Not statically checkable</td></tr><tr><td>1.2</td><td>Not statically checkable</td></tr>" +
            "<tr><td>1.3</td><td>Not statically checkable</td></tr><tr><td>1.4</td><td>Not statically checkable</td></tr>" +
            "<tr><td>1.5</td><td>Not statically checkable</td></tr>" +
            "<tr><td>2.1</td><td><a href='/coding_rules#rule_key=c%3AS1234'>S1234</a> Rule title...<br/>\n" +
            "</td></tr><tr><td>3.1</td><td>Not statically checkable</td></tr><tr><td>3.2</td><td>Not statically checkable</td></tr>" +
            "<tr><td>3.3</td><td>Not statically checkable</td></tr><tr><td>3.5</td><td>Not statically checkable</td></tr>" +
            "<tr><td>3.6</td><td>Not statically checkable</td></tr><tr><td>16.10</td><td>Not statically checkable</td></tr>" +
            "<tr><td>18.3</td><td>Not statically checkable</td></tr><tr><td>20.3</td><td>Not statically checkable</td></tr>" +
            "<tr><td>21.1</td><td>Not statically checkable</td></tr></table>" +
            "<h3>Summary</h3>" +
            "<table><tr><td>Mandatory rules covered:</td><td>1, 0.91%</td></tr>" +
            "<tr><td>Optional rules covered:</td><td>0, 0.00%</td></tr>" +
            "<tr><td>Total:</td><td>1, 0.78%</td></tr></table>";

    assertThat(report).isEqualTo(expectedReport);
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
  public void testIsRuleMandatory() {

    MisraC2004 m4 = new MisraC2004();

    assertThat(m4.isRuleMandatory(null)).isFalse();
    assertThat(m4.isRuleMandatory("1.5")).isFalse();
    assertThat(m4.isRuleMandatory("1.4")).isTrue();
    assertThat(m4.isRuleMandatory("0.0")).isFalse();
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
