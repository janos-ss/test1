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
  CWE(new Cwe()),
  OWASP(new OwaspTopTen()),
  CERT(new Cert() );


  private AbstractCodingStandard codingStandard;

  private SupportedCodingStandard(AbstractCodingStandard rulesRepository) {
    this.codingStandard = rulesRepository;
  }

  public AbstractCodingStandard getCodingStandard() {
    return codingStandard;
  }

}
