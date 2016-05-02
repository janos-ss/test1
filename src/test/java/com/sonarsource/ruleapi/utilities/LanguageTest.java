/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class LanguageTest {

  @Test
  public void testLanguagesEnum() {

    assertThat(Language.values().length).isEqualTo(19);
    assertThat(Language.ABAP.sq).isEqualTo("abap");
    assertThat(Language.ABAP.rspec).isEqualTo("ABAP");
    assertThat(Language.ABAP.getSqCommon()).isEqualTo("common-abap");
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

  @Test
  public void languageTypeMembership(){

    for (Language lang : Language.LEGACY_LANGUAGES) {
      assertThat(Language.LOOSLY_TYPE_LANGUAGES).excludes(lang);
      assertThat(Language.STRONGLY_TYPED_LANGUAGES).excludes(lang);
    }

    for (Language lang : Language.STRONGLY_TYPED_LANGUAGES) {
      assertThat(Language.LOOSLY_TYPE_LANGUAGES).excludes(lang);
      assertThat(Language.LEGACY_LANGUAGES).excludes(lang);
    }

    for (Language lang : Language.LOOSLY_TYPE_LANGUAGES) {
      assertThat(Language.LEGACY_LANGUAGES).excludes(lang);
      assertThat(Language.STRONGLY_TYPED_LANGUAGES).excludes(lang);
    }

    List<Language> langs = new ArrayList<>(Language.values().length);
    langs.addAll(Language.LEGACY_LANGUAGES);
    langs.addAll(Language.LOOSLY_TYPE_LANGUAGES);
    langs.addAll(Language.STRONGLY_TYPED_LANGUAGES);

    assertThat(langs).excludes(Language.XML);
    assertThat(langs).excludes(Language.WEB);
    assertThat(langs.size()).isEqualTo(Language.values().length - 2);

  }

}
