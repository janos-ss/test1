/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;

public class RuleTest extends TestCase {

  private static final String LANG = "Java";

  public void testMergeNullTitle() throws Exception {
    String title = "Rule title 1";
    Rule rule = new Rule(LANG);
    rule.setTitle(title);
    Rule subRule = new Rule(LANG);

    rule.merge(subRule);

    Assert.assertEquals(title, rule.getTitle());
  }
  public void testMergeTitle() throws Exception {
    Rule rule = new Rule(LANG);
    Rule subRule = new Rule(LANG);
    String title = "My title";
    subRule.setTitle(LANG + ": " +title);

    rule.merge(subRule);

    Assert.assertEquals(title, rule.getTitle());
  }

  public void testMergeDefaultActive() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setDefaultActive(Boolean.FALSE);
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setDefaultActive(Boolean.TRUE);

    rule.merge(subRule);

    Assert.assertEquals(subRule.getDefaultActive(), rule.getDefaultActive());
  }

  public void testMergeSeverity() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setSeverity(Rule.Severity.CRITICAL);
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setSeverity(Rule.Severity.BLOCKER);

    rule.merge(subRule);

    Assert.assertEquals(subRule.getSeverity(), rule.getSeverity());
  }

  public void testMergeMessage() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setMessage("Message one");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setMessage("Message two");

    rule.merge(subRule);

    Assert.assertEquals(subRule.getMessage(), rule.getMessage());
  }

  public void testMergeDescription() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setDescription("Some text1");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setDescription("Some text2");

    rule.merge(subRule);

    Assert.assertEquals(subRule.getDescription(), rule.getDescription());
  }

  public void testMergeNoncompliant() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setNonCompliant("Message one");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setNonCompliant("Message two");

    rule.merge(subRule);

    Assert.assertEquals(subRule.getNonCompliant(), rule.getNonCompliant());
  }

  public void testMergeCompliant() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setCompliant("Message one");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setCompliant("Message two");

    rule.merge(subRule);

    Assert.assertEquals(subRule.getCompliant(), rule.getCompliant());
  }

  public void testMergeExceptions() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setExceptions("Message one");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setExceptions("Message two");

    rule.merge(subRule);

    Assert.assertEquals(subRule.getExceptions(), rule.getExceptions());
  }

  public void testMergeReferences() throws Exception {
    Rule rule = new Rule(LANG);
    rule.setReferences("Message one");
    Rule subRule = new Rule(LANG);
    subRule.setTitle(LANG);
    subRule.setReferences("Message two");

    rule.merge(subRule);

    Assert.assertEquals(subRule.getReferences(), rule.getReferences());
  }

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

    Assert.assertEquals(1, rule.getParameterList().size());
  }

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

    Assert.assertEquals(1, rule.getParameterList().size());
  }

  public void testIsTextFunctionallyEquivalentEasy() throws Exception {
    String ruleTitle = "Methods should not be empty";
    Rule r = new Rule(LANG);

    Assert.assertTrue(r.isTextFunctionallyEquivalent(ruleTitle, ruleTitle));
  }

  public void testIsTextFunctionallyEquivalentBothNull () throws Exception {
    Rule r = new Rule(LANG);

    Assert.assertTrue(r.isTextFunctionallyEquivalent(null, null));
  }

  public void testIsTextFunctionallyEquivalentOneNull () throws Exception {
    Rule r = new Rule(LANG);

    Assert.assertFalse(r.isTextFunctionallyEquivalent("test", null));
  }

  public void testIsTextFunctionallyEquivalentSimple() throws Exception {
    String ruleTitle = "Methods should not be empty";
    String specTitle = "[Methods|functions|procedures] should not be empty";
    Rule r = new Rule(LANG);

    Assert.assertTrue(r.isTextFunctionallyEquivalent(ruleTitle, specTitle));
  }

  public void testIsTextFunctionallyEquivalentHarder() throws Exception {
    String specDescription = "A [function|method|module|subroutine] that grows too large tends to aggregate too many responsibilities.\n" +
            "Such [function|method|module|subroutine] inevitably become harder to understand and therefore harder to maintain.\n" +
            "Above a specific threshold, it is strongly advised to refactor into smaller [function|method|module|subroutine] which focus on well-defined tasks.\n" +
            "Those smaller [function|method|module|subroutine] will not only be easier to understand, but also probably easier to test.";
    String ruleDescription = "A module that grows too large tends to aggregate too many responsibilities.\n" +
            "Such module inevitably become harder to understand and therefore harder to maintain.\n" +
            "Above a specific threshold, it is strongly advised to refactor into smaller module which focus on well-defined tasks.\n" +
            "Those smaller module will not only be easier to understand, but also probably easier to test.";
    Rule r = new Rule(LANG);

    Assert.assertTrue(r.isTextFunctionallyEquivalent(specDescription, ruleDescription));
  }

  public void testIsTextFunctionallyEquivalentFalse() throws Exception {
    Rule r = new Rule(LANG);
    Assert.assertFalse(r.isTextFunctionallyEquivalent("Now is the time","Four score and seven years ago"));
  }
}
