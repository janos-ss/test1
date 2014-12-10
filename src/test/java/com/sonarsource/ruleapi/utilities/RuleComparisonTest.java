/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.get.RuleMaker;
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

    assertThat(rc.toString()).isEqualTo("Differences: tags, ");
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

    assertThat(rc.toString()).isEqualTo("Differences: parameter list, ");
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
  public void testCompareDefaultActive() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setDefaultActive(Boolean.TRUE);
    impl.setDefaultActive(Boolean.TRUE);

    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void testCompareDefaultActiveNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setDefaultActive(Boolean.TRUE);
    impl.setDefaultActive(Boolean.FALSE);

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void testCompareDefaultActiveNeqWithNull() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setDefaultActive(Boolean.TRUE);

    assertThat(rc.compare()).isEqualTo(-1);
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
  public void testCompareMessageNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setMessage("message");
    impl.setMessage("a message");

    assertThat(rc.compare()).isEqualTo(12);
  }

  @Test
  public void testCompareMessageNeqWithNull() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setMessage("message");

    assertThat(rc.compare()).isEqualTo(-1);
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

    assertThat(rc.toString()).isEqualTo("Differences: title, ");
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

    assertThat(rc.toString()).isEqualTo("Differences: severity, ");
  }

  @Test
  public void testToStringDefaultActive() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setDefaultActive(Boolean.TRUE);
    impl.setDefaultActive(Boolean.TRUE);

    assertThat(rc.toString()).hasSize(0);
  }

  @Test
  public void testToStringDefaultActiveNeq() throws Exception {

    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setDefaultActive(Boolean.TRUE);
    impl.setDefaultActive(Boolean.FALSE);

    assertThat(rc.toString()).isEqualTo("Differences: default active, ");
  }

  @Test
  public void testToStringDefaultActiveNeqWithNull() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setDefaultActive(Boolean.TRUE);

    assertThat(rc.toString()).isEqualTo("Differences: default active, ");
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

    assertThat(rc.toString()).isEqualTo("Differences: template, ");
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

    assertThat(rc.toString()).isEqualTo("Differences: description text, ");
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

    assertThat(rc.toString()).isEqualTo("Differences: noncompliant code example, ");
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

    assertThat(rc.toString()).isEqualTo("Differences: compliant solution, ");
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

    assertThat(rc.toString()).isEqualTo("Differences: references, ");
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

    assertThat(rc.toString()).isEqualTo("Differences: exceptions, ");
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

    assertThat(rc.toString()).isEqualTo("Differences: SQALE characteristic, ");
  }

  @Test
  public void differentSqaleSubChar() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "Processor use";

    RuleMaker.setSubcharacteristic(spec, str);

    assertThat(rc.compare()).isEqualTo(-1);
  }

  @Test
  public void differentSqaleSubCharToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "Memory use";

    RuleMaker.setSubcharacteristic(spec, str);

    assertThat(rc.toString()).isEqualTo("Differences: SQALE sub-characteristic, ");
  }

  @Test
  public void differentSqaleRemediation() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setSqaleRemediationFunction(str);

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void differentSqaleRemediationToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setSqaleRemediationFunction(str);

    assertThat(rc.toString()).isEqualTo("Differences: SQALE remediation function, ");
  }

  @Test
  public void differentSqaleLinearArg() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setSqaleLinearArg(str);

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void differentSqaleLinearArgToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setSqaleLinearArg(str);

    assertThat(rc.toString()).isEqualTo("Differences: SQALE linear argument, ");
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

    assertThat(rc.toString()).isEqualTo("Differences: SQALE linear factor, ");
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
    rc.setDetailedReport(true);

    StringBuilder sb = new StringBuilder();
    rc.logDifference(sb, "Title", rule1.getTitle(), rule2.getTitle());
    assertThat(sb.toString()).isEqualTo("Title\n" +
            "  spec: Rule1 title\n" +
            "  impl: Rule2 title\n");

  }

  @Test
  public void testCompareStrings() {

    String test = "test";
    assertThat(blankComparison.compareStrings(test, test)).isEqualTo(0);
    assertThat(blankComparison.compareStrings(null, test)).isEqualTo(-1);
  }

  @Test
  public void testCompareFunctionalEquivalence() {

    String test = "test";
    assertThat(blankComparison.compareTextFunctionalEquivalence(null, test, true)).isEqualTo(1);
  }

}
