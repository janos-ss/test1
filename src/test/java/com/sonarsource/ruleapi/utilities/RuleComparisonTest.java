/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Rule;
import junit.framework.Assert;
import junit.framework.TestCase;

public class RuleComparisonTest extends TestCase {

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
    Assert.assertFalse(RuleComparison.isTextFunctionallyEquivalent("Now is the time","Four score and seven years ago"));
  }

}
