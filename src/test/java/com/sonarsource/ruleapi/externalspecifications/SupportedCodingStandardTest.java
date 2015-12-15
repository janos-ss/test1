/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class SupportedCodingStandardTest {

  @Test
  public void testFromString() {

    assertThat(SupportedCodingStandard.fromString("fxcop")).isEqualTo(SupportedCodingStandard.FXCOP);
    assertThat(SupportedCodingStandard.fromString("boo!")).isNull();
  }
}
