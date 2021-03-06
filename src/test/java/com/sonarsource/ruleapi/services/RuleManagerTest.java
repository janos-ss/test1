/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Language;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class RuleManagerTest {

  private RuleManager manager = new RuleManager();

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
