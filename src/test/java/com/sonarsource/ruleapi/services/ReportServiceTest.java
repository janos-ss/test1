/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.externalRuleSpecifications.specifications.MisraCPP2008;
import com.sonarsource.ruleapi.utilities.RuleException;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class ReportServiceTest {

  private ReportService writer = new ReportService();


  @Test
  public void testtest(){
    try {

      writer.getSummaryCoverageReports();
    } catch (RuleException e) {
      e.printStackTrace();
    }
  }

}
