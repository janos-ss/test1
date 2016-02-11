/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.externalspecifications.ReportType;
import com.sonarsource.ruleapi.utilities.Language;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;
import org.fest.util.Strings;


/**
 * Some standards, such as CWE apply to multiple langauges, but we want
 * to report on them per-langauge.
 * This is the basic reporting implementation for that.
 */
public abstract class AbstractMultiLanguageStandard extends AbstractReportableStandard {

  private static final Logger LOGGER = Logger.getLogger(AbstractMultiLanguageStandard.class.getName());

  private static final ReportType[] reportTypes = {ReportType.INTERNAL_COVERAGE, ReportType.INTERNAL_COVERAGE_SUMMARY, ReportType.HTML};

  protected abstract String generateReport(String instance);

  protected abstract void setLanguage(Language language);


  @Override
  public ReportType[] getReportTypes() {
    return reportTypes;
  }

  public Map<Language, String> getHtmlLanguageReports(String instance) {

    if (instance == null) {
      return null;
    }

    Map<Language, String> reports = new EnumMap<>(Language.class);

    for (Language language : Language.values()) {
      LOGGER.info("Getting " + getStandardName() + " coverage report for " + language.getRspec());

      String report = getHtmlLanguageReport(instance, language);
      if (report != null) {
        reports.put(language, report);
      }
    }
    return reports;
  }

  public String getHtmlLanguageReport(String instance, Language language) {

    if (language == null || Strings.isNullOrEmpty(instance)) {
      return null;
    }

    setLanguage(language);

    initCoverageResults(instance);
    return generateReport(instance);

  }


  /**
   *
   * @param instance URL to the SonarQube instance running the reference implementation
   */
  protected void initCoverageResults(String instance) {

    if (getLanguage() == null) {
      return;
    }
    resetRulesCoverageMap();
    super.initCoverageResults(instance);
  }

}
