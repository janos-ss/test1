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


public class CheckstyleTest {
  private static String CHECKSTYLE_ID = "AnonInnerLength";

  @Test
  public void testComputeCoverage(){
    Checkstyle checkstyle = new Checkstyle();

    checkstyle.populateRulesCoverageMap();
    checkstyle.computeCoverage();

    assertThat(checkstyle.implementable).isEqualTo(99);
    assertThat(checkstyle.skipped).isEqualTo(46);
    assertThat(checkstyle.specified).isEqualTo(0);
    assertThat(checkstyle.implemented).isEqualTo(0);
  }

  @Test
  public void testComputeCoverageAgain() {

    Rule rule = new Rule("Java");
    List<String> ids = new ArrayList<String>();
    ids.add(CHECKSTYLE_ID);

    Checkstyle checkstyle = new Checkstyle();
    checkstyle.populateRulesCoverageMap();

    checkstyle.setCodingStandardRuleCoverageImplemented(ids, rule);
    checkstyle.setCodingStandardRuleCoverageSpecifiedBy(rule, ids);

    checkstyle.computeCoverage();
    assertThat(checkstyle.specified).isEqualTo(1);
    assertThat(checkstyle.implemented).isEqualTo(1);
  }

}
