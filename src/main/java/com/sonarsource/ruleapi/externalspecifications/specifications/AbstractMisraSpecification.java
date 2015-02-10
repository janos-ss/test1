/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.*;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractMisraSpecification extends AbstractReportableStandard implements TaggableStandard {

  public static final int DEFAULT_ROUNDING = 2;

  public static final double PERCENT_FACTOR = 100.0d;


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
  public String getReport(String instance) {

    initCoverageResults(instance);
    computeCoverage();
    return generateReport();
  }

  @Override
  public String getSummaryReport(String instance) {

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
    appendSummaryLine(buff, getMandatoryRulesToCoverCount() + getOptionalRulesToCoverCount(), totalRulesImplemented, getTotalCoveragePercent(), indent, "");

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

      if (Implementability.NOT_IMPLEMENTABLE.equals(rule.getImplementability())) {

        buff.append(indent).append("not implementable").append(linebreak);

      } else {

        String tmp = na;
        if (!coverage.getSpecifiedBy().isEmpty()) {
          tmp = coverage.getSpecifiedByKeysAsCommaList();
        }
        buff.append(indent).append("S: ").append(tmp);

        tmp = na;
        if (!coverage.getImplementedBy().isEmpty()) {
          tmp = coverage.getImplementedByKeysAsCommaList();
        }
        buff.append(indent).append("C: ").append(tmp);

        tmp = "N";
        if (!coverage.getImplementedBy().isEmpty()) {
          tmp = "Y";
        }
        buff.append(indent).append("I: ").append(tmp);
        buff.append(linebreak);
      }

    }
    buff.append("S = Specified | C = Covered in RSpec | I = Implemented in Plugin").append(linebreak).append(linebreak);

    buff.append(linebreak);
    buff.append(generateSummary());

    return buff.toString();
  }

  private void appendSummaryLine(StringBuilder buff, int toCover, int covered, double percent, String indent, String linebreak) {
    buff.append(String.format("%sSpecified: %d%sImplemented: %d%s=> %.2f%%%n", indent, toCover, indent, covered, indent, percent ));
  }

  protected void computeCoverage() {

    if (totalRulesImplemented > 0) {
      return;
    }

    for (CodingStandardRuleCoverage cov : getRulesCoverage().values()) {
      if (!cov.getImplementedBy().isEmpty()) {
        if (isRuleMandatory(cov.getCodingStandardRuleId())) {
          mandatoryRulesImplemented++;
        } else {
          optionalRulesImplemented++;
        }
      }
    }

    totalRulesImplemented = mandatoryRulesImplemented + optionalRulesImplemented;
  }

  public double getMandatoryCoveragePercent() {
    return mandatoryRulesImplemented * PERCENT_FACTOR / getMandatoryRulesToCoverCount();
  }

  public double getOptionalCoveragePercent() {
    return optionalRulesImplemented * PERCENT_FACTOR / getOptionalRulesToCoverCount();
  }

  public double getTotalCoveragePercent() {
    return totalRulesImplemented * PERCENT_FACTOR / (getMandatoryRulesToCoverCount() + getOptionalRulesToCoverCount());
  }
}
