/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class AbstractCodingStandardTest {

  private static String FB_ID = "BC_IMPOSSIBLE_CAST";


  @Test
  public void testPopulateRulesCoverageMap() {

    MisraC2004 m4 = new MisraC2004();
    assertThat(m4.getRulesCoverage()).isNull();
    m4.populateRulesCoverageMap();
    assertThat(m4.getRulesCoverage()).isNotNull();
  }


  @Test
  public void testFindBugsRegexKey() {

    FindBugs fb = new FindBugs();

    List<String> list = null;
    assertThat(fb.getExpandedStandardKeyList(list)).isNull();

    list = new ArrayList<String>();
    list.add("DMI.*");
    list.add(FB_ID);

    assertThat(fb.getExpandedStandardKeyList(list)).hasSize(27);
  }

  @Test
  public void testFindSpecifiedInRspec() {

    List<Rule> rspecRules = new ArrayList<Rule>();
    Rule rule = new Rule("Java");

    FindBugs fb = new FindBugs();
    fb.populateRulesCoverageMap();

    fb.setCodingStandardRuleCoverageSpecifiedBy(rule, null);
    assertThat(fb.getRulesCoverage().get(FB_ID).getImplementedBy()).isNull();

    List<String> findBugsIds = new ArrayList<String>();
    fb.setCodingStandardRuleCoverageSpecifiedBy(rule, null);
    assertThat(fb.getRulesCoverage().get(FB_ID).getImplementedBy()).isNull();

    findBugsIds.add(FB_ID);
    fb.setCodingStandardRuleCoverageSpecifiedBy(rule, findBugsIds);
    CodingStandardRuleCoverage cov =  fb.getRulesCoverage().get(FB_ID);
    assertThat(fb.getRulesCoverage().get(FB_ID).getSpecifiedBy()).isEqualTo(rule);

  }

  @Test
  public void testSetCodingStandardRuleCoverageImplemented() {
    List<String> ids = null;
    Rule rule = new Rule("Java");

    FindBugs fb = new FindBugs();
    fb.populateRulesCoverageMap();

    fb.setCodingStandardRuleCoverageImplemented(null, rule);
    assertThat(fb.getRulesCoverage().get(FB_ID).getSpecifiedBy()).isNull();

    List<String> findBugsIds = new ArrayList<String>();
    fb.setCodingStandardRuleCoverageImplemented(findBugsIds, rule);
    assertThat(fb.getRulesCoverage().get(FB_ID).getImplementedBy()).isNull();

    findBugsIds.add(FB_ID);
    fb.setCodingStandardRuleCoverageImplemented(findBugsIds, rule);
    assertThat(fb.getRulesCoverage().get(FB_ID).getImplementedBy()).isEqualTo(rule);

  }
}
