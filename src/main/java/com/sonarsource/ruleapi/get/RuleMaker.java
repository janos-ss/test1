/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.google.common.base.Strings;
import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Given a key and a language, retrieves the relevant Issue
 * from Jira (if possible), and populates a new Rule with
 * the details.
 *
 * Given a key, langauge and SQ instance URL, retrieves the
 * relevant Issue from the SonarQube instance.
 *
 * Rule population from Jira includes the retrieval
 * and overlay of values from any language-specific subtask,
 * as well as proper handling of labeled language-specific
 * variants such as {code} blocks and parameter variations.
 */
public class RuleMaker {

  private RuleMaker() {
  }

  public static List<Rule> getRulesFromSonarQubeForLanguage(Language language, String instance) {
    return RuleMaker.getRulesFromSonarQubeByQuery(instance, "repositories=" + language.getSq()+","+language.getSqCommon(),
            language.getSqProfileKey());
  }

  public static Rule getRuleFromSonarQubeByKey(String sonarQubeInstance, String ruleKey, Language language) {

    Fetcher fetcher = new Fetcher();

    String extendedKey = language.getSq() + ":" + ruleKey;

    JSONObject jsonRule = fetcher.fetchRuleFromSonarQube(sonarQubeInstance, extendedKey);
    return  populateFieldsFromSonarQube(jsonRule);
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
    Fetcher fetcher = new Fetcher();

    Rule rule = new Rule(language);
    JSONObject jsonRule = fetcher.fetchIssueByKey(key);
    fleshOutRule(fetcher, rule, jsonRule);

    return rule;
  }

  public static List<Rule> getRulesFromSonarQubeByQuery(String instance, String query, String sonarQubeDefaultProfileKey) {

    List<Rule> rules = new ArrayList<Rule>();

    Fetcher fetcher = new Fetcher();
    List<JSONObject> jsonRules = fetcher.fetchRulesFromSonarQube(instance, query);

    for (JSONObject jsonRule : jsonRules) {
      rules.add(populateFieldsFromSonarQube(jsonRule));
    }

    return rules;
  }

  /**
   * Retrieve a list of open rules based on a JQL snippet.
   * @param query the jql to use
   * @param language the language sought. Not used in the query but used to populate rule members
   * @return a list of retrieved rules
   */
  public static List<Rule> getRulesByJql(String query, String language) {
    List<Rule> rules = new ArrayList<Rule>();

    Fetcher fetcher = new Fetcher();
    List<JSONObject> issues = fetcher.fetchIssuesBySearch(query);

    for (JSONObject issue : issues) {
      Rule rule = new Rule(language);
      fleshOutRule(fetcher, rule, issue);
      rules.add(rule);
    }

    return rules;
  }

  protected static void fleshOutRule(Fetcher fetcher, Rule rule, JSONObject jsonRule) {
    if (jsonRule != null) {
      populateFields(rule, jsonRule);

      JSONObject fields = (JSONObject)jsonRule.get("fields");
      JSONArray subtasks = (JSONArray) fields.get("subtasks");

      JSONObject subIssue = getSubtask(fetcher, rule.getLanguage(), subtasks);
      if (subIssue != null) {

        Rule subRule = new Rule(rule.getLanguage());
        populateFields(subRule, subIssue);
        rule.merge(subRule);
      }
    }
  }

  protected static Rule populateFieldsFromSonarQube(JSONObject jsonRule) {
    Rule rule = new Rule((String) jsonRule.get("langName"));

    String rawKey = ((String) jsonRule.get("key")).split(":")[1];
    rule.setKey(Utilities.normalizeKey(rawKey));

    rule.setLegacyKeys(new ArrayList<String>());
    rule.getLegacyKeys().add(rawKey);

    rule.setStatus(Rule.Status.valueOf((String) jsonRule.get("status")));
    rule.setSeverity(Rule.Severity.valueOf((String) jsonRule.get("severity")));

    rule.setTitle((String) jsonRule.get("name"));
    setDescription(rule, (String) jsonRule.get("htmlDesc"), false);

    rule.setSqaleCharac((String) jsonRule.get("defaultDebtChar"));
    JiraHelper.setSubcharacteristic(rule, (String) jsonRule.get("defaultDebtSubChar"));
    JiraHelper.setRemediationFunction(rule, (String) jsonRule.get("defaultDebtRemFnType"));
    SonarQubeHelper.setSqaleConstantValueFromSqInstance(rule, (String) jsonRule.get("defaultDebtRemFnOffset"));
    rule.setSqaleLinearFactor((String) jsonRule.get("defaultDebtRemFnCoeff"));
    rule.setSqaleLinearArgDesc((String) jsonRule.get("effortToFixDescription"));

    rule.setTags(new ArrayList<String>((JSONArray) jsonRule.get("sysTags")));

    rule.setTemplate((Boolean) jsonRule.get("isTemplate"));

    JSONArray jsonParams = (JSONArray) jsonRule.get("params");
    for (JSONObject obj : (List<JSONObject>)jsonParams) {
      Parameter param = new Parameter();
      param.setKey((String) obj.get("key"));
      param.setDescription((String) obj.get("htmlDesc"));

      String tmp = (String) obj.get("defaultValue");
      if (tmp != null) {
        param.setDefaultVal(tmp);
      }
      param.setType((String) obj.get("type"));
      rule.getParameterList().add(param);
    }

    return rule;
  }

  protected static void populateFields(Rule rule, JSONObject issue) {
    rule.setKey(issue.get("key").toString());
    JiraHelper.setStatusFromLinks(rule, issue);

    String tmp = JiraHelper.getCustomFieldValue(issue, "Default Severity");
    if (tmp != null) {
      rule.setSeverity(Rule.Severity.valueOf(tmp.toUpperCase()));
    }

    JiraHelper.setDefaultProfiles(rule, issue);

    rule.setLegacyKeys(JiraHelper.getCustomFieldValueAsList(issue, "Legacy Key"));

    rule.setTargetedLanguages(JiraHelper.getCustomFieldStoredAsList(issue, "Targeted languages"));
    rule.setCoveredLanguages(JiraHelper.getCustomFieldStoredAsList(issue, "Covered Languages"));
    rule.setIrrelevantLanguages(JiraHelper.getCustomFieldStoredAsList(issue, "Irrelevant for Languages"));

    rule.setTitle(JiraHelper.getJsonFieldValue(issue, "summary"));
    rule.setMessage(JiraHelper.getCustomFieldValue(issue, "Message"));

    setDescription(rule, JiraHelper.getJsonFieldValue(issue, "description"), true);

    JiraHelper.setSqale(rule, issue);

    rule.setTemplate("Yes".equals(JiraHelper.getCustomFieldValue(issue, "Template Rule")));

    rule.setParameterList(JiraHelper.handleParameterList(JiraHelper.getCustomFieldValue(issue, "List of parameters"), rule.getLanguage()));

    rule.setTags(JiraHelper.getListFromJsonFieldValue(issue, "labels"));

    JiraHelper.setReferences(rule, issue);
  }

  private static JSONObject getSubtask(Fetcher fetcher, String language, JSONArray tasks) {
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

    if (Rule.Status.DEPRECATED.equals(rule.getStatus()) && ! rule.getDeprecationLinks().isEmpty()) {
      StringBuilder sb = new StringBuilder();

      for (String key : rule.getDeprecationLinks()) {
        if (sb.length() > 0) {
          sb.append(", ");
        }
        sb.append(Utilities.denormalizeKey(key));
      }

      sb.insert(0, "\r\n\r\nh2. Deprecated\r\nThis rule is deprecated, use ");
      sb.append(" instead.");

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

}
