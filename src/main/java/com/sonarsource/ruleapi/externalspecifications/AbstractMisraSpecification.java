/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.services.ReportService;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Specially-formatted reports for the three MISRA standards.
 */
public abstract class AbstractMisraSpecification extends AbstractReportableStandard
        implements TaggableStandard, CustomerReport, Badgable {

  private static final ReportType[] reportTypes = {ReportType.INTERNAL_COVERAGE, ReportType.INTERNAL_COVERAGE_SUMMARY, ReportType.HTML};

  private static final Logger LOGGER = Logger.getLogger(AbstractMisraSpecification.class.getName());

  private static final String TAG = "misra";
  private static final String TITLE_AND_INTRO = "<h2>%1$s Coverage of %2$s Standard</h2>\n" +
          "<p>The following table lists the %2$s standard items %1$s is able to detect, " +
          "and for each of them, the rules providing this coverage.</p>";

  protected int mandatoryRulesImplemented = 0;
  protected int mandatoryRulesNotImplementable = 0;
  private int mandatoryRulesToCover = 0;

  protected int optionalRulesImplemented = 0;
  protected int optionalRulesNotImplementable = 0;
  private int optionalRulesToCover = 0;

  private CodingStandardRequirableRule [] codingStandardRequirableRules = {};
  private Map<String, CodingStandardRule> ruleMap = new HashMap<>();


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

  @Override
  public String getNameIfStandardApplies(Rule rule) {
    Language ruleLang = Language.fromString(rule.getLanguage());
    if (ruleLang == this.getLanguage() && ! this.getRspecReferenceFieldValues(rule).isEmpty()){
      return this.getStandardName();
    }
    return null;
  }


  @Override
  public ReportType[] getReportTypes() {
    return reportTypes;
  }


  @Override
  public String getBadgeValue(String instance) {
    initCoverageResults(instance);
    computeCoverage();

    return Integer.toString(mandatoryRulesImplemented + optionalRulesImplemented);
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
      LOGGER.log(Level.INFO, "Unrecognized {0} value {1}, in {2}",
              new Object[] {getRSpecReferenceFieldName(), ref, ruleKey});
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
    String linebreak = System.lineSeparator();

    buff.append(getStandardName()).append(linebreak);

    buff.append("Mandatory:");
    appendSummaryLine(buff, mandatoryRulesToCover, mandatoryRulesImplemented, getPercent(mandatoryRulesImplemented, mandatoryRulesToCover), indent);

    buff.append("Optional:");
    appendSummaryLine(buff, optionalRulesToCover, optionalRulesImplemented, getPercent(optionalRulesImplemented, optionalRulesToCover), indent);

    buff.append("Total:");
    appendSummaryLine(buff, mandatoryRulesToCover + optionalRulesToCover
                            , totalRulesImplemented
                            , getPercent(totalRulesImplemented
                            , mandatoryRulesToCover + optionalRulesToCover)
                            , indent);

    return buff.toString();
  }

  private final String generateReport() {
    StringBuilder buff = new StringBuilder();
    String na = "NA\t";
    String indent = "\t";
    String linebreak = System.lineSeparator();

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

  private static void appendSummaryLine(StringBuilder buff, int toCover, int covered, String percent, String indent) {
    buff.append(String.format("%sSpecified: %d%sImplemented: %d%s=> %s%n", indent, toCover, indent, covered, indent, percent ));
  }

  private final String generateHtmlReport(String instance) {

    StringBuilder sb = new StringBuilder();

    sb.append(String.format(ReportService.HEADER_TEMPLATE, getLanguage().getReportName(), getStandardName()))
            .append(String.format(TITLE_AND_INTRO, getLanguage().getReportName(), getStandardName()))
            .append(ReportService.TABLE_OPEN)
            .append("<thead><tr><th>MISRA ID</th><th>MISRA Name</th><th>Implementing Rules</th></tr></thead>")
            .append("<tbody>");

    for (CodingStandardRule csr : getCodingStandardRules()) {
      String ruleId = csr.getCodingStandardRuleId();
      CodingStandardRuleCoverage coverage = getRulesCoverage().get(ruleId);

      if (! coverage.getImplementedBy().isEmpty()) {
        sb.append("<tr><td>").append(ruleId)
                .append("</td><td>")
                .append(((CodingStandardRequirableRule) csr).getTitle())
                .append("</td><td>");

        for (Rule rule : coverage.getImplementedBy()) {
          sb.append(Utilities.getNemoLinkedRuleReference(instance, rule));
        }
        sb.append("</td></tr>\n");
      }
    }

    sb.append("</table>")
            .append(String.format(ReportService.FOOTER_TEMPLATE,Utilities.getFormattedDateString()));

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
