/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.domain;

import com.sonarsource.ruleapi.get.JiraHelper;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

public class RuleComparisonTest {

  private static final String LANG = "Java";
  private static final RuleComparison blankComparison = new RuleComparison(new Rule(""),new Rule(""));


  @Test
  public void testCompareNullRules() throws Exception {
    RuleComparison rc = new RuleComparison(null,null);
    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void testCompareOneNullRule() throws Exception {
    Rule spec = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, null);

    assertThat(rc.compare()).isEqualTo(-1);
  }

  @Test
  public void testCompareTagsEq() throws Exception {
    Rule spec = new Rule(LANG);
    Set<String> specTags = new HashSet<String>();
    specTags.add("clumsy");
    specTags.add("java8");
    spec.setTags(specTags);

    Rule impl = new Rule(LANG);
    Set<String> implTags = new HashSet<String>();
    implTags.add("java8");
    implTags.add("clumsy");
    impl.setTags(implTags);

    RuleComparison rc = new RuleComparison(spec, impl);

    assertThat(rc.toString()).hasSize(0);
  }

  @Test
  public void testCompareTagsDifferentTags() throws Exception {
    Rule spec = new Rule(LANG);
    Set<String> specTags = new HashSet<String>();
    specTags.add("clumsy");
    specTags.add("java8");
    spec.setTags(specTags);

    Rule impl = new Rule(LANG);
    Set<String> implTags = new HashSet<String>();
    implTags.add("performance");
    implTags.add("bug");
    impl.setTags(implTags);

    RuleComparison rc = new RuleComparison(spec, impl);

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void testTagsDifferentTagsToString() throws Exception {
    Rule spec = new Rule(LANG);
    Set<String> specTags = new HashSet<String>();
    specTags.add("clumsy");
    specTags.add("java8");
    spec.setTags(specTags);

    Rule impl = new Rule(LANG);
    Set<String> implTags = new HashSet<String>();
    implTags.add("performance");
    implTags.add("bug");
    impl.setTags(implTags);

    RuleComparison rc = new RuleComparison(spec, impl);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  tags: +clumsy, java8; -bug, performance\n" +
            "    spec: clumsy, java8\n" +
            "    impl: bug, performance\n");
  }

  @Test
  public void testCompareTagsTagMissing() throws Exception {
    Rule spec = new Rule(LANG);
    Set<String> specTags = new HashSet<String>();
    specTags.add("clumsy");
    specTags.add("java8");
    spec.setTags(specTags);

    Rule impl = new Rule(LANG);
    Set<String> implTags = new HashSet<String>();
    implTags.add("clumsy");
    impl.setTags(implTags);

    RuleComparison rc = new RuleComparison(spec, impl);

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void testCompareParameterListEq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    List<Parameter> specList = spec.getParameterList();
    List<Parameter> implList = impl.getParameterList();

    Parameter p = new Parameter();
    p.setKey("aParam");
    p.setDescription("a param");
    p.setDefaultVal("blah");

    specList.add(p);

    p.setType("boolean");
    implList.add(p);

    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void testCompareParameterListParamDifference() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    List<Parameter> specList = spec.getParameterList();
    List<Parameter> implList = impl.getParameterList();

    Parameter p = new Parameter();
    p.setKey("aParam");
    p.setDescription("a param");
    p.setDefaultVal("blah");

    specList.add(p);

    p = new Parameter();
    p.setKey("aParam");
    p.setDescription("a new param");
    p.setDefaultVal("blah");
    implList.add(p);

    assertThat(rc.compare()).isEqualTo(2);
  }

  @Test
  public void testCompareParameterListParamOneEmpty() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    List<Parameter> specList = spec.getParameterList();

    Parameter p = new Parameter();
    p.setKey("aParam");
    p.setDescription("a param");
    p.setDefaultVal("blah");

    specList.add(p);

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void testParameterListParamOneEmptyToString() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    List<Parameter> specList = spec.getParameterList();

    Parameter p = new Parameter();
    p.setKey("aParam");
    p.setDescription("a param");
    p.setDefaultVal("blah");

    specList.add(p);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  parameter list\n" +
            "    spec: * key = aParam\n" +
            "* description = a param\n" +
            "* default = blah\n" +
            "\n" +
            "    impl: \n");
  }

  @Test
  public void testCompareSqaleConstantCostEasy() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleConstantCostOrLinearThreshold("5min");
    impl.setSqaleConstantCostOrLinearThreshold("5min");

    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void testCompareSqaleConstantCostHard() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleConstantCostOrLinearThreshold("5 MN");
    impl.setSqaleConstantCostOrLinearThreshold("5min");

    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void testCompareSqaleConstantCostNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleConstantCostOrLinearThreshold("1 h");
    impl.setSqaleConstantCostOrLinearThreshold("5min");

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void testCompareSqaleConstantCostWithNull() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleConstantCostOrLinearThreshold("5min");

    assertThat(rc.compare()).isEqualTo(-1);
  }

  @Test
  public void testCompareTitle() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setTitle("title");
    impl.setTitle("title");

    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void testCompareTitleNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setTitle("title");
    impl.setTitle("a title");

    assertThat(rc.compare()).isEqualTo(19);
  }

  @Test
  public void testCompareSeverity() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSeverity(Rule.Severity.CRITICAL);
    impl.setSeverity(Rule.Severity.CRITICAL);

    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void testCompareSeverityNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSeverity(Rule.Severity.CRITICAL);
    impl.setSeverity(Rule.Severity.MAJOR);

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void testCompareTemplate() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setTemplate(false);
    impl.setTemplate(false);

    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void testCompareTemplateNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setTemplate(false);
    impl.setTemplate(true);

    assertThat(rc.compare()).isEqualTo(-1);
  }

  @Test
  public void testCompareMessage() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setMessage("message");
    impl.setMessage("message");

    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void testToStringParameterListEq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    List<Parameter> specList = spec.getParameterList();
    List<Parameter> implList = impl.getParameterList();

    Parameter p = new Parameter();
    p.setKey("aParam");
    p.setDescription("a param");
    p.setDefaultVal("blah");

    specList.add(p);

    p.setType("boolean");
    implList.add(p);

    assertThat(rc.toString()).hasSize(0);
  }

  @Test
  public void testToStringParameterListParamDifference() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    List<Parameter> specList = spec.getParameterList();
    List<Parameter> implList = impl.getParameterList();

    Parameter p = new Parameter();
    p.setKey("aParam");
    p.setDescription("a param");
    p.setDefaultVal("blah");

    specList.add(p);

    p = new Parameter();
    p.setKey("aParam");
    p.setDescription("a new param");
    p.setDefaultVal("blah");
    implList.add(p);

    assertThat(rc.compare()).isEqualTo(2);
  }

  @Test
  public void testToStringParameterListParamOneEmpty() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    List<Parameter> specList = spec.getParameterList();

    Parameter p = new Parameter();
    p.setKey("aParam");
    p.setDescription("a param");
    p.setDefaultVal("blah");

    specList.add(p);

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void testToStringSqaleConstantCostEasy() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleConstantCostOrLinearThreshold("5min");
    impl.setSqaleConstantCostOrLinearThreshold("5min");

    assertThat(rc.toString()).hasSize(0);
  }

  @Test
  public void testToStringSqaleConstantCostHard() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleConstantCostOrLinearThreshold("5 MN");
    impl.setSqaleConstantCostOrLinearThreshold("5min");

    assertThat(rc.toString()).hasSize(0);
  }

  @Test
  public void testToStringSqaleConstantCostNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleConstantCostOrLinearThreshold("1 h");
    impl.setSqaleConstantCostOrLinearThreshold("5min");

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void testToStringTitle() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setTitle("title");
    impl.setTitle("title");

    assertThat(rc.toString()).hasSize(0);
  }

  @Test
  public void testToStringTitleNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setTitle("title");
    impl.setTitle("a title");

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  title\n" +
            "    spec: title\n" +
            "    impl: a title\n");
  }

  @Test
  public void testToStringSeverity() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSeverity(Rule.Severity.CRITICAL);
    impl.setSeverity(Rule.Severity.CRITICAL);

    assertThat(rc.toString()).hasSize(0);
  }

  @Test
  public void testToStringSeverityNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSeverity(Rule.Severity.CRITICAL);
    impl.setSeverity(Rule.Severity.MAJOR);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  severity\n" +
            "    spec: CRITICAL\n" +
            "    impl: MAJOR\n");
  }


  @Test
  public void testToStringTemplate() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setTemplate(false);
    impl.setTemplate(false);

    assertThat(rc.toString()).hasSize(0);
  }

  @Test
  public void testToStringTemplateNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setTemplate(false);
    impl.setTemplate(true);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  template\n" +
            "    spec: false\n" +
            "    impl: true\n");
  }

  @Test
  public void testToStringMessage() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setMessage("message");
    impl.setMessage("message");

    assertThat(rc.toString()).hasSize(0);
  }

  @Test
  public void differentDescription() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String desc = "this is a test";

    spec.setDescription(desc);

    assertThat(rc.compare()).isEqualTo(14);
  }

  @Test
  public void differentDescriptionToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String desc = "this is a test";

    spec.setDescription(desc);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  description text\n" +
            "    spec: this\n" +
            "    impl: \n");
  }

  @Test
  public void differentNoncompliant() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setNonCompliant(str);

    assertThat(rc.compare()).isEqualTo(14);
  }

  @Test
  public void differentNoncompliantToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setNonCompliant(str);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  noncompliant code example\n" +
            "    spec: this\n" +
            "    impl: \n");
  }

  @Test
  public void differentCompliant() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setCompliant(str);

    assertThat(rc.compare()).isEqualTo(14);
  }

  @Test
  public void differentCompliantToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setCompliant(str);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  compliant solution\n" +
            "    spec: this\n" +
            "    impl: \n");
  }

  @Test
  public void differentReferences() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setReferences(str);

    assertThat(rc.compare()).isEqualTo(14);
  }

  @Test
  public void differentReferencesToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setReferences(str);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  references\n" +
            "    spec: this\n" +
            "    impl: \n");
  }

  @Test
  public void differentExceptions() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setExceptions(str);

    assertThat(rc.compare()).isEqualTo(14);
  }

  @Test
  public void differentExceptionsToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setExceptions(str);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  exceptions\n" +
            "    spec: this\n" +
            "    impl: \n");
  }

  @Test
  public void differentSqaleChar() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setSqaleCharac(str);

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void differentSqaleCharToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setSqaleCharac(str);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  SQALE characteristic\n" +
            "    spec: this is a test\n" +
            "    impl: null\n");
  }

  @Test
  public void differentSqaleSubChar() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "Processor use";

    JiraHelper.setSubcharacteristic(spec, str);

    assertThat(rc.compare()).isEqualTo(-1);
  }

  @Test
  public void differentSqaleSubCharToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "Memory use";

    JiraHelper.setSubcharacteristic(spec, str);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  SQALE sub-characteristic\n" +
            "    spec: MEMORY_EFFICIENCY\n" +
            "    impl: null\n");
  }

  @Test
  public void differentSqaleRemediation() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);

    assertThat(rc.compare()).isEqualTo(-1);
  }

  @Test
  public void differentSqaleRemediationToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  SQALE remediation function\n" +
            "    spec: CONSTANT_ISSUE\n" +
            "    impl: null\n");
  }

  @Test
  public void differentSqaleLinearArg() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setSqaleLinearArgDesc(str);

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void differentSqaleLinearArgToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setSqaleLinearArgDesc(str);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  SQALE linear argument\n" +
            "    spec: this is a test\n" +
            "    impl: null\n");
  }

  @Test
  public void differentSqaleLinearFactor() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setSqaleLinearFactor(str);

    assertThat(rc.compare()).isEqualTo(-1);
  }

  @Test
  public void differentButEqualSqaleLinearFactor() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleLinearFactor("5min");
    impl.setSqaleLinearFactor("5mn");

    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void differentSqaleLinearFactorToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setSqaleLinearFactor(str);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  SQALE linear factor\n" +
            "    spec: this is a test\n" +
            "    impl: null\n");
  }

  @Test
  public void testTitle() {

    Rule one = new Rule("");
    Rule two = new Rule("");
    RuleComparison rc = new RuleComparison(one, two);

    String a1 = "SQL EXISTS subqueries should not be used ";
    String a2 = "SQL EXISTS subqueries should not be used";

    String b1 = "\"[if ... else if|IF ELSEIF|IF ... ELSIF]\" constructs shall be terminated with an \"[else|ELSE]\" clause";
    String b2 = "\"IF ELSEIF\" constructs shall be terminated with an \"ELSE\" clause";

    String c1 = "[Report/]Program names should comply with a naming convention";
    String c2 = "Report/Program names should comply with a naming convention";

    one.setTitle(a1);
    two.setTitle(a2);
    assertThat(rc.compareTitle()).isEqualTo(0);

    one.setTitle(b1);
    two.setTitle(b2);
    assertThat(rc.compareTitle()).isEqualTo(0);

    one.setTitle(c1);
    two.setTitle(c2);
    assertThat(rc.compareTitle()).isEqualTo(0);
  }

  @Test
  public void testLogDifference() {

    Rule rule1 = new Rule("");
    rule1.setTitle("Rule1 title");
    Rule rule2 = new Rule("");
    rule2.setTitle("Rule2 title");

    RuleComparison rc = new RuleComparison(rule1, rule2);

    StringBuilder sb = new StringBuilder();
    rc.logDifference(sb, "Title", rule1.getTitle(), rule2.getTitle());
    assertThat(sb.toString()).isEqualTo("  Title\n" +
            "    spec: Rule1 title\n" +
            "    impl: Rule2 title\n");

  }

  @Test
  public void testDifferentStatus() {

    Rule rule1 = new Rule("");
    rule1.setTitle("Rule title...");
    Rule rule2 = new Rule("");
    rule2.setTitle("Rule title...");
    rule2.setStatus(Rule.Status.DEPRECATED);

    RuleComparison rc = new RuleComparison(rule1, rule2);

    String expectedReport = "null\n" +
            "  status\n" +
            "    spec: null\n" +
            "    impl: DEPRECATED\n";

    assertThat(rc.compare()).isEqualTo(1);
    assertThat(rc.toString()).isEqualTo(expectedReport);

    rule1.setStatus(Rule.Status.READY);
    expectedReport = "null\n" +
            "  status\n" +
            "    spec: READY\n" +
            "    impl: DEPRECATED\n";

    assertThat(rc.compare()).isEqualTo(-1);
    assertThat(rc.toString()).isEqualTo(expectedReport);
  }
}
