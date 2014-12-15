/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.update.RuleUpdater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class IntegrityEnforcer {

  private static final Logger LOGGER = Logger.getLogger(IntegrityEnforcer.class.getName());

  private static final String CWE = "CWE";
  private static final String CWE_TAG = "cwe";
  private static final String CWE_PATTERN = "CWE-\\d+";
  public static final String NEMO = "http://nemo.sonarqube.org";


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

      key = getEnsureKeyIsNormal(key, language);
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

  public void setCoveredAndOutdatedLanguages(String login, String password) throws RuleException {

    for (Language lang : Language.values()) {
      if (!lang.update) {
        LOGGER.warning("Update disabled for " + lang.sq + "/" + lang.rspec);
      }
      setCoveredAndOutdatedForLanguage(login, password, lang);
    }
  }

  protected void setCoveredAndOutdatedForLanguage(String login, String password, Language language) throws RuleException {
    String rspecLanguage = language.rspec;

    List<Rule> rspec = getCoveredRulesForLangauge(language);
    Map<String, Rule> rspecRules = mapRulesByKey(rspec);

    Map<String,Rule> needsUpdating = new HashMap<String, Rule>();

    List<Rule> sqCovered = getImplementedRulesForLanguage(language, NEMO);
    for (Rule sqRule : sqCovered) {
      String key = sqRule.getKey();

      key = getEnsureKeyIsNormal(key, language);
      if (key == null) {
        continue;
      }

      if (language.update) {
        Rule rspecRule = rspecRules.remove(key);
        if (rspecRule == null) {
          rspecRule = RuleMaker.getRuleByKey(key, "");
        }

        addCoveredForNemoRules(rspecLanguage, needsUpdating, rspecRule);
        setOutdatedForNemoRules(rspecLanguage, needsUpdating, rspecRule, sqRule);
      }
    }

    if (language.update) {
      dropCoveredForNonNemoRules(rspecLanguage, rspecRules, needsUpdating);
      for (Rule rule : needsUpdating.values()) {
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("Covered Languages", rule.getCoveredLanguages());
        updates.put("Targeted languages", rule.getTargetedLanguages());
        updates.put("Outdated Languages", rule.getOutdatedLanguages());
        RuleUpdater.updateRule(rule.getKey(), updates, login, password);
      }
    }
  }

  protected void setOutdatedForNemoRules(String rspecLanguage, Map<String, Rule> needsUpdating, Rule rspec, Rule nemo) {
    RuleComparison rc = new RuleComparison(rspec, nemo);
    int result = rc.compare();
    List<String> outdatedLanguages = rspec.getOutdatedLanguages();

    if (result != 0 && outdatedLanguages.contains(rspecLanguage)) {
      outdatedLanguages.add(rspecLanguage);
      needsUpdating.put(rspec.getKey(), rspec);
      LOGGER.info(rspecLanguage + " " + rspec.getKey() + " setting outdated");
    } else  if (result == 0 && outdatedLanguages.remove(rspecLanguage)) {
      needsUpdating.put(rspec.getKey(), rspec);
      LOGGER.info(rspecLanguage + " " + rspec.getKey() + " UNsetting outdated");
    }
  }

  protected void dropCoveredForNonNemoRules(String rspecLanguage, Map<String, Rule> rspecRules, Map<String, Rule> needsUpdating) {

    for (Rule rspecRule : rspecRules.values()) {
      rspecRule.getCoveredLanguages().remove(rspecLanguage);
      rspecRule.getOutdatedLanguages().remove(rspecLanguage);

      rspecRule.getTargetedLanguages().add(rspecLanguage);
      LOGGER.info(rspecLanguage + " " + rspecRule.getKey() + " moving from covered to targeted");

      needsUpdating.put(rspecRule.getKey(), rspecRule);
    }
  }

  protected void addCoveredForNemoRules(String rspecLanguage, Map<String, Rule> needsUpdating, Rule rspecRule) {

    if (! rspecRule.getCoveredLanguages().contains(rspecLanguage)) {
      rspecRule.getCoveredLanguages().add(rspecLanguage);
      needsUpdating.put(rspecRule.getKey(), rspecRule);
      LOGGER.info(rspecLanguage + " " + rspecRule.getKey() + " adding covered");
    }
    if (rspecRule.getTargetedLanguages().remove(rspecLanguage) && ! needsUpdating.containsKey(rspecRule.getKey())) {
      needsUpdating.put(rspecRule.getKey(), rspecRule);
      LOGGER.info(rspecLanguage + " " + rspecRule.getKey() + " removing targeted");
    }
  }

  public void enforceCwe(String login, String password) throws RuleException {

    List<Rule> rules = RuleMaker.getRulesByJql("CWE is not EMPTY OR description ~ CWE OR labels = CWE", "");
    for (Rule rule : rules) {

      boolean tagPresent = isTagPresent(rule, CWE_TAG);
      List<String> references = getSpecificReferences(rule, CWE);
      List<String> cweField = rule.getCwe();

      if (!tagPresent || references.isEmpty() || cweField.isEmpty()) {

        Map<String, Object> updates = getCweUpdates(rule, tagPresent, references, cweField);

        if (! updates.isEmpty()) {
          RuleUpdater.updateRule(rule.getKey(), updates, login, password);
        }
      }
    }
  }

  protected Map<String, Object> getCweUpdates(Rule rule, boolean tagPresent, List<String> references,
                                              List<String> cweField) {

    Map<String, Object> updates = new HashMap<String, Object>();

    List<String> cweFieldValues = cweField;
    if (!isCweFieldEntryFormatValid(cweFieldValues, updates, rule)) {
      cweFieldValues = rule.getCwe();
    }

    if (tagPresent && references.isEmpty() && cweFieldValues.isEmpty()) {
      LOGGER.warning(rule.getKey() + " - cwe found in tags but not See & Reference field.");
    } else {
      addTagIfMissing(rule, updates, CWE_TAG);

      List<String> sees = parseCweFromSeeSection(references);

      addSeeToReferenceField(sees, cweFieldValues, CWE, updates);
      checkReferencesInSee(cweFieldValues, sees, rule);
    }
    return updates;
  }

  protected void checkReferencesInSee(List<String> referenceField, List<String> sees, Rule rule) {

    for (String reference : referenceField) {
      if (!sees.contains(reference)) {
        LOGGER.warning(rule.getKey() + " - " + reference + " missing from See section ");
      }
    }
  }

  protected boolean isCweFieldEntryFormatValid(List<String> references, Map<String, Object> updates, Rule rule) {

    boolean needUpdating = false;
    List<String> replacements = new ArrayList<String>();
    for (int i = 0; i < references.size() && !needUpdating; i++) {
      if (! references.get(i).matches(CWE_PATTERN)) {
        needUpdating = true;
      }
    }

    for (int i = 0; i < references.size() && needUpdating; i++) {
      String ref = references.get(i);
      if (ref.matches("\\d+")) {
        replacements.add(CWE+"-"+ref);
      } else if (ref.matches(CWE_PATTERN)) {
        replacements.add(ref);
      } else {
        LOGGER.warning("Strange CWE reference found in " + rule.getKey() + ": " + ref);
        needUpdating = false;
      }
    }

    if (needUpdating) {
      rule.setCwe(replacements);
      updates.put(CWE, replacements);
      return false;
    }
    return true;
  }

  protected void addSeeToReferenceField(List<String> sees, List<String> referenceField, String fieldName,
                                        Map<String, Object> updates) {

    for (String see : sees) {
      if (! referenceField.contains(see)) {
        referenceField.add(see);
        if (! updates.containsKey(fieldName)) {
          updates.put(fieldName, referenceField);
        }
      }
    }
  }

  protected List<String> parseCweFromSeeSection(List<String> references) {

    List<String> refs = new ArrayList<String>();

    for (String reference : references) {
      String[] pieces = reference.split(" ");
      for (String piece : pieces) {
        if (piece.matches(CWE_PATTERN)) {
          refs.add(piece);
        }
      }
    }

    return refs;
  }

  protected void addTagIfMissing(Rule rule, Map<String, Object> updates, String tag) {

    List tags = rule.getTags();
    if (!tags.contains(tag)) {
      tags.add(tag);
      updates.put("Labels", tags);
    }
  }

  protected List<String> getSpecificReferences(Rule rule, String authority) {

    List<String> referencesFound = new ArrayList<String>();

    String[] referenceLines = rule.getReferences().split("\n");
    for (String line : referenceLines) {
      line = stripHtml(line);
      if (line.contains(authority)) {
        referencesFound.add(line);
      }
    }
    return referencesFound;
  }

  protected boolean isTagPresent(Rule rule, String tag) {

    return rule.getTags().contains(tag);
  }

  protected String stripHtml(String source) {
    return source.replaceAll("<[^>]+>", "");
  }

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

  protected String getEnsureKeyIsNormal(String legacyKey, Language language) throws RuleException {
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

  public enum Language {
    ABAP  ("abap",        "ABAP",        "abap-sonar-way-38370",  true),
    C     ("c",           "C",           "c-sonar-way-44762",     true),
    COBOL ("cobol",       "Cobol",       "cobol-sonar-way-41769", false),
    CPP   ("cpp",         "C++",         "cpp-sonar-way-81587",   true),
    CSH   ("csharpsquid", "C#",          "cs-sonar-way-31865",    true),
    FLEX  ("flex",        "Flex",        "flex-sonar-way-91920",  true),
    JAVA  ("squid",       "Java",        "java-sonar-way-45126",  true),
    JS    ("javascript",  "JavaScript",  "js-sonar-way-56838",    true),
    OBJC  ("objc",        "Objective-C", "objc-sonar-way-83399",  true),
    PHP   ("php",         "PHP",         "php-sonar-way-059",     true),
    PLI   ("pli",         "PL/I",        "pli-sonar-way-95331",   true),
    PLSQL ("plsql",       "PL/SQL",      "plsql-sonar-way-37514", false),
    PY    ("python",      "Python",      "py-sonar-way-67511",    true),
    RPG   ("rpg",         "RPG",         "rpg-sonar-way-64226",   true),
    VB    ("vb",          "VB6",         "vb-sonar-way-21338",    true),
    VBNET ("vbnet",       "VB.NET",      "vbnet-sonar-way-31082", false),
    XML   ("xml",         "XML",         "web-sonar-way-50375",   true);

    protected final String sq;
    protected final String rspec;
    protected final String sqProfileKey;
    protected final boolean update;

    Language(String sq, String rspec, String sqProfileKey, boolean update) {

      this.sq = sq;
      this.rspec = rspec;
      this.sqProfileKey = sqProfileKey;
      this.update = update;
    }
  }

}
