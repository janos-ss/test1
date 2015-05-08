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
import com.sonarsource.ruleapi.utilities.Utilities;

import java.util.*;


public abstract class AbstractReportableExternalTool extends AbstractReportableStandard implements CustomerReport{

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

  protected Comparator<Rule> ruleKeyComparator = new Comparator<Rule>() {
    @Override
    public int compare(Rule rule, Rule rule2) {
      return rule.getKey().compareTo(rule2.getKey());
    }
  };


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
            getHtmlSummaryReport(instance) +
            getHtmlDeprecationByRuleKey(instance) +
            getHtmlDeprecationByToolKey(instance);
  }

  protected String getHtmlSummaryReport(String instance) {
    initCoverageResults(instance);
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

    isHtml = false;

    return sb.toString();
  }

  protected String getHtmlDeprecationByRuleKey(String instance) {

    initCoverageResults(instance);
    StringBuilder sb = new StringBuilder();

    Map<Rule,List<String>> ruleIdMap = getCoveringRules();

    List<Rule> sortedRuleList = new ArrayList<>(ruleIdMap.keySet());
    Collections.sort(sortedRuleList, ruleKeyComparator);

    sb.append("<h3>Implemented replacements by SonarQube " + getLanguage().getRspec() + " key</h3>");
    sb.append(TABLE_OPEN);

    for (Rule rule : sortedRuleList) {
      sb.append(TR_OPEN).append(getLinkedRuleReference(instance, rule))
              .append(TD)
              .append(Utilities.listToString(ruleIdMap.get(rule), true))
              .append(TR_CLOSE);
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

        for (Rule rule : cov.getImplementedBy()) {
          sb.append(getLinkedRuleReference(instance, rule));
        }
        sb.append(TR_CLOSE);

      } else if (Implementability.REJECTED.equals(csr.getImplementability())) {
        rejected.append(TR_OPEN).append(id);
        rejected.append(TR_CLOSE);

      } else {
        pending.append(TR_OPEN).append(id);
        pending.append(TR_CLOSE);

      }
    }

    sb.append(TABLE_CLOSE);
    rejected.append(TABLE_CLOSE);
    pending.append(TABLE_CLOSE);

    sb.append(rejected);
    sb.append(pending);

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
