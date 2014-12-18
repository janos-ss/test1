/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.specifications;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class MisraC2012Test {

  @Test
  public void numberOfMisraC2012RulesIs143() throws Exception {
    assertThat(new MisraC2012().getCodingStandardRules()).isNotNull();
    assertThat(new MisraC2012().getCodingStandardRules().size()).isEqualTo(143);
  }

  @Test
  public void numberOfRSpecRulesGreaterThan0() throws Exception {
    assertThat(new MisraC2012().getRSpectRulesCoveringLanguage().size()).isGreaterThan(0);
  }

}
