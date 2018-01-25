/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi;

import com.sonarsource.ruleapi.services.ReportService;
import com.sonarsource.ruleapi.services.RuleManager;
import org.junit.Test;

public class ReportGeneratorTest {

  @Test
  public void generateReports() {
  	if("true".equals(System.getProperty("reports.generation", "false"))) {
      ReportService rs = new ReportService();
      rs.writeInternalReports(RuleManager.SONARQUBE_COM);
      rs.writeUserFacingReports();
  	}
  }
}
