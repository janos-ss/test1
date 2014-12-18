/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import com.sonarsource.ruleapi.specifications.MisraC2004;
import com.sonarsource.ruleapi.specifications.MisraC2012;
import com.sonarsource.ruleapi.specifications.MisraCPP2008;

public enum SupportedCodingStandard {

  MISRA_C_2004("MISRA C 2004", "misra", "MISRA C 2004", new MisraC2004()),
  MISRA_C_2012("MISRA C 2012", "misra", "MISRA C 2012", new MisraC2012()),
  MISRA_CPP_2008("MISRA C++ 2008", "misra", "MISRA C++ 2008", new MisraCPP2008());

  // CWE("CWE - Common Weakness Enumeration", "cwe", "CWE"),
  // CERT_C("CERT C", "cert", "CERT");
  // FindBugs("FindBugs", "findbugs", "FindBugs");

  private String name;

  private String rspecLabel;
  private String rspecReference;

  private AbstractCodingStandardRulesRepository rulesRepository;

  private SupportedCodingStandard(String name, String rspecLabel, String rspecReference, AbstractCodingStandardRulesRepository rulesRepository) {
    this.name = name;
    this.rspecLabel = rspecLabel;
    this.rspecReference = rspecReference;
    this.rulesRepository = rulesRepository;
  }
  
  public String getName() {
    return name;
  }

  public String getRspecLabel() {
    return rspecLabel;
  }

  public String getRspecReference() {
    return rspecReference;
  }

  public AbstractCodingStandardRulesRepository getRulesRepository() {
    return rulesRepository;
  }

}
