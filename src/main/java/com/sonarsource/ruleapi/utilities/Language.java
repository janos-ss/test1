/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

import org.fest.util.Strings;

public enum Language {
  //     SonarQube      RSpec          Common          Nemo profile key       doUpdate
  ABAP  ("abap",        "ABAP",        "common-abap",  "abap-sonar-way-38370",  true),
  C     ("c",           "C",           "common-c",     "c-sonar-way-44762",     true),
  COBOL ("cobol",       "Cobol",       "common-cobol", "cobol-sonar-way-41769", true),
  CPP   ("cpp",         "C++",         "common-cpp",   "cpp-sonar-way-81587",   true),
  CSH   ("csharpsquid", "C#",          "common-cs",    "cs-sonar-way-31865",    true),
  FLEX  ("flex",        "Flex",        "common-flex",  "flex-sonar-way-91920",  true),
  JAVA  ("squid",       "Java",        "common-java",  "java-sonar-way-45126",  true),
  JS    ("javascript",  "JavaScript",  "common-js",    "js-sonar-way-56838",    true),
  OBJC  ("objc",        "Objective-C", "common-objc",  "objc-sonar-way-83399",  true),
  PHP   ("php",         "PHP",         "common-php",   "php-sonar-way-059",     true),
  PLI   ("pli",         "PL/I",        "common-pli",   "pli-sonar-way-95331",   true),
  PLSQL ("plsql",       "PL/SQL",      "common-plsql", "plsql-sonar-way-37514", true),
  PY    ("python",      "Python",      "common-py",    "py-sonar-way-67511",    true),
  RPG   ("rpg",         "RPG",         "common-rpg",   "rpg-sonar-way-64226",   true),
  SWIFT ("swift",       "Swift",       "",   "",                                false),
  VB    ("vb",          "VB6",         "common-vb",    "vb-sonar-way-21338",    true),
  VBNET ("vbnet",       "VB.Net",      "common-vbnet", "vbnet-sonar-way-31082", true),
  WEB   ("Web",         "Web",         "",             "web-sonar-way-50375",   true),
  XML   ("xml",         "XML",         "",             "xml-sonar-way-06052",   true);

  protected final String sq;
  protected final String sqCommon;
  protected final String rspec;
  protected final String sqProfileKey;
  protected final boolean update;

  Language(String sq, String rspec, String sqCommon, String sqProfileKey, boolean update) {
    this.sq = sq;
    this.rspec = rspec;
    this.sqCommon = sqCommon;
    this.sqProfileKey = sqProfileKey;
    this.update = update;
  }

  public String getSq() {
    return sq;
  }

  public String getRspec() {
    return rspec;
  }

  public String getSqProfileKey() {
    return sqProfileKey;
  }

  public String getSqCommon() {
    return sqCommon;
  }

  public boolean doUpdate() {
    return update;
  }

  public static Language fromString(String value) {

    if (Strings.isNullOrEmpty(value)) {
      return null;
    }

    String uc = value.toUpperCase();
    for (Language language : Language.values()) {
      if (language.name().equals(uc)) {
        return language;
      }
    }
    return null;
  }

}
