/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class CppcheckTest {

  @Test
  public void testComputeCoverage(){
    Cppcheck cppcheck = new Cppcheck();

    cppcheck.populateRulesCoverageMap();
    cppcheck.computeCoverage();

    assertThat(cppcheck.implementable).isEqualTo(321);
    assertThat(cppcheck.skipped).isEqualTo(3);
    assertThat(cppcheck.specified).isEqualTo(0);
    assertThat(cppcheck.implemented).isEqualTo(0);
  }
}
