/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import org.fest.util.Strings;

public enum Language {
  //     SonarQube      RSpec          doUpdate   securityProfile
  ABAP  ("abap",        "ABAP",        "common-abap",  true,      false),
  C     ("c",           "C",           "common-c",     true,      false),
  COBOL ("cobol",       "Cobol",       "common-cobol", true,      false),
  CPP   ("cpp",         "C++",         "common-cpp",   true,      false),
  CSH   ("csharpsquid", "C#",          "common-cs",    true,      false),
  FLEX  ("flex",        "Flex",        "common-flex",  true,      false),
  JAVA  ("squid",       "Java",        "common-java",  true,      false),
  JS    ("javascript",  "JavaScript",  "common-js",    true,      false),
  OBJC  ("objc",        "Objective-C", "common-objc",  true,      false),
  PHP   ("php",         "PHP",         "common-php",   true,      false),
  PLI   ("pli",         "PL/I",        "common-pli",   true,      false),
  PLSQL ("plsql",       "PL/SQL",      "common-plsql", true,      false),
  PY    ("python",      "Python",      "common-py",    true,      false),
  RPG   ("rpg",         "RPG",         "common-rpg",   true,      false),
  SWIFT ("swift",       "Swift",       "common-swift", true,      false),
  VB    ("vb",          "VB6",         "common-vb",    true,      false),
  VBNET ("vbnet",       "VB.Net",      "common-vbnet", true,      false),
  WEB   ("Web",         "Web",         "",             true,      false),
  XML   ("xml",         "XML",         "",             true,      false),;

  protected final String sq;
  protected final String rspec;
  protected final String sqCommon;
  protected final boolean update;
  protected final boolean securityProfile;

  Language(String sq, String rspec, String sqCommon, boolean update, boolean securityProfile) {
    this.sq = sq;
    this.rspec = rspec;
    this.sqCommon = sqCommon;
    this.update = update;
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

  public boolean doUpdate() {
    return update;
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
