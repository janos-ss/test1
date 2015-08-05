/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

import org.fest.util.Strings;

public enum Language {
  //     SonarQube      RSpec          doUpdate   securityProfile
  ABAP  ("abap",        "ABAP",        true,      false),
  C     ("c",           "C",           true,      false),
  COBOL ("cobol",       "Cobol",       true,      false),
  CPP   ("cpp",         "C++",         true,      false),
  CSH   ("csharpsquid", "C#",          true,      false),
  FLEX  ("flex",        "Flex",        true,      false),
  JAVA  ("squid",       "Java",        true,      false),
  JS    ("javascript",  "JavaScript",  true,      false),
  OBJC  ("objc",        "Objective-C", true,      false),
  PHP   ("php",         "PHP",         true,      false),
  PLI   ("pli",         "PL/I",        true,      false),
  PLSQL ("plsql",       "PL/SQL",      true,      false),
  PY    ("python",      "Python",      true,      false),
  RPG   ("rpg",         "RPG",         true,      false),
  SWIFT ("swift",       "Swift",       true,      false),
  VB    ("vb",          "VB6",         true,      false),
  VBNET ("vbnet",       "VB.Net",      true,      false),
  WEB   ("Web",         "Web",         true,      false),
  XML   ("xml",         "XML",         true,      false),;

  protected final String sq;
  protected final String rspec;
  protected final boolean update;
  protected final boolean securityProfile;

  Language(String sq, String rspec, boolean update, boolean securityProfile) {
    this.sq = sq;
    this.rspec = rspec;
    this.update = update;
    this.securityProfile = securityProfile;
  }

  public String getSq() {
    return sq;
  }

  public String getRspec() {
    return rspec;
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
