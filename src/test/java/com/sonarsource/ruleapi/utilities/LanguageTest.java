/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class LanguageTest {

  @Test
  public void testLanguagesEnum() {

    assertThat(Language.values().length).isEqualTo(19);
    assertThat(Language.ABAP.sq).isEqualTo("abap");
    assertThat(Language.ABAP.rspec).isEqualTo("ABAP");
    assertThat(Language.ABAP.update).isTrue();
    assertThat(Language.ABAP.getSqCommon()).isEqualTo("common-abap");
    assertThat(Language.ABAP.doUpdate()).isTrue();
  }

  @Test
  public void testFromString() {

    assertThat(Language.fromString("Java")).isEqualTo(Language.JAVA);
    assertThat(Language.fromString("java")).isEqualTo(Language.JAVA);
    assertThat(Language.fromString("")).isNull();
    assertThat(Language.fromString(null)).isNull();
    assertThat(Language.fromString("Orange")).isNull();
    assertThat(Language.fromString("JavaScript")).isEqualTo(Language.JS);
    assertThat(Language.fromString("Python")).isEqualTo(Language.PY);
  }

}
