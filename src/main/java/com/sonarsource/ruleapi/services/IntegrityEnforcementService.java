/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.services;

import com.google.common.base.Strings;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.externalspecifications.CodingStandard;
import com.sonarsource.ruleapi.externalspecifications.DerivativeTaggableStandard;
import com.sonarsource.ruleapi.externalspecifications.SupportedCodingStandard;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;
import com.sonarsource.ruleapi.externalspecifications.specifications.AbstractReportableStandard;
import com.sonarsource.ruleapi.get.Fetcher;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.update.RuleUpdater;
import com.sonarsource.ruleapi.utilities.ComparisonUtilities;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class IntegrityEnforcementService extends RuleManager {

  private static final Logger LOGGER = Logger.getLogger(IntegrityEnforcementService.class.getName());

  private static final String TARGETED_LANGUAGES = "Targeted languages";


  public void enforceIntegrity(String login, String password) {
    enforceTagReferenceIntegrity(login, password);
    cleanUpDeprecatedRules(login, password);
    dropTargetedForIrrelevant(login, password);
    checkUrls();
  }

  public void checkUrls() {

    Fetcher fetcher = new Fetcher();

    List<Rule> rules = RuleMaker.getCachedRulesByJql("description ~ \"See http://\" or description ~ \"https://\"", "");
    for (Rule rule : rules) {
      if (!Strings.isNullOrEmpty(rule.getReferences())) {
        String[] lines = rule.getReferences().split("\n");
        for (String line : lines) {
          checkUrlInReferenceLine(fetcher, rule.getKey(), line);
        }
      }
    }
  }

  protected void checkUrlInReferenceLine(Fetcher fetcher, String ruleKey, String line) {

    if (line.contains("http")) {

      String link = line.replaceAll(".*\"(https?://[^'\"]+)\".*", "$1");
      try {
        if (!Strings.isNullOrEmpty(link) && !fetcher.isUrlGood(link)) {
          LOGGER.warning("Bad url in " + ruleKey + ": " + link);
        }
      } catch (RuleException e) {
        LOGGER.warning("Bad url in " + ruleKey + ": " + link + "\n" + e.getMessage());
      }
    }
  }

  public void cleanUpDeprecatedRules(String login, String password) {

    List<Rule> rules = RuleMaker.getCachedRulesByJql(" issueFunction in hasLinks(\"is deprecated by\") OR status = DEPRECATED", "");
    for (Rule rule : rules) {

      Map<String, Object> updates = getDeprecationUpdates(rule);

      Map<Rule, Map<String, Object>> deprecatingRulesNeedingUpdate = getDeprecatingRulesNeedingUpdate(rule, updates);

      for (Map.Entry<Rule, Map<String,Object>> entry : deprecatingRulesNeedingUpdate.entrySet()) {
        LOGGER.info("Adding references to replacement rule: " + rule.getKey());
        RuleUpdater.updateRule(entry.getKey().getKey(), entry.getValue(), login, password);
      }

      if (!Rule.Status.DEPRECATED.equals(rule.getStatus())) {
        LOGGER.info("Setting status to DEPRECATED for " + rule.getKey());
        RuleUpdater.updateRuleStatus(rule.getKey(), Rule.Status.DEPRECATED, login, password);
      }
      if (!updates.isEmpty()) {
        RuleUpdater.updateRule(rule.getKey(), updates, login, password);
      }
    }
  }

  protected Map<Rule, Map<String, Object>> getDeprecatingRulesNeedingUpdate(Rule oldRule, Map<String, Object> oldRuleUpdates) {

    Map<Rule,Map<String,Object>> newRules = new HashMap<>();
    for (SupportedCodingStandard scs : SupportedCodingStandard.values()) {

      CodingStandard cs = scs.getCodingStandard();
      if (cs instanceof AbstractReportableStandard) {

        moveReferencesToNewRules(oldRule, oldRuleUpdates, newRules, cs);
      }
    }

    dropEmptyMapEntries(newRules);
    return newRules;
  }

  protected void dropEmptyMapEntries(Map<Rule, Map<String, Object>> newRules) {

    Iterator<Map.Entry<Rule, Map<String, Object>>> itr = newRules.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry<Rule, Map<String, Object>> entry = itr.next();
      if (entry.getValue().isEmpty()) {
        itr.remove();
      }
    }
  }

  protected void moveReferencesToNewRules(Rule oldRule, Map<String, Object> oldRuleUpdates,
                                        Map<Rule, Map<String, Object>> newRules,CodingStandard cs) {

    List<String> oldReferences = cs.getRspecReferenceFieldValues(oldRule);
    if (!oldReferences.isEmpty()) {

      if (newRules.isEmpty()) {
        for (String link : oldRule.getDeprecationLinks()) {
          newRules.put(RuleMaker.getCachedRuleByKey(link, ""), new HashMap<String, Object>());
        }
      }

      for (Map.Entry<Rule, Map<String, Object>> entry : newRules.entrySet()) {
        copyUniqueReferences(cs, oldReferences, entry);
      }

      LOGGER.info("Removing " + cs.getStandardName() + " references from deprecated rule: " + oldRule.getKey());

      oldReferences.clear();
      oldRuleUpdates.put(cs.getRSpecReferenceFieldName(), oldReferences);
    }
  }

  private static void copyUniqueReferences(CodingStandard cs, List<String> oldReferences, Map.Entry<Rule, Map<String, Object>> newRuleEntry) {

    List<String> newReferences = cs.getRspecReferenceFieldValues(newRuleEntry.getKey());

    for (String old : oldReferences) {
      if (!newReferences.contains(old)) {
        newReferences.add(old);
        if (!newRuleEntry.getValue().containsKey(cs.getRSpecReferenceFieldName())) {
          newRuleEntry.getValue().put(cs.getRSpecReferenceFieldName(), newReferences);
        }
      }
    }
  }


  protected Map<String, Object> getDeprecationUpdates(Rule rule) {

    Map<String,Object> updates = new HashMap<String,Object>();
    if (!rule.getTargetedLanguages().isEmpty()) {
      LOGGER.info("Removing targeted langauges for deprecated rule: " + rule.getKey());
      rule.getTargetedLanguages().clear();
      updates.put(TARGETED_LANGUAGES, rule.getTargetedLanguages());
    }

    if (!rule.getDefaultProfiles().isEmpty()) {
      LOGGER.info("Removing default profiles for deprecated rule: " + rule.getKey());
      rule.getDefaultProfiles().clear();
      updates.put("Default Quality Profiles", rule.getDefaultProfiles());
    }

    if (!rule.getTags().isEmpty()) {
      LOGGER.info("Removing tags for deprecated rule " + rule.getKey());
      rule.getTags().clear();
      updates.put("Labels", rule.getTags());
    }
    return updates;
  }

  private static boolean doesFieldEntryNeedUpdating(TaggableStandard taggable, Map<String, Object> updates, Rule rule, List<String> sees) {

    List<String> references = taggable.getRspecReferenceFieldValues(rule);

    boolean needUpdating = false;

    for (String see : sees) {
      if (! references.contains(see)) {
        references.add(see);
        needUpdating = true;
      }
    }

    List<String> replacements = new ArrayList<String>();
    for (String ref : references) {
      if (taggable.doesReferenceNeedUpdating(ref, replacements, rule.getKey())) {
        needUpdating = true;
      }
    }

    if (needUpdating) {
      taggable.setRspecReferenceFieldValues(rule, replacements);
      updates.put(taggable.getRSpecReferenceFieldName(), replacements);
    }

    return needUpdating;
  }


  /**
   * No language should show up in both the Targeted and Irrelevant lists
   *
   * @param login
   * @param password
   */
  protected void dropTargetedForIrrelevant(String login, String password) {

    List<Rule> rules = RuleMaker.getRulesByJql("\"Irrelevant for Languages\" is not empty", "");
    for (Rule rule : rules) {
      RuleUpdater.updateRule(rule.getKey(), doDropTargetedForIrrelevant(rule), login, password);
    }
  }

  protected Map<String,Object> doDropTargetedForIrrelevant(Rule rule) {

    Map<String, Object> updates = new HashMap<>();
    List<String> targeted = rule.getTargetedLanguages();
    for (String lang : rule.getIrrelevantLanguages()) {
      if (targeted.contains(lang)) {
        targeted.remove(lang);
        updates.put(TARGETED_LANGUAGES, targeted);
      }
    }
    return updates;
  }


  public void setCoveredLanguages(String login, String password) {

    String url = RuleManager.NEMO;

    for (Language lang : Language.values()) {
      LOGGER.info("Setting covered for " + lang.getRspec());
      if (!lang.doUpdate()) {
        LOGGER.warning("Update disabled for " + lang.getSq() + "/" + lang.getRspec());
      }
      setCoveredForLanguage(login, password, lang, url);
    }
  }

  public void setCoveredForLanguage(String login, String password, Language language, String url) {
    String rspecLanguage = language.getRspec();

    Map<String,Rule> needsUpdating = new HashMap<String, Rule>();

    Map<String, Rule> rspecRules = mapRulesByKey(getCoveredRulesForLangauge(language));

    List<Rule> sqCovered = RuleMaker.getRulesFromSonarQubeForLanguage(language, url);
    List<Rule> specNotFound = standardizeKeysAndIdentifyMissingSpecs(language, sqCovered);

    for (Rule sqRule : sqCovered) {

      if (specNotFound.contains(sqRule)) {
        continue;
      }

      if (language.doUpdate()) {
        String key = sqRule.getKey();
        Rule rspecRule = rspecRules.remove(key);
        if (rspecRule == null) {
          rspecRule = RuleMaker.getCachedRuleByKey(key, language.getRspec());
        }

        addCoveredForNemoRules(rspecLanguage, needsUpdating, rspecRule);
      }
    }

    if (language.doUpdate()) {
      dropCoveredForNonNemoRules(rspecLanguage, rspecRules, needsUpdating);
      for (Rule rule : needsUpdating.values()) {
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("Covered Languages", rule.getCoveredLanguages());
        updates.put(TARGETED_LANGUAGES, rule.getTargetedLanguages());
        RuleUpdater.updateRule(rule.getKey(), updates, login, password);
      }
    }
  }

  protected void dropCoveredForNonNemoRules(String rspecLanguage, Map<String, Rule> rspecRules, Map<String, Rule> needsUpdating) {

    for (Rule rspecRule : rspecRules.values()) {
      rspecRule.getCoveredLanguages().remove(rspecLanguage);

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

  public void enforceTagReferenceIntegrity(String login, String password) {

    for (SupportedCodingStandard supportedStandard : SupportedCodingStandard.values()) {

      if (supportedStandard.getCodingStandard() instanceof TaggableStandard) {
        TaggableStandard taggableStandard = (TaggableStandard)supportedStandard.getCodingStandard();
        enforceTagReferenceIntegrity(login, password, taggableStandard);
      }
    }
  }

  public void enforceTagReferenceIntegrity(String login, String password, TaggableStandard taggable) {

    List<Rule> rules = RuleMaker.getRulesByJql(
            "('" + taggable.getRSpecReferenceFieldName() + "' is not EMPTY OR description ~ '" +
                    taggable.getSeeSectionSearchString() + "' OR labels = " + taggable.getTag() + ")",
            "");

    for (Rule rule : rules) {

      if (!Rule.Status.DEPRECATED.equals(rule.getStatus())) {

        Map<String, Object> updates = getUpdates(rule, taggable);
        RuleUpdater.updateRule(rule.getKey(), updates, login, password);
      }
    }
  }

  protected Map<String, Object> getUpdates(Rule rule, TaggableStandard taggable) {

    Map<String, Object> updates = new HashMap<String, Object>();

    List<String> seeSectionReferences = getSpecificReferences(rule, taggable.getSeeSectionSearchString());
    List<String> referenceFieldValues = taggable.getRspecReferenceFieldValues(rule);

    if (seeSectionReferences.isEmpty() && referenceFieldValues.isEmpty()) {
      if (isTagPresent(rule, taggable) && ! taggable.isTagShared()) {
        LOGGER.warning(rule.getKey() + " " + taggable.getTag() + " found in tags but not See & Reference field.");
      }
    }else {

      List<String> sees = parseReferencesFromStrings(taggable, seeSectionReferences);

      if (doesFieldEntryNeedUpdating(taggable, updates, rule, sees)) {
        referenceFieldValues = taggable.getRspecReferenceFieldValues(rule);
      }

      if (taggable instanceof DerivativeTaggableStandard) {
        DerivativeTaggableStandard derivativeStandard = (DerivativeTaggableStandard) taggable;

        derivativeStandard.addTagIfMissing(rule, updates);
        derivativeStandard.checkReferencesInSeeSection(rule);

      } else {

        addTagIfMissing(rule, updates, taggable.getTag());
        addSeeToReferenceField(sees, referenceFieldValues, taggable.getRSpecReferenceFieldName(), updates);
        checkReferencesInSee(referenceFieldValues, sees, rule);

      }
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

  protected void addTagIfMissing(Rule rule, Map<String, Object> updates, String tag) {

    if (Rule.Status.DEPRECATED.equals(rule.getStatus())) {
      return;
    }

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
      line = ComparisonUtilities.stripHtml(line);
      if (line.toLowerCase().contains("see also")) {
        break;
      }
      if (line.contains(authority)) {
        referencesFound.add(line);
      }
    }
    return referencesFound;
  }

  protected boolean isTagPresent(Rule rule, TaggableStandard taggable) {

    return rule.getTags().contains(taggable.getTag());
  }


  public List<String> parseReferencesFromStrings(TaggableStandard taggable, List<String> references) {
    List<String> refs = new ArrayList<String>();

    String pattern = taggable.getReferencePattern();

    for (String reference : references) {
      if (!reference.matches(".*" + pattern + ".*")) {
        continue;
      }

      String[] pieces = reference.split(" ");
      for (String piece : pieces) {
        if (piece.matches(pattern)) {
          refs.add(piece);
        }
      }
    }

    return refs;
  }
}
