/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.utilities.Language;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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

  @Test
  public void testSortRulesBySeverity() {

    ReportService rs = new ReportService();

    List<Rule> rules = new ArrayList<>();
    Rule rule = new Rule("");
    rule.setSeverity(Rule.Severity.CRITICAL);

    rules.add(rule);

    Map<Rule.Severity, List<Rule>> severityMap = rs.sortRulesBySeverity(rules);

    assertThat(severityMap.get(Rule.Severity.CRITICAL)).hasSize(1);
    assertThat(severityMap.get(Rule.Severity.BLOCKER)).isNull();
  }

  @Test
  public void testAssembleLanguageRuleReport(){

    ReportService rs = new ReportService();

    Map<Rule.Severity, List<Rule>> severityMap = new EnumMap<Rule.Severity, List<Rule>>(Rule.Severity.class);

    List<Rule> list = new ArrayList<>();
    Rule rule = new Rule("Java");
    rule.setSeverity(Rule.Severity.MAJOR);
    rule.setKey("RSPEC-123");
    rule.setLegacyKeys(new ArrayList<String>());
    rule.getLegacyKeys().add("S123");
    rule.setTitle("X should [not] y!");
    rule.setRepo("squid");

    list.add(rule);
    severityMap.put(Rule.Severity.MAJOR, list);

    String report = rs.assembleLanguageRuleReport(Language.JAVA, "http://nemo.sonarqube.org", severityMap);
    assertThat(report).contains("Available Java rules");
    assertThat(report).contains("http://nemo.sonarqube.org/coding_rules#rule_key=squid%3AS123");

  }
}
