/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.external_rule_specifications.specifications;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class MisraCPP2008Test {

  @Test
  public void numberOfMisraCPP2008RulesIs228() throws Exception {

    assertThat(MisraCPP2008.Rules.values().length).isEqualTo(228);
  }

}
