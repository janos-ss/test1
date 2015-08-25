/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class CodingStandardRuleCoverageTest {


  @Test
  public void testGetRuleKeysAsString() {

    List<Rule> rules = new ArrayList<Rule>();
    Rule rule = new Rule("");
    rule.setKey("RSPEC-1234");
    rule.getLegacyKeys().add("S1234");
    rules.add(rule);

    rule = new Rule("");
    rule.setKey("RSPEC-2345");
    rule.getLegacyKeys().add("S2345");
    rules.add(rule);

    CodingStandardRuleCoverage cov = new CodingStandardRuleCoverage();

    String expected = "S1234, S2345";
    assertThat(cov.getRuleKeysAsString(rules)).isEqualTo(expected);

  }

}
