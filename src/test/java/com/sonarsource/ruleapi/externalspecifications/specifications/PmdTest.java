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

import static org.fest.assertions.api.Assertions.assertThat;

public class PmdTest {

  @Test
  public void testComputeCoverage(){
    Pmd pmd = new Pmd();

    pmd.populateRulesCoverageMap();;
    pmd.computeCoverage();;

    assertThat(pmd.specified).isEqualTo(0);
    assertThat(pmd.implemented).isEqualTo(0);

  }

  @Test
  public void testComputeCoverageNonEmpty() {

    Rule rule = new Rule("Java");
    List<String> ids = new ArrayList<String>();
    ids.add("BrokenNullCheck");

    Pmd pmd = new Pmd();
    pmd.populateRulesCoverageMap();

    pmd.setCodingStandardRuleCoverageImplemented(ids, rule);
    pmd.setCodingStandardRuleCoverageSpecifiedBy(rule, ids);

    pmd.computeCoverage();
    assertThat(pmd.specified).isEqualTo(1);
    assertThat(pmd.implemented).isEqualTo(1);
  }


}
