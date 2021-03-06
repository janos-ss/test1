/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleSpecTest {

  private Rule getRuleWithEachLanguageType(){
    Rule rule = new Rule("");
    rule.setKey("S1234");
    rule.setTitle("Don't do bad things");
    Set<String> covered = rule.getCoveredLanguages();

    // Strongly typed
    covered.add("Java");
    covered.add("C#");

    // Loosely typed
    covered.add("JavaScript");

    // Legacy
    covered.add("COBOL");

    // Not typed
    covered.add("XML");

    // Not supported
    covered.add("Groovy");

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

    List<Language> irrelevant = new ArrayList<>();

    RuleSpec.populateCoveredLanguageLists(rule, strong, weak, legacy);

    assertThat(RuleSpec.getMissing(Language.LOOSLY_TYPE_LANGUAGES, weak, irrelevant)).contains(Language.PHP.getRspec())
            .contains(Language.PY.getRspec()).doesNotContain(Language.JS.getRspec());

    weak.clear();
    assertThat(RuleSpec.getMissing(Language.LOOSLY_TYPE_LANGUAGES, weak, irrelevant)).isEmpty();

    assertThat(RuleSpec.getMissing(Language.STRONGLY_TYPED_LANGUAGES, strong, irrelevant)).contains(",");

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

    RuleSpec.populateCoveredLanguageLists(rule, strong, weak, legacy);

    assertThat(strong).hasSize(2).contains(Language.JAVA).contains(Language.CSH);
    assertThat(weak).hasSize(1).contains(Language.JS);
    assertThat(legacy).hasSize(1).contains(Language.COBOL);

    // reset
    rule = getEmptyRule();
    strong.clear();
    weak.clear();
    legacy.clear();

    RuleSpec.populateCoveredLanguageLists(rule, strong, weak, legacy);

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
    String table = ruleSpec.getRuleTable(rules);
    assertThat(table).contains("<h2>").contains("</h2>").contains("<table>").contains("</table>").contains(rule.getTitle());
    assertThat(table).endsWith(Utilities.getFormattedDateString());
  }

  @Test
  public void buildRuleRow(){

    Rule rule = new Rule("");
    rule.setKey("S1212");
    rule.setTitle("This is my rule");
    Set<String> covered = rule.getCoveredLanguages();

    // rules with only one covered language suppressed
    covered.add("foo");
    assertThat(RuleSpec.buildRuleRow(rule)).isEmpty();

    // rules with all languages covered suppressed
    for (Language language : Language.values()) {
      covered.add(language.getRspec());
    }
    covered.remove("XML");
    covered.remove("Web");
    assertThat(RuleSpec.buildRuleRow(rule)).isEmpty();

    // rules with only irrelevant rules left uncovered suppressed
    covered.remove("JavaScript");
    rule.getIrrelevantLanguages().add("JavaScript");
    assertThat(RuleSpec.buildRuleRow(rule)).isEmpty();


    // rule types with no covering langauges suppressed
    rule = getRuleWithEachLanguageType();
    covered = rule.getCoveredLanguages();

    assertThat(RuleSpec.buildRuleRow(rule)).contains("ABAP").contains("C++").contains("PHP");

    covered.remove("COBOL");
    assertThat(RuleSpec.buildRuleRow(rule)).doesNotContain("ABAP").contains("PHP").contains("C++");
    covered.remove("JavaScript");
    assertThat(RuleSpec.buildRuleRow(rule)).doesNotContain("ABAP").doesNotContain("PHP").contains("C++");


    covered.add("COBOL");
    assertThat(RuleSpec.buildRuleRow(rule)).contains("ABAP").doesNotContain("PHP").contains("C++");
    covered.remove("Java");
    covered.remove("C#");
    assertThat(RuleSpec.buildRuleRow(rule)).contains("ABAP").doesNotContain("PHP").doesNotContain("C++");

    covered.add("JavaScript");
    assertThat(RuleSpec.buildRuleRow(rule)).contains("ABAP").contains("PHP").doesNotContain("C++");
    covered.remove("COBOL");
    assertThat(RuleSpec.buildRuleRow(rule)).doesNotContain("ABAP").contains("PHP").doesNotContain("C++");

    covered.add("Java");
    assertThat(RuleSpec.buildRuleRow(rule)).doesNotContain("ABAP").contains("PHP").contains("C++");


  }

}
