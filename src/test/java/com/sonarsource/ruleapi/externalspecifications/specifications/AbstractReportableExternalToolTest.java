/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.SupportedCodingStandard;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;


public class AbstractReportableExternalToolTest {

  private FindBugs findBugsTestInstance = new FindBugs();
  private Rule rule = new Rule("Java");
  private List<String> list = new ArrayList<>();

  private static final String FB_KEY1 = FindBugs.StandardRule.AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION.name();
  private static final String FB_KEY2 = FindBugs.StandardRule.BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS.name();

  public AbstractReportableExternalToolTest() {
    findBugsTestInstance.populateRulesCoverageMap();
    findBugsTestInstance.computeCoverage();

    rule.setKey("S1234");
    rule.setTitle("X should (not) Y");

    list.add(FB_KEY1);
    list.add(FB_KEY2);

    findBugsTestInstance.setCodingStandardRuleCoverageImplemented(list, rule);
    findBugsTestInstance.setCodingStandardRuleCoverageSpecifiedBy(rule, list);
  }

  @Test
  public void testFormatLine() {

    FindBugs fbLocal = (FindBugs) SupportedCodingStandard.FINDBUGS.getCodingStandard();

    String result = String.format("  yo                9   37.67%%%n");
    assertThat(fbLocal.formatLine("yo", 9, (float)37.671293)).isEqualTo(result);
  }

  @Test
  public void testGetCoveringRules() {

    Map<Rule, List<String>> map = findBugsTestInstance.getCoveringRules();
    assertThat(map.size()).isEqualTo(1);
    assertThat(map.get(rule)).contains(FB_KEY1);
    assertThat(map.get(rule)).contains(FB_KEY2);

  }

  @Test
  public void testRuleKeyComparator() {

    Rule r1 = new Rule("");
    r1.setKey("a");

    Rule r2 = new Rule("");
    r2.setKey("b");

    assertThat(findBugsTestInstance.ruleKeyComparator.compare(r1, r2)).isEqualTo(-1);
  }

  @Test
  public void testHtmlReportPieces(){
    String byRuleKey = "<h3>Implemented replacements by SonarQube Java key</h3><table><tr><td><a href='/coding_rules#rule_key=squid%3AS1234'>S1234</a> X should (not) Y<br/>\n" +
            "</td><td>BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS, AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION</td></tr></table><br/><br/>\n";

    assertThat(findBugsTestInstance.getHtmlDeprecationByRuleKey("")).isEqualTo(byRuleKey);

  }

  @Test
  public void testDeprecationReport(){
    String expectedReport = "BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS\tS1234\n" +
            "AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION\tS1234\n";

    assertThat(findBugsTestInstance.getDeprecationReport("")).isEqualTo(expectedReport);

  }

}