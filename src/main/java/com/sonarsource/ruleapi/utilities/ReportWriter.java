/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.specifications.FindBugs;
import com.sonarsource.ruleapi.specifications.Implementability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ReportWriter extends RuleManager {

  private static final Logger LOGGER = Logger.getLogger(ReportWriter.class.getName());


  public void getFindBugsCoverageReport() throws RuleException {

    Language java = Language.JAVA;

    Map<FindBugs, List<Rule>> fbSpecified = new HashMap<FindBugs, List<Rule>>();
    Map<String,Rule> rspecs = mapRulesByKey(RuleMaker.getRulesByJql("Findbugs is not empty", java.rspec));
    for (Rule rspec : rspecs.values()) {
      mapFindBugsRules(fbSpecified, rspec);
    }

    Map<FindBugs, List<Rule>> fbImplemented = new HashMap<FindBugs, List<Rule>>();
    List<Rule> impls = getImplementedRulesForLanguage(java, NEMO);
    for (Rule impl : impls) {
      String key = getNormalKey(impl.getKey(), java);
      if (key != null) {
        mapFindBugsRules(fbImplemented, rspecs.get(key));
      }
    }

    int implementable = 0;
    int skipped = 0;

    for (FindBugs fb : FindBugs.values()) {
      Implementability ability = fb.getImplementability();
      if (ability == Implementability.IMPLEMENTABLE) {
        implementable++;
      } else if (ability == Implementability.REJECTED) {
        skipped++;
      }
    }

    int count = FindBugs.values().length;

    LOGGER.info("FindBugs:\n" +
            "  implementable:     " +  implementable + " " + ((float)implementable/count)*100 + "%\n" +
            "  rejected:          " + skipped + " " + ((float)skipped/count)*100 + "%\n" +
            "  specified:         " + fbSpecified.size() + " " + ((float)fbSpecified.size()/count)*100 + "%\n" +
            "  implemented:       " + fbImplemented.size() + " " + ((float)fbImplemented.size()/count)*100 + "%");

  }

  protected void mapFindBugsRules(Map<FindBugs, List<Rule>> fbMap, Rule rspec) {

    if (rspec != null && rspec.getFindbugs() != null) {
      for (String key : rspec.getFindbugs()) {
        try {
          FindBugs fb = FindBugs.valueOf(key);
          List<Rule> fbRules = fbMap.get(fb);
          if (fbRules == null) {
            fbRules = new ArrayList<Rule>();
            fbMap.put(fb, fbRules);
          }

          fbRules.add(rspec);
        } catch (IllegalArgumentException e) {
          LOGGER.warning("Unrecognized FindBugs key: " + key + " in " + rspec.getKey());
        }
      }
    }
  }


  public void getOutdatedRulesReport(Language language, boolean detailedReport) throws RuleException {
    getOutdatedRulesReport(language, detailedReport, NEMO);
  }

  public void getOutdatedRulesReport(Language language, boolean detailedReport, String instance) throws RuleException {

    List<Rule> rspec = getCoveredRulesForLangauge(language);
    Map<String, Rule> rspecRules = mapRulesByKey(rspec);

    List<Rule> sqCovered = getImplementedRulesForLanguage(language, instance);

    int notAlike = 0;
    for (Rule sqRule : sqCovered) {
      String key = sqRule.getKey();

      key = getNormalKey(key, language);
      if (key == null) {
        continue;
      }

      Rule rspecRule = rspecRules.remove(key);
      if (rspecRule != null) {
        RuleComparison rc = new RuleComparison(rspecRule, sqRule);
        rc.setDetailedReport(detailedReport);
        if (rc.compare() != 0) {
          notAlike++;
          LOGGER.warning("\n" + sqRule.getKey() + "\n" + rc.toString());
        }
      }
    }
    LOGGER.warning("\n\n" + notAlike + " different out of " + sqCovered.size());

  }

}
