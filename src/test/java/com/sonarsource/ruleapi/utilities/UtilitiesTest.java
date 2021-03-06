/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class UtilitiesTest {

  @Test
  public void getFormattedDateString(){
    LocalDateTime ldt = LocalDateTime.now();
    String dateString = Utilities.getFormattedDateString();

    assertThat(dateString).contains(String.valueOf(ldt.getDayOfMonth()));
    assertThat(dateString).contains(String.valueOf(ldt.getMonthValue()));
    assertThat(dateString).contains(String.valueOf(ldt.getYear()));
  }

  @Test
  public void testNormalizeKey() {
    String key1 = "StrictMode";
    String key2 = "S1111";
    String key3 = "S000109";

    assertThat(Utilities.normalizeKey(key1)).isEqualTo(key1);
    assertThat(Utilities.normalizeKey(key2)).isEqualTo("RSPEC-1111");
    assertThat(Utilities.normalizeKey(key3)).isEqualTo("RSPEC-109");
  }

  @Test
  public void testDenormalizeKey() {
    String key1 = "StrictMode";
    String key2 = "RSPEC-1111";

    assertThat(Utilities.denormalizeKey(key1)).isEqualTo(key1);
    assertThat(Utilities.denormalizeKey(key2)).isEqualTo("S1111");
  }

  @Test
  public void testIsKeyNormal() {
    String key1 = "StrictMode";
    String key2 = "RSPEC-1111";
    String key3 = "S000109";

    assertThat(Utilities.isKeyNormal(key1)).isFalse();
    assertThat(Utilities.isKeyNormal(key2)).isTrue();
    assertThat(Utilities.isKeyNormal(key3)).isFalse();
    assertThat(Utilities.isKeyNormal(null)).isFalse();
  }

  @Test
  public void testListToString() {

    List<String> list = new ArrayList<>();

    assertThat(Utilities.listToString(list, true)).isEqualTo("");

    list.add("red");
    list.add("green");
    list.add("blue");

    assertThat(Utilities.listToString(list, true)).isEqualTo("red, green, blue");
    assertThat(Utilities.listToString(list, false)).isEqualTo("red green blue");
  }

  @Test
  public void testFindCharBefore() {
    String str = "a red, a yellow, a blue";
    int pos = str.indexOf("yellow");

    assertThat(Utilities.findBefore(str, pos, ',')).isEqualTo(5);
    assertThat(Utilities.findBefore(str, pos, '&')).isEqualTo(-1);
  }

  @Test
  public void testFindStringBefore() {
    String str = "this code, that code and the other code.";
    int pos = str.indexOf("and");

    assertThat(Utilities.findBefore(str, pos, "code")).isEqualTo(16);
    assertThat(Utilities.findBefore(str, pos, "purple")).isEqualTo(-1);
  }


  @Test
  public void testGetNemoLinkedRuleReference() {
    Rule rule = new Rule("C");
    rule.setRepo("c");
    rule.setKey("RSPEC-1234");
    rule.setTitle("This is a rule title");

    String expectedLink = "<a href='http://localhost:9000/coding_rules#rule_key=c%3AS1234' target='rule'>S1234</a> This is a rule title<br/>\n";

    assertThat(Utilities.getNemoLinkedRuleReference("http://localhost:9000", rule)).isEqualTo(expectedLink);

  }

  @Test
  public void testGetJiraLinkedRuleReference(){

    Rule rule = new Rule("Java");
    rule.setKey("RSPEC-1234");
    rule.setTitle("Test rule");

    String expectedLink = "<a href='https://jira.sonarsource.com/browse/RSPEC-1234'>RSPEC-1234</a> Test rule<br/>\n";

    assertThat(Utilities.getJiraLinkedRuleReference(rule)).isEqualTo(expectedLink);

  }

  @Test
  public void getInstanceLinkedRuleKey(){

    String instance = "http://bubba.com";

    Rule rule = new Rule("Java");
    rule.setKey("RSPEC-123");
    rule.setRepo("squid");

    String expectedLink = "<a href='http://bubba.com/coding_rules#rule_key=squid%3AS123' target='rule'>S123</a>";
    assertThat(Utilities.getInstanceLinkedRuleKey(instance, rule, false)).isEqualTo(expectedLink);

    rule.getLegacyKeys().add("foo");
    expectedLink = "<a href='http://bubba.com/coding_rules#rule_key=squid%3Afoo' target='rule'>foo</a>";
    assertThat(Utilities.getInstanceLinkedRuleKey(instance, rule, false)).isEqualTo(expectedLink);

    rule.getLegacyKeys().clear();
    rule.getLegacyKeys().add("ThisIsAReallyLongLegacyKey");
    expectedLink = "<a href='http://bubba.com/coding_rules#rule_key=squid%3AThisIsAReallyLongLegacyKey' target='rule'>ThisIsAR.</a>";
    assertThat(Utilities.getInstanceLinkedRuleKey(instance, rule, true)).isEqualTo(expectedLink);

    expectedLink = "<a href='http://bubba.com/coding_rules#rule_key=squid%3AThisIsAReallyLongLegacyKey' target='rule'>ThisIsAReallyLongLegacyKey</a>";
    assertThat(Utilities.getInstanceLinkedRuleKey(instance, rule, false)).isEqualTo(expectedLink);

  }

  @Test
  public void getDeployedKey(){
    Rule rule = new Rule("Java");
    rule.setKey("RSPEC-123");

    assertThat(Utilities.getDeployedKey(rule)).isEqualTo("S123");

    rule.getLegacyKeys().add("foo");
    assertThat(Utilities.getDeployedKey(rule)).isEqualTo("foo");

    rule.getLegacyKeys().add("bar");
    assertThat(Utilities.getDeployedKey(rule)).isEqualTo("foo");

  }

}
