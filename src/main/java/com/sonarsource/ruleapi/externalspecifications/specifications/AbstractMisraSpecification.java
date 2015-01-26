/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.*;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;
import com.sonarsource.ruleapi.services.RuleManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractMisraSpecification extends AbstractReportableStandard implements TaggableStandard {

  public static final int DEFAULT_ROUNDING = 2;

  public static final float PERCENT_FACTOR = 100.0f;


  public abstract boolean isRuleMandatory(String ruleKey);

  public abstract int getMandatoryRulesToCoverCount();

  public abstract int getOptionalRulesToCoverCount();

  public abstract CodingStandardRule getCodingStandardRuleFromId(String id);


  private static final Logger LOGGER = Logger.getLogger(AbstractMisraSpecification.class.getName());

  private static final String TAG = "misra";

  protected int mandatoryRulesImplemented = 0;
  protected int optionalRulesImplemented = 0;
  protected int totalRulesImplemented = 0;


  @Override
  public String getTag() {

    return TAG;
  }

  @Override
  public boolean isTagShared() {

    return true;
  }

  @Override
  public boolean isFieldEntryFormatNeedUpdating(Map<String, Object> updates, Rule rule) {

    List<String> references = getRspecReferenceFieldValues(rule);

    for (int i = 0; i < references.size(); i++) {
      String ref = references.get(i);

      if (getCodingStandardRuleFromId(ref) == null) {
        LOGGER.info("Unrecognized " + getRSpecReferenceFieldName() + " value, " + ref + ", in " + rule.getKey());
      }
    }

    return false;
  }

  @Override
  public String getReport() throws RuleException {

    return getReport(RuleManager.NEMO);
  }

  @Override
  public String getReport(String instance) throws RuleException {

    initCoverageResults(instance);
    computeCoverage();
    return generateReport();
  }

  @Override
  public String getSummaryReport() throws RuleException {

    return getSummaryReport(RuleManager.NEMO);
  }

  @Override
  public String getSummaryReport(String instance) throws RuleException {

    initCoverageResults(instance);
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

    if (totalRulesImplemented > 0) {
      return;
    }

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
    return round(mandatoryRulesImplemented * 100.0f / getMandatoryRulesToCoverCount());
  }

  public float getOptionalCoveragePercent() {
    return round(optionalRulesImplemented * 100.0f / getOptionalRulesToCoverCount());
  }

  public float getTotalCoveragePercent() {
    return round(totalRulesImplemented * PERCENT_FACTOR / getCodingStandardRules().length);
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
