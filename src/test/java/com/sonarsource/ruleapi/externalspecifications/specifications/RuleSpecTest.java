/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.utilities.Language;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class RuleSpecTest {

  private Rule getRuleWithEachLanguageType(){
    Rule rule = new Rule("");
    rule.setKey("S1234");
    rule.setTitle("Don't do bad things");
    Set<String> covered = rule.getCoveredLanguages();
    covered.add("Java");
    covered.add("C#");
    covered.add("JavaScript");
    covered.add("COBOL");
    covered.add("XML");  // supported language, but not included in any type
    covered.add("Groovy");  // not a supported language
    rule.getIrrelevantLanguages().add("Flex");
    return rule;
  }

  private Rule getEmptyRule() {

    return new Rule("");
  }

  @Test
  public void getMissing(){

    Rule rule = getRuleWithEachLanguageType();
    List<Language> strong = new ArrayList<>();
    List<Language> weak = new ArrayList<>();
    List<Language> legacy = new ArrayList<>();

    RuleSpec.populateLanguageLists(rule, strong, weak, legacy);

    assertThat(RuleSpec.getMissing(Language.LOOSLY_TYPE_LANGUAGES, weak)).contains(Language.PHP.getRspec())
            .contains(Language.PY.getRspec()).doesNotContain(Language.JS.getRspec());

    weak.clear();
    assertThat(RuleSpec.getMissing(Language.LOOSLY_TYPE_LANGUAGES, weak)).isEmpty();

    assertThat(RuleSpec.getMissing(Language.STRONGLY_TYPED_LANGUAGES, strong)).contains(",");

  }

  @Test
  public void getIrrelevantLanguageList() {
    Rule rule = getRuleWithEachLanguageType();
    assertThat(RuleSpec.getIrrelevantLanguageList(rule)).hasSize(1).contains(Language.FLEX);

    rule = getEmptyRule();
    assertThat(RuleSpec.getIrrelevantLanguageList(rule)).isEmpty();
  }

  @Test
  public void populateLanguageLists(){

    Rule rule = getRuleWithEachLanguageType();

    List<Language> strong = new ArrayList<>();
    List<Language> weak = new ArrayList<>();
    List<Language> legacy = new ArrayList<>();

    RuleSpec.populateLanguageLists(rule, strong, weak, legacy);

    assertThat(strong).hasSize(2).contains(Language.JAVA).contains(Language.CSH);
    assertThat(weak).hasSize(1).contains(Language.JS);
    assertThat(legacy).hasSize(1).contains(Language.COBOL);

    // reset
    rule = getEmptyRule();
    strong.clear();
    weak.clear();
    legacy.clear();

    RuleSpec.populateLanguageLists(rule, strong, weak, legacy);

    assertThat(strong).isEmpty();
    assertThat(weak).isEmpty();
    assertThat(legacy).isEmpty();
  }

  @Test
  public void getRuleTable(){

    List<Rule> rules = new ArrayList<>();

    RuleSpec ruleSpec = new RuleSpec();
    assertThat(ruleSpec.getRuleTable(rules)).contains("<h2>").contains("</h2>").contains("<table>").contains("</table>");

    Rule rule = getRuleWithEachLanguageType();
    rules.add(rule);
    ruleSpec = new RuleSpec();
    assertThat(ruleSpec.getRuleTable(rules)).contains("<h2>").contains("</h2>").contains("<table>").contains("</table>").contains(rule.getTitle());

  }

}
