/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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
  public void ruleTypes() {

    Rule bug = new Rule("");
    bug.setType(Rule.Type.BUG);

    Rule vuln = new Rule("");
    vuln.setType(Rule.Type.VULNERABILITY);

    Rule bug2 = new Rule("");
    bug2.setType(Rule.Type.BUG);

    RuleComparison rc = new RuleComparison(bug, vuln);
    assertThat(rc.compare()).isEqualTo(-1);
    assertThat(rc.toString()).isEqualTo("null\n" +
            "  type\n" +
            "    spec: Bug\n" +
            "    impl: Vulnerability\n");

    rc = new RuleComparison(bug, bug2);
    assertThat(rc.compare()).isEqualTo(0);
    assertThat(rc.toString()).isEqualTo("");

  }


  @Test
  public void testCompareTagsEq() throws Exception {
    Rule spec = new Rule(LANG);
    Set<String> specTags = new HashSet<>();
    specTags.add("clumsy");
    specTags.add("java8");
    spec.setTags(specTags);

    Rule impl = new Rule(LANG);
    Set<String> implTags = new HashSet<>();
    implTags.add("java8");
    implTags.add("clumsy");
    impl.setTags(implTags);

    RuleComparison rc = new RuleComparison(spec, impl);

    assertThat(rc.toString()).hasSize(0);
  }

  @Test
  public void testCompareTagsDifferentTags() throws Exception {
    Rule spec = new Rule(LANG);
    Set<String> specTags = new HashSet<>();
    specTags.add("clumsy");
    specTags.add("java8");
    spec.setTags(specTags);

    Rule impl = new Rule(LANG);
    Set<String> implTags = new HashSet<>();
    implTags.add("performance");
    implTags.add("bug");
    impl.setTags(implTags);

    RuleComparison rc = new RuleComparison(spec, impl);

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void testTagsDifferentTagsToString() throws Exception {
    Rule spec = new Rule(LANG);
    Set<String> specTags = new HashSet<>();
    specTags.add("clumsy");
    specTags.add("java8");
    spec.setTags(specTags);

    Rule impl = new Rule(LANG);
    Set<String> implTags = new HashSet<>();
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
    Set<String> specTags = new HashSet<>();
    specTags.add("clumsy");
    specTags.add("java8");
    spec.setTags(specTags);

    Rule impl = new Rule(LANG);
    Set<String> implTags = new HashSet<>();
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
  public void testCompareConstantCostEasy() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setConstantCostOrLinearThreshold("5min");
    impl.setConstantCostOrLinearThreshold("5min");

    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void testCompareConstantCostHard() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setConstantCostOrLinearThreshold("5 MN");
    impl.setConstantCostOrLinearThreshold("5min");

    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void testCompareConstantCostNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setConstantCostOrLinearThreshold("1 h");
    impl.setConstantCostOrLinearThreshold("5min");

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void testCompareConstantCostWithNull() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setConstantCostOrLinearThreshold("5min");

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
  public void testToStringConstantCostEasy() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setConstantCostOrLinearThreshold("5min");
    impl.setConstantCostOrLinearThreshold("5min");

    assertThat(rc.toString()).hasSize(0);
  }

  @Test
  public void testToStringConstantCostHard() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setConstantCostOrLinearThreshold("5 MN");
    impl.setConstantCostOrLinearThreshold("5min");

    assertThat(rc.toString()).hasSize(0);
  }

  @Test
  public void testToStringConstantCostNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setConstantCostOrLinearThreshold("1 h");
    impl.setConstantCostOrLinearThreshold("5min");

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
  public void differentRemediation() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);

    assertThat(rc.compare()).isEqualTo(-1);
  }

  @Test
  public void differentRemediationToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  Remediation function\n" +
            "    spec: CONSTANT_ISSUE\n" +
            "    impl: null\n");
  }

  @Test
  public void differentLinearArg() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setLinearArgDesc(str);

    assertThat(rc.compare()).isEqualTo(1);
  }

  @Test
  public void differentLinearArgToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setLinearArgDesc(str);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  Linear argument\n" +
            "    spec: this is a test\n" +
            "    impl: null\n");
  }

  @Test
  public void differentLinearFactor() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setLinearFactor(str);

    assertThat(rc.compare()).isEqualTo(-1);
  }

  @Test
  public void differentButEqualLinearFactor() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setLinearFactor("5min");
    impl.setLinearFactor("5mn");

    assertThat(rc.compare()).isEqualTo(0);
  }

  @Test
  public void differentLinearFactorToString() {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    String str = "this is a test";

    spec.setLinearFactor(str);

    assertThat(rc.toString()).isEqualTo("null\n" +
            "  Linear factor\n" +
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

  @Test
  public void testDefaultProfileDiffs() {

    Rule rule1 = new Rule("Java");
    rule1.getDefaultProfiles().add(new Profile("Sonar way"));

    Rule rule2 = new Rule("");

    RuleComparison rc = new RuleComparison(rule1, rule2);
    assertThat(rc.compare()).isEqualTo(1);

    String expectedText = "null\n" +
            "  profile list\n" +
            "    spec: Sonar way, \n" +
            "    impl: \n";
    assertThat(rc.toString()).isEqualTo(expectedText);


    rule2.getDefaultProfiles().add(new Profile("SonarQube Way"));

    assertThat(rc.compare()).isEqualTo(0);
    assertThat(rc.toString()).isEqualTo("");


    Rule rule3 = new Rule("C#");
    rule3.getDefaultProfiles().add(new Profile("Sonar C# way"));

    rc = new RuleComparison(rule1, rule3);
    assertThat(rc.compare()).isEqualTo(0);
    assertThat(rc.toString()).isEqualTo("");

    Rule rule4 = new Rule("");
    rule4.getDefaultProfiles().add(new Profile("Warrior's Way"));
    rc = new RuleComparison(rule1, rule4);
    expectedText = "null\n" +
            "  profile list\n" +
            "    spec: Sonar way, \n" +
            "    impl: Warrior's Way, \n";
    assertThat(rc.compare()).isEqualTo(-4);
    assertThat(rc.toString()).isEqualTo(expectedText);

    rc = new RuleComparison(rule2, rule3);
    assertThat(rc.compare()).isEqualTo(0);
    assertThat(rc.toString()).isEqualTo("");

  }

}
