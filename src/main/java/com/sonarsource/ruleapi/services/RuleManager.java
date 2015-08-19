/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.services;

import com.sonar.orchestrator.Orchestrator;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RuleManager {
  private static final Logger LOGGER = Logger.getLogger(RuleManager.class.getName());

  public static final String NEMO = "http://nemo.sonarqube.org";

  private Orchestrator orchestrator = null;

  public List<Rule> getCoveredRulesForLangauge(Language language) {
    return RuleMaker.getRulesByJql("\"Covered Languages\" = \"" + language.getRspec() + "\"", language.getRspec());
  }

  public List<Rule> getCoveredAndTargetedRulesForLangauge(Language language) {
    return RuleMaker.getRulesByJql("\"Covered Languages\" = \"" + language.getRspec()
                            + "\" or \"Targeted Languages\" = \"" + language.getRspec() + "\"",
            language.getRspec());
  }

  protected Map<String,Rule> mapRulesByKey(List<Rule> rules) {

    Map<String, Rule> map = new HashMap<String, Rule>();
    for (Rule rule : rules) {
      map.put(rule.getKey(), rule);
    }
    return map;
  }

  protected String getNormalKey(String legacyKey, Language language) {

    String key = Utilities.normalizeKey(legacyKey);

    if (! key.matches("RSPEC-\\d+")) {

      Rule freshFetch = RuleMaker.getCachedRuleByKey(legacyKey, language.getRspec());
      key = freshFetch.getKey();
      if (key == null) {
        LOGGER.warning("Legacy key not found for " + language.getRspec() + "/" + language.getSq() + ": " + legacyKey);
      }
    }
    return key;
  }

  protected List<Rule> standardizeKeysAndIdentifyMissingSpecs(Language language, List<Rule> sqCovered) {

    List<Rule> specNotFound = new ArrayList<Rule>(sqCovered.size()/2);
    for (Rule sqRule : sqCovered) {
      String key = sqRule.getKey();

      key = getNormalKey(key, language);
      if (key == null) {
        specNotFound.add(sqRule);
        continue;
      }
      sqRule.setKey(key);
    }
    return specNotFound;
  }
}
