/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.externalspecifications.CodingStandard;
import com.sonarsource.ruleapi.externalspecifications.DerivativeTaggableStandard;
import com.sonarsource.ruleapi.externalspecifications.Standard;
import com.sonarsource.ruleapi.externalspecifications.SupportedStandard;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.update.RuleUpdater;
import com.sonarsource.ruleapi.utilities.ComparisonUtilities;
import com.sonarsource.ruleapi.utilities.Language;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


public class IntegrityEnforcementService extends RuleManager {

  private static final Logger LOGGER = Logger.getLogger(IntegrityEnforcementService.class.getName());

  private static final String TARGETED_LANGUAGES = "Targeted languages";
  private static final String LABELS = "Labels";
  private final RuleUpdater ruleUpdater;

  public IntegrityEnforcementService(String login, String password) {
    super();
    ruleUpdater = new RuleUpdater(login, password);
  }


  public void enforceIntegrity() {
    enforceTagReferenceIntegrity();
    cleanUpDeprecatedRules();
    dropTargetedForIrrelevant();
    checkUrls();
  }

  private static void checkUrls() {
    List<Rule> rules = RuleMaker.getRulesByJql("description ~ \"See http://\" or description ~ \"https://\"", "");
    for (Rule rule : rules) {
      if (!Strings.isNullOrEmpty(rule.getReferences())) {
        String[] lines = rule.getReferences().split("\n");
        for (String line : lines) {
          checkUrlInReferenceLine(rule.getKey(), line);
        }
      }
    }
  }

  private static void checkUrlInReferenceLine(String ruleKey, String line) {

    if (line.contains("http")) {

      String link = line.replaceAll(".*\"(https?://[^'\"]+)\".*", "$1");
      try {
        if (!Strings.isNullOrEmpty(link) && !isUrlGood(link)) {
          LOGGER.warning("Bad url in " + ruleKey + ": " + link);
        }
      } catch (RuleException e) {
        LOGGER.warning("Bad url in " + ruleKey + ": " + link + "\n" + e.getMessage());
      }
    }
  }

  private static boolean isUrlGood(String urlString) {
    try {
      URL u = new URL(urlString);
      HttpURLConnection huc = (HttpURLConnection) u.openConnection();
      huc.setRequestMethod("GET");
      huc.setInstanceFollowRedirects(true);
      huc.connect();
      int code = huc.getResponseCode();

      return code >= 200 && code <=299;
    } catch (IOException e) {
      throw new RuleException(e);
    }
  }

  private void cleanUpDeprecatedRules() {

    List<Rule> rules = RuleMaker.getRulesByJql(" issueFunction in hasLinks(\"is deprecated by\") OR status = DEPRECATED", "");
    for (Rule rule : rules) {

      Map<String, Object> updates = new HashMap<>();

      Map<Rule, Map<String, Object>> deprecatingRulesNeedingUpdate = getDeprecationUpdates(rule, updates);

      for (Map.Entry<Rule, Map<String,Object>> entry : deprecatingRulesNeedingUpdate.entrySet()) {
        String newRuleKey = entry.getKey().getKey();
        LOGGER.info("Submitting updates to replacement rule: " + newRuleKey);
        ruleUpdater.updateRule(newRuleKey, entry.getValue());
      }

      if (!Rule.Status.DEPRECATED.equals(rule.getStatus())) {
        LOGGER.info("Setting status to DEPRECATED for " + rule.getKey());
        ruleUpdater.updateRuleStatus(rule.getKey(), Rule.Status.DEPRECATED);
      }
      if (!updates.isEmpty()) {
        LOGGER.info("Submitting updates to deprecated rule: " + rule.getKey());
        ruleUpdater.updateRule(rule.getKey(), updates);
      }
    }
  }

  @VisibleForTesting
  protected Map<Rule, Map<String, Object>> getDeprecationUpdates(Rule oldRule, Map<String, Object> oldRuleUpdates) {

    Map<Rule,Map<String,Object>> newRules = new HashMap<>();
    for (SupportedStandard scs : SupportedStandard.values()) {

      Standard cs = scs.getStandard();
      if (cs instanceof CodingStandard) {
        moveReferencesToNewRules(oldRule, oldRuleUpdates, newRules, (CodingStandard)cs);
      }
    }

    moveLanguagesToNewRules(oldRule, oldRuleUpdates, newRules);
    moveTagsToNewRules(oldRule, oldRuleUpdates, newRules);
    moveProfilesToNewRules(oldRule, oldRuleUpdates, newRules);

    dropEmptyMapEntries(newRules);
    return newRules;
  }

  @VisibleForTesting
  protected void dropEmptyMapEntries(Map<Rule, Map<String, Object>> newRules) {

    Iterator<Map.Entry<Rule, Map<String, Object>>> itr = newRules.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry<Rule, Map<String, Object>> entry = itr.next();
      if (entry.getValue().isEmpty()) {
        itr.remove();
      }
    }
  }

  @VisibleForTesting
  protected void moveProfilesToNewRules(Rule oldRule, Map<String, Object> oldRuleUpdates,
                                         Map<Rule, Map<String, Object>> newRules) {

    if (oldRule.getDefaultProfiles().isEmpty()) {
      return;
    }
    for (Map.Entry<Rule, Map<String, Object>> entry : newRules.entrySet()) {

      Rule newRule = entry.getKey();
      int startLen = newRule.getDefaultProfiles().size();
      newRule.getDefaultProfiles().addAll(oldRule.getDefaultProfiles());
      if (startLen != newRule.getDefaultProfiles().size()) {
        entry.getValue().put("Default Quality Profiles", newRule.getDefaultProfiles());
      }
    }

    LOGGER.info("Moving default profiles from deprecated rule: " + oldRule.getKey());
    oldRule.getDefaultProfiles().clear();
    oldRuleUpdates.put("Default Quality Profiles", oldRule.getDefaultProfiles());
  }

  @VisibleForTesting
  protected void moveLanguagesToNewRules(Rule oldRule, Map<String, Object> oldRuleUpdates,
                                         Map<Rule, Map<String, Object>> newRules) {

    if (oldRule.getTargetedLanguages().isEmpty()) {
      return;
    }

    for (Map.Entry<Rule, Map<String, Object>> entry : newRules.entrySet()) {

      Rule newRule = entry.getKey();
      int startLen = newRule.getTargetedLanguages().size();
      newRule.getTargetedLanguages().addAll(oldRule.getTargetedLanguages());
      newRule.getTargetedLanguages().addAll(oldRule.getCoveredLanguages());

      newRule.getTargetedLanguages().removeAll(newRule.getIrrelevantLanguages());
      newRule.getTargetedLanguages().removeAll(newRule.getCoveredLanguages());

      if (startLen != newRule.getTargetedLanguages().size()){
        entry.getValue().put(TARGETED_LANGUAGES, newRule.getTargetedLanguages());
      }
    }
    LOGGER.info("Moving targeted languages from deprecated rule: " + oldRule.getKey());
    oldRule.getTargetedLanguages().clear();
    oldRuleUpdates.put(TARGETED_LANGUAGES, oldRule.getTargetedLanguages());
  }

  @VisibleForTesting
  protected void moveTagsToNewRules(Rule oldRule, Map<String, Object> oldRuleUpdates,
                                          Map<Rule, Map<String, Object>> newRules) {

    if (oldRule.getTags().isEmpty()) {
      return;
    }

    for (Map.Entry<Rule, Map<String, Object>> entry : newRules.entrySet()) {
      Rule newRule = entry.getKey();
      int startLen = newRule.getTags().size();
      newRule.getTags().addAll(oldRule.getTags());
      if (startLen != newRule.getTags().size()){
        entry.getValue().put(LABELS, newRule.getTags());
      }
    }
    LOGGER.info("Moving tags from deprecated rule " + oldRule.getKey());
    oldRule.getTags().clear();
    oldRuleUpdates.put(LABELS, oldRule.getTags());
  }

  @VisibleForTesting
  protected void moveReferencesToNewRules(Rule oldRule, Map<String, Object> oldRuleUpdates,
                                        Map<Rule, Map<String, Object>> newRules,CodingStandard cs) {

    List<String> oldReferences = cs.getRspecReferenceFieldValues(oldRule);

    if (!oldReferences.isEmpty()) {
      if (newRules.isEmpty()) {
        for (String link : oldRule.getDeprecationLinks()) {
          newRules.put(RuleMaker.getRuleByKey(link, ""), new HashMap<String, Object>());
        }
      }

      for (Map.Entry<Rule, Map<String, Object>> entry : newRules.entrySet()) {
        copyUniqueReferences(cs, oldReferences, entry);
      }

      LOGGER.info("Moving " + cs.getStandardName() + " references from deprecated rule: " + oldRule.getKey());

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

  private static boolean doesFieldEntryNeedUpdating(TaggableStandard taggable, Map<String, Object> updates, Rule rule, List<String> sees) {

    List<String> references = taggable.getRspecReferenceFieldValues(rule);

    boolean needUpdating = false;

    for (String see : sees) {
      if (! references.contains(see)) {
        references.add(see);
        needUpdating = true;
      }
    }

    List<String> replacements = new ArrayList<>();
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
   */
  protected void dropTargetedForIrrelevant() {

    List<Rule> rules = RuleMaker.getRulesByJql("\"Irrelevant for Languages\" is not empty", "");
    for (Rule rule : rules) {
      ruleUpdater.updateRule(rule.getKey(), doDropTargetedForIrrelevant(rule));
    }
  }

  protected Map<String,Object> doDropTargetedForIrrelevant(Rule rule) {

    Map<String, Object> updates = new HashMap<>();
    Set<String> targeted = rule.getTargetedLanguages();
    for (String lang : rule.getIrrelevantLanguages()) {
      if (targeted.contains(lang)) {
        targeted.remove(lang);
        updates.put(TARGETED_LANGUAGES, targeted);
      }
    }
    return updates;
  }


  public void setCoveredLanguages() {
    for (Language lang : Language.values()) {
      LOGGER.info("Setting covered for " + lang.getRspec());
      setCoveredForLanguage(lang);
    }
  }

  private void setCoveredForLanguage(Language language) {
    String rspecLanguage = language.getRspec();

    Map<String,Rule> needsUpdating = new HashMap<>();

    Map<String, Rule> rspecRules = getCoveredRulesForLanguage(language);

    List<Rule> sqCovered = RuleMaker.getRulesFromSonarQubeForLanguage(language, RuleManager.SONARQUBE_COM);
    List<Rule> specNotFound = standardizeKeysAndIdentifyMissingSpecs(language, sqCovered);

    for (Rule sqRule : sqCovered) {

      if (specNotFound.contains(sqRule)) {
        continue;
      }

      String key = sqRule.getKey();
      Rule rspecRule = rspecRules.remove(key);
      if (rspecRule == null) {
        rspecRule = RuleMaker.getRuleByKey(key, language.getRspec());
      }

      addCoveredForNemoRules(rspecLanguage, needsUpdating, rspecRule);
    }

    dropCoveredForNonNemoRules(rspecLanguage, rspecRules, needsUpdating);
    for (Rule rule : needsUpdating.values()) {
      Map<String, Object> updates = new HashMap<>();
      updates.put("Covered Languages", rule.getCoveredLanguages());
      updates.put(TARGETED_LANGUAGES, rule.getTargetedLanguages());
      ruleUpdater.updateRule(rule.getKey(), updates);
    }
  }

  @VisibleForTesting
  protected void dropCoveredForNonNemoRules(String rspecLanguage, Map<String, Rule> rspecRules, Map<String, Rule> needsUpdating) {

    for (Rule rspecRule : rspecRules.values()) {
      rspecRule.getCoveredLanguages().remove(rspecLanguage);

      rspecRule.getTargetedLanguages().add(rspecLanguage);
      LOGGER.info(rspecLanguage + " " + rspecRule.getKey() + " moving from covered to targeted");

      needsUpdating.put(rspecRule.getKey(), rspecRule);
    }
  }

  @VisibleForTesting
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

  private void enforceTagReferenceIntegrity() {

    for (SupportedStandard supportedStandard : SupportedStandard.values()) {

      if (supportedStandard.getStandard() instanceof TaggableStandard) {
        TaggableStandard taggableStandard = (TaggableStandard)supportedStandard.getStandard();
        enforceTagReferenceIntegrity(taggableStandard);
      }
    }
  }

  public void enforceTagReferenceIntegrity(TaggableStandard taggable) {

    LOGGER.info("STARTING: " + taggable.getStandardName());

    List<Rule> rules = RuleMaker.getRulesByJql(
            "('" + taggable.getRSpecReferenceFieldName() + "' is not EMPTY OR description ~ '" +
                    taggable.getSeeSectionSearchString() + "' OR labels = " + taggable.getTag() + ")",
            "");

    for (Rule rule : rules) {

      if (!Rule.Status.DEPRECATED.equals(rule.getStatus())) {

        Map<String, Object> updates = getUpdates(rule, taggable);
        ruleUpdater.updateRule(rule.getKey(), updates);
      }
    }
  }

  protected Map<String, Object> getUpdates(Rule rule, TaggableStandard taggable) {

    Map<String, Object> updates = new HashMap<>();

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

    Set<String> tags = rule.getTags();
    LOGGER.info("Adding missing tag '" + tag + "' to " + rule.getKey());
    if (!tags.contains(tag)) {
      tags.add(tag);
      updates.put(LABELS, tags);
    }
  }

  protected List<String> getSpecificReferences(Rule rule, String authority) {

    List<String> referencesFound = new ArrayList<>();

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


  public static List<String> parseReferencesFromStrings(TaggableStandard taggable, List<String> references) {
    List<String> refs = new ArrayList<>();

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
