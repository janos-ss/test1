/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.specifications.FindBugs;
import com.sonarsource.ruleapi.specifications.Implementability;

public class ReportWriter extends RuleManager {

  private static final Logger LOGGER = Logger.getLogger(ReportWriter.class.getName());


  public void getFindBugsCoverageReport() throws RuleException {
    getFindBugsCoverageReport(NEMO);
  }

  public void getFindBugsCoverageReport(String instance) throws RuleException {

    Language java = Language.JAVA;

    Map<FindBugs, List<Rule>> fbSpecified = new HashMap<FindBugs, List<Rule>>();
    Map<String,Rule> rspecs = mapRulesByKey(RuleMaker.getRulesByJql("Findbugs is not empty", java.rspec));
    for (Rule rspec : rspecs.values()) {
      mapFindBugsRules(fbSpecified, rspec);
    }

    Map<FindBugs, List<Rule>> fbImplemented = new HashMap<FindBugs, List<Rule>>();
    List<Rule> impls = getImplementedRulesForLanguage(java, instance);
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
    int unspecified = FindBugs.values().length - fbSpecified.size() - skipped;

    LOGGER.info("\nFindBugs:\n" +
            formatLine("FB rule count:", count, 100) +
            formatLine("rejected:", skipped, ((float)skipped/count)*100) +
            formatLine("implementable:", implementable, ((float)implementable/count)*100) +
            "\nOf Implementable rules:\n" +
            formatLine("unspecified:", unspecified, ((float)unspecified/implementable)*100) +
            formatLine("specified:", fbSpecified.size(), ((float)fbSpecified.size()/implementable)*100) +
            formatLine("implemented:", fbImplemented.size(), ((float)fbImplemented.size()/implementable)*100));

  }

  protected String formatLine(String label, int count, float percentage) {
    return String.format("  %-15s %3d  %6.2f%%%n", label, count, percentage);
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
        rspecRule = RuleMaker.getRuleByKey(key, language.rspec);
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
