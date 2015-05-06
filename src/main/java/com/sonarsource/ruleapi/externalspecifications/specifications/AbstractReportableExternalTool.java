/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.CustomerReport;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.ComparisonUtilities;

import java.util.*;


public abstract class AbstractReportableExternalTool extends AbstractReportableStandard implements CustomerReport{

  protected int implementable = 0;
  protected int skipped = 0;
  protected int specified = 0;
  protected int implemented = 0;

  private boolean isHtml = false;

  private static final String TABLE_OPEN = "<table>";
  private static final String TABLE_CLOSE = "</table><br/><br/>";
  private static final String TD = "</td><td>";


  @Override
  public String getReport(String instance) {

    return getSummaryReport(instance);
  }

  @Override
  public String getSummaryReport(String instance) {

    initCoverageResults(instance);
    computeCoverage();

    String linebreak = String.format("%n");

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

  @Override
  public String getHtmlReport(String instance) {
    return "<h2>" + getStandardName() + " deprecation</h2>" +
            "<p>Implemented replacements <a href=\"#rule\">by SonarQube rule</a>, <a href=\"#tool\">by "+ getStandardName() +" rule</a></p>" +
            getHtmlSummaryReport(instance) +
            "<a name='rule'></a>" +
            getHtmlDeprecationByRuleKey(instance) +
            "<a name='tool'></a>" +
            getHtmlDeprecationByToolKey(instance);
  }

  protected String getHtmlSummaryReport(String instance) {
    initCoverageResults(instance);
    computeCoverage();

    int count = getCodingStandardRules().length;
    int unspecified = count - specified - skipped;

    isHtml = true;

    StringBuilder sb = new StringBuilder();
    sb.append("<h3>Summary</h3>");
    sb.append(TABLE_OPEN)
            .append(formatLine("Total rule count:", count, 100))
            .append(formatLine("&nbsp;&nbsp;rejected:", skipped, ((double)skipped/count)*100))
            .append(formatLine("&nbsp;&nbsp;remaining:", implementable, ((double) implementable / count) * 100));

    sb.append("<tr><td colspan='3'>").append("Of remaining rules:").append("</td></tr>")
            .append(formatLine("&nbsp;&nbsp;pending:", unspecified, ((double) unspecified / implementable) * 100))
            .append(formatLine("&nbsp;&nbsp;planned:", specified-implemented, ((double)(specified-implemented)/implementable)*100))
            .append(formatLine("&nbsp;&nbsp;implemented:", implemented, ((double) implemented / implementable) * 100));

    sb.append(TABLE_CLOSE);

    isHtml = false;

    return sb.toString();
  }

  protected String getHtmlDeprecationByRuleKey(String instance) {

    initCoverageResults(instance);
    StringBuilder sb = new StringBuilder();

    Map<Rule,List<String>> ruleIdMap = getCoveringRules();

    List<Rule> sortedRuleList = new ArrayList<>(ruleIdMap.keySet());
    Collections.sort(sortedRuleList, new Comparator<Rule>() {
      @Override
      public int compare(Rule rule, Rule rule2) {

        return rule.getKey().compareTo(rule2.getKey());
      }
    });

    sb.append("<h3>Implemented replacements by SonarQube " + getLanguage().getRspec() + " key</h3>");
    sb.append(TABLE_OPEN);

    for (Rule rule : sortedRuleList) {
      sb.append("<tr><td>").append(getLinkedRuleReference(instance, rule))
              .append(TD)
              .append(ComparisonUtilities.listToString(ruleIdMap.get(rule), true))
              .append("</td></tr>");
    }

    sb.append(TABLE_CLOSE);
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

    initCoverageResults(instance);
    StringBuilder sb = new StringBuilder();

    List<CodingStandardRuleCoverage> csrcList = new ArrayList<>(getRulesCoverage().values());

    Comparator<CodingStandardRuleCoverage> toolKeyComparator = new Comparator<CodingStandardRuleCoverage>()
    {
      @Override
      public int compare(CodingStandardRuleCoverage c1, CodingStandardRuleCoverage c2) {
        return c1.getCodingStandardRuleId().compareTo(c2.getCodingStandardRuleId());
      }
    };

    Collections.sort(csrcList, toolKeyComparator);

    sb.append("<h3>Implemented replacements by " + getStandardName() + " key</h3>");
    sb.append(TABLE_OPEN);

    for (CodingStandardRuleCoverage cov : csrcList) {
      if (!cov.getImplementedBy().isEmpty()) {
        sb.append("<tr><td>")
                .append(cov.getCodingStandardRuleId())
                .append(TD);

        for (Rule rule : cov.getImplementedBy()) {
          sb.append(getLinkedRuleReference(instance, rule));
        }

        sb.append("</td></tr>");
      }
    }

    sb.append(TABLE_CLOSE);

    return sb.toString();
  }

  public String getDeprecationReport(String instance) {

    initCoverageResults(instance);
    StringBuilder sb = new StringBuilder();

    for (CodingStandardRuleCoverage cov : getRulesCoverage().values()) {
      if (!cov.getImplementedBy().isEmpty()) {
        sb.append(cov.getCodingStandardRuleId())
                .append("\t")
                .append(cov.getImplementedByKeysAsCommaList())
                .append("\n");
      }
    }

    return sb.toString();
  }

  protected void computeCoverage() {

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
