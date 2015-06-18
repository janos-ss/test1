/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class SonarQubeHelperTest {


  @Test
  public void testSqaleConstantValueFromSqInstance() {

    Rule rule = new Rule("");
    String cost = "5h";

    rule.setSqaleRemediationFunction(Rule.RemediationFunction.LINEAR);
    SonarQubeHelper.setSqaleConstantValueFromSqInstance(rule, cost);
    assertThat(rule.getSqaleConstantCostOrLinearThreshold()).isNull();
    assertThat(rule.getSqaleLinearOffset()).isNull();

    rule.setSqaleRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);
    SonarQubeHelper.setSqaleConstantValueFromSqInstance(rule, cost);
    assertThat(rule.getSqaleConstantCostOrLinearThreshold()).isEqualTo(cost);
    assertThat(rule.getSqaleLinearOffset()).isNull();

    rule.setSqaleRemediationFunction(Rule.RemediationFunction.LINEAR_OFFSET);
    SonarQubeHelper.setSqaleConstantValueFromSqInstance(rule, cost);
    assertThat(rule.getSqaleConstantCostOrLinearThreshold()).isNull();
    assertThat(rule.getSqaleLinearOffset()).isEqualTo(cost);

  }
}
