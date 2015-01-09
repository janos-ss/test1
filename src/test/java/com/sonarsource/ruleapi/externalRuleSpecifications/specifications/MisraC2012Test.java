/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalRuleSpecifications.specifications;

import static org.fest.assertions.Assertions.assertThat;

import com.sonarsource.ruleapi.externalRuleSpecifications.specifications.MisraC2012;
import org.junit.Test;

public class MisraC2012Test {

  @Test
  public void numberOfMisraC2012RulesIs143() throws Exception {

    assertThat(MisraC2012.Rules.values().length).isEqualTo(143);
  }

}
