/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Basic reporting implementation for tracking the reimlementation of
 * other tools' rules.
 */
public abstract class AbstractReportableExternalTool extends AbstractReportableStandard implements CustomerReport {

  protected int implementable = 0;
  protected int skipped = 0;
  protected int specified = 0;
  protected int implemented = 0;

  private boolean isHtml = false;

  private static final String TABLE_OPEN = "<table>";
  private static final String TABLE_CLOSE = "</table><br/><br/>\n";
  private static final String TD = "</td><td>";
  private static final String TR_OPEN = "<tr><td>";
  private static final String TR_CLOSE = "</td></tr>";

  private static final ReportType[] reportTypes = {
          ReportType.INTERNAL_COVERAGE, ReportType.INTERNAL_COVERAGE_SUMMARY,
          ReportType.HTML, ReportType.UNSPECIFIED};

  protected Comparator<Rule> ruleKeyComparator = new RuleKeyComparator();
  private static class RuleKeyComparator implements Comparator<Rule> {
    @Override
    public int compare(Rule rule, Rule rule2) {
      return rule.getKey().compareTo(rule2.getKey());
    }
  }

  @Override
  public ReportType[] getReportTypes() {
    return reportTypes;
  }

  @Override
  public String getReport(String instance) {

    return getSummaryReport(instance);
  }

  @Override
  public String getSummaryReport(String instance) {

    initCoverageResults(instance);
    computeCoverage();

    return generateSummaryReport();
  }

  @Override
  public String getNameIfStandardApplies(Rule rule) {
    return null;
  }

  protected String generateSummaryReport(){

    String linebreak = System.lineSeparator();

    int count = getCodingStandardRules().length;
    int unspecified = count - specified - skipped;

    StringBuilder sb = new StringBuilder();
    sb.append(linebreak).append(getStandardName()).append(linebreak);
    sb.append(formatLine("Rule count:", count, 100));
    sb.append(formatLine("rejected:", skipped, ((double)skipped/count)*100));
    sb.append(formatLine("implementable:", implementable, ((double)implementable/count)*100));
    sb.append(linebreak).append("Of Implementable rules:").append(linebreak);
    sb.append(formatLine("unspecified:", unspecified, ((double)unspecified/implementable)*100));
    sb.append(formatLine("specified:", specified, ((double)specified/implementable)*100));
    sb.append(formatLine("implemented:", implemented, ((double)implemented/implementable)*100));

    return sb.toString();
  }

  public String getUnspecifiedReport() {
    initCoverageResults(null);

    StringBuilder sb = new StringBuilder();
    sb.append(getStandardName()).append(" unspecified rules\n\n");

    for (CodingStandardRule csr : getCodingStandardRules()) {
      CodingStandardRuleCoverage cov = getRulesCoverage().get(csr.getCodingStandardRuleId());

      if (cov.getSpecifiedBy().isEmpty() && ! Implementability.REJECTED.equals(csr.getImplementability())) {
        sb.append(csr.getCodingStandardRuleId());
        if (csr instanceof HasLevel) {
          sb.append(" - ").append(((HasLevel) csr).getLevel());
        }
        sb.append("\n");
      }
    }
    return sb.toString();
  }

  @Override
  public String getHtmlReport(String instance) {
    initCoverageResults(instance);

    return "<h2>SonarQube " + getLanguage().getRspec() + " Plugin coverage/deprecation of " + getStandardName() + "</h2>" +
            getHtmlSummaryReport() +
            getHtmlDeprecationByToolKey(instance);
  }

  protected String getHtmlSummaryReport() {
    computeCoverage();

    int count = getCodingStandardRules().length;
    int pending = count - skipped - implemented;

    isHtml = true;

    StringBuilder sb = new StringBuilder();
    sb.append("<h3>Summary</h3>");
    sb.append(TABLE_OPEN)
            .append(formatLine("Total rule count:", count, 100))
            .append(formatLine("&nbsp;&nbsp;<a href='#standard_rejected'>rejected</a>:", skipped, ((double)skipped/count)*100))
            .append(formatLine("&nbsp;&nbsp;remaining:", implementable, ((double) implementable / count) * 100));


    sb.append("<tr><td colspan='3'>").append("Of remaining rules:").append(TR_CLOSE)
            .append(formatLine("&nbsp;&nbsp;<a href='#standard_pending'>pending</a>:", pending, ((double)(pending)/implementable)*100))
            .append(formatLine("&nbsp;&nbsp;<a href='#standard_implemented'>implemented</a>:", implemented, ((double) implemented / implementable) * 100));

    sb.append(TABLE_CLOSE);
    sb.append(Utilities.getFormattedDateString());

    isHtml = false;

    return sb.toString();
  }

  protected Map<Rule,List<String>> getCoveringRules() {

    Map<Rule, List<String>> map = new HashMap<>();

    for (CodingStandardRuleCoverage cov : getRulesCoverage().values()) {
      String csrId = cov.getCodingStandardRuleId();

      for (Rule rule : cov.getImplementedBy()) {
        List<String> csrIds = map.get(rule);
        if (csrIds == null) {
          csrIds = new ArrayList<>();
          map.put(rule, csrIds);
        }
        csrIds.add(csrId);
      }
    }

    return map;
  }

  protected String getHtmlDeprecationByToolKey(String instance) {

    StringBuilder sb = new StringBuilder();
    sb.append("<a name='standard_implemented'></a>");
    sb.append("<h3>Implemented replacements by " + getStandardName() + " key</h3>");
    sb.append(TABLE_OPEN);

    StringBuilder rejected = new StringBuilder();
    rejected.append("<a name='standard_rejected'></a>");
    rejected.append("<h3>Rejected " + getStandardName() + " rules</h3>");
    rejected.append(TABLE_OPEN);

    StringBuilder pending = new StringBuilder();
    pending.append("<a name='standard_pending'></a>");
    pending.append("<h3>Pending " + getStandardName() + " rules</h3>");
    pending.append(TABLE_OPEN);


    for (CodingStandardRule csr : getCodingStandardRules()) {

      String id = csr.getCodingStandardRuleId();

      CodingStandardRuleCoverage cov = getRulesCoverage().get(id);
      if (!cov.getImplementedBy().isEmpty()) {
        sb.append(TR_OPEN).append(id).append(TD);

        if (csr instanceof HasLevel) {
          sb.append(((HasLevel) csr).getLevel()).append(TD);
        }

        for (Rule rule : cov.getImplementedBy()) {
          sb.append(Utilities.getNemoLinkedRuleReference(instance, rule));
        }
        sb.append(TR_CLOSE);

      } else if (Implementability.REJECTED.equals(csr.getImplementability())) {
        rejected.append(TR_OPEN).append(id);
        if (csr instanceof HasLevel) {
          rejected.append(TD).append(((HasLevel) csr).getLevel()).append(TD);
        }

        rejected.append(TR_CLOSE);

      } else {
        pending.append(TR_OPEN).append(id).append(TD);
        if (csr instanceof HasLevel) {
          pending.append(((HasLevel) csr).getLevel()).append(TD);
        }

        for (Rule rule : cov.getSpecifiedBy()) {
          pending.append(Utilities.getJiraLinkedRuleReference(rule));
        }

        pending.append(TR_CLOSE);

      }
    }

    sb.append(TABLE_CLOSE);
    rejected.append(TABLE_CLOSE);
    pending.append(TABLE_CLOSE);

    sb.append(pending);
    sb.append(rejected);

    return sb.toString();
  }

  public void computeCoverage() {

    if (implementable == 0) {
      for (CodingStandardRule csr : getCodingStandardRules()) {
        Implementability impl = csr.getImplementability();
        if (impl.equals(Implementability.IMPLEMENTABLE)) {
          implementable++;
        } else if (impl.equals(Implementability.REJECTED)) {
          skipped++;
        }
      }

      Iterator<CodingStandardRuleCoverage> itr = getRulesCoverage().values().iterator();
      while (itr.hasNext()) {
        CodingStandardRuleCoverage cov = itr.next();

        if (!cov.getImplementedBy().isEmpty()) {
          implemented++;
        }
        if (!cov.getSpecifiedBy().isEmpty()) {
          specified++;
        }
      }
    }
  }

  protected String formatLine(String label, int count, double percentage) {

    if (isHtml) {
      return String.format("<tr><td>%s</td><td>%3d</td><td>%.2f%%</td></tr>", label, count, percentage);
    }
    return String.format("  %-15s %3d  %6.2f%%%n", label, count, percentage);
  }
}
