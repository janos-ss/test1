/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
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
import com.sonarsource.ruleapi.externalspecifications.specifications.SansTop25;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.update.RuleUpdater;
import com.sonarsource.ruleapi.utilities.ComparisonUtilities;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IntegrityEnforcementService extends RuleManager {

  private static final Logger LOGGER = Logger.getLogger(IntegrityEnforcementService.class.getName());

  private static final String TARGETED_LANGUAGES = "Targeted languages";
  private static final String LABELS = "Labels";
  public static final String DEFAULT_QUALITY_PROFILES = "Default Quality Profiles";
  private final RuleUpdater ruleUpdater;

  public IntegrityEnforcementService(String login, String password) {
    super();
    ruleUpdater = new RuleUpdater(login, password);
  }


  public void enforceIntegrity() {
    enforceTagReferenceIntegrity();
    cleanUpDeprecatedRules();
    cleanUpSupersededRules();
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

  @VisibleForTesting
  static void checkUrlInReferenceLine(String ruleKey, String line) {

    if (line.contains("http")) {

      String link = line.replaceAll(".*\"(https?://[^'\"]+)\".*", "$1");
      try {
        if (!Strings.isNullOrEmpty(link) && !isUrlGood(link)) {
          LOGGER.log(Level.WARNING, "Bad url in {0}: {1}",
                  new Object[]{ruleKey, link});
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

  protected void cleanUpSupersededRules() {
    List<Rule> rules = RuleMaker.getRulesByJql(" issueFunction in hasLinks(\"is superceded by\") OR status = SUPERSEDED", "");
    for (Rule rule : rules) {

      Map<Rule,Map<String,Object>> replacingRules = getReplacingRules(rule);

      getSupersederUpdates(rule, replacingRules);

      processReplacementRuleUpdates(replacingRules);

      if (!Rule.Status.SUPERSEDED.equals(rule.getStatus())) {
        LOGGER.info("Setting status to SUPERSEDED for " + rule.getKey());
        ruleUpdater.updateRuleStatus(rule.getKey(), Rule.Status.SUPERSEDED);
      }
    }
  }

  protected Map<Rule, Map<String, Object>> getSupersederUpdates(Rule oldRule, Map<Rule,Map<String,Object>> replacingRules) {

    if (! replacingRules.isEmpty()) {
      for (SupportedStandard scs : SupportedStandard.values()) {

        Standard cs = scs.getStandard();
        if (cs instanceof CodingStandard) {
          copyReferencesToReplacingRules(oldRule, replacingRules, (CodingStandard) cs);
        }
      }

      copyLanguagesToReplacingRules(oldRule, replacingRules);
      copyTagsToReplacingRules(oldRule, replacingRules);
      copyProfilesToReplacingRules(oldRule, replacingRules);

      dropEmptyMapEntries(replacingRules);
    }
    return replacingRules;
  }


  private void cleanUpDeprecatedRules() {

    List<Rule> rules = RuleMaker.getRulesByJql(" issueFunction in hasLinks(\"is deprecated by\") OR status = DEPRECATED", "");
    for (Rule rule : rules) {

      Map<String, Object> updates = new HashMap<>();

      Map<Rule,Map<String,Object>> deprecatingRulesNeedingUpdate = getReplacingRules(rule);

      getDeprecationUpdates(rule, updates, deprecatingRulesNeedingUpdate);
      processReplacementRuleUpdates(deprecatingRulesNeedingUpdate);

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
  protected Map<Rule, Map<String, Object>> getDeprecationUpdates(Rule oldRule, Map<String, Object> oldRuleUpdates,
                                                                 Map<Rule,Map<String,Object>> newRules) {

    for (SupportedStandard scs : SupportedStandard.values()) {

      Standard cs = scs.getStandard();
      if (cs instanceof CodingStandard) {
        moveReferencesToReplacingRules(oldRule, oldRuleUpdates, newRules, (CodingStandard) cs);
      }
    }

    moveLanguagesToReplacingRules(oldRule, oldRuleUpdates, newRules);
    moveTagsToReplacingRules(oldRule, oldRuleUpdates, newRules);
    moveProfilesToReplacingRules(oldRule, oldRuleUpdates, newRules);

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

  private static Map<Rule, Map<String, Object>> getReplacingRules(Rule oldRule) {
    Map<Rule, Map<String, Object>> newRules = new HashMap<>();

    for (Rule rule : RuleMaker.getReplacingRules(oldRule)){
      newRules.put(rule, new HashMap<>());
    }
    return newRules;
  }

  @VisibleForTesting
  protected void moveProfilesToReplacingRules(Rule oldRule, Map<String, Object> oldRuleUpdates,
                                              Map<Rule, Map<String, Object>> newRules) {

    if (oldRule.getDefaultProfiles().isEmpty()) {
      return;
    }
    for (Map.Entry<Rule, Map<String, Object>> entry : newRules.entrySet()) {

      Rule newRule = entry.getKey();
      int startLen = newRule.getDefaultProfiles().size();
      newRule.getDefaultProfiles().addAll(oldRule.getDefaultProfiles());
      if (startLen != newRule.getDefaultProfiles().size()) {
        entry.getValue().put(DEFAULT_QUALITY_PROFILES, newRule.getDefaultProfiles());
      }
    }

    LOGGER.info("Moving default profiles from deprecated rule: " + oldRule.getKey());
    oldRule.getDefaultProfiles().clear();
    oldRuleUpdates.put(DEFAULT_QUALITY_PROFILES, oldRule.getDefaultProfiles());
  }

  protected void copyProfilesToReplacingRules(Rule oldRule, Map<Rule, Map<String, Object>> newRules) {
    if (oldRule.getDefaultProfiles().isEmpty()) {
      return;
    }
    for (Map.Entry<Rule, Map<String, Object>> entry : newRules.entrySet()) {

      Rule newRule = entry.getKey();
      int startLen = newRule.getDefaultProfiles().size();
      newRule.getDefaultProfiles().addAll(oldRule.getDefaultProfiles());
      if (startLen != newRule.getDefaultProfiles().size()) {
        entry.getValue().put(DEFAULT_QUALITY_PROFILES, newRule.getDefaultProfiles());
      }
    }

    LOGGER.info("Copying default profiles from old rule: " + oldRule.getKey());
  }

  @VisibleForTesting
  protected void moveLanguagesToReplacingRules(Rule oldRule, Map<String, Object> oldRuleUpdates,
                                               Map<Rule, Map<String, Object>> newRules) {

    if (oldRule.getTargetedLanguages().isEmpty()) {
      return;
    }

    copyLanguagesToReplacingRules(oldRule, newRules);

    oldRule.getTargetedLanguages().clear();
    oldRuleUpdates.put(TARGETED_LANGUAGES, oldRule.getTargetedLanguages());
  }

  protected void copyLanguagesToReplacingRules(Rule oldRule, Map<Rule, Map<String, Object>> newRules) {

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
    LOGGER.info("Copying targeted languages from old rule: " + oldRule.getKey());
  }

  @VisibleForTesting
  protected void moveTagsToReplacingRules(Rule oldRule, Map<String, Object> oldRuleUpdates,
                                          Map<Rule, Map<String, Object>> newRules) {

    if (oldRule.getTags().isEmpty()) {
      return;
    }

    copyTagsToReplacingRules(oldRule, newRules);

    oldRule.getTags().clear();
    oldRuleUpdates.put(LABELS, oldRule.getTags());
  }

  protected void copyTagsToReplacingRules(Rule oldRule, Map<Rule, Map<String, Object>> newRules) {

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
    LOGGER.info("Copying tags from old rule " + oldRule.getKey());
  }

  @VisibleForTesting
  protected void moveReferencesToReplacingRules(Rule oldRule, Map<String, Object> oldRuleUpdates,
                                                Map<Rule, Map<String, Object>> newRules, CodingStandard cs) {

    List<String> oldReferences = cs.getRspecReferenceFieldValues(oldRule);

    if (!oldReferences.isEmpty()) {

      copyReferencesToReplacingRules(oldRule, newRules, cs);

      oldReferences.clear();
      oldRuleUpdates.put(cs.getRSpecReferenceFieldName(), oldReferences);
    }
  }

  protected void copyReferencesToReplacingRules(Rule oldRule, Map<Rule, Map<String, Object>> newRules, CodingStandard cs) {
    List<String> oldReferences = cs.getRspecReferenceFieldValues(oldRule);

    if (!oldReferences.isEmpty()) {

      for (Map.Entry<Rule, Map<String, Object>> entry : newRules.entrySet()) {
        copyUniqueReferences(cs, oldReferences, entry);
      }

      LOGGER.info("Copying " + cs.getStandardName() + " references from old rule: " + oldRule.getKey());
    }
  }

  private void processReplacementRuleUpdates(Map<Rule, Map<String, Object>> updateMap) {

    for (Map.Entry<Rule, Map<String,Object>> entry : updateMap.entrySet()) {
      String newRuleKey = entry.getKey().getKey();
      LOGGER.log(Level.INFO, "Submitting updates to replacement rule: {0}", newRuleKey);
      ruleUpdater.updateRule(newRuleKey, entry.getValue());
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
        LOGGER.log(Level.WARNING,  "{0} - {1} missing from See section ",
                new Object[]{rule.getKey(), reference});
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
    LOGGER.log(Level.INFO, "Adding missing tag {0} to {1}",
            new Object[]{tag, rule.getKey()});
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
    String separator = (taggable instanceof SansTop25.Category ? " - " : " ");

    for (String reference : references) {
      if (!reference.matches(".*" + pattern + ".*")) {
        continue;
      }

      String[] pieces = reference.split(separator);
      for (String piece : pieces) {
        if (piece.matches(pattern)) {
          refs.add(piece);
        }
      }
    }

    return refs;
  }
}
