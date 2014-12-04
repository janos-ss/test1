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


  public void setCoveredLanguages(String login, String password) throws RuleException {

    for (Language lang : Language.values()) {
      if (!lang.update) {
        LOGGER.warning("Update disabled for " + lang.sq + "/" + lang.rspec);
      }
      setCoveredForLanguage(login,password,lang);
    }
  }


  protected void setCoveredForLanguage(String login, String password, Language language) throws RuleException {
    String rspecLanguage = language.rspec;
    String sqRepo = language.sq;

    List<Rule> rspec = RuleMaker.getRulesByJql("\"Covered Languages\" = \"" + rspecLanguage + "\"", rspecLanguage);

    Map<String, Rule> rspecRules = new HashMap<String, Rule>();
    for (Rule rule : rspec) {
      rspecRules.put(rule.getKey(), rule);
    }

    Map<String,Rule> needsUpdating = new HashMap<String, Rule>();

    List<Rule> sqCovered = RuleMaker.getRulesFromSonarQubeByQuery(NEMO, "repositories=" + sqRepo);
    for (Rule sqRule : sqCovered) {
      String key = sqRule.getKey();

      if (!isKeyNormal(key)) {
        Rule freshFetch = RuleMaker.getRuleByKey(key, rspecLanguage);
        key = freshFetch.getKey();
        if (key == null) {
          LOGGER.warning("Legacy key not found for "
                  + rspecLanguage + "/" + sqRepo + ": " + sqRule.getKey());
          continue;
        }
      }

      if (language.update) {
        Rule rspecRule = rspecRules.remove(key);
        if (rspecRule == null) {
          rspecRule = RuleMaker.getRuleByKey(key, "");
        }

        addCoveredForNemoRules(rspecLanguage, needsUpdating, rspecRule);
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

  protected boolean isKeyNormal(String key) {

    return key.matches("RSPEC-\\d+");
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
    return source.replaceAll("<[^>]+>","");
  }

  private enum Language {
    ABAP  ("abap",        "ABAP",       true),
    C     ("c",           "C",          true),
    COBOL ("cobol",       "Cobol",      false),
    CPP   ("cpp",         "C++",        true),
    CSH   ("csharpsquid", "C#",         true),
    FLEX  ("flex",        "Flex",       true),
    JAVA  ("squid",       "Java",       true),
    JS    ("javascript",  "JavaScript", true),
    OBJC  ("objc",        "Objective-C",true),
    PHP   ("php",         "PHP",        true),
    PLI   ("pli",         "PL/I",       true),
    PLSQL ("plsql",       "PL/SQL",     false),
    PY    ("python",      "Python",     true),
    RPG   ("rpg",         "RPG",        true),
    VB    ("vb",          "VB6",        true),
    VBNET ("vbnet",       "VB.NET",     false),
    XML   ("xml",         "XML",        true);

    private final String sq;
    private final String rspec;
    private final boolean update;

    Language(String sq, String rspec, boolean update) {

      this.sq = sq;
      this.rspec = rspec;
      this.update = update;
    }
  }

}
