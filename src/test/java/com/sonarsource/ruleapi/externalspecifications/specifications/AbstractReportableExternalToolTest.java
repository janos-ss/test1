/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.SupportedCodingStandard;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;


public class AbstractReportableExternalToolTest {

  @Test
  public void testFormatLine() {

    FindBugs fb = (FindBugs) SupportedCodingStandard.FINDBUGS.getCodingStandard();

    String result = String.format("  yo                9   37.67%%%n");
    assertThat(fb.formatLine("yo", 9, (float)37.671293)).isEqualTo(result);
  }

  @Test
  public void testGetCoveringRules() {
    FindBugs fb = new FindBugs();
    fb.populateRulesCoverageMap();

    Rule rule = new Rule("Java");
    List<String> list = new ArrayList<>();
    list.add(FindBugs.StandardRule.AM_CREATES_EMPTY_JAR_FILE_ENTRY.name());
    list.add(FindBugs.StandardRule.AM_CREATES_EMPTY_ZIP_FILE_ENTRY.name());

    fb.setCodingStandardRuleCoverageImplemented(list, rule);

    Map<Rule, List<String>> map = fb.getCoveringRules();
    assertThat(map.size()).isEqualTo(1);
    assertThat(map.get(rule)).isEqualTo(list);

  }

}
