/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.specifications.MisraC2004;
import com.sonarsource.ruleapi.utilities.RuleException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class AbstractMisraSpecificationTest {

  @Test
  public void testSummaryEmpty() {

    MisraC2004 c4 = new MisraC2004();
    c4.populateRulesCoverageMap();

    String summaryReport = "";
    try {
      summaryReport = c4.getSummaryReport();
    } catch (RuleException e) {
      e.printStackTrace();
    }

    String expected = "MISRA C 2004\n" +
            "Mandatory:\tSpecified: 121\tImplemented: 0\t=> 0.0%\n" +
            "Optional:\tSpecified: 20\tImplemented: 0\t=> 0.0%\n" +
            "Total:\tSpecified: 141\tImplemented: 0\t=> 0.0%";

    assertThat(summaryReport).isEqualTo(expected);
  }

  @Test
  public void testSummaryNonEmpty() {

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
    try {
      summaryReport = c4.getSummaryReport();
    } catch (RuleException e) {
      e.printStackTrace();
    }

    String expected = "MISRA C 2004\n" +
            "Mandatory:\tSpecified: 121\tImplemented: 2\t=> 1.65%\n" +
            "Optional:\tSpecified: 20\tImplemented: 2\t=> 10.0%\n" +
            "Total:\tSpecified: 141\tImplemented: 4\t=> 2.84%";

    assertThat(summaryReport).isEqualTo(expected);
  }

  @Test
  public void testSummary() {

    MisraC2004 c4 = new MisraC2004();
    c4.populateRulesCoverageMap();

    Rule rule = new Rule("C");
    rule.setMisraC04(new ArrayList<String>());

  }

}
