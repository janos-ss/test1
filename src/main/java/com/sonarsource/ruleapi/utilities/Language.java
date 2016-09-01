/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import org.fest.util.Strings;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;


public enum Language {
  //     SonarQube      RSpec          common repo  
  ABAP  ("abap",        "ABAP",        "common-abap"),
  C     ("c",           "C",           "common-c"),
  COBOL ("cobol",       "Cobol",       "common-cobol"),
  CPP   ("cpp",         "C++",         "common-cpp"),
  CSH   ("csharpsquid", "C#",          "common-cs"),
  FLEX  ("flex",        "Flex",        "common-flex"),
  JAVA  ("squid",       "Java",        "common-java"),
  JS    ("javascript",  "JavaScript",  "common-js"),
  OBJC  ("objc",        "Objective-C", "common-objc"),
  PHP   ("php",         "PHP",         "common-php"),
  PLI   ("pli",         "PL/I",        "common-pli"),
  PLSQL ("plsql",       "PL/SQL",      "common-plsql"),
  PY    ("python",      "Python",      "common-py"),
  RPG   ("rpg",         "RPG",         "common-rpg"),
  SWIFT ("swift",       "Swift",       "common-swift"),
  VB    ("vb",          "VB6",         "common-vb"),
  VBNET ("vbnet",       "VB.Net",      "common-vbnet"),
  WEB   ("Web",         "Web",         ""),
  XML   ("xml",         "XML",         "");

  protected final String sq;
  protected final String rspec;
  protected final String sqCommon;

  public static final Set<Language> LEGACY_LANGUAGES = Collections.unmodifiableSet(EnumSet.of(
          Language.ABAP,
          Language.COBOL,
          Language.PLI,
          Language.PLSQL,
          Language.RPG
  ));
  public static final Set<Language> STRONGLY_TYPED_LANGUAGES = Collections.unmodifiableSet(EnumSet.of(
          Language.C,
          Language.CPP,
          Language.CSH,
          Language.FLEX,
          Language.JAVA,
          Language.OBJC,
          Language.SWIFT,
          Language.VB,
          Language.VBNET
  ));
  public static final Set<Language> LOOSLY_TYPE_LANGUAGES = Collections.unmodifiableSet(EnumSet.of(
          Language.JS,
          Language.PHP,
          Language.PY
  ));

  Language(String sq, String rspec, String sqCommon) {
    this.sq = sq;
    this.rspec = rspec;
    this.sqCommon = sqCommon;
  }

  public String getSq() {
    return sq;
  }

  public String getRspec() {
    return rspec;
  }

  public String getSqCommon() {

    return sqCommon;
  }

  public static Language fromString(String value) {

    if (Strings.isNullOrEmpty(value)) {
      return null;
    }

    for (Language language : Language.values()) {
      if (language.name().equalsIgnoreCase(value) || language.rspec.equalsIgnoreCase(value) || language.sq.equalsIgnoreCase(value)) {
        return language;
      }
    }
    return null;
  }

}
