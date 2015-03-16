/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    List<String> ids = new ArrayList<String>();
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

    String report = misraC2004.getHtmlReport("");
    String expectedReport = "<h2>C coverage of MISRA C 2004</h2>" +
            "<table><tr><td>1.1</td><td>Not statically checkable</td></tr><tr><td>1.2</td><td>Not statically checkable</td></tr>" +
            "<tr><td>1.3</td><td>Not statically checkable</td></tr><tr><td>1.4</td><td>Not statically checkable</td></tr>" +
            "<tr><td>1.5</td><td>Not statically checkable</td></tr><tr><td>3.1</td><td>Not statically checkable</td></tr>" +
            "<tr><td>3.2</td><td>Not statically checkable</td></tr><tr><td>3.3</td><td>Not statically checkable</td></tr>" +
            "<tr><td>3.5</td><td>Not statically checkable</td></tr><tr><td>3.6</td><td>Not statically checkable</td></tr>" +
            "<tr><td>16.10</td><td>Not statically checkable</td></tr><tr><td>18.3</td><td>Not statically checkable</td></tr>" +
            "<tr><td>20.3</td><td>Not statically checkable</td></tr><tr><td>21.1</td><td>Not statically checkable</td></tr></table>";

    assertThat(report).isEqualTo(expectedReport);
  }

  @Test
  public void testIsFieldEntryFormatNeedUpdating() {

    MisraC2004 misra = new MisraC2004();

    Rule rule = new Rule("C");
    rule.setKey("RSPEC-1345");
    rule.getMisraC04().add("hello");

    Map<String, Object> map = new HashMap<String, Object>();

    assertThat(map).isEmpty();

    misra.isFieldEntryFormatNeedUpdating(map, rule);

    assertThat(map).isEmpty();

  }
}
