/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class PylintTest {

  @Test
  public void testComputeCoverage(){
    Pylint py = new Pylint();

    py.populateRulesCoverageMap();
    py.computeCoverage();

    assertThat(py.implementable).isEqualTo(159);
    assertThat(py.skipped).isEqualTo(29);
    assertThat(py.specified).isEqualTo(0);
    assertThat(py.implemented).isEqualTo(0);
  }
}
