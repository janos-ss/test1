/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.services;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.specifications.*;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.externalspecifications.*;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.domain.RuleComparison;
import com.sonarsource.ruleapi.domain.RuleException;

public class ReportService extends RuleManager {

  private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

  public void getReports(String instance) throws RuleException {

    getOutdatedRulesReports(instance);
    getFindBugsDeprecationReport(instance);
    getSummaryCoverageReports(instance);
    getMisraDetailedCoverageReports(instance);

  }

  public void getSummaryCoverageReports(String instance) throws RuleException {

    LOGGER.info("Getting summary coverage report  on " + instance);


    StringBuilder sb = new StringBuilder();
    for (SupportedCodingStandard standard : SupportedCodingStandard.values()) {
      sb.append(standard.getCodingStandard().getSummaryReport(instance)).append("\n\n");
    }
    writeFile("SummaryCoverageReports.txt", sb.toString());

  }

  public void getMisraDetailedCoverageReports(String instance) throws RuleException {

    for (SupportedCodingStandard standard : SupportedCodingStandard.values()) {

      if (standard.getCodingStandard() instanceof AbstractMisraSpecification) {

        AbstractMisraSpecification misra = (AbstractMisraSpecification) standard.getCodingStandard();

        LOGGER.info("Getting detailed coverage report for " + misra.getStandardName() + " on " + instance);

        writeFile(misra.getStandardName().concat("Coverage.txt"), standard.getCodingStandard().getReport(instance));
      }
    }
  }

  private void writeFile(String fileName, String content) throws RuleException {
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(fileName, "UTF-8");
      writer.println(content);
      writer.close();
    } catch (FileNotFoundException e) {
      throw new RuleException(e);
    } catch (UnsupportedEncodingException e) {
      throw new RuleException(e);
    }

  }

  public void getFindBugsDeprecationReport(String instance) throws RuleException {
    LOGGER.info("Getting Findbugs deprecation report on " + instance);

    writeFile("DeprecatedFindBugsIds.txt",
            ((ExternalTool) SupportedCodingStandard.FINDBUGS.getCodingStandard()).getDeprecationReport(instance));
  }

  public void getOutdatedRulesReports(String instance) throws RuleException {

    for (Language language : Language.values()) {
      getOutdatedRulesReport(language, instance);
    }

  }

  public void getOutdatedRulesReport(Language language, String instance) throws RuleException {

    LOGGER.info("Getting outdated rules report for " + language.getRspec() + " on " + instance);

    List<Rule> rspec = getCoveredRulesForLangauge(language);
    Map<String, Rule> rspecRules = mapRulesByKey(rspec);

    List<Rule> sqCovered = getImplementedRulesForLanguage(language, instance);
    List<Rule> specNotFoundForLegacyKey = standardizeKeysAndIdentifyMissingSpecs(language, sqCovered);

    int notAlike = 0;
    StringBuilder sb = new StringBuilder();
    sb.append("\nOn ").append(instance).append("\n");
    for (Rule sqRule : sqCovered) {

      if (specNotFoundForLegacyKey.contains(sqRule)) {
        continue;
      }

      String key = sqRule.getKey();
      Rule rspecRule = rspecRules.remove(key);
      if (rspecRule == null) {
        rspecRule = RuleMaker.getRuleByKey(key, language.getRspec());
      }
      if (rspecRule != null) {
        RuleComparison rc = new RuleComparison(rspecRule, sqRule);
        if (rc.compare() != 0) {
          notAlike++;
          sb.append("\n").append(rc);
        }
      }
    }
    if (sb.length() > 0 && notAlike > 0) {
      sb.insert(0, sqCovered.size());
      sb.insert(0, " different out of ");
      sb.insert(0, notAlike);

      writeFile(language.getSq().concat("OutdatedRules.txt"), sb.toString());

    } else {
      writeFile(language.getSq().concat("OutdatedRules.txt"), "No differences found");
    }
  }

}
