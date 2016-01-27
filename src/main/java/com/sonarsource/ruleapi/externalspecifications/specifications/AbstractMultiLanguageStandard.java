/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.ReportType;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.Language;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import org.fest.util.Strings;


public abstract class AbstractMultiLanguageStandard extends AbstractReportableStandard {

  private static final ReportType[] reportTypes = {ReportType.INTERNAL_COVERAGE, ReportType.INTERNAL_COVERAGE_SUMMARY, ReportType.HTML};


  protected abstract String generateReport(String instance, Map<String, List<Rule>> standardRules);

  protected abstract void setLanguage(Language language);


  private static final Logger LOGGER = Logger.getLogger(AbstractMultiLanguageStandard.class.getName());


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
    Map<String, List<Rule>> standardRules = initCoverage(instance);
    return generateReport(instance, standardRules);

  }


  /**
   *
   * @param instance URL to the SonarQube instance running the reference implementation
   * @return Standard id mapped to list of rules that implement the standard
   */
  protected Map<String, List<Rule>> initCoverage(String instance) {

    if (getLanguage() == null) {
      return null;
    }

    Map<String, List<Rule>> standardRules = new TreeMap<>();

    List<Rule> sqImplemented = RuleMaker.getRulesFromSonarQubeForLanguage(getLanguage(), instance);
    for (Rule sq : sqImplemented) {

      Rule rspec = RuleMaker.getRuleByKey(sq.getKey(), getLanguage().getRspec());
      populateStandardMap(standardRules, sq, rspec);
    }

    return standardRules;
  }

  protected void populateStandardMap(Map<String, List<Rule>> standardRules, Rule sq, Rule rspec) {

    for (String id : getRspecReferenceFieldValues(rspec)) {

      List<Rule> rules = standardRules.get(id);
      if (rules == null) {
        rules = new ArrayList<>();
        standardRules.put(id, rules);
      }
      rules.add(sq);
    }
  }

}
