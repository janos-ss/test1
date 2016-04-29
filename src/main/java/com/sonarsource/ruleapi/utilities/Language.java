/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import org.fest.util.Strings;


public enum Language {
  //     SonarQube      RSpec          common repo  securityProfile
  ABAP  ("abap",        "ABAP",        "common-abap",  false),
  C     ("c",           "C",           "common-c",     false),
  COBOL ("cobol",       "Cobol",       "common-cobol", false),
  CPP   ("cpp",         "C++",         "common-cpp",   false),
  CSH   ("csharpsquid", "C#",          "common-cs",    false),
  FLEX  ("flex",        "Flex",        "common-flex",  false),
  JAVA  ("squid",       "Java",        "common-java",  false),
  JS    ("javascript",  "JavaScript",  "common-js",    false),
  OBJC  ("objc",        "Objective-C", "common-objc",  false),
  PHP   ("php",         "PHP",         "common-php",   false),
  PLI   ("pli",         "PL/I",        "common-pli",   false),
  PLSQL ("plsql",       "PL/SQL",      "common-plsql", false),
  PY    ("python",      "Python",      "common-py",    false),
  RPG   ("rpg",         "RPG",         "common-rpg",   false),
  SWIFT ("swift",       "Swift",       "common-swift", false),
  VB    ("vb",          "VB6",         "common-vb",    false),
  VBNET ("vbnet",       "VB.Net",      "common-vbnet", false),
  WEB   ("Web",         "Web",         "",             false),
  XML   ("xml",         "XML",         "",             false);

  protected final String sq;
  protected final String rspec;
  protected final String sqCommon;
  protected final boolean securityProfile;

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

  Language(String sq, String rspec, String sqCommon, boolean securityProfile) {
    this.sq = sq;
    this.rspec = rspec;
    this.sqCommon = sqCommon;
    this.securityProfile = securityProfile;
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

  public boolean hasSecurityProfile() {

    return securityProfile;
  }

  public static Language fromString(String value) {

    if (Strings.isNullOrEmpty(value)) {
      return null;
    }

    for (Language language : Language.values()) {
      if (language.name().equalsIgnoreCase(value) || language.rspec.equalsIgnoreCase(value)) {
        return language;
      }
    }
    return null;
  }

}
