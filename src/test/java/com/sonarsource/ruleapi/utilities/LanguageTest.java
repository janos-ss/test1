/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class LanguageTest {

  @Test
  public void testLanguagesEnum() {

    assertThat(Language.values().length).isEqualTo(22);
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
      assertThat(Language.LOOSLY_TYPE_LANGUAGES).doesNotContain(lang);
      assertThat(Language.STRONGLY_TYPED_LANGUAGES).doesNotContain(lang);
    }

    for (Language lang : Language.STRONGLY_TYPED_LANGUAGES) {
      assertThat(Language.LOOSLY_TYPE_LANGUAGES).doesNotContain(lang);
      assertThat(Language.LEGACY_LANGUAGES).doesNotContain(lang);
    }

    for (Language lang : Language.LOOSLY_TYPE_LANGUAGES) {
      assertThat(Language.LEGACY_LANGUAGES).doesNotContain(lang);
      assertThat(Language.STRONGLY_TYPED_LANGUAGES).doesNotContain(lang);
    }

    List<Language> langs = new ArrayList<>(Language.values().length);
    langs.addAll(Language.LEGACY_LANGUAGES);
    langs.addAll(Language.LOOSLY_TYPE_LANGUAGES);
    langs.addAll(Language.STRONGLY_TYPED_LANGUAGES);

    assertThat(langs).doesNotContain(Language.XML);
    assertThat(langs).doesNotContain(Language.WEB);
    assertThat(langs.size()).isEqualTo(Language.values().length - 2);

  }

}
