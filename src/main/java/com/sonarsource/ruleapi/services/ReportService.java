/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.services;

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


  public static void getSummaryCoverageReports() throws RuleException {

    StringBuilder sb = new StringBuilder();
    for (SupportedCodingStandard standard : SupportedCodingStandard.values()) {
      sb.append(standard.getCodingStandard().getSummaryReport()).append("\n\n");
    }
    LOGGER.info(sb.toString());
  }

  public static void getFindBugsCoverageReport() throws RuleException {

    LOGGER.info(SupportedCodingStandard.FINDBUGS.getCodingStandard().getReport());
  }

  public static void getFindBugsDeprecationReport() throws RuleException {

    LOGGER.info(((ExternalTool)SupportedCodingStandard.FINDBUGS.getCodingStandard()).getDeprecationReport());
  }

  public static void getMisraC2004CoverageReport() throws RuleException {

    LOGGER.info(SupportedCodingStandard.MISRA_C_2004.getCodingStandard().getReport());
  }

  public static void getMisraC2012CoverageReport() throws RuleException {

    LOGGER.info(SupportedCodingStandard.MISRA_C_2012.getCodingStandard().getReport());
  }

  public static void getMisraCpp2008CoverageReport() throws RuleException {

    LOGGER.info(SupportedCodingStandard.MISRA_CPP_2008.getCodingStandard().getReport());
  }

  public void getOutdatedRulesReport(Language language) throws RuleException {
    getOutdatedRulesReport(language, NEMO);
  }

  public void getOutdatedRulesReport(Language language, String instance) throws RuleException {

    List<Rule> rspec = getCoveredRulesForLangauge(language);
    Map<String, Rule> rspecRules = mapRulesByKey(rspec);

    List<Rule> sqCovered = getImplementedRulesForLanguage(language, instance);
    List<Rule> specNotFoundForLegacyKey = standardizeKeysAndIdentifyMissingSpecs(language, sqCovered);

    int notAlike = 0;
    StringBuilder sb = new StringBuilder();
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
      sb.append("\n\n").append(notAlike).append(" different out of ").append(sqCovered.size());
      sb.insert(0,"\nDifferences Found:\n");
      LOGGER.warning(sb.toString());
    } else {
      LOGGER.info("No differences found");
    }
  }

}
