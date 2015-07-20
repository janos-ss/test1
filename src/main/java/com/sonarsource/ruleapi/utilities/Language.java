/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

import org.fest.util.Strings;

public enum Language {
  //     SonarQube      RSpec          doUpdate
  ABAP  ("abap",        "ABAP",        true),
  C     ("c",           "C",           true),
  COBOL ("cobol",       "Cobol",       true),
  CPP   ("cpp",         "C++",         true),
  CSH   ("csharpsquid", "C#",          true),
  FLEX  ("flex",        "Flex",        true),
  JAVA  ("squid",       "Java",        true),
  JS    ("javascript",  "JavaScript",  true),
  OBJC  ("objc",        "Objective-C", true),
  PHP   ("php",         "PHP",         true),
  PLI   ("pli",         "PL/I",        true),
  PLSQL ("plsql",       "PL/SQL",      true),
  PY    ("python",      "Python",      true),
  RPG   ("rpg",         "RPG",         true),
  SWIFT ("swift",       "Swift",       true),
  VB    ("vb",          "VB6",         true),
  VBNET ("vbnet",       "VB.Net",      true),
  WEB   ("Web",         "Web",         true),
  XML   ("xml",         "XML",         true);

  protected final String sq;
  protected final String rspec;
  protected final boolean update;

  Language(String sq, String rspec, boolean update) {
    this.sq = sq;
    this.rspec = rspec;
    this.update = update;
  }

  public String getSq() {
    return sq;
  }

  public String getRspec() {
    return rspec;
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
