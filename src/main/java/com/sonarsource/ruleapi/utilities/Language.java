/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import org.fest.util.Strings;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;


public enum Language {
  //     SonarQube      RSpec          common repo      reportName
  ABAP  ("abap",        "ABAP",        "common-abap",    "SonarABAP"),
  C     ("c",           "C",           "common-c",       "SonarC++ for C"),
  COBOL ("cobol",       "Cobol",       "common-cobol",   "SonarCOBOL"),
  CPP   ("cpp",         "C++",         "common-cpp",     "SonarC++"),
  CSH   ("csharpsquid", "C#",          "common-cs",      "SonarC#"),
  FLEX  ("flex",        "Flex",        "common-flex",    "SonarFlex"),
  JAVA  ("squid",       "Java",        "common-java",    "SonarJava"),
  JS    ("javascript",  "JavaScript",  "common-js",      "SonarJS"),
  OBJC  ("objc",        "Objective-C", "common-objc",    "SonarC++ for Objective-C"),
  PHP   ("php",         "PHP",         "common-php",     "SonarPHP"),
  PLI   ("pli",         "PL/I",        "common-pli",     "SonarPLI"),
  PLSQL ("plsql",       "PL/SQL",      "common-plsql",   "SonarPLSQL"),
  PY    ("python",      "Python",      "common-py",      "SonarPython"),
  RPG   ("rpg",         "RPG",         "common-rpg",     "SonarRPG"),
  SWIFT ("swift",       "Swift",       "common-swift",   "SonarSwift"),
  TSQL  ("tsql",        "T-SQL",       "",               "SonarT-SQL"),
  TS    ("typescript",  "TypeScript",  "",               "SonarTS"),
  VB    ("vb",          "VB6",         "common-vb",      "SonarVB6"),
  VBNET ("vbnet",       "VB.Net",      "common-vbnet",   "SonarVB.NET"),
  WEB   ("Web",         "Web",         "",               "SonarWeb"),
  XML   ("xml",         "XML",         "",               "SonarXML");

  protected final String sq;
  protected final String rspec;
  protected final String sqCommon;
  protected final String reportName;

  public static final Set<Language> LEGACY_LANGUAGES = Collections.unmodifiableSet(EnumSet.of(
          Language.ABAP,
          Language.COBOL,
          Language.PLI,
          Language.PLSQL,
          Language.RPG,
          Language.TSQL
  ));
  public static final Set<Language> STRONGLY_TYPED_LANGUAGES = Collections.unmodifiableSet(EnumSet.of(
          Language.C,
          Language.CPP,
          Language.CSH,
          Language.FLEX,
          Language.JAVA,
          Language.OBJC,
          Language.SWIFT,
          Language.TS,
          Language.VB,
          Language.VBNET
  ));
  public static final Set<Language> LOOSLY_TYPE_LANGUAGES = Collections.unmodifiableSet(EnumSet.of(
          Language.JS,
          Language.PHP,
          Language.PY
  ));

  Language(String sq, String rspec, String sqCommon, String reportName) {
    this.sq = sq;
    this.rspec = rspec;
    this.sqCommon = sqCommon;
    this.reportName = reportName;
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

  public String getReportName() {
    return reportName;
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
