/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class MisraC2012Test {

  @Test
  public void numberOfMisraC2012RulesIs143() throws Exception {

    assertThat(MisraC2012.StandardRule.values().length).isEqualTo(143);
  }

  @Test
  public void testIsRuleMandatory() {

    MisraC2012 m12 = new MisraC2012();

    assertThat(m12.isRuleMandatory(null)).isFalse();
    assertThat(m12.isRuleMandatory("1.1")).isTrue();
    assertThat(m12.isRuleMandatory("1.2")).isFalse();
    assertThat(m12.isRuleMandatory("0.0")).isFalse();
  }

  @Test
  public void testGetCodingStandardRuleFromId() {

    MisraC2012 m12 = new MisraC2012();

    assertThat(m12.getCodingStandardRuleFromId(null)).isNull();

    assertThat(m12.getCodingStandardRuleFromId("1.1")).isEqualTo(MisraC2012.StandardRule.MISRAC2012_1POINT1);

  }
}
