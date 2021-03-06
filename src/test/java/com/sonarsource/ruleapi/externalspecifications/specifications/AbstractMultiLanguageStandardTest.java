/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.ReportAndBadge;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.utilities.Language;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class AbstractMultiLanguageStandardTest extends TestCase {

  private static Cwe cwe = new Cwe();

  @Test
  public void testGetReportTypes(){
    assertThat(cwe.getReportTypes()).isNotEmpty();
  }

  @Test
  public void testGetHtmlLanguageReports() {

    Map<Language,ReportAndBadge> reports = cwe.getHtmlLanguageReports(null);
    assertThat(reports).isNull();

  }

  @Test
  public void getBadgeValue(){

    assertThat(cwe.getBadgeValue(null)).isEqualTo("");
  }


  @Test
  public void testGetHtmlLanguageReport() {

    assertThat(cwe.getHtmlLanguageReport(RuleManager.SONARQUBE_COM, null)).isNull();
    assertThat(cwe.getHtmlLanguageReport(null, Language.JAVA)).isNull();
    assertThat(cwe.getHtmlLanguageReport(null, null)).isNull();

  }

  @Test
  public void testInitCoverage() throws Exception {

    cwe.initCoverageResults(null);

    assertThat(cwe.getRulesCoverage()).isNull();
  }

  @Test
  public void testSetLanguage() {
    cwe.setLanguage(null);
    assertThat(cwe.getLanguage()).isNull();

    cwe.setLanguage(Language.JAVA);
    assertThat(cwe.getLanguage()).isEqualTo(Language.JAVA);
  }

  @Test
  public void testGetNameIfStandardApplies(){
    Cwe cwe = new Cwe();

    Rule rule = new Rule("Java");

    assertThat(cwe.getNameIfStandardApplies(rule)).isNull();

    rule.getCwe().add("CWE-123");
    assertThat(cwe.getNameIfStandardApplies(rule)).isEqualTo(cwe.getStandardName());
  }

}
