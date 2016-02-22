/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import org.fest.util.Strings;


public enum Language {
  //     SonarQube      RSpec          common repo  securityProfile
  ABAP  ("abap",        "ABAP",        "common-abap",  false,         LanguageType.LEGACY),
  C     ("c",           "C",           "common-c",     false,         LanguageType.STRONG),
  COBOL ("cobol",       "Cobol",       "common-cobol", false,         LanguageType.LEGACY),
  CPP   ("cpp",         "C++",         "common-cpp",   false,         LanguageType.STRONG),
  CSH   ("csharpsquid", "C#",          "common-cs",    false,         LanguageType.STRONG),
  FLEX  ("flex",        "Flex",        "common-flex",  false,         LanguageType.STRONG),
  JAVA  ("squid",       "Java",        "common-java",  false,         LanguageType.STRONG),
  JS    ("javascript",  "JavaScript",  "common-js",    false,         LanguageType.LOOSE),
  OBJC  ("objc",        "Objective-C", "common-objc",  false,         LanguageType.STRONG),
  PHP   ("php",         "PHP",         "common-php",   false,         LanguageType.LOOSE),
  PLI   ("pli",         "PL/I",        "common-pli",   false,         LanguageType.LEGACY),
  PLSQL ("plsql",       "PL/SQL",      "common-plsql", false,         LanguageType.LEGACY),
  PY    ("python",      "Python",      "common-py",    false,         LanguageType.LOOSE),
  RPG   ("rpg",         "RPG",         "common-rpg",   false,         LanguageType.LEGACY),
  SWIFT ("swift",       "Swift",       "common-swift", false,         LanguageType.STRONG),
  VB    ("vb",          "VB6",         "common-vb",    false,         LanguageType.STRONG),
  VBNET ("vbnet",       "VB.Net",      "common-vbnet", false,         LanguageType.STRONG),
  WEB   ("Web",         "Web",         "",             false,         LanguageType.LEGACY),
  XML   ("xml",         "XML",         "",             false,         LanguageType.LEGACY),;

  protected final String sq;
  protected final String rspec;
  protected final String sqCommon;
  protected final boolean securityProfile;
  protected final LanguageType languageType;

  Language(String sq, String rspec, String sqCommon, boolean securityProfile, LanguageType languageType) {
    this.sq = sq;
    this.rspec = rspec;
    this.sqCommon = sqCommon;
    this.securityProfile = securityProfile;
    this.languageType = languageType;
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

  public LanguageType getLanguageType() {
    return this.languageType;
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
