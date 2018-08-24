/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.misra.MisraC2004;
import com.sonarsource.ruleapi.externalspecifications.tools.FindBugs;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class AbstractReportableStandardTest {

  private static String FB_ID = "BC_IMPOSSIBLE_CAST";
  private static String BOGUS = "BOGUS_FINDBUGS_ID";


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

    list = new ArrayList<>();
    list.add("DMI.*");
    list.add(FB_ID);

    assertThat(fb.getExpandedStandardKeyList(list)).hasSize(27);
  }

  @Test
  public void testSetCodingStandardRuleCoverageSpecifiedBy() {

    List<Rule> rspecRules = new ArrayList<>();
    Rule rule = new Rule("Java");

    FindBugs fb = new FindBugs();
    fb.populateRulesCoverageMap();

    fb.setCodingStandardRuleCoverageSpecifiedBy(rule, null);
    assertThat(fb.getRulesCoverage().get(FB_ID).getSpecifiedBy()).hasSize(0);

    List<String> findBugsIds = new ArrayList<>();
    fb.setCodingStandardRuleCoverageSpecifiedBy(rule, findBugsIds);
    assertThat(fb.getRulesCoverage().get(FB_ID).getSpecifiedBy()).hasSize(0);

    findBugsIds.add(BOGUS);
    fb.setCodingStandardRuleCoverageSpecifiedBy(rule, findBugsIds);
    assertThat(fb.getRulesCoverage().get(BOGUS)).isNull();


    findBugsIds.add(FB_ID);
    fb.setCodingStandardRuleCoverageSpecifiedBy(rule, findBugsIds);
    CodingStandardRuleCoverage cov =  fb.getRulesCoverage().get(FB_ID);
    assertThat(fb.getRulesCoverage().get(FB_ID).getSpecifiedBy().get(0)).isEqualTo(rule);

  }

  @Test
  public void testGetSqRepoList() {
    MisraC2004 misraC2004 = new MisraC2004();
    String reposList = "abap,apex,c,cobol,cpp,csharpsquid,css,flex,go,squid,javascript,kotlin,objc,php,pli,plsql,python,rpg,ruby,rust,scala,solidity,swift,tsql,typescript,vb,vbnet,Web,xml";

    assertThat(misraC2004.getSqRepoList()).isEqualTo(reposList);
  }

  @Test
  public void testSetCodingStandardRuleCoverageImplemented() {
    List<String> ids = null;
    Rule rule = new Rule("Java");

    FindBugs fb = new FindBugs();
    fb.populateRulesCoverageMap();

    fb.setCodingStandardRuleCoverageImplemented(null, rule);
    assertThat(fb.getRulesCoverage().get(FB_ID).getImplementedBy()).hasSize(0);

    List<String> findBugsIds = new ArrayList<>();
    fb.setCodingStandardRuleCoverageImplemented(findBugsIds, rule);
    assertThat(fb.getRulesCoverage().get(FB_ID).getImplementedBy()).hasSize(0);

    findBugsIds.add(BOGUS);
    fb.setCodingStandardRuleCoverageImplemented(findBugsIds, rule);
    assertThat(fb.getRulesCoverage().get(BOGUS)).isNull();

    findBugsIds.add(FB_ID);
    fb.setCodingStandardRuleCoverageImplemented(findBugsIds, rule);
    assertThat(fb.getRulesCoverage().get(FB_ID).getImplementedBy().get(0)).isEqualTo(rule);

  }

  @Test
  public void testFindSpecifiedInRspec() {

    List<Rule> rules = new ArrayList<>();
    Rule rule = new Rule("C");
    rule.setMisraC04(new ArrayList<String>());
    rule.getMisraC04().add("1.1");
    rule.getMisraC04().add("1.2");

    rules.add(rule);

    MisraC2004 misraC2004 = new MisraC2004();
    misraC2004.populateRulesCoverageMap();
    misraC2004.findSpecifiedInRspec(rules);

    Map<String,CodingStandardRuleCoverage> coverageMap = misraC2004.getRulesCoverage();
    assertThat(coverageMap.get("1.1").getSpecifiedBy().get(0)).isEqualTo(rule);
    assertThat(coverageMap.get("1.2").getSpecifiedBy().get(0)).isEqualTo(rule);
    assertThat(coverageMap.get("1.3").getSpecifiedBy()).hasSize(0);
  }

  @Test
  public void testCleanRulesCoverageMap(){
    MisraC2004 misraC2004 = new MisraC2004();
    misraC2004.populateRulesCoverageMap();

    Rule rule = new Rule("C");
    rule.setKey("key");

    for (CodingStandardRuleCoverage csrc : misraC2004.getRulesCoverage().values()) {
      csrc.addImplementedBy(rule);
    }

    misraC2004.cleanRulesCoverageMap();
    for (CodingStandardRuleCoverage csrc : misraC2004.getRulesCoverage().values()) {
      assertThat(csrc.getImplementedBy()).isEmpty();
    }
  }

  @Test
  public void testNullRulesCoverageMap(){
    FindBugs fb = new FindBugs();

    assertThat(fb.getRulesCoverage()).isNull();

    Rule rule = new Rule("Java");
    List<String> ids = new ArrayList<>();
    ids.add("ASDF");

    fb.setCodingStandardRuleCoverageImplemented(ids, rule);
    assertThat(fb.getRulesCoverage()).isNotNull();


    fb.resetRulesCoverageMap();
    assertThat(fb.getRulesCoverage()).isNull();

    fb.setCodingStandardRuleCoverageSpecifiedBy(rule, ids);
    assertThat(fb.getRulesCoverage()).isNotNull();

  }

}
