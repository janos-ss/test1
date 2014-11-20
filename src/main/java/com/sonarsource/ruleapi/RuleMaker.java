/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.FetchException;
import com.sonarsource.ruleapi.utilities.IssueFetcher;
import com.sonarsource.ruleapi.utilities.MarkdownConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Given a key and a language, retrieves the relevant Issue
 * from Jira (if possible), and populates a new Rule with
 * the details. Rule population includes the retrieval
 * and overlay of values from any language-specific subtask,
 * as well as proper handling of labeled language-specific
 * variants such as {code} blocks and parameter variations.
 */
public class RuleMaker {

  private static final String MARKDOWN_H2_MATCH = "h2\\.";
  private static final String MARKDOWN_H2 = "h2.";
  private static final String HTML_H2 = "<h2>";
  private static final String VALUE = "value";
  private static final String FIELDS = "fields";


  private RuleMaker() {
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
  public static Rule getRuleByKey(String key, String language) throws FetchException {
    IssueFetcher fetcher = new IssueFetcher();

    Rule rule = new Rule(language);
    JSONObject jsonRule = fetcher.fetchIssueByKey(key);
    fleshOutRule(fetcher, rule, jsonRule);

    return rule;
  }

  /**
   * Retrieve a list of open rules based on a JQL snippet.
   * @param query the jql to use
   * @param language the language sought. Not used in the query but used to populate rule members
   * @return a list of retrieved rules
   */
  public static List<Rule> getRulesByJql(String query, String language) throws FetchException {
    List<Rule> rules = new ArrayList<Rule>();

    IssueFetcher fetcher = new IssueFetcher();
    List<JSONObject> issues = fetcher.fetchIssuesBySearch(query);

    for (JSONObject issue : issues) {
      Rule rule = new Rule(language);
      fleshOutRule(fetcher, rule, issue);
      rules.add(rule);
    }

    return rules;
  }

  private static void fleshOutRule(IssueFetcher fetcher, Rule rule, JSONObject jsonRule) throws FetchException {
    if (jsonRule != null) {
      populateFields(rule, jsonRule);

      JSONObject fields = (JSONObject)jsonRule.get(FIELDS);
      JSONArray subtasks = (JSONArray) fields.get("subtasks");

      JSONObject subIssue = getSubtask(fetcher, rule.getLanguage(), subtasks);
      if (subIssue != null) {

        Rule subRule = new Rule(rule.getLanguage());
        populateFields(subRule, subIssue);
        rule.merge(subRule);
      }
    }
  }

  protected static void populateFields(Rule rule, JSONObject issue) {
    rule.setKey(issue.get("key").toString());
    rule.setStatus(getJsonFieldValue(issue, "status"));

    String tmp = getCustomFieldValue(issue, "Default Severity");
    if (tmp != null) {
      rule.setSeverity(Rule.Severity.valueOf(tmp.toUpperCase()));
    }

    tmp = getCustomFieldValue(issue, "Activated by default");
    if (tmp != null) {
      rule.setDefaultActive("Yes".equals(tmp));
    }

    rule.setLegacyKeys(getCustomFieldValueAsList(issue, "Legacy Key"));

    rule.setTitle(getJsonFieldValue(issue, "summary"));
    rule.setMessage(getCustomFieldValue(issue, "Message"));

    setDescription(rule, getJsonFieldValue(issue, "description"));

    setSqale(rule, issue);

    rule.setTemplate("Yes".equals(getCustomFieldValue(issue, "Template Rule")));

    rule.setParameterList(handleParameterList(getCustomFieldValue(issue, "List of parameters"), rule.getLanguage()));

    rule.setTags(getListFromJsonFieldValue(issue, "labels"));

    setReferences(rule, issue);
  }

  protected static void setReferences(Rule rule, JSONObject issue) {

    rule.setFindbugs(getCustomFieldValueAsList(issue, "FindBugs"));
    rule.setPmd(getCustomFieldValueAsList(issue, "PMD"));
    rule.setCheckstyle(getCustomFieldValueAsList(issue, "Checkstyle"));
    rule.setMisraC04(getCustomFieldValueAsList(issue, "MISRA C 2004"));
    rule.setMisraC12(getCustomFieldValueAsList(issue, "MISRA C 2012"));
    rule.setMisraCpp(getCustomFieldValueAsList(issue, "MISRA C++ 2008"));
    rule.setFindSecBugs(getCustomFieldValueAsList(issue, "FindSecBugs"));
    rule.setCert(getCustomFieldValueAsList(issue, "CERT"));
    rule.setOwasp(getCustomFieldValueAsList(issue, "OWASP"));
    rule.setPhpFig(getCustomFieldValueAsList(issue, "PHP-FIG"));
    rule.setCwe(getCustomFieldValueAsList(issue, "CWE"));
  }

  protected static void setSqale(Rule rule, JSONObject issue) {

    rule.setSqaleCharac(getCustomFieldValue(issue, "SQALE Characteristic"));
    JSONObject sqaleCharMap = getJsonField(issue, "SQALE Characteristic");
    if (sqaleCharMap != null) {
      Object o = sqaleCharMap.get("child");
      if (o instanceof Map) {
        rule.setSqaleSubCharac((String)((Map<String, Object>) o).get(VALUE));
      }
    }

    rule.setSqaleRemediationFunction(getCustomFieldValue(issue, "SQALE Remediation Function"));
    rule.setSqaleConstantCostOrLinearThreshold(getCustomFieldValue(issue, "SQALE Constant Cost or Linear Threshold"));
    rule.setSqaleLinearArg(getCustomFieldValue(issue,"SQALE Linear Argument"));
    rule.setSqaleLinearFactor(getCustomFieldValue(issue,"SQALE Linear Factor"));
    rule.setSqaleLinearOffset(getCustomFieldValue(issue,"SQALE Linear Offset"));
  }

  private static JSONObject getSubtask(IssueFetcher fetcher, String language, JSONArray tasks) throws FetchException {
    if (tasks != null) {
      for (JSONObject subt : (Iterable<JSONObject>) tasks) {
        if (isLanguageMatch(language, getJsonFieldValue(subt, "summary").trim())) {
          return fetcher.fetchIssueByKey(subt.get("key").toString());
        }
      }
    }
    return null;
  }

  protected static boolean isLanguageMatch(String language, String candidate) {
    if (language.equals(candidate)) {
      return true;
    }
    if (!candidate.startsWith(language)) {
      return false;
    }
    return candidate.matches(language + "\\W.*");
  }

  protected static List<Parameter> handleParameterList(String paramString, String language) {
    List<Parameter> list = new ArrayList<Parameter>();
    if (paramString == null) {
      return list;
    }
    Parameter param = null;

    String[] lines = paramString.split("\r\n");
    for (String line : lines) {

      if (line.trim().length() > 0) {

        line = stripLeading(line, "**");
        line = stripLeading(line, "*");
        line = line.trim();
        String label = extractParamLcLabel(line);

        if (isParamLanguageMatch(label, language)) {
          if (label == null || label.startsWith("key")) {
            param = new Parameter();
            param.setKey(extractParamValue(line));
            list.add(param);
          } else {
            fillInParam(param, label, line);
          }
        }
      }
    }
    return list;
  }

  private static void fillInParam(Parameter param, String label, String line) {
    if (label.startsWith("default")) {
      param.setDefaultVal(extractParamValue(line));
    } else if (label.startsWith("description")) {
      param.setDescription(extractParamValue(line));
    } else if (label.startsWith("type")) {
      param.setType(extractParamValue(line));
    }
  }

  private static boolean isParamLanguageMatch(String label, String language) {
    if (label == null) {
      return true;
    }

    String[] words = label.split(" ");
    if (words.length == 1) {
      return true;
    }
    for (String word : words) {
      if (word.compareToIgnoreCase(language) == 0) {
        return true;
      }
    }
    return false;
  }

  private static String stripLeading(String line, String toStrip) {
    String target = line.trim();
    if (target.startsWith(toStrip)) {
      return target.substring(toStrip.length());
    }
    return target;
  }

  private static String extractParamValue(String line) {
    if (line.indexOf('=') > -1) {
      return line.substring(line.indexOf('=') + 1).trim();
    } else if (line.indexOf(':') > -1) {
      return line.substring(line.indexOf(':') + 1).trim();
    }
    return line;
  }

  private static String extractParamLcLabel(String line) {
    if (line.indexOf('=') > -1) {
      return tidyParamLabel(line.substring(0, line.indexOf('=')));
    } else if (line.indexOf(':') > -1) {
      return tidyParamLabel(line.substring(0, line.indexOf(':')));
    }
    return null;
  }

  protected static String tidyParamLabel(String paramLabel) {
    if (paramLabel != null) {
      return paramLabel.trim().toLowerCase().replace(" value", "");
    }
    return null;
  }

  protected static String getJsonFieldValue(JSONObject jobj, String key) {
    JSONObject fields = (JSONObject)jobj.get(FIELDS);
    if (fields != null && key != null) {
      Object obj = fields.get(key);
      if (obj instanceof String) {
        return (String) obj;
      }
      if (obj instanceof JSONObject) {
        JSONObject value = (JSONObject) obj;
        if (value.containsKey("name")) {
          return value.get("name").toString();
        }
        return value.get(VALUE).toString();
      }
    }
    return null;
  }

  protected static JSONObject getJsonField(JSONObject jobj, String key) {
    JSONObject fields = (JSONObject)jobj.get(FIELDS);

    if (fields != null && key != null) {
      Object obj = fields.get(key);
      if (obj == null) {
        obj = fields.get(getCustomFieldKey(jobj, key));
      }
      if (obj instanceof JSONObject) {
        return (JSONObject) obj;
      }
    }
    return null;
  }

  protected static List<String> getListFromJsonFieldValue(JSONObject jobj, String key) {

    List<String> list = new ArrayList<String>();

    JSONObject fields = (JSONObject) jobj.get(FIELDS);
    if (fields != null) {

      Object obj = fields.get(key);
      if (obj instanceof JSONArray) {
        JSONArray value = (JSONArray) obj;

        for (Object aValue : value) {
          list.add((String) aValue);
        }
      }
    }
    return list;
  }

  /**
   * Handles all the steps to retrieve the value of a custom field
   *
   * @param issue The Issue to interrogate
   * @param name The exact name of the custom field
   * @return The field value returned in the issue
   */
  protected static String getCustomFieldValue(JSONObject issue, String name) {

    return getJsonFieldValue(issue, getCustomFieldKey(issue, name));
  }

  protected static String getCustomFieldKey(JSONObject issue, String name)  {
    JSONObject names = (JSONObject)issue.get("names");

    if (name != null && names != null) {

      for (Map.Entry entry : (Iterable<Map.Entry>) names.entrySet()) {

        if (entry.getValue().equals(name)) {
          return (String) entry.getKey();
        }
      }
    }
    return null;
  }

  protected static List<String> getCustomFieldValueAsList(JSONObject issue, String name) {
    String str = getCustomFieldValue(issue, name);
    return stringToList(str);
  }

  protected static List<String> stringToList(String str) {
    List<String> list = new ArrayList<String>();
    if (str != null) {
      String tmp = str.replace('&', ',');
      String[] pieces = tmp.split(",");
      for (String piece : pieces) {
        String target = piece.trim();

        if (target.length() > 0) {
          list.add(target);
        }
      }
    }
    return list;
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
  public static void setDescription(Rule rule, String fullDescription) {

    rule.setFullDescription(fullDescription);
    if (fullDescription != null && fullDescription.length() > 0) {
      String[] markdownPieces = fullDescription.split(MARKDOWN_H2_MATCH);
      String[] htmlPieces = fullDescription.split(HTML_H2);

      if (markdownPieces.length > 1 || htmlPieces.length == 1) {
        handleMarkdown(rule, markdownPieces);
      } else {
        handleHtml(rule, htmlPieces);
      }
    }
  }

  private static void handleMarkdown(Rule rule, String[] pieces) {
    MarkdownConverter markdownConverter = new MarkdownConverter();

    rule.setDescription(markdownConverter.transform(pieces[0], rule.getLanguage()));

    for (int i = 1; i < pieces.length; i++) {

      String piece = pieces[i];
      if (piece.contains("Noncompliant Code Example")) {
        rule.setNonCompliant(markdownConverter.transform(MARKDOWN_H2 + piece, rule.getLanguage()));

      } else if (piece.contains("Compliant Solution")) {
        rule.setCompliant(markdownConverter.transform(MARKDOWN_H2 + piece, rule.getLanguage()));

      } else if (piece.contains("Exceptions")) {
        rule.setExceptions(markdownConverter.transform(MARKDOWN_H2 + piece, rule.getLanguage()));

      }  else if (piece.contains("See")) {
        rule.setReferences(markdownConverter.transform(MARKDOWN_H2 + piece, rule.getLanguage()));
      }
    }
  }

  private static void handleHtml(Rule rule, String[] pieces) {

    rule.setDescription(pieces[0]);
    for (String piece : pieces) {
      if (piece.contains("Noncompliant Code Example")) {
        rule.setNonCompliant(HTML_H2 + piece);

      } else if (piece.contains("Compliant Solution")) {
        rule.setCompliant(HTML_H2 + piece);

      }  else if (piece.contains("Exceptions")) {
        rule.setExceptions(HTML_H2 + piece);

      } else if (piece.contains("See")) {
        rule.setReferences(HTML_H2 + piece);
      }
    }
  }

}
