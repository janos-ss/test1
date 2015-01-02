/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

public enum Language {
  ABAP  ("abap",        "ABAP",        "abap-sonar-way-38370",  true),
  C     ("c",           "C",           "c-sonar-way-44762",     true),
  COBOL ("cobol",       "Cobol",       "cobol-sonar-way-41769", false),
  CPP   ("cpp",         "C++",         "cpp-sonar-way-81587",   true),
  CSH   ("csharpsquid", "C#",          "cs-sonar-way-31865",    true),
  FLEX  ("flex",        "Flex",        "flex-sonar-way-91920",  true),
  JAVA  ("squid",       "Java",        "java-sonar-way-45126",  true),
  JS    ("javascript",  "JavaScript",  "js-sonar-way-56838",    true),
  OBJC  ("objc",        "Objective-C", "objc-sonar-way-83399",  true),
  PHP   ("php",         "PHP",         "php-sonar-way-059",     true),
  PLI   ("pli",         "PL/I",        "pli-sonar-way-95331",   true),
  PLSQL ("plsql",       "PL/SQL",      "plsql-sonar-way-37514", false),
  PY    ("python",      "Python",      "py-sonar-way-67511",    true),
  RPG   ("rpg",         "RPG",         "rpg-sonar-way-64226",   true),
  VB    ("vb",          "VB6",         "vb-sonar-way-21338",    true),
  VBNET ("vbnet",       "VB.NET",      "vbnet-sonar-way-31082", false),
  XML   ("xml",         "XML",         "web-sonar-way-50375",   true);

  protected final String sq;
  protected final String rspec;
  protected final String sqProfileKey;
  protected final boolean update;

  Language(String sq, String rspec, String sqProfileKey, boolean update) {
    this.sq = sq;
    this.rspec = rspec;
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
  
}
