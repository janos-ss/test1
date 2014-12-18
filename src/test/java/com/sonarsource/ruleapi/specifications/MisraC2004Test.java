/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.specifications;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class MisraC2004Test {

  @Test
  public void numberOfMisraC2004RulesIs141() throws Exception {
    assertThat(new MisraC2004().getCodingStandardRules()).isNotNull();
    assertThat(new MisraC2004().getCodingStandardRules().size()).isEqualTo(141);
  }

  @Test
  public void numberOfRSpecRulesGreaterThan0() throws Exception {
    assertThat(new MisraC2004().getRSpectRulesCoveringLanguage().size()).isGreaterThan(0);
  }

}
