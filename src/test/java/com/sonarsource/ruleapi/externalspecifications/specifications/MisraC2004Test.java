/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import static org.fest.assertions.Assertions.assertThat;

import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class MisraC2004Test {

  @Test
  public void numberOfMisraC2004RulesIs141() throws Exception {

    assertThat(MisraC2004.StandardRule.values().length).isEqualTo(141);
  }

  @Test
  public void testIsRuleMandatory() {

    MisraC2004 m4 = new MisraC2004();

    assertThat(m4.isRuleMandatory(null)).isFalse();
    assertThat(m4.isRuleMandatory("1.5")).isFalse();
    assertThat(m4.isRuleMandatory("1.4")).isTrue();
    assertThat(m4.isRuleMandatory("0.0")).isFalse();
  }

  @Test
  public void testGetCodingStandardRuleFromId() {

    MisraC2004 m4 = new MisraC2004();

    assertThat(m4.getCodingStandardRuleFromId(null)).isNull();

    assertThat(m4.getCodingStandardRuleFromId("1.1")).isEqualTo(MisraC2004.StandardRule.MISRAC2004_1POINT1);

  }

  @Test
  public void testSetRspecReferenceFieldValues() {
    MisraC2004 m4 = new MisraC2004();
    Rule rule = new Rule("C");
    List<String> ids = new ArrayList<String>(3);
    ids.add("red");
    ids.add("green");
    ids.add("blue");

    m4.setRspecReferenceFieldValues(rule, ids);

    assertThat(rule.getMisraC04()).isEqualTo(ids);
  }

}
