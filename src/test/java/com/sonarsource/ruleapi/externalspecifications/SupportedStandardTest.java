/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class SupportedStandardTest {

  @Test
  public void testFromString() {

    assertThat(SupportedStandard.fromString("fxcop")).isEqualTo(SupportedStandard.FXCOP);
    assertThat(SupportedStandard.fromString("boo!")).isNull();
  }
}
