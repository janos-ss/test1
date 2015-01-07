/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.RuleException;

public class CodingStandardCoverage {

  private static final int DEFAULT_ROUNDING = 2;

  private static final float PERCENT_FACTOR = 100.0f;

  private static final Logger LOGGER = Logger.getLogger(CodingStandardCoverage.class.getName());

  private SupportedCodingStandard codingStandard;

  private Map<String, CodingStandardRuleCoverage> rulesCoverage;

  private int mandatoryRulesToCover = 0;
  private int mandatoryRulesCovered = 0;

  private int optionalRulesToCover = 0;
  private int optionalRulesCovered = 0;

  private int implementedRules = 0;

  public CodingStandardCoverage(SupportedCodingStandard codingStandard) throws RuleException {
    this.codingStandard = codingStandard;
    initCoverageResults();
  }

  private void initCoverageResults() throws RuleException {
    LOGGER.info("Init Coverage Results");
    rulesCoverage = new HashMap<String, CodingStandardRuleCoverage>();
    List<Rule> rspecRules = codingStandard.getRulesRepository().getRSpecRules();

    for (CodingStandardRule rule : codingStandard.getRulesRepository().getCodingStandardRules()) {
      CodingStandardRuleCoverage cov = new CodingStandardRuleCoverage();
      cov.setRule(rule);

      for (Rule rspecRule : rspecRules) {
        List<String> rspecField = codingStandard.getRulesRepository().getStandardIdsFromRSpecRule(rspecRule);
        if (rspecField != null) {
          for (String ruleKey : rspecField) {
            if (StringUtils.equals(ruleKey, rule.getKey())) {
              cov.setSpecifiedBy(rspecRule);
              break;
            }
          }
        }
        if (cov.getSpecifiedBy() != null) {
          break;
        }
      }

      rulesCoverage.put(rule.getKey(), cov);
    }
  }

  public void computeCoverage() throws RuleException {
    LOGGER.info("Computing Coverage from RSpec Rules");
    List<Rule> rspecRules = codingStandard.getRulesRepository().getRSpecRulesCoveringLanguage();

    computeCoverageOfMandatoryRules(rspecRules);
    computeCoverageOfOptionalRules(rspecRules);

    computeImplementedByPlugin();
  }

  private void computeImplementedByPlugin() throws RuleException {
    LOGGER.info("computeImplementedByPlugin");
    int rulesCovered = 0;

    List<Rule> sqCovered = codingStandard.getRulesRepository().getImplementedRules();

    for (Rule sqRule : sqCovered) {
      String key = sqRule.getKey();
      Rule rspecRule = RuleMaker.getRuleByKey(key, codingStandard.getRulesRepository().getLanguage().getSq());
      List<String> ruleIDs = codingStandard.getRulesRepository().getStandardIdsFromRSpecRule(rspecRule);
      if (ruleIDs != null) {
        for (String ruleID : ruleIDs) {
          if (codingStandard.getRulesRepository().isRuleKeyInCodingStandardRules(ruleID)) {
            CodingStandardRuleCoverage cov = rulesCoverage.get(ruleID);
            cov.setImplemented(Boolean.TRUE);
            rulesCovered++;
            break;
          }
        }
      }
    }
    implementedRules = rulesCovered;
  }

  private void computeCoverageOfMandatoryRules(List<Rule> rules) throws RuleException {
    LOGGER.info("computeCoverageOfMandatoryRules");
    int rulesToCover = getNumberOfMandatoryRulesToCover();
    int rulesCovered = 0;

    for (Rule rule : rules) {
      List<String> ruleIDs = codingStandard.getRulesRepository().getStandardIdsFromRSpecRule(rule);
      if (ruleIDs != null) {
        for (String ruleID : ruleIDs) {
          if (codingStandard.getRulesRepository().isRuleKeyInCodingStandardRules(ruleID) && codingStandard.getRulesRepository().isRuleMandatory(ruleID)) {
            rulesCovered++;
            CodingStandardRuleCoverage cov = rulesCoverage.get(ruleID);
            cov.setCoveredBy(rule);
            break;
          }
        }
      }
    }

    mandatoryRulesToCover = rulesToCover;
    mandatoryRulesCovered = rulesCovered;
  }

  private void computeCoverageOfOptionalRules(List<Rule> rules) throws RuleException {
    LOGGER.info("computeCoverageOfOptionalRules");

    int rulesToCover = getNumberOfOptionalRulesToCover();
    int rulesCovered = 0;

    for (Rule rule : rules) {
      List<String> rspecField = codingStandard.getRulesRepository().getStandardIdsFromRSpecRule(rule);
      if (rspecField != null) {
        for (String ruleKey : rspecField) {
          if (codingStandard.getRulesRepository().isRuleKeyInCodingStandardRules(ruleKey) && !codingStandard.getRulesRepository().isRuleMandatory(ruleKey)) {
            rulesCovered++;
            CodingStandardRuleCoverage cov = rulesCoverage.get(ruleKey);
            cov.setCoveredBy(rule);
            break;
          }
        }
      }
    }

    optionalRulesToCover = rulesToCover;
    optionalRulesCovered = rulesCovered;
  }

  private int getNumberOfMandatoryRulesToCover() {
    int value = 0;

    List<CodingStandardRule> rules = codingStandard.getRulesRepository().getCodingStandardRules();
    for (CodingStandardRule rule : rules) {
      if (rule.isMandatory()) {
        value++;
      }
    }
    return value;
  }

  private int getNumberOfOptionalRulesToCover() {
    int value = 0;

    List<CodingStandardRule> rules = codingStandard.getRulesRepository().getCodingStandardRules();
    for (CodingStandardRule rule : rules) {
      if (!rule.isMandatory()) {
        value++;
      }
    }
    return value;
  }

  public SupportedCodingStandard getCodingStandard() {
    return codingStandard;
  }

  public Map<String, CodingStandardRuleCoverage> getRulesCoverage() {
    return rulesCoverage;
  }

  public int getMandatoryRulesToCover() {
    return mandatoryRulesToCover;
  }

  public int getMandatoryRulesCovered() {
    return mandatoryRulesCovered;
  }

  public int getOptionalRulesToCover() {
    return optionalRulesToCover;
  }

  public int getOptionalRulesCovered() {
    return optionalRulesCovered;
  }

  public int getTotalRulesToCover() {
    return mandatoryRulesToCover + optionalRulesToCover;
  }

  public int getTotalRulesCovered() {
    return mandatoryRulesCovered + optionalRulesToCover;
  }

  public int getImplementedRules() {
    return implementedRules;
  }

  public float getMandatoryCoveragePercent() {
    if (getMandatoryRulesToCover() != 0) {
      return round(getMandatoryRulesCovered() * 100.0f / getMandatoryRulesToCover(), 2);
    } else {
      return 0.0f;
    }
  }

  public float getOptionalCoveragePercent() {
    if (getOptionalRulesToCover() != 0) {
      return round(getOptionalRulesCovered() * 100.0f / getOptionalRulesToCover(), 2);
    } else {
      return 0.0f;
    }
  }

  public float getTotalCoveragePercent() {
    if (getTotalRulesToCover() != 0) {
      return round(getTotalRulesCovered() * PERCENT_FACTOR / getTotalRulesToCover(), DEFAULT_ROUNDING);
    } else {
      return 0.0f;
    }
  }

  public float getImplementedCoveragePercent() {
    if (getTotalRulesToCover() != 0) {
      return round(getImplementedRules() * 100.0f / getTotalRulesToCover(), 2);
    } else {
      return 0.0f;
    }
  }

  public List<CodingStandardRule> getCodingStandardRules() {
    return codingStandard.getRulesRepository().getCodingStandardRules();
  }

  public static float round(float d, int decimalPlace) {
    BigDecimal bd = new BigDecimal(Float.toString(d));
    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
    return bd.floatValue();
  }

  public void printReport() {
    StringBuilder buff = new StringBuilder();

    List<CodingStandardRule> rules = codingStandard.getRulesRepository().getCodingStandardRules();
    for (CodingStandardRule rule : rules) {
      CodingStandardRuleCoverage coverage = rulesCoverage.get(rule.getKey());
      buff = buff.append(rule.getKey());
      if (coverage.getSpecifiedBy() != null) {
        buff = buff.append("\tS: ").append(coverage.getSpecifiedBy().getKey());
      } else {
        buff = buff.append("\tS: NA\t");
      }
      if (coverage.getCoveredBy() != null) {
        buff = buff.append("\tC: ").append(coverage.getCoveredBy().getKey());
      } else {
        buff = buff.append("\tC: NA\t");
      }
      if (coverage.isImplemented()) {
        buff = buff.append("\tI: Y");
      } else {
        buff = buff.append("\tI: N");
      }
      buff = buff.append("\n");
    }
    buff = buff.append("\n");
    buff = buff.append("S = Specified | C = Covered in RSpec | I = Implemented in Plugin\n\n");

    if (getOptionalRulesToCover() > 0) {
      buff = buff.append("Mandatory:\tToCover: ").append(getMandatoryRulesToCover()).append("\tCov. in RSpec: ").append(getMandatoryRulesCovered()).append("\t=> ")
        .append(getMandatoryCoveragePercent()).append("%\n");
      buff = buff.append("Optional:\tToCover: ").append(getOptionalRulesToCover()).append("\tCov. in RSpec: ").append(getOptionalRulesCovered()).append("\t=> ")
        .append(getOptionalCoveragePercent()).append("%\n");
    }
    buff = buff.append("Total:\t\tToCover: ").append(getTotalRulesToCover()).append("\tCov. in RSpec: ").append(getTotalRulesCovered()).append("\t=> ")
      .append(getTotalCoveragePercent()).append("%\tImplemented: ").append(getImplementedCoveragePercent()).append("%\n");

    System.out.println(buff.toString());
  }
}