/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.CodingStandardCoverage;
import com.sonarsource.ruleapi.utilities.RuleException;

public class MisraCPP2008CoverageReport {

  public static void main(String[] args) throws RuleException {
    CodingStandardCoverage coverage = CodingStandardService.INSTANCE.findMisraCPP2008Coverage();
    coverage.printReport();
  }
 
}
