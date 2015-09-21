/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.specifications.AbstractReportableStandard;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.fest.util.Strings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractMisraSpecification extends AbstractReportableStandard
        implements TaggableStandard, CustomerReport {

  public static final double PERCENT_FACTOR = 100.0D;


  private static final Logger LOGGER = Logger.getLogger(AbstractMisraSpecification.class.getName());

  private static final String TAG = "misra";

  protected int mandatoryRulesImplemented = 0;
  protected int optionalRulesImplemented = 0;
  protected int totalRulesImplemented = 0;

  private CodingStandardRequirableRule [] codingStandardRequirableRules = {};
  private Map<String, CodingStandardRule> ruleMap = new HashMap<String, CodingStandardRule>();
  private int mandatoryRulesToCover = 0;
  private int optionalRulesToCover = 0;


  public AbstractMisraSpecification(CodingStandardRequirableRule [] codingStandardRequirableRules1equirableRules){
    this.codingStandardRequirableRules = codingStandardRequirableRules1equirableRules;

    for (CodingStandardRequirableRule standardRule: this.codingStandardRequirableRules) {
      ruleMap.put(standardRule.getCodingStandardRuleId(), (CodingStandardRule)standardRule);

      if (Implementability.IMPLEMENTABLE.equals(standardRule.getImplementability())) {
        if (standardRule.isRuleRequired()) {
          mandatoryRulesToCover++;
        } else {
          optionalRulesToCover++;
        }
      }
    }

  }

  public CodingStandardRule getCodingStandardRuleFromId(String id) {

    return ruleMap.get(id);
  }

  public boolean isRuleMandatory(String ruleKey) {
    if (Strings.isNullOrEmpty(ruleKey)) {
      return false;
    }
    CodingStandardRequirableRule sr = (CodingStandardRequirableRule) ruleMap.get(ruleKey);
    if (sr != null) {
      return sr.isRuleRequired();
    }
    return false;
  }

  public int getMandatoryRulesToCoverCount() {
    return mandatoryRulesToCover;
  }

  public int getOptionalRulesToCoverCount() {
    return optionalRulesToCover;
  }

  @Override
  public String getTag() {

    return TAG;
  }

  @Override
  public boolean isTagShared() {

    return true;
  }

  @Override
  public boolean doesReferenceNeedUpdating(String ref, List<String> updates, String ruleKey) {

    if (getCodingStandardRuleFromId(ref) == null) {
      LOGGER.info("Unrecognized " + getRSpecReferenceFieldName() + " value, " + ref + ", in " + ruleKey);
    }

    return false;
  }

  @Override
  public String getHtmlReport(String instance) {

    initCoverageResults(instance);
    computeCoverage();
    return generateHtmlReport(instance);
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

  private static void appendSummaryLine(StringBuilder buff, int toCover, int covered, double percent, String indent, String linebreak) {
    buff.append(String.format("%sSpecified: %d%sImplemented: %d%s=> %.2f%%%n", indent, toCover, indent, covered, indent, percent ));
  }

  private final String generateHtmlReport(String instance) {
    StringBuilder sb = new StringBuilder();

    sb.append("<h2>").append(getLanguage().getRspec()).append(" coverage of ").append(getStandardName()).append("</h2>");
    sb.append("These are the ").append(getStandardName())
            .append(" rules covered for ").append(getLanguage().getRspec())
            .append(" by the <a href='http://sonarsource.com'>SonarSource</a> ")
            .append("<a href='http://www.sonarsource.com/products/plugins/languages/cpp/'>C/C++ plugin</a>.");

    sb.append("<table>");

    for (CodingStandardRule csr : getCodingStandardRules()) {
      String ruleId = csr.getCodingStandardRuleId();
      CodingStandardRuleCoverage coverage = getRulesCoverage().get(ruleId);

      if (Implementability.NOT_IMPLEMENTABLE.equals(csr.getImplementability())) {
        sb.append("<tr><td>").append(ruleId).append("</td><td>Not statically checkable</td></tr>");

      } else if (! coverage.getImplementedBy().isEmpty()) {
        sb.append("<tr><td>").append(ruleId).append("</td><td>");
        for (Rule rule : coverage.getImplementedBy()) {
          sb.append(Utilities.getLinkedRuleReference(instance, rule));
        }
        sb.append("</td></tr>");
      }
    }

    sb.append("</table>");

    String rowEnd = "%</td></tr>";

    sb.append("<h3>Summary</h3><table>");
    sb.append("<tr><td>Mandatory rules covered:</td>");
    sb.append("<td>").append(mandatoryRulesImplemented).append(", ").append(String.format("%.2f",getMandatoryCoveragePercent())).append(rowEnd);
    sb.append("<tr><td>Optional rules covered:</td>");
    sb.append("<td>").append(optionalRulesImplemented).append(", ").append(String.format("%.2f",getOptionalCoveragePercent())).append(rowEnd);
    sb.append("<tr><td>Total:</td>");
    sb.append("<td>").append(totalRulesImplemented).append(", ").append(String.format("%.2f",getTotalCoveragePercent())).append(rowEnd);
    sb.append("</table>");

    return sb.toString();
  }

  protected void computeCoverage() {

    mandatoryRulesImplemented = 0;
    optionalRulesImplemented = 0;
    totalRulesImplemented = 0;

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
