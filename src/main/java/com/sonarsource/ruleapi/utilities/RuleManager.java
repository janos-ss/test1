/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.get.RuleMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RuleManager {
  private static final Logger LOGGER = Logger.getLogger(RuleManager.class.getName());


  public static final String NEMO = "http://nemo.sonarqube.org";

  protected List<Rule> getCoveredRulesForLangauge(Language language) throws RuleException {
    return RuleMaker.getRulesByJql("\"Covered Languages\" = \"" + language.rspec + "\"", language.rspec);
  }

  protected List<Rule> getImplementedRulesForLanguage(Language language, String instance) throws RuleException {
    return RuleMaker.getRulesFromSonarQubeByQuery(instance, "repositories=" + language.sq, language.sqProfileKey);
  }

  protected Map<String,Rule> mapRulesByKey(List<Rule> rules) {

    Map<String, Rule> map = new HashMap<String, Rule>();
    for (Rule rule : rules) {
      map.put(rule.getKey(), rule);
    }
    return map;
  }

  protected String getNormalKey(String legacyKey, Language language) throws RuleException {
    String key = legacyKey;
    if (! legacyKey.matches("RSPEC-\\d+")) {

      Rule freshFetch = RuleMaker.getRuleByKey(legacyKey, language.rspec);
      key = freshFetch.getKey();
      if (key == null) {
        LOGGER.warning("Legacy key not found for " + language.rspec + "/" + language.sq + ": " + legacyKey);
      }
    }
    return key;
  }

  protected List<Rule> standardizeKeysAndIdentifyMissingSpecs(Language language, List<Rule> sqCovered) throws RuleException {

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
