/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.specifications;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class MisraCPP2008Test {

  @Test
  public void numberOfMisraCPP2008RulesIs228() throws Exception {
    assertThat(new MisraCPP2008().getCodingStandardRules()).isNotNull();
    assertThat(new MisraCPP2008().getCodingStandardRules().size()).isEqualTo(228);
  }

  @Test
  public void numberOfRSpecRulesGreaterThan0() throws Exception {
    assertThat(new MisraCPP2008().getRSpectRulesCoveringLanguage().size()).isGreaterThan(0);
  }

}
