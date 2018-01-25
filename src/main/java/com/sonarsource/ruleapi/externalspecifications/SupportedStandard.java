/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.externalspecifications.misra.MisraC2004;
import com.sonarsource.ruleapi.externalspecifications.misra.MisraC2012;
import com.sonarsource.ruleapi.externalspecifications.misra.MisraCPP2008;
import com.sonarsource.ruleapi.externalspecifications.specifications.Cert;
import com.sonarsource.ruleapi.externalspecifications.specifications.Cwe;
import com.sonarsource.ruleapi.externalspecifications.specifications.OwaspTopTen;
import com.sonarsource.ruleapi.externalspecifications.specifications.RuleSpec;
import com.sonarsource.ruleapi.externalspecifications.specifications.RulesInLanguage;
import com.sonarsource.ruleapi.externalspecifications.specifications.SansTop25;
import com.sonarsource.ruleapi.externalspecifications.tools.*;


public enum SupportedStandard {

  RULES_IN_LANGUAGE(new RulesInLanguage()),
  RULE_SPEC(new RuleSpec()),

  // ReportableStandard
  MISRA_C_2004(new MisraC2004()),
  MISRA_C_2012(new MisraC2012()),
  MISRA_CPP_2008(new MisraCPP2008()),

  // Tools
  FINDBUGS(new FindBugs()),
  FINDSECBUGS(new FindSecBugs()),
  PMD(new Pmd()),
  CHECKSTYLE(new Checkstyle()),
  PYLINT(new Pylint()),
  CPP_CHECK(new Cppcheck()),
  RESHARPER(new ReSharper()),
  RESHARPER_WARNINGS(new ReSharperWarnings()),
  RESHARPER_VBNET(new ReSharperVbNet()),
  RESHARPER_JS(new ReSharperJavaScript()),
  FXCOP(new FxCop()),
  PC_LINT_C(new PcLintC()),
  PC_LINT_CPP(new PcLintCpp()),
  ESLINT(new EsLint()),
  SWIFTLINT(new SwiftLint()),

  // TaggableStandards
  CWE(new Cwe()),
  CERT(new Cert()),

  OWASP(new OwaspTopTen()),  // ReportableStandard
  OWASP_A1(OwaspTopTen.StandardRule.A1),  // TaggableStandards
  OWASP_A2(OwaspTopTen.StandardRule.A2),
  OWASP_A3(OwaspTopTen.StandardRule.A3),
  OWASP_A4(OwaspTopTen.StandardRule.A4),
  OWASP_A5(OwaspTopTen.StandardRule.A5),
  OWASP_A6(OwaspTopTen.StandardRule.A6),
  OWASP_A7(OwaspTopTen.StandardRule.A7),
  OWASP_A8(OwaspTopTen.StandardRule.A8),
  OWASP_A9(OwaspTopTen.StandardRule.A9),
  OWASP_A10(OwaspTopTen.StandardRule.A10),

  SANS_TOP_25(new SansTop25()),  // ReportableStandard
  SANS_TOP_25_INSECURE(SansTop25.Category.INSECURE_INTERACTION),  // TaggableStandards
  SANS_TOP_25_RISKY(SansTop25.Category.RISKY_RESOURCE),
  SANS_TOP_25_POROUS(SansTop25.Category.POROUS_DEFENSES);


  private Standard standard;

  SupportedStandard(Standard rulesRepository) {
    this.standard = rulesRepository;
  }

  public Standard getStandard() {
    return standard;
  }

  public static SupportedStandard fromString(String name) {
    for (SupportedStandard std : SupportedStandard.values()) {
      if (std.name().equalsIgnoreCase(name)) {
        return std;
      }
    }
    return null;
  }

}
