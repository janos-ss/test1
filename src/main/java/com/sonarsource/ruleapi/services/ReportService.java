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
import com.sonarsource.ruleapi.external_rule_specifications.specifications.FindBugs;
import com.sonarsource.ruleapi.external_rule_specifications.specifications.MisraC2004;
import com.sonarsource.ruleapi.external_rule_specifications.specifications.MisraC2012;
import com.sonarsource.ruleapi.external_rule_specifications.specifications.MisraCPP2008;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.external_rule_specifications.*;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.RuleComparison;
import com.sonarsource.ruleapi.utilities.RuleException;
import com.sonarsource.ruleapi.utilities.RuleManager;

public class ReportService extends RuleManager {

  private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());


  public void getSummaryCoverageReports() throws RuleException {

    for (SupportedCodingStandard standard : SupportedCodingStandard.values()) {
      LOGGER.info(standard.getCodingStandard().getSummaryReport());
    }
  }

  public void getFindBugsCoverageReport() throws RuleException {
    FindBugs fb = new FindBugs();

    LOGGER.info(fb.getReport());
  }

  public void getMisraC2004CoverageReport() throws RuleException {

    MisraC2004 misraC2004 = new MisraC2004();
    LOGGER.info(misraC2004.getReport());
  }

  public void getMisraC2012CoverageReport() throws RuleException {

    MisraC2012 misraC2012 = new MisraC2012();
    LOGGER.info(misraC2012.getReport());
  }

  public void getMisraCpp2008CoverageReport() throws RuleException {

    MisraCPP2008 misraCPP2008 = new MisraCPP2008();
    LOGGER.info(misraCPP2008.getReport());
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
