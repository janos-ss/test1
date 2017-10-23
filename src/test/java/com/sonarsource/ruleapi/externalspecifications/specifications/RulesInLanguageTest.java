/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Profile;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RulesInLanguageTest {

  RulesInLanguage ril = new RulesInLanguage();

  @Test
  public void getBadgeValue(){
    RulesInLanguage report = new RulesInLanguage();

    assertThat(report.getBadgeValue(null)).isEqualTo("");
  }

  @Test
  public void getHtmlLanguageReport() {

    assertThat(ril.getHtmlLanguageReport(null, null).getReport()).isEmpty();
    assertThat(ril.getHtmlLanguageReport(null, Language.JAVA).getReport()).isEmpty();
    assertThat(ril.getHtmlLanguageReport(RuleManager.SONARQUBE_COM, null).getReport()).isEmpty();
  }

  @Test
  public void isRuleDefault(){
    Rule rule = new Rule("");
    rule.setTitle("Rule1");

    assertThat(ril.isRuleDefault(rule)).isFalse();

    rule.getDefaultProfiles().add(new Profile("PHP"));
    assertThat(ril.isRuleDefault(rule)).isFalse();


    rule.getDefaultProfiles().add(new Profile("Sonar way"));
    assertThat(ril.isRuleDefault(rule)).isTrue();

  }

  @Test
  public void generateReport(){
    ril.setLanguage(Language.JAVA);

    List<Rule> rules = new ArrayList<>();
    Rule rule = new Rule("Java");
    rule.setTitle("Rule1");
    rule.setType(Rule.Type.BUG);
    rule.setKey("S123");
    rule.setSeverity(Rule.Severity.MAJOR);
    rules.add(rule);

    String report = ril.generateReport(RuleManager.SONARQUBE_COM, rules);
    assertThat(report).contains(rule.getTitle());
    assertThat(report).contains(Utilities.getFormattedDateString());
  }

  @Test
  public void groupRulesByType(){
    List<Rule> rules = new ArrayList<>();
    Rule rule = new Rule("");
    rule.setTitle("Rule1");
    rule.setType(Rule.Type.BUG);
    rules.add(rule);

    rule = new Rule("");
    rule.setTitle("Rule2");
    rule.setType(Rule.Type.BUG);
    rules.add(rule);

    rule = new Rule("");
    rule.setTitle("Rule3");
    rules.add(rule);

    Map<Rule.Type, List<Rule>> map = ril.groupRulesByType(rules);

    assertThat(map.get(Rule.Type.VULNERABILITY)).isNull();
    assertThat(map.get(Rule.Type.BUG)).hasSize(2);
    assertThat(map.get(Rule.Type.CODE_SMELL)).hasSize(1);

    rule = new Rule("");
    rule.setTitle("Rule2");
    rule.setType(Rule.Type.VULNERABILITY);
    rules.add(rule);

    map = ril.groupRulesByType(rules);

    assertThat(map.get(Rule.Type.VULNERABILITY)).hasSize(1);
    assertThat(map.get(Rule.Type.BUG)).hasSize(2);
    assertThat(map.get(Rule.Type.CODE_SMELL)).hasSize(1);
  }

  @Test
  public void getRuleRow(){
    Rule rule = new Rule("Java");
    rule.setKey("S111");

    rule.setTitle("\"<stdio.h>\" should not be used");
    rule.setSeverity(Rule.Severity.CRITICAL);

    String expectedHtml="<tr><td><a href='https://sonarqube.com/coding_rules#rule_key=null%3AS111' target='rule'>S111</a></td><td><a title='Critical'><i class=\"icon-severity-Critical\"></i></a> \"&lt;stdio.h&gt;\" should not be used</td><td class=\"text-center\"></td><td></td><td></td></tr>\n";

    assertThat(ril.getRuleRow(rule, RuleManager.SONARQUBE_COM, "")).isEqualTo(expectedHtml);

  }

}
