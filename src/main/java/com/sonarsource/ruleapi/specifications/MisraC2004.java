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

public class MisraC2004 extends AbstractCodingStandardRuleRepository {

  private static final List<CodingStandardRule> RULES = new ArrayList<CodingStandardRule>();

  static {
    RULES.add(new CodingStandardRule.Builder("1.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("1.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("1.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("1.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("1.5").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("2.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("2.4").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("3.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3.3").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3.5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("3.6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("4.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("4.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("5.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5.5").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5.6").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("5.7").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("6.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6.3").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("6.5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("7.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("8.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8.5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8.6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8.7").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8.8").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8.9").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8.10").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8.11").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("8.12").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("9.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("9.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("9.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("10.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("10.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("10.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("10.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("10.5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("10.6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("11.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("11.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("11.3").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("11.4").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("11.5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("12.1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12.5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12.6").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12.7").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12.8").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12.9").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12.10").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12.11").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12.12").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("12.13").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("13.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("13.2").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("13.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("13.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("13.5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("13.6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("13.7").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("14.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14.5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14.6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14.7").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14.8").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14.9").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("14.10").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("15.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("15.5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("16.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16.5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16.6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16.7").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16.8").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16.9").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("16.10").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("17.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("17.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("17.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("17.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("17.5").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("17.6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("18.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("18.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("18.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("18.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("19.1").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.2").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.7").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.8").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.9").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.10").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.11").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.12").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.13").isMandatory(Boolean.FALSE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.14").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.15").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.16").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("19.17").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("20.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("20.2").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("20.3").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("20.4").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("20.5").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("20.6").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("20.7").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("20.8").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("20.9").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("20.10").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("20.11").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());
    RULES.add(new CodingStandardRule.Builder("20.12").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

    RULES.add(new CodingStandardRule.Builder("21.1").isMandatory(Boolean.TRUE).canBeCoveredByStaticAnalysis(Boolean.TRUE).build());

  }

  @Override
  public List<CodingStandardRule> getCodingStandardRules() {
    return RULES;
  }

  @Override
  public List<Rule> getRSpecRulesCoveringLanguage() throws RuleException {
    String query = "project = RSPEC and 'Covered Languages' = 'C' and 'MISRA C 2004' is not EMPTY ";
    return RuleMaker.getRulesByJql(query, "C");
  }

  @Override
  public List<Rule> getRSpecRules() throws RuleException {
    String query = "project = RSPEC and 'MISRA C 2004' is not EMPTY ";
    return RuleMaker.getRulesByJql(query, "C");
  }

  @Override
  public List<String> getStandardIdsFromRSpecRule(Rule rule) {
    return rule.getMisraC04();
  }

  @Override
  public List<Rule> getImplementedRules() throws RuleException {
    return RuleMaker.getRulesFromSonarQubeByQuery(RuleManager.NEMO, "repositories=" + getLanguage().getSq(), getLanguage().getSqProfileKey());
  }

  @Override
  public Language getLanguage() {
    return Language.C;
  }

}
