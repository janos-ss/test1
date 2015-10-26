/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Language;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;


public class RuleManagerTest {

  private RuleManager manager = new RuleManager();

  @Test
  public void testMapRulesByKey() {

    List<Rule> rules = new ArrayList<>();

    Rule r1 = new Rule(Language.JAVA.getRspec());
    r1.setKey("key1");
    rules.add(r1);

    Rule r2 = new Rule(Language.JAVA.getRspec());
    r2.setKey("key2");
    rules.add(r2);

    Map<String, Rule> map = manager.mapRulesByKey(rules);
    assertThat(map.size()).isEqualTo(2);
    assertThat(map.get("key1")).isEqualTo(r1);
  }

  @Test
  public void testGetNormalKey() {

    String key = "RSPEC-123";
    assertThat(manager.getNormalKey(key, Language.ABAP)).isEqualTo(key);

  }

  @Test
  public void testStandardizeKeysAndIdentifyMissingRspecs() {

    Rule rule = new Rule("Java");
    rule.setKey("RSPEC-123");

    List<Rule> sqCovered = new ArrayList<>();
    sqCovered.add(rule);

    List notFound = manager.standardizeKeysAndIdentifyMissingSpecs(Language.JAVA, sqCovered);

    assertThat(notFound).isEmpty();

  }

}
