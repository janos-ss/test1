/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.ReportAndBadge;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableStandard;
import com.sonarsource.ruleapi.externalspecifications.BadgableMultiLanguage;
import com.sonarsource.ruleapi.externalspecifications.ReportType;
import com.sonarsource.ruleapi.services.badge.BadgeGenerator;
import com.sonarsource.ruleapi.utilities.Language;
import java.util.Map;
import org.fest.util.Strings;


/**
 * Some standards, such as CWE apply to multiple langauges,
 * and we want to report on them per-langauge.
 * This is the basic reporting implementation for that.
 */
public abstract class AbstractMultiLanguageStandard extends AbstractReportableStandard implements BadgableMultiLanguage {

  private static final ReportType[] reportTypes = {ReportType.INTERNAL_COVERAGE, ReportType.INTERNAL_COVERAGE_SUMMARY, ReportType.HTML};

  protected abstract String generateReport(String instance);


  @Override
  public ReportType[] getReportTypes() {
    return reportTypes;
  }

  @Override
  public ReportAndBadge getHtmlLanguageReport(String instance, Language language) {

    if (language == null || Strings.isNullOrEmpty(instance)) {
      return null;
    }

    setLanguage(language);

    initCoverageResults(instance);
    ReportAndBadge reportAndBadge = new ReportAndBadge();

    reportAndBadge.setReport(generateReport(instance));

    BadgeGenerator badger = new BadgeGenerator();
    int count = getImplementedCount();
    if (count > 0) {
      reportAndBadge.setBadge(badger.getBadge(getStandardName(), Integer.toString(count)));
    }

    return reportAndBadge;
  }

  @Override
  protected void initCoverageResults(String instance) {

    if (getLanguage() == null) {
      return;
    }
    resetRulesCoverageMap();
    super.initCoverageResults(instance);
  }

  @Override
  public String getBadgeValue(String instance) {

    if (Strings.isNullOrEmpty(instance)) {
      return "";
    }
    initCoverageResults(instance);

    return Integer.toString(getImplementedCount());
  }

  private int getImplementedCount(){
    Map<String, CodingStandardRuleCoverage> rulesCoverage = getRulesCoverage();
    if (rulesCoverage != null) {
      int count = 0;
      for (CodingStandardRuleCoverage csrc : getRulesCoverage().values()) {
        if (!csrc.getImplementedBy().isEmpty()) {
          count++;
        }
      }
      return count;
    }
    return 0;
  }

}
