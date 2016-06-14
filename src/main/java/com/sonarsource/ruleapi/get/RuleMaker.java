/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.google.common.base.Strings;
import com.sonarsource.ruleapi.domain.Profile;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Given a key and a language, retrieves the relevant Issue
 * from Jira (if possible), and populates a new Rule with
 * the details.
 *
 * Given a key, language and SQ instance URL, retrieves the
 * relevant Issue from the SonarQube instance.
 *
 * Rule population from Jira includes the retrieval
 * and overlay of values from any language-specific subtask,
 * as well as proper handling of labeled language-specific
 * variants such as {code} blocks and parameter variations.
 */
public class RuleMaker {

  private static final JiraFetcher JIRA_FETCHER = new JiraFetcherImpl();
  private static final SQFetcher SQ_FETCHER = new SQFetcherImpl();

  private RuleMaker() {
  }

  public static List<Rule> getRulesFromSonarQubeForLanguage(Language language, String instance) {

    List<Rule> allRules = RuleMaker.getRulesFromSonarQubeByQuery(instance, "repositories=" + language.getSq()+","+language.getSqCommon());

    populateSonarQubeProfiles(language, instance, allRules);

    return allRules;
  }

  protected static void populateSonarQubeProfiles(Language language, String instance, List<Rule> allRules) {

    List<Profile> profiles = getProfiles(language, SQ_FETCHER.fetchProfilesFromSonarQube(instance));

    for (Profile profile : profiles) {
      List<JSONObject> profileRules = SQ_FETCHER.fetchRulesFromSonarQube(instance,
              "activation=true&f=internalKey&qprofile=" + profile.getKey());
      addProfilesToSonarQubeRules(allRules, profile, profileRules);
    }
  }

  protected static void addProfilesToSonarQubeRules(List<Rule> allRules, Profile profile, List<JSONObject> profileRules) {

    Map<String, Rule> rulesByKey = new HashMap<>();
    for (Rule rule : allRules) {
      rulesByKey.put(rule.getKey(), rule);
    }

    for (JSONObject jsonObject : profileRules) {
      String internalKey = (String) jsonObject.get("key");
      if (internalKey != null) {
        String key = Utilities.normalizeKey(internalKey);
        Rule rule = rulesByKey.get(key);
        if (rule != null) {
          rule.getDefaultProfiles().add(profile);
        }
      }
    }
  }

  /**
   * Given a rule key and a language (e.g. Java, ABAP, etc), fetches
   * Issue from Jira and creates from it a Rule. Rule population includes
   * translation of markdown to HTML so that return value can be compared
   * directly with Implementation.
   *
   * @param key rule key - legacy key, S### or RSPEC-###
   * @param language language of rule. Used to distinguish among language-specific options
   * @return rule fetched from Jira
   */
  public static Rule getRuleByKey(String key, String language) {

    Rule rule = new Rule(language);
    JSONObject jsonRule = JIRA_FETCHER.fetchIssueByKey(key);
    fleshOutRule(JIRA_FETCHER, rule, jsonRule);

    return rule;
  }

  public static List<Rule> getRulesFromSonarQubeByQuery(String instance, String query) {

    List<Rule> rules = new ArrayList<>();

    List<JSONObject> jsonRules = SQ_FETCHER.fetchRulesFromSonarQube(instance, query);

    for (JSONObject jsonRule : jsonRules) {
      rules.add(SonarQubeHelper.populateFields(jsonRule));
    }

    return rules;
  }

  protected static List<Profile> getProfiles(Language language, List<JSONObject> jsonProfiles){
    List<Profile> profiles = new ArrayList<>();

    for (JSONObject jobj : jsonProfiles) {

      Language lang = Language.fromString((String) jobj.get("lang"));
      if (language != null && language.equals(lang)) {
        profiles.add(new Profile((String) jobj.get("name"), (String) jobj.get("key")));
      }
    }
    return profiles;
  }

  /**
   * Retrieve a list of open rules based on a JQL snippet.
   * @param query the jql to use
   * @param language the language sought. Not used in the query but used to populate rule members
   * @return a list of retrieved rules
   */
  public static List<Rule> getRulesByJql(String query, String language) {
    List<Rule> rules = new ArrayList<>();

    List<JSONObject> issues = JIRA_FETCHER.fetchIssuesBySearch(query);

    for (JSONObject jsonRule : issues) {
      Rule rule = new Rule(language);
      fleshOutRule(JIRA_FETCHER, rule, jsonRule);
      rules.add(rule);
    }

    return rules;
  }

  protected static void fleshOutRule(JiraFetcher fetcher, Rule rule, JSONObject jsonRule) {
    if (jsonRule != null) {
      JiraHelper.populateFields(rule, jsonRule);

      JSONObject fields = (JSONObject)jsonRule.get("fields");
      JSONArray subtasks = (JSONArray) fields.get("subtasks");

      JSONObject subIssue = getSubtask(fetcher, rule.getLanguage(), subtasks);
      if (subIssue != null) {

        Rule subRule = new Rule(rule.getLanguage());
        JiraHelper.populateFields(subRule, subIssue);
        rule.merge(subRule);
      }
    }
  }

  private static JSONObject getSubtask(JiraFetcher fetcher, String language, JSONArray tasks) {
    if (tasks != null && ! Strings.isNullOrEmpty(language)) {
      for (JSONObject subt : (Iterable<JSONObject>) tasks) {
        if (isLanguageMatch(language, JiraHelper.getJsonFieldValue(subt, "summary").trim())) {
          return fetcher.fetchIssueByKey(subt.get("key").toString());
        }
      }
    }
    return null;
  }

  protected static boolean isLanguageMatch(String language, String candidate) {

    String ucLanguage = language.toUpperCase();
    String ucCandidate = candidate.toUpperCase();

    if (isCFamilyMatch(ucLanguage)) {
      return isCFamilyMatch(ucCandidate);
    }

    if (ucLanguage.equals(ucCandidate)) {
      return true;
    }
    if (!ucCandidate.startsWith(ucLanguage)) {
      return false;
    }
    return ucCandidate.matches(ucLanguage + "\\W.*");
  }

  protected static boolean isCFamilyMatch(String candidate) {
    String[] cFamily = {"C-Family", "C", "CPP", "C++", "Objective-C"};

    if ("C#".equalsIgnoreCase(candidate) || candidate.matches("(?i:C#\\W.*)")) {
      return false;
    }

    for (String option : cFamily) {
      if (option.equalsIgnoreCase(candidate)) {
        return true;
      }
      if (candidate.matches("(?i:" + option + "\\W.*)")) {
        return true;
      }
    }
    return false;
  }

  /**
   * <p>Splits the full rule description into into its constituent parts
   * and processes each, converting markdown to HTML and making the
   * correct choices among language-specific {code} blocks based
   * on the rule's language property.</p>
   *
   * <p>NOTE that this method cannot distinguish among language-specific
   * word choices, e.g. [Function|Method|Procedure], so the full set of
   * choices will remain intact in the resulting text.</p>
   *
   * @param rule the rule to be populated
   * @param fullDescription the text from which to populate it.
   */
  public static void setDescription(Rule rule, String fullDescription, boolean isMarkdown) {

    String fullDesc = fullDescription;
    String markdownH2Match = "h2\\.";
    String htmlH2 = "<h2>";

    if (Rule.Status.DEPRECATED.equals(rule.getStatus())) {
      StringBuilder sb = new StringBuilder();

      for (String key : rule.getDeprecationLinks()) {
        if (sb.length() > 0) {
          sb.append(", ");
        }
        sb.append(Utilities.denormalizeKey(key));
      }

      if (! rule.getDeprecationLinks().isEmpty()) {
        sb.insert(0, "\r\n\r\nh2. Deprecated\r\nThis rule is deprecated, use ");
        sb.append(" instead.");
      } else {
        sb.append("\r\n\r\nh2. Deprecated\r\nThis rule is deprecated, and will eventually be removed.");
      }

      sb.insert(0, fullDesc);

      fullDesc = sb.toString();
    }

    rule.setFullDescription(fullDesc);
    if (fullDesc != null && fullDesc.length() > 0) {
      String[] markdownPieces = fullDesc.split(markdownH2Match);
      String[] htmlPieces = fullDesc.split(htmlH2);

      if (isMarkdown) {
        JiraHelper.handleMarkdown(rule, markdownPieces);
      } else {
        SonarQubeHelper.handleHtml(rule, htmlPieces);
      }
    }
  }

  public static void setRemediationFunction(Rule rule, String candidate) {
    if (! Strings.isNullOrEmpty(candidate)) {
      for (Rule.RemediationFunction fcn: Rule.RemediationFunction.values()) {
        if (fcn.name().equals(candidate) || fcn.getFunctionName().equals(candidate)) {
          rule.setSqaleRemediationFunction(fcn);
          return;
        }
      }
    }
  }
}
