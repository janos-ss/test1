/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.utilities.Language;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.fest.assertions.Assertions.assertThat;


public class AbstractMultiLanguageStandardTest extends TestCase {

  private static Cwe cwe = new Cwe();

  public void testGetHtmlLangaugeReports() {

    Map<Language,String> reports = cwe.getHtmlLanguageReports(null);
    assertThat(reports).isNull();

  }

  public void testGetHtmlLanguageReport() {

    assertThat(cwe.getHtmlLanguageReport(RuleManager.NEMO, null)).isNull();
    assertThat(cwe.getHtmlLanguageReport(null, Language.JAVA)).isNull();
    assertThat(cwe.getHtmlLanguageReport(null, null)).isNull();

  }

  public void testInitCoverage() throws Exception {

    assertThat(cwe.initCoverage(null)).isNull();
  }

  public void testPopulateStandardMap() {

    String id = "CWE-123";

    Rule rule = new Rule("");
    List<String> list = cwe.getRspecReferenceFieldValues(rule);
    list.add(id);
    list.add("CWE-234");

    Map<String, List<Rule>> standardRules = new TreeMap<String, List<Rule>>();
    cwe.populateStandardMap(standardRules, rule, rule);

    assertThat(standardRules).hasSize(2);
    List<Rule> ruleList = standardRules.get(id);
    assertThat(ruleList).hasSize(1);
    assertThat(ruleList.contains(rule)).isTrue();

  }

  @Test
  public void testSetLanguage() {
    cwe.setLanguage(null);
    assertThat(cwe.getLanguage()).isNull();

    cwe.setLanguage(Language.JAVA);
    assertThat(cwe.getLanguage()).isEqualTo(Language.JAVA);
  }
}
