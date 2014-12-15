/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.specifications.FindBugs;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;


public class ReportWriterTest {

  private ReportWriter writer = new ReportWriter();

  @Test
  public void testMapFindBugsRules() {

    Map<FindBugs, List<Rule>> map = new HashMap<FindBugs, List<Rule>>();

    Rule rule = new Rule(Language.JAVA.rspec);
    List<String> list = new ArrayList<String>();
    list.add(FindBugs.AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION.name());
    list.add(FindBugs.BC_BAD_CAST_TO_ABSTRACT_COLLECTION.name());
    rule.setFindbugs(list);

    writer.mapFindBugsRules(map, rule);

    assertThat(map.size()).isEqualTo(2);
    assertThat(map.get(FindBugs.AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION)).hasSize(1);
    assertThat(map.get(FindBugs.BC_BAD_CAST_TO_ABSTRACT_COLLECTION)).hasSize(1);
  }

  @Test
  public void testMapFindBugsRulesSadPaths() {
    Map<FindBugs, List<Rule>> map = new HashMap<FindBugs, List<Rule>>();
    writer.mapFindBugsRules(map, null);

    assertThat(map.size()).isEqualTo(0);

    Rule rule = new Rule(Language.JAVA.rspec);
    writer.mapFindBugsRules(map, rule);

    assertThat(map.size()).isEqualTo(0);

    rule.setFindbugs(new ArrayList<String>());
    writer.mapFindBugsRules(map, rule);

    assertThat(map.size()).isEqualTo(0);
  }
}
