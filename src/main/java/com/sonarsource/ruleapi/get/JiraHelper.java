/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Profile;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.MarkdownConverter;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Helper class to translate Jira-specific JSON
 * into the fields of a Rule
 */
public class JiraHelper {

  private static final Logger LOGGER = Logger.getAnonymousLogger();

  private static final String MARKDOWN_H2 = "h2.";
  private static final String FIELDS = "fields";
  private static final String VALUE = "value";


  private JiraHelper(){
    // this space intentionally left blank
  }


  public static void populateFields(Rule rule, JSONObject issue) {
    rule.setKey(issue.get("key").toString());
    setStatus(rule, issue);
    setReplacementLinks(rule, issue);

    String tmp = getCustomFieldValue(issue, "Default Severity");
    if (tmp != null) {
      rule.setSeverity(Rule.Severity.valueOf(tmp.toUpperCase()));
    }

    setDefaultProfiles(rule, issue);

    rule.setLegacyKeys(getCustomFieldValueAsList(issue, "Legacy Key"));

    rule.setTargetedLanguages(new HashSet<>(getCustomFieldStoredAsList(issue, "Targeted languages")));
    rule.setCoveredLanguages(new HashSet<>(getCustomFieldStoredAsList(issue, "Covered Languages")));
    rule.setIrrelevantLanguages(new HashSet<>(getCustomFieldStoredAsList(issue, "Irrelevant for Languages")));
    rule.setScope(new HashSet<>(getCustomFieldStoredAsList(issue, "Analysis Scope")));

    rule.setTitle(getJsonFieldValue(issue, "summary"));
    rule.setMessage(getCustomFieldValue(issue, "Message"));

    RuleMaker.setDescription(rule, getJsonFieldValue(issue, "description"), true);

    setRemediation(rule, issue);

    rule.setTemplate("Yes".equals(getCustomFieldValue(issue, "Template Rule")));

    rule.setParameterList(handleParameterList(getCustomFieldValue(issue, "List of parameters"), rule.getLanguage()));

    rule.setTags(getListFromJsonFieldValue(issue, "labels"));

    rule.setType(Rule.Type.fromString(getJsonFieldValue(issue, "issuetype")));


    setReferences(rule, issue);

    validateRuleDeprecation(rule, RuleMaker.getReplacingRules(rule));

    initializeLegacyKey(rule);
  }

  /**
   * Update status and deprecation notice based on whether or not
   * deprecating/superseding rules have actually been implemented yet.
   * If no replacements, leave status at Deprecated.
   *
   * Based on rule status and deprecationLinks, so those need to have
   * been set before this is invoked.
   *
   * @param replacingRules list of replacement rule links
   * @param rule
   */
  static void validateRuleDeprecation(Rule rule, List<Rule> replacingRules) {

    Language lang = Language.fromString(rule.getLanguage());
    if (lang == null) {
      return;
    }

    List<String> implementedReplacements = new ArrayList<>();

    if (Rule.Status.SUPERSEDED == rule.getStatus() || Rule.Status.DEPRECATED == rule.getStatus()) {

      if (! replacingRules.isEmpty()) {
        rule.setStatus(Rule.Status.READY);
      }

      for (Rule replacement : replacingRules) {
        if (replacement.getCoveredLanguages().contains(lang.getRspec())) {
          rule.setStatus(Rule.Status.DEPRECATED);
          implementedReplacements.add(replacement.getKey());
        }
      }
    }

    setDeprecationMessage(rule, implementedReplacements);
  }

  private static void initializeLegacyKey(Rule rule) {
    rule.setSqKey(Utilities.denormalizeKey(rule.getKey()));
    if (rule.getCoveredLanguages().size() == 1 && rule.getLegacyKeys().size() == 1) {
      rule.setSqKey(rule.getLegacyKeys().get(0));
    }
  }

  static void setDeprecationMessage(Rule rule, List<String> implementedReplacements){
    if (Rule.Status.DEPRECATED == rule.getStatus()) {
      StringBuilder sb = new StringBuilder();

      for (String key : implementedReplacements) {
        if (sb.length() > 0) {
          sb.append(", ");
        }
        sb.append(Utilities.denormalizeKey(key));
      }

      if (! implementedReplacements.isEmpty() ) {
        sb.insert(0, "h2. Deprecated\r\nThis rule is deprecated; use ");
        sb.append(" instead.");
      } else {
        sb.append("h2. Deprecated\r\nThis rule is deprecated, and will eventually be removed.");
      }

      MarkdownConverter markdownConverter = new MarkdownConverter();
      rule.setDeprecation(markdownConverter.transform(sb.toString(), rule.getLanguage()));
    }
  }

  static void handleMarkdown(Rule rule, String[] pieces) {
    MarkdownConverter markdownConverter = new MarkdownConverter();

    rule.setDescription(markdownConverter.transform(pieces[0], rule.getLanguage()));

    for (int i = 1; i < pieces.length; i++) {

      String piece = pieces[i];
      String pieceContent = markdownConverter.transform(MARKDOWN_H2 + piece, rule.getLanguage());
      if (piece.contains("Noncompliant Code Example")) {
        rule.setNonCompliant(pieceContent);

      } else if (piece.contains("Compliant Solution")) {
        rule.setCompliant(pieceContent);

      } else if (piece.contains("Exceptions")) {
        rule.setExceptions(pieceContent);

      } else if (piece.contains("See")) {
        rule.setReferences(pieceContent);

      } else if (piece.contains("Deprecated")) {
        rule.setDeprecation(pieceContent);

      } else {
        if (LOGGER.isLoggable(Level.WARNING)) {
          LOGGER.warning(String.format("Unknown section in rule %s: %s ", rule.getKey(), pieceContent));
        }
      }
    }
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

  protected static List<String> getCustomFieldValueAsList(JSONObject issue, String name) {
    String str = getCustomFieldValue(issue, name);
    return stringToList(str);
  }

  protected static List<String> getCustomFieldStoredAsList(JSONObject issue, String name) {
    String key = getCustomFieldKey(issue, name);

    List<String> list = new ArrayList<>();

    JSONObject fields = (JSONObject) issue.get(FIELDS);
    if (fields != null) {

      Object obj = fields.get(key);
      if (obj instanceof JSONArray) {
        List<JSONObject> value = (List<JSONObject>) obj;

        for (JSONObject aValue : value) {
          list.add((String) aValue.get(VALUE));
        }
      }
    }
    return list;
  }

  protected static String getJsonFieldValue(JSONObject jobj, String key) {
    JSONObject fields = (JSONObject)jobj.get(FIELDS);
    if (fields != null && key != null) {
      Object obj = fields.get(key);

      if (obj instanceof String) {
        return (String) obj;
      }

      if (obj instanceof JSONArray) {
        JSONArray arr = (JSONArray) obj;
        obj = arr.get(0);
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

  protected static JSONArray getJsonArrayField(JSONObject jobj, String key) {

    JSONObject fields = (JSONObject) jobj.get(FIELDS);

    if(fields != null && key != null) {
      Object obj = fields.get(key);
      if (obj == null) {
        obj = fields.get(getCustomFieldKey(jobj, key));
      }
      if (obj instanceof JSONArray) {
        return (JSONArray) obj;
      }
    }
    return null;
  }

  protected static void setDefaultProfiles(Rule rule, JSONObject issue) {
    List<String> profileNames = getCustomFieldStoredAsList(issue, "Default Quality Profiles");
    for (String name : profileNames) {
      rule.getDefaultProfiles().add(new Profile(name));
    }
  }

  protected static void setStatus(Rule rule, JSONObject issue) {

    rule.setStatus(Rule.Status.fromString(getJsonFieldValue(issue, "status")));
  }

  protected static void setReplacementLinks(Rule rule, JSONObject issue) {

    JSONArray links = getJsonArrayField(issue, "issuelinks");
    if (links != null) {
      for (Object obj : links) {
        JSONObject linkObj = (JSONObject) obj;

        JSONObject linkType = (JSONObject) linkObj.get("type");
        JSONObject inwardIssue = (JSONObject) linkObj.get("inwardIssue");

        if (inwardIssue != null &&
                ("Deprecate".equals(linkType.get("name")) || "Supercedes".equals(linkType.get("name")))) {
          rule.getReplacementLinks().add((String)inwardIssue.get("key"));
        }
      }
    }
  }

  protected static void setReferences(Rule rule, JSONObject issue) {

    rule.setCwe(getCustomFieldValueAsList(issue, "CWE"));
    rule.setCert(getCustomFieldValueAsList(issue, "CERT"));
    rule.setEsLint(getCustomFieldValueAsList(issue, "ESLint"));
    rule.setMisraC04(getCustomFieldValueAsList(issue, "MISRA C 2004"));
    rule.setMisraC12(getCustomFieldValueAsList(issue, "MISRA C 2012"));
    rule.setMisraCpp(getCustomFieldValueAsList(issue, "MISRA C++ 2008"));
    rule.setFindbugs(getCustomFieldValueAsList(issue, "FindBugs"));
    rule.setFbContrib(getCustomFieldValueAsList(issue, "fb-contrib"));
    rule.setFindSecBugs(getCustomFieldValueAsList(issue, "FindSecBugs"));
    rule.setOwasp(getCustomFieldValueAsList(issue, "OWASP"));
    rule.setSansTop25(getCustomFieldValueAsList(issue, "SANS Top 25"));
    rule.setPmd(getCustomFieldValueAsList(issue, "PMD"));
    rule.setCheckstyle(getCustomFieldValueAsList(issue, "Checkstyle"));
    rule.setPhpFig(getCustomFieldValueAsList(issue, "PHP-FIG"));
    rule.setResharper(getCustomFieldValueAsList(issue, "ReSharper"));
    rule.setCppCheck(getCustomFieldValueAsList(issue, "CPPCheck"));
    rule.setPylint(getCustomFieldValueAsList(issue, "Pylint"));
    rule.setFxCop(getCustomFieldValueAsList(issue, "FxCop"));
    rule.setPcLint(getCustomFieldValueAsList(issue, "PC-Lint"));
    rule.setMsftRoslyn(getCustomFieldValueAsList(issue, "MSFT Roslyn"));
    rule.setSwiftLint(getCustomFieldValueAsList(issue, "SwiftLint"));
  }

  protected static void setRemediation(Rule rule, JSONObject issue) {

    RuleMaker.setRemediationFunction(rule, getCustomFieldValue(issue, "Remediation Function"));
    rule.setConstantCostOrLinearThreshold(getCustomFieldValue(issue, "Constant Cost"));
    rule.setLinearArgDesc(getCustomFieldValue(issue, "Linear Argument Description"));
    rule.setLinearFactor(getCustomFieldValue(issue, "Linear Factor"));
    rule.setLinearOffset(getCustomFieldValue(issue, "Linear Offset"));
  }

  protected static List<String> stringToList(String str) {
    List<String> list = new ArrayList<>();
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

  protected static List<String> getListFromJsonFieldValue(JSONObject jobj, String key) {

    List<String> list = new ArrayList<>();

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

  public static List<Parameter> handleParameterList(String paramString, String language) {
    List<Parameter> list = new ArrayList<>();
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

        if (! isParamLanguageMatch(label, language)) {
          continue;
        }
        if (label == null || label.startsWith("key")) {
          param = new Parameter();
          param.setKey(extractParamValue(line));
          list.add(param);
        } else if (param != null) {
          fillInParam(param, label, line);
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
}
