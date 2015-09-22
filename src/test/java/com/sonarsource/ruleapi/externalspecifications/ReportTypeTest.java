/*
 * Copyright (C) 2014 SonarSource SA
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
}
