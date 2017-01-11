/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class RuleMakerIntegrationTest {
  @Test
  public void testGetRulesByJql(){

    List<Rule> rules = RuleMaker.getRulesByJql("labels = clumsy", "Java");
    assertThat(rules).isNotEmpty();

    assertThat(rules.get(0).getTags()).contains("clumsy");
    assertThat(rules.get(0).getStatus()).isNotNull();
  }
}
