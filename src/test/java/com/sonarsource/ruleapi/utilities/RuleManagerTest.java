/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;


public class RuleManagerTest {

  private RuleManager manager = new RuleManager();

  @Test
  public void testMapRulesByKey() {

    List<Rule> rules = new ArrayList<Rule>();

    Rule r1 = new Rule(Language.JAVA.rspec);
    r1.setKey("key1");
    rules.add(r1);

    Rule r2 = new Rule(Language.JAVA.rspec);
    r2.setKey("key2");
    rules.add(r2);

    Map<String, Rule> map = manager.mapRulesByKey(rules);
    assertThat(map.size()).isEqualTo(2);
    assertThat(map.get("key1")).isEqualTo(r1);
  }
}
