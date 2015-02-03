/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import org.junit.Test;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;


public class RuleTest {

  private static final String LANG = "Java";

  @Test
  public void testMergeSqale() {

    Rule r1 = new Rule("Java");
    Rule r2 = new Rule("Java");

    r2.setSqaleCharac("Blue");
    r2.setSqaleSubCharac(Rule.Subcharacteristic.DATA_CHANGEABILITY);
    r2.setSqaleRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);
    r2.setSqaleConstantCostOrLinearThreshold("10min");

    r2.mergeSqalePieces(r2);

    assertThat(r1.equals(r2));
  }

  @Test
  public void testMergeSqaleNulls() {
    Rule r1 = new Rule("Java");
    Rule r2 = new Rule("Java");

    r1.setSqaleCharac("Blue");
    r1.setSqaleSubCharac(Rule.Subcharacteristic.DATA_CHANGEABILITY);
    r1.setSqaleRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);
    r1.setSqaleConstantCostOrLinearThreshold("10min");

    r1.mergeSqalePieces(r2);

    assertThat(r1.getSqaleCharac()).isEqualTo("Blue");
    assertThat(r1.getSqaleSubCharac()).isEqualTo(Rule.Subcharacteristic.DATA_CHANGEABILITY);
    assertThat(r1.getSqaleRemediationFunction()).isEqualTo(Rule.RemediationFunction.CONSTANT_ISSUE);
    assertThat(r1.getSqaleConstantCostOrLinearThreshold()).isEqualTo("10min");

  }

  @Test
  public void testMergeNullTitle() throws Exception {
    String title = "Rule title 1";
    Rule rule = new Rule(LANG);
    rule.setTitle(title);
    Rule subRule = new Rule(LANG);

    rule.merge(subRule);

    assertThat(rule.getTitle()).isEqualTo(title);
  }

  @Test
  public void testMergeTitle() throws Exception {
    Rule rule = new Rule(LANG);
    Rule subRule = new Rule(LANG);
    String title = "My title";
    subRule.setTitle(LANG + ": " +title);

    rule.merge(subRule);

    assertThat(rule.getTitle()).isEqualTo(title);
  }

  @Test
  public void testMergeDefaultActive() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setDefaultActive(Boolean.FALSE);
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setDefaultActive(Boolean.TRUE);

    rule.merge(subRule);

    assertThat(rule.getDefaultActive()).isEqualTo(subRule.getDefaultActive());
  }

  @Test
  public void testMergeSeverity() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setSeverity(Rule.Severity.CRITICAL);
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setSeverity(Rule.Severity.BLOCKER);

    rule.merge(subRule);

    assertThat(rule.getSeverity()).isEqualTo(subRule.getSeverity());
  }

  @Test
  public void testMergeMessage() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setMessage("Message one");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setMessage("Message two");

    rule.merge(subRule);

    assertThat(rule.getMessage()).isEqualTo(subRule.getMessage());
  }

  @Test
  public void testMergeDescription() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setDescription("Some text1");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setDescription("Some text2");

    rule.merge(subRule);

    assertThat(rule.getDescription()).isEqualTo(subRule.getDescription());
  }

  @Test
  public void testMergeNoncompliant() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setNonCompliant("Message one");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setNonCompliant("Message two");

    rule.merge(subRule);

    assertThat(rule.getNonCompliant()).isEqualTo(subRule.getNonCompliant());
  }

  @Test
  public void testMergeCompliant() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setCompliant("Message one");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setCompliant("Message two");

    rule.merge(subRule);

    assertThat(rule.getCompliant()).isEqualTo(subRule.getCompliant());
  }

  @Test
  public void testMergeExceptions() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setExceptions("Message one");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setExceptions("Message two");

    rule.merge(subRule);

    assertThat(rule.getExceptions()).isEqualTo(subRule.getExceptions());
  }

  @Test
  public void testMergeReferences() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setReferences("Message one");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setReferences("Message two");

    rule.merge(subRule);

    assertThat(rule.getReferences()).isEqualTo(subRule.getReferences());
  }

  @Test
  public void testMergeParamListEmpty() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setParameterList(new ArrayList<Parameter>());
    Parameter p = new Parameter();
    rule.getParameterList().add(p);
    p.setKey("p");
    p.setType("text");
    p.setDescription("This is a param");

    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);

    rule.merge(subRule);

    assertThat(rule.getParameterList().size()).isEqualTo(1);
  }

  @Test
  public void testMergeParmListNonEmpty() throws Exception {
    Rule rule = new Rule(LANG);

    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setParameterList(new ArrayList<Parameter>());
    Parameter p = new Parameter();
    subRule.getParameterList().add(p);
    p.setKey("p");
    p.setType("text");
    p.setDescription("This is a param");

    rule.merge(subRule);

    assertThat(rule.getParameterList().size()).isEqualTo(1);
  }

  @Test
  public void testAnnotationsAllOptions() {

    Rule rule = new Rule(LANG);

    rule.setKey("RSPEC-1997");

    rule.getTags().add("misra");
    rule.getTags().add("cwe");

    rule.setSeverity(Rule.Severity.MAJOR);

    rule.setTitle("This is a \"test\"");

    rule.setDefaultActive(Boolean.TRUE);

    rule.setSqaleRemediationFunction(Rule.RemediationFunction.LINEAR_OFFSET);
    rule.setSqaleLinearArgDesc("per point of complexity above the threshold");
    rule.setSqaleLinearOffset("10min");
    rule.setSqaleLinearFactor("1min");
    rule.setSqaleSubCharac(Rule.Subcharacteristic.COMPILER_RELATED_PORTABILITY);

    String expectedAnnotation = "@Rule(key = \"S1997\",\n" +
            "  priority = Priority.MAJOR\n" +
            "  name = \"This is a \\\"test\\\"\",\n" +
            "  tags = \"misra\", \"cwe\")\n" +
            "@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.COMPILER_RELATED_PORTABILITY)\n" +
            "@SqaleLinearWithOffsetRemediation( coeff=\"1min\", effortToFixDescription=\"per point of complexity above the threshold\", offset=\"10min\")\n" +
            "@ActivatedByDefault\n";

    assertThat(rule.getAnnotations()).isEqualTo(expectedAnnotation);
  }

  @Test
  public void testAnnotationsMinimal(){
    Rule rule = new Rule(LANG);

    rule.setKey("RSPEC-1997");

    rule.setSeverity(Rule.Severity.MAJOR);

    rule.setTitle("This is a test");

    String expectedAnnotation = "@Rule(key = \"S1997\",\n" +
            "  priority = Priority.MAJOR\n" +
            "  name = \"This is a test\",\n" +
            "  tags = \"\")\n";

    assertThat(rule.getAnnotations()).isEqualTo(expectedAnnotation);
  }

  @Test
  public void testAnnotationsConstantSqaleLegacyKey(){
    Rule rule = new Rule(LANG);

    rule.setKey("LegacyKey");

    rule.getTags().add("cwe");

    rule.setSeverity(Rule.Severity.MAJOR);

    rule.setTitle("This is a \"test\"");

    rule.setDefaultActive(Boolean.FALSE);

    rule.setSqaleRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);
    rule.setSqaleConstantCostOrLinearThreshold("10min");
    rule.setSqaleSubCharac(Rule.Subcharacteristic.COMPILER_RELATED_PORTABILITY);

    String expectedAnnotation = "@Rule(key = \"LegacyKey\",\n" +
            "  priority = Priority.MAJOR\n" +
            "  name = \"This is a \\\"test\\\"\",\n" +
            "  tags = \"cwe\")\n" +
            "@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.COMPILER_RELATED_PORTABILITY)\n" +
            "@SqaleConstantRemediation( \"10min\" )\n";

    assertThat(rule.getAnnotations()).isEqualTo(expectedAnnotation);
  }

  @Test
  public void testAnnotationsLinearSqale(){
    Rule rule = new Rule(LANG);

    rule.setKey("RSPEC-1997");

    rule.setSeverity(Rule.Severity.MAJOR);

    rule.setTitle("This is a \"test\"");

    rule.setDefaultActive(Boolean.TRUE);

    rule.setSqaleRemediationFunction(Rule.RemediationFunction.LINEAR);
    rule.setSqaleLinearArgDesc("per point of complexity above the threshold");
    rule.setSqaleLinearFactor("1min");
    rule.setSqaleSubCharac(Rule.Subcharacteristic.COMPILER_RELATED_PORTABILITY);

    String expectedAnnotation = "@Rule(key = \"S1997\",\n" +
            "  priority = Priority.MAJOR\n" +
            "  name = \"This is a \\\"test\\\"\",\n" +
            "  tags = \"\")\n" +
            "@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.COMPILER_RELATED_PORTABILITY)\n" +
            "@SqaleLinearRemediation( coeff=\"1min\", effortToFixDescription=\"per point of complexity above the threshold\")\n" +
            "@ActivatedByDefault\n";

    assertThat(rule.getAnnotations()).isEqualTo(expectedAnnotation);
  }

}
