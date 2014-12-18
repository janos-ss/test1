/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.CodingStandardCoverage;
import com.sonarsource.ruleapi.utilities.RuleException;

public class MisraC2004CoverageReport {

  public static void main(String[] args) throws RuleException {
    CodingStandardCoverage coverage = CodingStandardService.INSTANCE.findMisraC2004Coverage();
    coverage.printReport();
  }
 
}
