/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import static org.fest.assertions.Assertions.assertThat;

import com.sonarsource.ruleapi.domain.CodingStandardCoverage;

public class CodingStandardServiceTest {

  public void checkMisraC2004CoverageCanBeComputed() throws Exception {
    CodingStandardCoverage coverage = CodingStandardService.INSTANCE.findMisraC2004Coverage();

    assertThat(coverage).isNotNull();

    assertThat(coverage.getMandatoryRulesToCover()).isGreaterThan(0);
    assertThat(coverage.getOptionalRulesToCover()).isGreaterThan(0);

    assertThat(coverage.getMandatoryRulesCovered()).isGreaterThan(0);
    assertThat(coverage.getOptionalRulesCovered()).isGreaterThan(0);
  }

}
