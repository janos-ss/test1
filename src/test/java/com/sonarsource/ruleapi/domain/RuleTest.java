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
}
