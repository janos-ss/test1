/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class SupportedStandardTest {

  @Test
  public void testFromString() {

    assertThat(SupportedStandard.fromString("fxcop")).isEqualTo(SupportedStandard.FXCOP);
    assertThat(SupportedStandard.fromString("boo!")).isNull();
  }
}
