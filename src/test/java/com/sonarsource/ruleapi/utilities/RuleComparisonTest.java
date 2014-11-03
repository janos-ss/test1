/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RuleComparisonTest extends TestCase {

  private static final String LANG = "Java";
  private static final String EQUIVALENT = "Rules are equivalent.";

  public void testIsTextFunctionallyEquivalentEasy() throws Exception {
    String ruleTitle = "Methods should not be empty";

    Assert.assertTrue(RuleComparison.isTextFunctionallyEquivalent(ruleTitle, ruleTitle));
  }

  public void testIsTextFunctionallyEquivalentBothNull () throws Exception {

    Assert.assertTrue(RuleComparison.isTextFunctionallyEquivalent(null, null));
  }

  public void testIsTextFunctionallyEquivalentOneNull () throws Exception {

    Assert.assertFalse(RuleComparison.isTextFunctionallyEquivalent("test", null));
  }

  public void testIsTextFunctionallyEquivalentSimple() throws Exception {
    String ruleTitle = "Methods should not be empty";
    String specTitle = "[Methods|functions|procedures] should not be empty";

    Assert.assertTrue(RuleComparison.isTextFunctionallyEquivalent(ruleTitle, specTitle));
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

    Assert.assertTrue(RuleComparison.isTextFunctionallyEquivalent(specDescription, ruleDescription));
  }

  public void testIsTextFunctionallyEquivalentFalse() throws Exception {
    Assert.assertFalse(RuleComparison.isTextFunctionallyEquivalent("Now is the time", "Four score and seven years ago"));
  }

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

    Assert.assertEquals(EQUIVALENT, rc.toString());
  }

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

    Assert.assertEquals(1, rc.compareTags());
  }

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

    Assert.assertEquals(1, rc.compareTags());
  }

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

    Assert.assertEquals(EQUIVALENT, rc.toString());
  }

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

    Assert.assertEquals(2, rc.compareParameterList());
  }

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

    Assert.assertEquals(1, rc.compareParameterList());
  }

  public void testCompareSqaleConstantCostEasy() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleConstantCostOrLinearThreshold("5min");
    impl.setSqaleConstantCostOrLinearThreshold("5min");

    Assert.assertEquals(EQUIVALENT, rc.toString());
  }

  public void testCompareSqaleConstantCostHard() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleConstantCostOrLinearThreshold("5 MN");
    impl.setSqaleConstantCostOrLinearThreshold("5min");

    Assert.assertEquals(EQUIVALENT, rc.toString());
  }

  public void testCompareSqaleConstantCostNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSqaleConstantCostOrLinearThreshold("1 h");
    impl.setSqaleConstantCostOrLinearThreshold("5min");

    Assert.assertEquals(1, rc.compareSqaleConstantCost());
  }

  public void testCompareTitle() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setTitle("title");
    impl.setTitle("title");

    Assert.assertEquals(EQUIVALENT, rc.toString());
  }

  public void testCompareTitleNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setTitle("title");
    impl.setTitle("a title");

    Assert.assertEquals("Differences: title, ", rc.toString());
  }

  public void testCompareSeverity() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSeverity(Rule.Severity.CRITICAL);
    impl.setSeverity(Rule.Severity.CRITICAL);

    Assert.assertEquals(EQUIVALENT, rc.toString());
  }

  public void testCompareSeverityNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setSeverity(Rule.Severity.CRITICAL);
    impl.setSeverity(Rule.Severity.MAJOR);

    Assert.assertEquals("Differences: severity, ", rc.toString());
  }

  public void testCompareDefaultActive() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setDefaultActive(Boolean.TRUE);
    impl.setDefaultActive(Boolean.TRUE);

    Assert.assertEquals(EQUIVALENT, rc.toString());
  }

  public void testCompareDefaultActiveNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setDefaultActive(Boolean.TRUE);
    impl.setDefaultActive(Boolean.FALSE);

    Assert.assertEquals("Differences: default active, ", rc.toString());
  }

  public void testCompareDefaultActiveNeqWithNull() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setDefaultActive(Boolean.TRUE);

    Assert.assertEquals("Differences: default active, ", rc.toString());
  }

  public void testCompareTemplate() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setTemplate(false);
    impl.setTemplate(false);

    Assert.assertEquals(EQUIVALENT, rc.toString());
  }

  public void testCompareTemplateNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setTemplate(false);
    impl.setTemplate(true);

    Assert.assertEquals("Differences: template, ", rc.toString());
  }

  public void testCompareMessage() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setMessage("message");
    impl.setMessage("message");

    Assert.assertEquals(EQUIVALENT, rc.toString());
  }

  public void testCompareMessageNeq() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setMessage("message");
    impl.setMessage("a message");

    Assert.assertEquals("Differences: message, ", rc.toString());
  }

  public void testCompareMessageNeqWithNull() throws Exception {
    Rule spec = new Rule(LANG);
    Rule impl = new Rule(LANG);
    RuleComparison rc = new RuleComparison(spec, impl);

    spec.setMessage("message");

    Assert.assertEquals("Differences: message, ", rc.toString());
  }
}
