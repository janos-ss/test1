/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi;

import com.atlassian.jira.rest.client.domain.Field;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Subtask;
import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.IssueFetcher;
import com.sonarsource.ruleapi.utilities.MarkdownConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
  public static Rule getRuleByKey(String key, String language) {
    IssueFetcher fetcher = new IssueFetcher();

    Rule rule = new Rule(language);
    Issue issue = fetcher.fetchIssueByKey(key);
    fleshOutRule(fetcher, rule, issue);

    return rule;
  }

  /**
   * Retrieve a list of open rules based on a JQL snippet.
   * @param query the jql to use
   * @param language the language sought. Not used in the query but used to populate rule members
   * @return a list of retrieved rules
   */
  public static List<Rule> getRulesByJql(String query, String language) {
    List<Rule> rules = new ArrayList<Rule>();

    IssueFetcher fetcher = new IssueFetcher();
    List<Issue> issues = fetcher.fetchIssuesBySearch(query);

    Iterator<Issue> itr = issues.iterator();
    while (itr.hasNext()) {
      Rule rule = new Rule(language);
      Issue issue = itr.next();
      fleshOutRule(fetcher, rule, issue);
      rules.add(rule);
    }

    return rules;
  }

  private static void fleshOutRule(IssueFetcher fetcher, Rule rule, Issue issue) {
    if (issue != null) {
      populateFields(rule, issue);

      Issue subIssue = getSubtask(fetcher, rule.getLanguage(), issue.getSubtasks());
      if (subIssue != null) {

        Rule subRule = new Rule(rule.getLanguage());
        populateFields(subRule, subIssue);
        rule.merge(subRule);
      }
    }
  }

  private static void populateFields(Rule rule, Issue issue) {
    rule.setKey(issue.getKey());
    rule.setStatus(issue.getStatus().getName());

    String tmp = getCustomFieldValue(issue, "Default Severity");
    if (tmp != null) {
      rule.setSeverity(Rule.Severity.valueOf(pullValueFromJson(tmp).toUpperCase()));
    }
    rule.setDefaultActive("Yes".equals(pullValueFromJson(getFieldValue(issue,"Activated by default"))));

    tmp = getCustomFieldValue(issue, "Legacy Key");
    if (tmp != null) {
      rule.setLegacyKeys(tmp.split(","));
    }

    rule.setTitle(issue.getSummary());
    rule.setMessage(getFieldValue(issue,"Message"));

    setDescription(rule, issue.getDescription());

    Map<String,Object> sqaleCharMap = getMapFromJson(getCustomFieldValue(issue, "SQALE Characteristic"));
    if (sqaleCharMap != null) {
      rule.setSqaleCharac((String)sqaleCharMap.get("value"));
      Object o = sqaleCharMap.get("child");
      if (o instanceof Map) {
        rule.setSqaleSubCharac(getValueFromMap((Map<String, Object>) o));
      }
    }
    rule.setSqaleRemediationFunction(pullValueFromJson(getCustomFieldValue(issue, "SQALE Remediation Function")));
    rule.setSqaleConstantCostOrLinearThreshold(getCustomFieldValue(issue, "SQALE Constant Cost or Linear Threshold"));
    rule.setSqaleLinearArg(getCustomFieldValue(issue,"SQALE Linear Argument"));
    rule.setSqaleLinearFactor(getCustomFieldValue(issue,"SQALE Linear Factor"));
    rule.setSqaleLinearOffset(getCustomFieldValue(issue,"SQALE Linear Offset"));

    rule.setTemplate("Yes".equals(pullValueFromJson(getCustomFieldValue(issue, "Template Rule"))));

    rule.setParameterList(handleParameterList(getCustomFieldValue(issue, "List of parameters"), rule.getLanguage()));
    rule.setTags(issue.getLabels());

  }

  private static Issue getSubtask(IssueFetcher fetcher, String language, Iterable<Subtask> tasks) {
    if (tasks != null) {
      Iterator<Subtask> itr = tasks.iterator();
      while (itr.hasNext()) {
        Subtask subt = itr.next();
        if (isLanguageMatch(language, subt.getSummary().trim())) {
          return fetcher.fetchIssueByKey(subt.getIssueKey());
        }
      }
    }
    return null;
  }

  private static String getFieldValue(Issue issue, String fieldName) {
    if (issue != null && fieldName != null) {
      Field f = issue.getFieldByName(fieldName);
      if (f != null && f.getValue() != null) {
        return f.getValue().toString();
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
    if (candidate.matches(language + "\\W.*")) {
      // Java vs Javascript
      return true;
    }
    return false;
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
    for (int i = 0; i < words.length; i++) {
      if (words[i].compareToIgnoreCase(language) == 0) {
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

  /**
   * Custom field values are returned embedded in JSON strings.
   * This convenience method pulls the "value" component out.
   * @param json the JSON string
   * @return  the value of the "value" key
   */
  protected static String pullValueFromJson(String json) {
    if (json != null && json.length() > 0) {
      Map<String, Object> m = getMapFromJson(json);
      return getValueFromMap(m);
    }
    return null;
  }

  protected static String getValueFromMap(Map<String,Object> map) {
    if (map != null) {
      Object o = map.get("value");
      if (o instanceof String) {
        return (String) o;
      }
    }
    return null;
  }

  protected static Map<String,Object> getMapFromJson(String json) {
    if (json != null) {
      JSONParser jsonParser = new JSONParser();

      try {
        Object o = jsonParser.parse(json);
        if (o instanceof JSONArray) {
          o = ((JSONArray) o).get(0);
        }
        if (o instanceof Map) {
          return (Map<String, Object>) o;
        }
      } catch (ParseException e) {
        // nothing to see here
      }
    }
    return null;
  }

  /**
   * Custom fields presented in Jira as multi-selects will
   * return arrays. This method extracts the "value" from
   * each array item and returns them as a list of Strings.
   *
   * @param json The JSON to parse
   * @return the value of the "value" keys in each object.
   */
  protected static List<String> getValueListFromJson(String json) {
    if (json != null) {
      JSONParser jsonParser = new JSONParser();

      try {
        Object o = jsonParser.parse(json);
        if (o instanceof JSONArray) {
          List<String> list = new ArrayList<String>();
          JSONArray arr = (JSONArray) o;
          Iterator<JSONObject> itr = arr.iterator();
          while (itr.hasNext()) {
            JSONObject jsonObject = itr.next();
            list.add((String)jsonObject.get("value"));
          }
          return list;
        }
      } catch (ParseException e) {
        // nothing to see here
      }
    }
    return null;

  }

  /**
   * Handles all the steps to retrieve the value of a custom field
   *
   * @param issue The Issue to interrogate
   * @param name The exact name of the custom field
   * @return The field value returned in the issue
   */
  protected static String getCustomFieldValue(Issue issue, String name) {
    if (name != null) {
      Field f = issue.getFieldByName(name);
      if (f != null && f.getValue() != null) {
        return f.getValue().toString();
      }
    }
    return null;
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
      if (piece.indexOf("Noncompliant Code Example") > -1) {
        rule.setNonCompliant(markdownConverter.transform(MARKDOWN_H2 + piece, rule.getLanguage()));

      } else if (piece.indexOf("Compliant Solution") > -1) {
        rule.setCompliant(markdownConverter.transform(MARKDOWN_H2 + piece, rule.getLanguage()));

      } else if (piece.indexOf("Exceptions") > -1) {
        rule.setExceptions(markdownConverter.transform(MARKDOWN_H2 + piece, rule.getLanguage()));

      }  else if (piece.indexOf("See") > -1) {
        rule.setReferences(markdownConverter.transform(MARKDOWN_H2 + piece, rule.getLanguage()));
      }
    }
  }

  private static void handleHtml(Rule rule, String[] pieces) {

    rule.setDescription(pieces[0]);
    for (String piece : pieces) {
      if (piece.indexOf("Noncompliant Code Example") > -1) {
        rule.setNonCompliant(HTML_H2 + piece);

      } else if (piece.indexOf("Compliant Solution") > -1) {
        rule.setCompliant(HTML_H2 + piece);

      }  else if (piece.indexOf("Exceptions") > -1) {
        rule.setExceptions(HTML_H2 + piece);

      } else if (piece.indexOf("See") > -1) {
        rule.setReferences(HTML_H2 + piece);
      }
    }
  }

}
