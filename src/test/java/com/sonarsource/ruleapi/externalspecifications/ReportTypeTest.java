/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class ReportTypeTest {

  @Test
  public void testFromString(){
    assertThat(ReportType.fromString("boo!")).isNull();
    assertThat(ReportType.fromString("html")).isEqualTo(ReportType.HTML);
  }

  @Test
  public void testTheRest(){
    assertThat(ReportType.INTERNAL_COVERAGE.isInternal()).isTrue();
    assertThat(ReportType.HTML.isInternal()).isFalse();
  }
}
