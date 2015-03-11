/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.SupportedCodingStandard;
import com.sonarsource.ruleapi.get.Fetcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class FindbugsTest {

  private static String FB_ID = "BC_IMPOSSIBLE_CAST";

  @Test
  public void testFormatLine() {

    FindBugs fb = (FindBugs)SupportedCodingStandard.FINDBUGS.getCodingStandard();

    String result = String.format("  yo                9   37.67%%%n");
    assertThat(fb.formatLine("yo", 9, (float)37.671293)).isEqualTo(result);
  }

  @Test
  public void testComputeCoverage(){
    FindBugs fb = new FindBugs();

    fb.populateRulesCoverageMap();
    fb.computeCoverage();

    assertThat(fb.implementable).isEqualTo(391);
    assertThat(fb.skipped).isEqualTo(32);
    assertThat(fb.specified).isEqualTo(0);
    assertThat(fb.implemented).isEqualTo(0);
  }

  @Test
  public void testComputeCoverageAgain() {

    String blah = "red";
    blah.matches("a\\(b");

    Rule rule = new Rule("Java");
    List<String> ids = new ArrayList<String>();
    ids.add(FB_ID);

    FindBugs fb = new FindBugs();
    fb.populateRulesCoverageMap();

    fb.setCodingStandardRuleCoverageImplemented(ids,rule);
    fb.setCodingStandardRuleCoverageSpecifiedBy(rule, ids);

    fb.computeCoverage();
    assertThat(fb.specified).isEqualTo(1);
    assertThat(fb.implemented).isEqualTo(1);
  }
}
