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

  FindBugs fb = new FindBugs();
  Rule rule = new Rule("Java");
  List<String> list = new ArrayList<>();

  public AbstractReportableExternalToolTest() {
    fb.populateRulesCoverageMap();
    fb.computeCoverage();

    rule.setKey("S1234");
    rule.setTitle("X should (not) Y");

    list.add(FindBugs.StandardRule.AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION.name());
    list.add(FindBugs.StandardRule.BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS.name());

    fb.setCodingStandardRuleCoverageImplemented(list, rule);
    fb.setCodingStandardRuleCoverageSpecifiedBy(rule, list);
  }

  @Test
  public void testFormatLine() {

    FindBugs fbLocal = (FindBugs) SupportedCodingStandard.FINDBUGS.getCodingStandard();

    String result = String.format("  yo                9   37.67%%%n");
    assertThat(fbLocal.formatLine("yo", 9, (float)37.671293)).isEqualTo(result);
  }

  @Test
  public void testGetCoveringRules() {

    Map<Rule, List<String>> map = fb.getCoveringRules();
    assertThat(map.size()).isEqualTo(1);
    assertThat(map.get(rule)).isEqualTo(list);

  }

  @Test
  public void testRuleKeyComparator() {

    Rule r1 = new Rule("");
    r1.setKey("a");

    Rule r2 = new Rule("");
    r2.setKey("b");

    assertThat(fb.ruleKeyComparator.compare(r1, r2)).isEqualTo(-1);
  }

  @Test
  public void testToolKeyComparator(){

    CodingStandardRuleCoverage csrc1 = new CodingStandardRuleCoverage();
    csrc1.setCodingStandardRuleId("a");

    CodingStandardRuleCoverage csrc2 = new CodingStandardRuleCoverage();
    csrc2.setCodingStandardRuleId("b");

    assertThat(fb.toolKeyComparator.compare(csrc1, csrc2)).isEqualTo(-1);
  }

  @Test
  public void testHtmlReportPieces(){
    String byRuleKey = "<h3>Implemented replacements by SonarQube Java key</h3><table><tr><td><a href='/coding_rules#rule_key=squid%3AS1234'>S1234</a> X should (not) Y<br/>\n" +
            "</td><td>BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS, AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION</td></tr></table><br/><br/>";

    assertThat(fb.getHtmlDeprecationByRuleKey("")).isEqualTo(byRuleKey);

    String byToolKey = "<h3>Implemented replacements by FindBugs key</h3><table><tr><td>AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION</td><td><a href='/coding_rules#rule_key=squid%3AS1234'>S1234</a> X should (not) Y<br/>\n" +
            "</td></tr><tr><td>BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS</td><td><a href='/coding_rules#rule_key=squid%3AS1234'>S1234</a> X should (not) Y<br/>\n" +
            "</td></tr></table><br/><br/>";

    assertThat(fb.getHtmlDeprecationByToolKey("")).isEqualTo(byToolKey);
  }

}
