/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.specifications;

import java.util.ArrayList;
import java.util.List;

import com.sonarsource.ruleapi.domain.AbstractCodingStandardRuleRepository;
import com.sonarsource.ruleapi.domain.CodingStandardRule;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.RuleException;
import com.sonarsource.ruleapi.utilities.RuleManager;

public class MisraCPP2008 extends AbstractCodingStandardRuleRepository {

  private static final List<CodingStandardRule> RULES = new ArrayList<CodingStandardRule>();

  static {
    RULES.add(new CodingStandardRule.Builder("0-1-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("0-1-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("0-1-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("0-1-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("0-1-5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("0-1-6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("0-1-7").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("0-1-8").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("0-1-9").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("0-1-10").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("0-1-11").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("0-1-12").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("0-2-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("0-3-1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.FALSE).build());
    RULES.add(new CodingStandardRule.Builder("0-3-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("0-4-1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.FALSE).build());
    RULES.add(new CodingStandardRule.Builder("0-4-2").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.FALSE).build());
    RULES.add(new CodingStandardRule.Builder("0-4-3").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.FALSE).build());

    RULES.add(new CodingStandardRule.Builder("1-0-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("1-0-2").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.FALSE).build());
    RULES.add(new CodingStandardRule.Builder("1-0-3").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.FALSE).build());

    RULES.add(new CodingStandardRule.Builder("2-2-1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.FALSE).build());

    RULES.add(new CodingStandardRule.Builder("2-3-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("2-5-1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("2-7-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2-7-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2-7-3").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("2-10-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2-10-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2-10-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2-10-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2-10-5").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2-10-6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("2-13-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2-13-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2-13-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2-13-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2-13-5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("3-1-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3-1-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3-1-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("3-2-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3-2-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3-2-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3-2-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("3-3-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3-3-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("3-4-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("3-9-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3-9-2").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3-9-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("4-5-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("4-5-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("4-5-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("4-10-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("4-10-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("5-0-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-2").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-7").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-8").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-9").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-10").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-11").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-12").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-13").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-14").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-15").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-16").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-17").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-18").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-19").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-20").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-0-21").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("5-2-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-2-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-2-3").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-2-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-2-5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-2-6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-2-7").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-2-8").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-2-9").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-2-10").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-2-11").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-2-12").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("5-3-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-3-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-3-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5-3-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("5-8-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("5-14-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("5-17-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("5-18-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("5-19-1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("6-2-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-2-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-2-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("6-3-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("6-4-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-4-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-4-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-4-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-4-5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-4-6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-4-7").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-4-8").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("6-5-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-5-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-5-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-5-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-5-5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-5-6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("6-6-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-6-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-6-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-6-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6-6-5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("7-1-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("7-1-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("7-2-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("7-3-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("7-3-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("7-3-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("7-3-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("7-3-5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("7-3-6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("7-4-1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.FALSE).build());
    RULES.add(new CodingStandardRule.Builder("7-4-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("7-4-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("7-5-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("7-5-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("7-5-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("7-5-4").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("8-0-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("8-3-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("8-4-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8-4-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8-4-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8-4-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("8-5-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8-5-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8-5-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("9-3-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("9-3-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("9-3-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("9-5-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("9-6-1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.FALSE).build());
    RULES.add(new CodingStandardRule.Builder("9-6-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("9-6-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("9-6-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("10-1-1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("10-1-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("10-1-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("10-2-1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("10-3-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("10-3-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("10-3-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("11-0-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("12-1-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12-1-2").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12-1-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("12-8-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12-8-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("14-5-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14-5-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14-5-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("14-6-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14-6-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("14-7-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14-7-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14-7-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("14-8-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14-8-2").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("15-0-1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.FALSE).build());
    RULES.add(new CodingStandardRule.Builder("15-0-2").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15-0-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("15-1-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15-1-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15-1-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("15-3-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15-3-2").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15-3-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15-3-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15-3-5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15-3-6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15-3-7").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("15-4-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("15-5-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15-5-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15-5-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("16-0-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-0-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-0-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-0-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-0-5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-0-6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-0-7").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-0-8").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("16-1-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-1-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("16-2-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-2-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-2-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-2-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-2-5").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-2-6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("16-3-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16-3-2").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("16-6-1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.FALSE).build());

    RULES.add(new CodingStandardRule.Builder("17-0-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("17-0-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("17-0-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("17-0-4").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.FALSE).build());
    RULES.add(new CodingStandardRule.Builder("17-0-5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("18-0-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("18-0-2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("18-0-3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("18-0-4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("18-0-5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("18-2-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("18-4-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("18-7-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("19-3-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("27-0-1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
  }

  @Override
  public List<CodingStandardRule> getCodingStandardRules() {
    return RULES;
  }

  @Override
  public List<Rule> getRSpecRulesCoveringLanguage() throws RuleException {
    String query = "project = RSPEC and 'Covered Languages' = 'C++' and 'MISRA C++ 2008' is not EMPTY";
    return RuleMaker.getRulesByJql(query, "C++");
  }

  @Override
  public List<Rule> getRSpecRules() throws RuleException {
    String query = "project = RSPEC and 'MISRA C++ 2008' is not EMPTY";
    return RuleMaker.getRulesByJql(query, "C++");
  }

  @Override
  public List<String> getStandardIdsFromRSpecRule(Rule rule) {
    return rule.getMisraCpp();
  }

  @Override
  public List<Rule> getImplementedRules() throws RuleException {
    return RuleMaker.getRulesFromSonarQubeByQuery(RuleManager.NEMO, "repositories=" + getLanguage().getSq(), getLanguage().getSqProfileKey());
  }

  @Override
  public Language getLanguage() {
    return Language.CPP;
  }

}
