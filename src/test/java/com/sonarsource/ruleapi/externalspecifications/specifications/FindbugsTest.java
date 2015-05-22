/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class FindbugsTest {

  private static String FB_ID = "BC_IMPOSSIBLE_CAST";


  @Test
  public void testComputeCoverage(){
    FindBugs fb = new FindBugs();

    fb.populateRulesCoverageMap();
    fb.computeCoverage();

    assertThat(fb.implementable).isEqualTo(400);
    assertThat(fb.skipped).isEqualTo(42);
    assertThat(fb.specified).isEqualTo(0);
    assertThat(fb.implemented).isEqualTo(0);
  }

  @Test
  public void testComputeCoverageAgain() {

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
