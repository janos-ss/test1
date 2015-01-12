/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.*;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.specifications.AbstractCodingStandard;
import com.sonarsource.ruleapi.utilities.RuleException;

import java.math.BigDecimal;

public abstract class AbstractMisraSpecification extends AbstractCodingStandard {

  public static final int DEFAULT_ROUNDING = 2;

  public static final float PERCENT_FACTOR = 100.0f;


  public abstract boolean isRuleMandatory(String ruleKey);

  public abstract int getMandatoryRulesToCoverCount();

  public abstract int getOptionalRulesToCoverCount();


  protected int mandatoryRulesImplemented = 0;
  protected int optionalRulesImplemented = 0;
  protected int totalRulesImplemented = 0;


  @Override
  public String getReport() throws RuleException {

    initCoverageResults();
    computeCoverage();
    return generateReport();
  }

  @Override
  public String getSummaryReport() throws RuleException {

    initCoverageResults();
    computeCoverage();
    return generateSummary();
  }

  private final String generateSummary() {
    StringBuilder buff = new StringBuilder();
    String indent = "\t";
    String linebreak = String.format("%n");

    buff.append(getStandardName()).append(linebreak);

    buff.append("Mandatory:");
    appendSummaryLine(buff, getMandatoryRulesToCoverCount(), mandatoryRulesImplemented, getMandatoryCoveragePercent(), indent, linebreak);

    buff.append("Optional:");
    appendSummaryLine(buff, getOptionalRulesToCoverCount(), optionalRulesImplemented, getOptionalCoveragePercent(), indent, linebreak);

    buff.append("Total:");
    appendSummaryLine(buff, getCodingStandardRules().length, totalRulesImplemented, getTotalCoveragePercent(), indent, "");

    return buff.toString();
  }

  private final String generateReport() {
    StringBuilder buff = new StringBuilder();
    String na = "NA\t";
    String indent = "\t";
    String linebreak = String.format("%n");

    for (CodingStandardRule rule : getCodingStandardRules()) {
      String ruleId = rule.getCodingStandardRuleId();
      CodingStandardRuleCoverage coverage = getRulesCoverage().get(ruleId);

      buff.append(ruleId);

      String tmp = na;
      if (coverage.getSpecifiedBy() != null) {
        tmp = coverage.getSpecifiedBy().getKey();
      }
      buff.append(indent).append("S: ").append(tmp);

      tmp = na;
      if (coverage.getImplementedBy() != null) {
        tmp = coverage.getImplementedBy().getKey();
      }
      buff.append(indent).append("C: ").append(tmp);

      tmp = "N";
      if (coverage.getImplementedBy() != null) {
        tmp = "Y";
      }
      buff.append(indent).append("I: ").append(tmp);
      buff.append(linebreak);

    }
    buff.append("S = Specified | C = Covered in RSpec | I = Implemented in Plugin").append(linebreak).append(linebreak);

    buff.append(linebreak);
    buff.append(generateSummary());

    return buff.toString();
  }

  private void appendSummaryLine(StringBuilder buff, int toCover, int covered, float percent, String indent, String linebreak) {
    buff.append(indent).append("Specified: ").append(toCover)
            .append(indent).append("Implemented: ").append(covered)
            .append(indent).append("=> ").append(percent).append("%")
            .append(linebreak);
  }

  protected void computeCoverage() {

    for (CodingStandardRuleCoverage cov : getRulesCoverage().values()) {
      if (cov.getImplementedBy() != null) {
        if (isRuleMandatory(cov.getRule())) {
          mandatoryRulesImplemented++;
        } else {
          optionalRulesImplemented++;
        }
      }
    }

    totalRulesImplemented = mandatoryRulesImplemented + optionalRulesImplemented;
  }

  public float getMandatoryCoveragePercent() {
    if (getMandatoryRulesToCoverCount() != 0) {
      return round(mandatoryRulesImplemented * 100.0f / getMandatoryRulesToCoverCount());
    } else {
      return 0.0f;
    }
  }

  public float getOptionalCoveragePercent() {
    if (getOptionalRulesToCoverCount() != 0) {
      return round(optionalRulesImplemented * 100.0f / getOptionalRulesToCoverCount());
    } else {
      return 0.0f;
    }
  }

  public float getTotalCoveragePercent() {
    if (getCodingStandardRules().length != 0) {
      return round(totalRulesImplemented * PERCENT_FACTOR / getCodingStandardRules().length);
    } else {
      return 0.0f;
    }
  }

  public static float round(float d) {
    return round(d, DEFAULT_ROUNDING);
  }

  public static float round(float d, int decimalPlace) {
    BigDecimal bd = new BigDecimal(Float.toString(d));
    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
    return bd.floatValue();
  }
}
