/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.externalspecifications.specifications.*;

public enum SupportedCodingStandard {

  MISRA_C_2004(new MisraC2004()),
  MISRA_C_2012(new MisraC2012()),
  MISRA_CPP_2008(new MisraCPP2008()),
  FINDBUGS(new FindBugs()),
  PMD(new Pmd()),
  PYLINT(new Pylint()),
  CWE(new Cwe()),
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
  CERT(new Cert()),
  SANS_TOP_25(new SansTop25()),  // ReportableStandard
  SANS_TOP_25_INSECURE(SansTop25.Category.INSECURE_INTERACTION),  // TaggableStandards
  SANS_TOP_25_RISKY(SansTop25.Category.RISKY_RESOURCE),
  SANS_TOP_25_POROUS(SansTop25.Category.POROUS_DEFENSES);


  private CodingStandard codingStandard;

  private SupportedCodingStandard(CodingStandard rulesRepository) {
    this.codingStandard = rulesRepository;
  }

  public CodingStandard getCodingStandard() {
    return codingStandard;
  }

}
