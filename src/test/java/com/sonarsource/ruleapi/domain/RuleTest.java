/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


public class RuleTest {

  private static final String LANG = "Java";

  @Test
  public void testMergeRemediation() {

    Rule r1 = new Rule("Java");
    Rule r2 = new Rule("Java");

    r2.setRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);
    r2.setConstantCostOrLinearThreshold("10min");

    r1.mergeRemediationPieces(r2);

    assertThat(r1.getRemediationFunction()).isEqualTo(r2.getRemediationFunction());
    assertThat(r1.getConstantCostOrLinearThreshold()).isEqualTo(r2.getConstantCostOrLinearThreshold());
  }

  @Test
  public void testMergeRemediationNulls() {
    Rule r1 = new Rule("Java");
    Rule r2 = new Rule("Java");

    r1.setRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);
    r1.setConstantCostOrLinearThreshold("10min");

    r1.mergeRemediationPieces(r2);

    assertThat(r1.getRemediationFunction()).isEqualTo(Rule.RemediationFunction.CONSTANT_ISSUE);
    assertThat(r1.getConstantCostOrLinearThreshold()).isEqualTo("10min");

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
  public void testMergeAskYourself() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setAskYourself("Message one");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setAskYourself("Message two");

    rule.merge(subRule);

    assertThat(rule.getAskYourself()).isEqualTo(subRule.getAskYourself());
  }

  @Test
  public void testMergeRecommended() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setRecommended("Message one");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setRecommended("Message two");

    rule.merge(subRule);

    assertThat(rule.getRecommended()).isEqualTo(subRule.getRecommended());
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
  public void testMergeTitle() throws Exception {
    Rule rule = new Rule(LANG);
    Rule subRule = new Rule(LANG);
    String title = "My title";
    subRule.setTitle(LANG + ": " +title);

    rule.merge(subRule);

    assertThat(rule.getTitle()).isEqualTo(title);
  }

  @Test
  public void testMergeTitleCFamily() {

    String title = "This is a title";
    Rule rule = new Rule("C");
    Rule subRule = new Rule("");

    rule.setTitle(title);
    subRule.setTitle("C");
    rule.mergeTitle(subRule);
    assertThat(rule.getTitle()).isEqualTo(title);

    rule.setTitle(title);
    subRule.setTitle("C: blah");
    rule.mergeTitle(subRule);
    assertThat(rule.getTitle()).isEqualTo("blah");

    rule.setTitle(title);
    subRule.setTitle("C - blah");
    rule.mergeTitle(subRule);
    assertThat(rule.getTitle()).isEqualTo("blah");

    rule.setTitle(title);
    subRule.setTitle("C-Family");
    rule.mergeTitle(subRule);
    assertThat(rule.getTitle()).isEqualTo(title);

    rule.setTitle(title);
    subRule.setTitle("C-Family: blah");
    rule.mergeTitle(subRule);
    assertThat(rule.getTitle()).isEqualTo("blah");

    rule.setTitle(title);
    subRule.setTitle("C-Family - blah");
    rule.mergeTitle(subRule);
    assertThat(rule.getTitle()).isEqualTo("blah");

  }

  @Test
  public void testStatusFromString() {

    assertThat(Rule.Status.fromString("beta")).isEqualTo(Rule.Status.BETA);
    assertThat(Rule.Status.fromString("DEPRECATED")).isEqualTo(Rule.Status.DEPRECATED);
    assertThat(Rule.Status.fromString("Yellow")).isEqualTo(Rule.Status.READY);
  }

  @Test
  public void testMergeDefaultProfiles() {

    Rule rule = new Rule("");
    Rule subRule = new Rule("");
    subRule.setTitle("Java");
    subRule.getDefaultProfiles().add(new Profile("Drupal"));

    rule.merge(subRule);

    assertThat(rule.getDefaultProfiles()).hasSize(1);
    assertThat(rule.getDefaultProfiles()).isNotSameAs(subRule.getDefaultProfiles());
  }

  @Test
  public void testMergeDefaultProfilesOverrideNone(){
    Rule rule = new Rule("");
    rule.getDefaultProfiles().add(new Profile("Sonar Way"));

    Rule subRule = new Rule("");
    subRule.setTitle("Java");
    subRule.getDefaultProfiles().add(new Profile("Override None"));

    assertThat(rule.getDefaultProfiles()).hasSize(1);

    rule.merge(subRule);

    assertThat(rule.getDefaultProfiles()).hasSize(0);

  }

  @Test
  public void testMergeTags(){

    Rule rule = new Rule("");
    Rule subRule = new Rule("");
    subRule.setTitle("Java");

    rule.getTags().add("red");
    subRule.getTags().add("red");

    rule.getTags().add("green");

    subRule.getTags().add("blue");

    rule.merge(subRule);

    assertThat(rule.getTags().size()).isEqualTo(3);
    assertThat(rule.getTags()).contains("blue");

  }

  @Test
  public void testMergeSqKey() {
    Rule rule = new Rule("");
    Rule subRule = new Rule("");
    subRule.setTitle("Java");

    rule.setSqKey("S1234");
    subRule.setLegacyKeys(Collections.singletonList("SomeLegacyKey"));

    rule.merge(subRule);

    assertThat(rule.getSqKey()).isEqualTo("SomeLegacyKey");
  }
}
