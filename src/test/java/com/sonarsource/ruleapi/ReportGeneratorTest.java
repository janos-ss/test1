/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi;

import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.services.ReportService;
import org.junit.Test;

import com.sonar.orchestrator.Orchestrator;

public class ReportGeneratorTest {

  @Test
  public void generateReports() {
  	if("true".equals(System.getProperty("reports.generation", "false"))) {
      ReportService rs = new ReportService();
      rs.writeReportsWithOrchestrator();
  	}
  }
}
