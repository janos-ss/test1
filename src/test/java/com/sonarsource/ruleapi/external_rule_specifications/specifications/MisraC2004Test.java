/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.external_rule_specifications.specifications;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;


public class MisraC2004Test {

  @Test
  public void numberOfMisraC2004RulesIs141() throws Exception {

    assertThat(MisraC2004.Rules.values().length).isEqualTo(141);
  }

}
