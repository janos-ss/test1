/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class MisraCPP2008Test {

  @Test
  public void numberOfMisraCPP2008RulesIs228() throws Exception {

    assertThat(MisraCPP2008.StandardRule.values().length).isEqualTo(228);
  }

  @Test
  public void testIsRuleMandatory() {

    MisraCPP2008 m8 = new MisraCPP2008();

    assertThat(m8.isRuleMandatory(null)).isFalse();
    assertThat(m8.isRuleMandatory("0-3-1")).isFalse();
    assertThat(m8.isRuleMandatory("0-1-1")).isTrue();
    assertThat(m8.isRuleMandatory("0-0-0")).isFalse();
  }

  @Test
  public void testGetCodingStandardRuleFromId() {

    MisraCPP2008 m8 = new MisraCPP2008();

    assertThat(m8.getCodingStandardRuleFromId(null)).isNull();

    assertThat(m8.getCodingStandardRuleFromId("0-1-1")).isEqualTo(MisraCPP2008.StandardRule.MISRACPP2008_0_1_1);

  }

}
