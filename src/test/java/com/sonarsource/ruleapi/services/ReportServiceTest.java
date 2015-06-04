/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.utilities.Language;
import org.junit.Assert;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class ReportServiceTest {

  @Test
  public void testNoOutdatedReport() {
    ReportService rs = new ReportService();
    try {
      rs.writeOutdatedRulesReport(null, null);
      Assert.fail("An exception should have been thrown.");
    } catch (RuleException e) {

    }
    try {
      rs.writeOutdatedRulesReport(Language.C, null);
      Assert.fail("An exception should have been thrown.");
    } catch (RuleException e) {

    }
    try {
      rs.writeOutdatedRulesReport(null, "http://localhost:9000");
      Assert.fail("An exception should have been thrown.");
    } catch (RuleException e) {

    }

  }

}
