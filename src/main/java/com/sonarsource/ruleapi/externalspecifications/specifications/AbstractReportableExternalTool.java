/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;

import java.util.Iterator;


public abstract class AbstractReportableExternalTool extends AbstractReportableStandard {

  protected int implementable = 0;
  protected int skipped = 0;
  protected int specified = 0;
  protected int implemented = 0;


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
    return String.format("  %-15s %3d  %6.2f%%%n", label, count, percentage);
  }
}
