/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Profile;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.utilities.Language;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class RulesInLanguageTest {

  RulesInLanguage ril = new RulesInLanguage();


  @Test
  public void getHtmlLanguageReport() {

    assertThat(ril.getHtmlLanguageReport(null, null)).isEmpty();
    assertThat(ril.getHtmlLanguageReport(null, Language.JAVA)).isEmpty();
    assertThat(ril.getHtmlLanguageReport(RuleManager.SONARQUBE_COM, null)).isEmpty();
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

}
