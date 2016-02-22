package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class RuleSpecTest {

  @Test
  public void testBuildRuleRow(){

    Rule rule = new Rule("");
    rule.setKey("S1234");
    rule.setTitle("Don't do bad things");
    Set<String> covered = rule.getCoveredLanguages();
    covered.add("Java");
    covered.add("C#");

    String expectedResult = "<tr><td><a href='https://jira.sonarsource.com/browse/RSPEC-1234'>RSPEC-1234</a> Don't do bad things<br/>\n" +
            "</td><td>C#, Java</td><td></td><td></td></tr>";

    RuleSpec ruleSpec = new RuleSpec();

    assertThat(ruleSpec.buildRuleRow(rule)).isEqualTo(expectedResult);
  }

  @Test
  public void testGetRuleTable(){

    List<Rule> rules = new ArrayList<>();

    Rule rule = new Rule("");
    rule.setKey("S1234");
    rule.setTitle("Don't do bad things");
    Set<String> covered = rule.getCoveredLanguages();
    covered.add("Java");
    covered.add("C#");

    rules.add(rule);

    rule = new Rule("");
    rule.setKey("S4567");
    rule.setTitle("X should be Y");
    covered = rule.getCoveredLanguages();
    covered.add("PHP");
    covered.add("COBOL");
    covered.add("Groovy");  // not a recognized language

    rules.add(rule);

    String expectedResult = "<h2>RSpec coverage by language</h2>\n" +
            "<table><tr><th></th><th>Stronly typed</th><th>Weakly typed</th><th>Legacy</th></tr><tr><td><a href='https://jira.sonarsource.com/browse/RSPEC-4567'>RSPEC-4567</a> X should be Y<br/>\n" +
            "</td><td></td><td>PHP</td><td>COBOL</td></tr><tr><td><a href='https://jira.sonarsource.com/browse/RSPEC-1234'>RSPEC-1234</a> Don't do bad things<br/>\n" +
            "</td><td>C#, Java</td><td></td><td></td></tr></table>";

    RuleSpec ruleSpec = new RuleSpec();
    String result = ruleSpec.getRuleTable(rules);

    assertThat(result.contains("Groovy")).isFalse();
    assertThat(result).isEqualTo(expectedResult);
  }

}
