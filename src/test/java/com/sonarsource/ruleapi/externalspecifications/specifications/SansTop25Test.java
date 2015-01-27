/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;


public class SansTop25Test {

  private SansTop25 sansTop25 = new SansTop25();

  @Test
  public void testAddTagIfMissingAddTag() {

    Rule rule = new Rule("");
    rule.setTags(new ArrayList<String>());

    rule.getCwe().add("CWE-89");

    Map<String, Object> updates = new HashMap<String, Object>();

    sansTop25.addTagIfMissing(rule, updates);
    assertThat(updates).hasSize(1);
    assertThat((List<String>)updates.get("Labels")).hasSize(1);
    assertThat(rule.getTags()).hasSize(1);


    updates.clear();
    rule.getCwe().clear();
    sansTop25.addTagIfMissing(rule, updates);

  }

  @Test
  public void testAddTagIfMissingDoNothing(){

    Rule rule = new Rule("");
    rule.setTags(new ArrayList<String>());
    rule.getTags().add("sans-top25");

    rule.getCwe().add("CWE-89");

    Map<String, Object> updates = new HashMap<String, Object>();

    sansTop25.addTagIfMissing(rule, updates);
    assertThat(updates).isEmpty();

  }

  @Test
  public void testAddTagIfMissingRemoveTag(){

    Rule rule = new Rule("");
    rule.setTags(new ArrayList<String>());
    rule.getTags().add("sans-top25");

    Map<String, Object> updates = new HashMap<String, Object>();

    sansTop25.addTagIfMissing(rule, updates);
    assertThat(updates).hasSize(1);
    assertThat((List<String>)updates.get("Labels")).isEmpty();
    assertThat(rule.getTags()).isEmpty();
  }

  @Test
  public void testIsSansRuleItsNot() {

    Rule rule = new Rule("");

    assertThat(sansTop25.isSansRule(rule)).isFalse();

    rule.getCwe().add("CWE-1");
    assertThat(sansTop25.isSansRule(rule)).isFalse();
  }
}
