/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Specially-formatted reports for the three MISRA standards.
 */
public abstract class AbstractMisraSpecification extends AbstractReportableStandard
        implements TaggableStandard, CustomerReport {

  private static final ReportType[] reportTypes = {ReportType.INTERNAL_COVERAGE, ReportType.INTERNAL_COVERAGE_SUMMARY, ReportType.HTML};

  private static final Logger LOGGER = Logger.getLogger(AbstractMisraSpecification.class.getName());

  private static final String TAG = "misra";

  protected int mandatoryRulesImplemented = 0;
  protected int mandatoryRulesNotImplementable = 0;
  private int mandatoryRulesToCover = 0;

  protected int optionalRulesImplemented = 0;
  protected int optionalRulesNotImplementable = 0;
  private int optionalRulesToCover = 0;

  private CodingStandardRequirableRule [] codingStandardRequirableRules = {};
  private Map<String, CodingStandardRule> ruleMap = new HashMap<>();


  @Override
  public ReportType[] getReportTypes() {
    return reportTypes;
  }

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
    int totalRulesImplemented = mandatoryRulesImplemented + optionalRulesImplemented;

    StringBuilder buff = new StringBuilder();
    String indent = "\t";
    String linebreak = String.format("%n");

    buff.append(getStandardName()).append(linebreak);

    buff.append("Mandatory:");
    appendSummaryLine(buff, mandatoryRulesToCover, mandatoryRulesImplemented, getPercent(mandatoryRulesImplemented, mandatoryRulesToCover), indent, linebreak);

    buff.append("Optional:");
    appendSummaryLine(buff, optionalRulesToCover, optionalRulesImplemented, getPercent(optionalRulesImplemented, optionalRulesToCover), indent, linebreak);

    buff.append("Total:");
    appendSummaryLine(buff, mandatoryRulesToCover + optionalRulesToCover, totalRulesImplemented, getPercent(totalRulesImplemented, mandatoryRulesToCover + optionalRulesToCover), indent, "");

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

  private static void appendSummaryLine(StringBuilder buff, int toCover, int covered, String percent, String indent, String linebreak) {
    buff.append(String.format("%sSpecified: %d%sImplemented: %d%s=> %s%n", indent, toCover, indent, covered, indent, percent ));
  }

  private final String generateHtmlReport(String instance) {

    String rowStart = "<tr><td>";
    String rowEnd = "</td></tr>";
    String cellEnd = "</td><td>";
    String numCellEnd = "</td><td class='number'>";
    String tableEnd = "</table>";

    StringBuilder sb = new StringBuilder();

    sb.append("<h2>SonarQube ").append(getLanguage().getRspec()).append(" coverage of ").append(getStandardName()).append("</h2>");
    sb.append("These are the ").append(getStandardName())
            .append(" rules covered for ").append(getLanguage().getRspec())
            .append(" by the <a href='http://sonarsource.com'>SonarSource</a> ")
            .append("<a href='http://www.sonarsource.com/products/plugins/languages/cpp/'>C/C++ plugin</a>.");

    sb.append("<h3>Summary</h3>")
            .append("<table><tr><th>&nbsp;</th><th>Optional</th><th>Mandatory</th></tr>");
    sb.append("<tr><td>Rule count</td><td class='number'>")
            .append(optionalRulesToCover + optionalRulesNotImplementable)
            .append(numCellEnd)
            .append(mandatoryRulesToCover + mandatoryRulesNotImplementable)
            .append(rowEnd);
    sb.append("<tr><td><a href='#nc'>Not statically checkable</a></td><td class='number'>")
            .append(optionalRulesNotImplementable)
            .append(numCellEnd)
            .append(mandatoryRulesNotImplementable)
            .append(rowEnd);
    sb.append("<tr><td>Statically checkable</td><td class='number'>")
            .append(optionalRulesToCover)
            .append(numCellEnd)
            .append(mandatoryRulesToCover)
            .append(rowEnd);
    sb.append("<tr><td><a href='#c'>Covered</a></td><td class='number'>")
            .append(optionalRulesImplemented)
            .append(numCellEnd)
            .append(mandatoryRulesImplemented)
            .append(rowEnd);
    sb.append("<tr><td><a href='#p'>Pending</a></td><td class='number'>")
            .append(optionalRulesToCover - optionalRulesImplemented)
            .append(numCellEnd)
            .append(mandatoryRulesToCover - mandatoryRulesImplemented)
            .append(rowEnd);
    sb.append(tableEnd);

    StringBuilder nc = new StringBuilder();
    nc.append("<a name='nc' id='nc'></a><h3>Not Statically Coverable</h3><table>");

    StringBuilder covered = new StringBuilder();
    covered.append("<a name='c' id='c'></a><h3>Covered</h3><table>");

    StringBuilder pending = new StringBuilder();
    pending.append("<a name='p' id='p'></a><h3>Pending</h3><table>");

    for (CodingStandardRule csr : getCodingStandardRules()) {
      String ruleId = csr.getCodingStandardRuleId();
      CodingStandardRuleCoverage coverage = getRulesCoverage().get(ruleId);
      String requirable = ((CodingStandardRequirableRule) csr).isRuleRequired() ? "Required" : "Optional";

      if (Implementability.NOT_IMPLEMENTABLE.equals(csr.getImplementability())) {
        nc.append(rowStart).append(ruleId)
                .append(cellEnd)
                .append(requirable)
                .append(rowEnd);

      } else if (! coverage.getImplementedBy().isEmpty()) {
        covered.append(rowStart).append(ruleId)
                .append(cellEnd)
                .append(requirable)
                .append(cellEnd);

        for (Rule rule : coverage.getImplementedBy()) {
          covered.append(Utilities.getNemoLinkedRuleReference(instance, rule));
        }
        covered.append(rowEnd);
      } else {
        pending.append(rowStart).append(ruleId)
                .append(cellEnd)
                .append(requirable)
                .append(cellEnd);
        for (Rule rule : coverage.getSpecifiedBy()) {
          pending.append(Utilities.getJiraLinkedRuleReference(rule));
        }
        pending.append(rowEnd);
      }
    }

    sb.append(covered).append(tableEnd)
            .append(pending).append(tableEnd)
            .append(nc).append(tableEnd);

    return sb.toString();
  }

  protected void computeCoverage() {

    if (mandatoryRulesImplemented > 0 || mandatoryRulesNotImplementable > 0) {
      return;
    }

    for (CodingStandardRule csr : getCodingStandardRules()){
      if (((CodingStandardRequirableRule)csr).isRuleRequired()) {
        if (Implementability.NOT_IMPLEMENTABLE.equals(csr.getImplementability())) {
          mandatoryRulesNotImplementable++;
        } else if (! getRulesCoverage().get(csr.getCodingStandardRuleId()).getImplementedBy().isEmpty()){
          mandatoryRulesImplemented++;
        }
      } else  if (Implementability.NOT_IMPLEMENTABLE.equals(csr.getImplementability())) {
        optionalRulesNotImplementable++;
      } else if (! getRulesCoverage().get(csr.getCodingStandardRuleId()).getImplementedBy().isEmpty()) {
        optionalRulesImplemented++;
      }
    }
  }

  public String getPercent(int num, int denom) {
    return String.format("%.2f%%",(double)num * 100 / denom);
  }
}
