/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.tools.Checkstyle;
import com.sonarsource.ruleapi.externalspecifications.tools.FindBugs;
import com.sonarsource.ruleapi.externalspecifications.tools.ReSharper;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class AbstractReportableExternalToolTest {

  private FindBugs findBugsTestInstance = new FindBugs();
  private Rule rule = new Rule("Java");
  private List<String> list = new ArrayList<>();

  private static final String FB_KEY1 = FindBugs.StandardRule.AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION.name();
  private static final String FB_KEY2 = FindBugs.StandardRule.BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS.name();

  public AbstractReportableExternalToolTest() {
    findBugsTestInstance.populateRulesCoverageMap();
    findBugsTestInstance.computeCoverage();

    rule.setRepo("squid");
    rule.getLegacyKeys().add("S1234");
    rule.setKey("RSPEC-1234");
    rule.setTitle("X should (not) Y");

    list.add(FB_KEY1);
    list.add(FB_KEY2);

    findBugsTestInstance.setCodingStandardRuleCoverageImplemented(list, rule);
    findBugsTestInstance.setCodingStandardRuleCoverageSpecifiedBy(rule, list);
  }

  @Test
  public void getNameIfStandardApplies(){
    Checkstyle checkstyle = new Checkstyle();

    Rule rule = new Rule("Java");
    rule.setKey("S123");
    rule.setTitle("Foo");

    assertThat(checkstyle.getNameIfStandardApplies(rule)).isNull();
  }

  @Test
  public void testFormatLine() {

    FindBugs fbLocal = (FindBugs) SupportedStandard.FINDBUGS.getStandard();

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

  private static String FB_ID = "BC_IMPOSSIBLE_CAST";
  private static String FB_ID2 = "AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION";
  private static String REJECTED_FB_ID = "AM_CREATES_EMPTY_JAR_FILE_ENTRY";


  @Test
  public void testGenerateSummaryReport(){

    FindBugs fb = new FindBugs();
    fb.populateRulesCoverageMap();
    fb.computeCoverage();

    String report = fb.generateSummaryReport();

    assertThat(report).contains("Rule count:");
    assertThat(report).contains("Of Implementable rules:");
  }


  @Test
  public void testGetHtmlDeprecationByToolKey(){

    Checkstyle cs = new Checkstyle();
    cs.populateRulesCoverageMap();

    List<String> ids = new ArrayList<>();

    ids.add(Checkstyle.CheckstyleRule.BOOLEANEXPRESSIONCOMPLEXITY.getCodingStandardRuleId());
    Rule r = new Rule("Java");
    cs.setCodingStandardRuleCoverageImplemented(ids, rule);
    cs.setCodingStandardRuleCoverageSpecifiedBy(rule, ids);

    ids.clear();
    ids.add(Checkstyle.CheckstyleRule.AVOIDSTARIMPORT.getCodingStandardRuleId());
    Rule r2 = new Rule("Java");
    cs.setCodingStandardRuleCoverageSpecifiedBy(rule, ids);

    cs.computeCoverage();

    String report = cs.getHtmlDeprecationByToolKey(RuleManager.SONARQUBE_COM);

    assertThat(report).isNotEmpty();
    assertThat(report).contains("<h3>Rejected " + cs.getStandardName() + " rules</h3>");
    assertThat(report).contains("Pending");
    assertThat(report).contains("Implemented");

  }

  @Test
  public void testComputeCoverage(){
    FindBugs fb = new FindBugs();

    ((AbstractReportableExternalTool)fb).populateRulesCoverageMap();
    fb.computeCoverage();

    assertThat(fb.implementable).isGreaterThan(0);
    assertThat(fb.skipped).isGreaterThan(0);
    assertThat(fb.specified).isEqualTo(0);
    assertThat(fb.implemented).isEqualTo(0);

    Rule rule = new Rule("Java");
    List<String> ids = new ArrayList<>();
    ids.add(FB_ID);

    fb = new FindBugs();
    fb.populateRulesCoverageMap();

    fb.setCodingStandardRuleCoverageImplemented(ids,rule);
    fb.setCodingStandardRuleCoverageSpecifiedBy(rule, ids);

    fb.computeCoverage();
    assertThat(fb.implementable).isGreaterThan(0);
    assertThat(fb.skipped).isGreaterThan(0);
    assertThat(fb.specified).isEqualTo(1);
    assertThat(fb.implemented).isEqualTo(1);
  }

  @Test
  public void testGetUnspecifiedReport(){

    Rule rule = new Rule("Java");
    List<String> ids = new ArrayList<>();
    ids.add(FB_ID);

    FindBugs fb = new FindBugs();
    fb.populateRulesCoverageMap();

    fb.setCodingStandardRuleCoverageImplemented(ids,rule);
    fb.setCodingStandardRuleCoverageSpecifiedBy(rule, ids);

    fb.computeCoverage();

    String report = fb.getUnspecifiedReport();

    assertThat(report).isNotNull();
    assertThat(report).isNotEmpty();
    assertThat(report).doesNotContain(FB_ID);
    assertThat(report).doesNotContain(REJECTED_FB_ID);
    assertThat(report).contains(FB_ID2);
  }

  @Test
  public void testHasLevelReports(){

    Rule rule = new Rule("C#");
    List<String> ids = new ArrayList<>();
    ids.add(ReSharper.ReSharperRule.ACCESSTODISPOSEDCLOSURE.getCodingStandardRuleId());

    ReSharper rs = new ReSharper();
    rs.populateRulesCoverageMap();

    rs.setCodingStandardRuleCoverageImplemented(ids,rule);
    rs.setCodingStandardRuleCoverageSpecifiedBy(rule, ids);

    rs.computeCoverage();

    String report = rs.getUnspecifiedReport();

    assertThat(report).contains("warning");
    assertThat(report).contains("hint");
    assertThat(report).contains("do_not_show");
  }

  @Test
  public void testGetHtmlReport(){
    Rule rule = new Rule("C#");
    List<String> ids = new ArrayList<>();
    ids.add(ReSharper.ReSharperRule.ACCESSTODISPOSEDCLOSURE.getCodingStandardRuleId());

    ReSharper rs = new ReSharper();
    rs.populateRulesCoverageMap();

    rs.setCodingStandardRuleCoverageImplemented(ids,rule);
    rs.setCodingStandardRuleCoverageSpecifiedBy(rule, ids);

    rs.computeCoverage();

    String report = rs.getHtmlReport(null);

    assertThat(report).isNotNull();
    assertThat(report).isNotEmpty();
    assertThat(report).contains("Plugin coverage/deprecation of");
    assertThat(report).contains(Utilities.getFormattedDateString());
  }

}
