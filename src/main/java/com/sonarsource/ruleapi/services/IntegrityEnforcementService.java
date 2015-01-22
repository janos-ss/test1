/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.SupportedCodingStandard;
import com.sonarsource.ruleapi.externalspecifications.specifications.AbstractTaggableStandard;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.update.RuleUpdater;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.domain.RuleComparison;
import com.sonarsource.ruleapi.domain.RuleException;


public class IntegrityEnforcementService extends RuleManager {

  private static final Logger LOGGER = Logger.getLogger(IntegrityEnforcementService.class.getName());


  public void setCoveredAndOutdatedLanguages(String login, String password) throws RuleException {

    for (Language lang : Language.values()) {
      if (!lang.doUpdate()) {
        LOGGER.warning("Update disabled for " + lang.getSq() + "/" + lang.getRspec());
      }
      setCoveredAndOutdatedForLanguage(login, password, lang);
    }
  }

  public void setCoveredAndOutdatedForLanguage(String login, String password, Language language) throws RuleException {
    String rspecLanguage = language.getRspec();

    List<Rule> rspec = getCoveredRulesForLangauge(language);
    Map<String, Rule> rspecRules = mapRulesByKey(rspec);

    Map<String,Rule> needsUpdating = new HashMap<String, Rule>();

    List<Rule> sqCovered = getImplementedRulesForLanguage(language, NEMO);
    List<Rule> specNotFound = standardizeKeysAndIdentifyMissingSpecs(language, sqCovered);

    for (Rule sqRule : sqCovered) {

      if (specNotFound.contains(sqRule)) {
        continue;
      }

      if (language.doUpdate()) {
        String key = sqRule.getKey();
        Rule rspecRule = rspecRules.remove(key);
        if (rspecRule == null) {
          rspecRule = RuleMaker.getRuleByKey(key, language.getRspec());
        }

        addCoveredForNemoRules(rspecLanguage, needsUpdating, rspecRule);
        setOutdatedForNemoRules(rspecLanguage, needsUpdating, rspecRule, sqRule);
      }
    }

    if (language.doUpdate()) {
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

    if (result != 0 && !outdatedLanguages.contains(rspecLanguage)) {
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

  public void enforceTagReferenceIntegrity(String login, String password) throws RuleException {

    for (SupportedCodingStandard supportedStandard : SupportedCodingStandard.values()) {

      if (supportedStandard.getCodingStandard() instanceof  AbstractTaggableStandard) {
        AbstractTaggableStandard taggableStandard = (AbstractTaggableStandard)supportedStandard.getCodingStandard();
        enforceTagReferenceIntegrity(login, password, taggableStandard);
      }
    }
  }

  public void enforceTagReferenceIntegrity(String login, String password, AbstractTaggableStandard taggable) throws RuleException {

    List<Rule> rules = RuleMaker.getRulesByJql(
            taggable.getRSpecReferenceFieldName() + " is not EMPTY OR description ~ '" +
                    taggable.getSeeSectionSearchString() + "' OR labels = " + taggable.getTag(),
            "");

    for (Rule rule : rules) {

      Map<String, Object> updates = getUpdates(rule, taggable);
      if (! updates.isEmpty()) {
        RuleUpdater.updateRule(rule.getKey(), updates, login, password);
      }
    }
  }

  protected Map<String, Object> getUpdates(Rule rule, AbstractTaggableStandard taggable) {

    Map<String, Object> updates = new HashMap<String, Object>();

    List<String> seeSectionReferences = getSpecificReferences(rule, taggable.getSeeSectionSearchString());
    List<String> referenceFieldValues = taggable.getRspecReferenceFieldValues(rule);

    if (seeSectionReferences.isEmpty() && referenceFieldValues.isEmpty()) {
      if (isTagPresent(rule, taggable)) {
        LOGGER.warning(rule.getKey() + " " + taggable.getTag() + " found in tags but not See & Reference field.");
      }
    }else {

      if (taggable.isFieldEntryFormatNeedUpdating(updates, rule)) {
        referenceFieldValues = taggable.getRspecReferenceFieldValues(rule);
      }

      addTagIfMissing(rule, updates, taggable.getTag());

      List<String> sees = taggable.parseReferencesFromStrings(seeSectionReferences);
      addSeeToReferenceField(sees, referenceFieldValues, taggable.getRSpecReferenceFieldName(), updates);
      checkReferencesInSee(referenceFieldValues, sees, rule);
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

  protected boolean isTagPresent(Rule rule, AbstractTaggableStandard taggable) {

    return rule.getTags().contains(taggable.getTag());
  }

  protected String stripHtml(String source) {
    return source.replaceAll("<[^>]+>", "");
  }

}
