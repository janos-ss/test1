/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class LangugeTest {

  @Test
  public void testLanguagesEnum() {

    assertThat(Language.values().length).isEqualTo(18);
    assertThat(Language.ABAP.sq).isEqualTo("abap");
    assertThat(Language.ABAP.rspec).isEqualTo("ABAP");
    assertThat(Language.ABAP.update).isTrue();
  }

}
