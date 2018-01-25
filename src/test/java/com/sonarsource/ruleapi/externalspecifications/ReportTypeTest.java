/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


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
