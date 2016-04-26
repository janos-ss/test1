/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.google.common.annotations.VisibleForTesting;
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

  public static final String NEMO = "https://nemo.sonarqube.org";

  public Map<String,Rule> getCoveredRulesForLanguage(Language language) {
    return mapRulesByKey(RuleMaker.getRulesByJql("\"Covered Languages\" = \"" + language.getRspec() + "\"", language.getRspec()));
  }

  private static Map<String,Rule> mapRulesByKey(List<Rule> rules) {
    Map<String, Rule> map = new HashMap<>();
    for (Rule rule : rules) {
      map.put(rule.getKey(), rule);
    }
    return map;
  }

  @VisibleForTesting
  protected String getNormalKey(String legacyKey, Language language) {
    String key = Utilities.normalizeKey(legacyKey);
    if (! key.matches("RSPEC-\\d+")) {
      Rule freshFetch = RuleMaker.getRuleByKey(legacyKey, language.getRspec());
      key = freshFetch.getKey();
      if (key == null) {
        LOGGER.warning("Legacy key not found for " + language.getRspec() + "/" + language.getSq() + ": " + legacyKey);
      }
    }
    return key;
  }

  protected List<Rule> standardizeKeysAndIdentifyMissingSpecs(Language language, List<Rule> sqCovered) {

    List<Rule> specNotFound = new ArrayList<>(sqCovered.size() / 2);
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
