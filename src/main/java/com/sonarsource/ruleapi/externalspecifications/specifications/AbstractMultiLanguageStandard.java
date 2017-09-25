/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.google.common.base.Strings;
import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.ReportAndBadge;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableStandard;
import com.sonarsource.ruleapi.externalspecifications.BadgableMultiLanguage;
import com.sonarsource.ruleapi.externalspecifications.ReportType;
import com.sonarsource.ruleapi.services.badge.BadgeGenerator;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Some standards, such as CWE apply to multiple langauges,
 * and we want to report on them per-langauge.
 * This is the basic reporting implementation for that.
 */
public abstract class AbstractMultiLanguageStandard extends AbstractReportableStandard implements BadgableMultiLanguage {

  private static final ReportType[] reportTypes = {ReportType.INTERNAL_COVERAGE, ReportType.INTERNAL_COVERAGE_SUMMARY, ReportType.HTML};

  protected abstract String generateReport(String instance);


  @Override
  public String getNameIfStandardApplies(Rule rule) {
    if (getAllLanguages().contains(Language.fromString(rule.getLanguage()))
            && !getRspecReferenceFieldValues(rule).isEmpty()) {
      return getStandardName();
    }
    return null;
  }

  protected List<Language> getAllLanguages(){
    return Arrays.asList(Language.values());
  }


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
    int count = 0;

    Map<String, CodingStandardRuleCoverage> rulesCoverage = getRulesCoverage();
    if (rulesCoverage != null) {
      for (CodingStandardRuleCoverage csrc : getRulesCoverage().values()) {
        if (!csrc.getImplementedBy().isEmpty()) {
          count++;
        }
      }
    }
    return count;
  }

}
