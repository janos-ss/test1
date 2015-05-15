/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CustomerReport;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.*;
import java.util.logging.Logger;


public abstract class AbstractMultiLanguageStandard extends AbstractReportableStandard {

  abstract protected String generateReport(String instance, Map<String, ArrayList<Rule>> standardRules);

  abstract protected void setLanguage(Language language);


  private static final Logger LOGGER = Logger.getLogger(AbstractMultiLanguageStandard.class.getName());


  public Map<Language, String> getHtmlLangaugeReports(String instance) {

    Map<Language, String> reports = new HashMap<>();

    for (Language language : Language.values()) {
      LOGGER.info("Getting " + getStandardName() + " coverage report for " + language.getRspec());

      String report = getHtmlLanguageReport(instance, language);
      if (report != null) {
        reports.put(language, report);
      }
    }
    return reports;
  }

  protected String getHtmlLanguageReport(String instance, Language language) {

    if (language == null) {
      return null;
    }

    setLanguage(language);
    Map<String, ArrayList<Rule>> standardRules = initCoverage(instance);
    return generateReport(instance, standardRules);

  }


  protected Map<String, ArrayList<Rule>> initCoverage(String instance) {

    if (getLanguage() == null) {
      return null;
    }

    TreeMap<String, ArrayList<Rule>> stamdardRules = new TreeMap<String, ArrayList<Rule>>();

    List<Rule> sqImplemented = RuleMaker.getRulesFromSonarQubeForLanguage(getLanguage(), instance);
    for (Rule sq : sqImplemented) {

      Rule rspec = RuleMaker.getRuleByKey(sq.getKey(), getLanguage().getRspec());
      populateStandardMap(stamdardRules, sq, rspec);
    }

    return stamdardRules;
  }

  protected void populateStandardMap(Map<String, ArrayList<Rule>> standardRules, Rule sq, Rule rspec) {

    for (String id : getRspecReferenceFieldValues(rspec)) {

      ArrayList<Rule> rules = standardRules.get(id);
      if (rules == null) {
        rules = new ArrayList<>();
        standardRules.put(id, rules);
      }
      rules.add(sq);
    }
  }

}
