/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.sonarsource.ruleapi.domain.CodingStandardCoverage;
import com.sonarsource.ruleapi.domain.SupportedCodingStandard;
import com.sonarsource.ruleapi.utilities.RuleException;

public enum CodingStandardService {

  INSTANCE;

  public List<SupportedCodingStandard> supportedCodingStandards() {
    return ImmutableList.of(
      SupportedCodingStandard.MISRA_C_2004,
      SupportedCodingStandard.MISRA_C_2012,
      SupportedCodingStandard.MISRA_CPP_2008
      /*
       * SupportedCodingStandard.CWE,
       * SupportedCodingStandard.CERT_C
       */
      );
  }

  public CodingStandardCoverage findMisraC2004Coverage() throws RuleException {
    CodingStandardCoverage result = new CodingStandardCoverage(SupportedCodingStandard.MISRA_C_2004);
    result.computeCoverage();
    return result;
  }

  public CodingStandardCoverage findMisraC2012Coverage() throws RuleException {
    CodingStandardCoverage result = new CodingStandardCoverage(SupportedCodingStandard.MISRA_C_2012);
    result.computeCoverage();
    return result;
  }

  public CodingStandardCoverage findMisraCPP2008Coverage() throws RuleException {
    CodingStandardCoverage result = new CodingStandardCoverage(SupportedCodingStandard.MISRA_CPP_2008);
    result.computeCoverage();
    return result;
  }
}
