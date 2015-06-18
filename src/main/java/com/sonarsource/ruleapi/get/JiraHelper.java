package com.sonarsource.ruleapi.get;

import com.google.common.base.Strings;
import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.MarkdownConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JiraHelper {

  private static final String MARKDOWN_H2 = "h2.";
  private static final String FIELDS = "fields";
  private static final String VALUE = "value";


  static void handleMarkdown(Rule rule, String[] pieces) {
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

      } else if (piece.contains("See")) {
        rule.setReferences(markdownConverter.transform(MARKDOWN_H2 + piece, rule.getLanguage()));

      } else if (piece.contains("Deprecated")) {
        rule.setDeprecation(markdownConverter.transform(MARKDOWN_H2 + piece, rule.getLanguage()));
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

    List<String> list = new ArrayList<String>();

    JSONObject fields = (JSONObject) issue.get(FIELDS);
    if (fields != null) {

      Object obj = fields.get(key);
      if (obj instanceof JSONArray) {
        List<JSONObject> value = (List<JSONObject>) obj;

        for (JSONObject aValue : value) {
          list.add((String) aValue.get("value"));
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

  protected static void setDefaultProfiles(Rule rule, JSONObject issue) {
    List<String> profileNames = getCustomFieldStoredAsList(issue, "Default Quality Profiles");
    for (String name : profileNames){
      Rule.Profile p = Rule.Profile.fromString(name);
      if (p != null) {
        rule.getDefaultProfiles().add(p);
      }
    }
  }

  protected static void setStatusFromLinks(Rule rule, JSONObject issue) {

    rule.setStatus(Rule.Status.READY);

    JSONArray links = getJsonArrayField(issue, "issuelinks");
    if (links != null) {
      for (Object obj : links) {
        JSONObject linkObj = (JSONObject) obj;

        JSONObject linkType = (JSONObject) linkObj.get("type");
        JSONObject inwardIssue = (JSONObject) linkObj.get("inwardIssue");

        if ("Deprecate".equals(linkType.get("name")) && inwardIssue != null) {
          rule.setStatus(Rule.Status.DEPRECATED);
          rule.getDeprecationLinks().add((String)inwardIssue.get("key"));
        }
      }
    }
  }

  protected static void setReferences(Rule rule, JSONObject issue) {

    rule.setCwe(getCustomFieldValueAsList(issue, "CWE"));
    rule.setCert(getCustomFieldValueAsList(issue, "CERT"));
    rule.setMisraC04(getCustomFieldValueAsList(issue, "MISRA C 2004"));
    rule.setMisraC12(getCustomFieldValueAsList(issue, "MISRA C 2012"));
    rule.setMisraCpp(getCustomFieldValueAsList(issue, "MISRA C++ 2008"));
    rule.setFindbugs(getCustomFieldValueAsList(issue, "FindBugs"));
    rule.setFbContrib(getCustomFieldValueAsList(issue, "fb-contrib"));
    rule.setFindSecBugs(getCustomFieldValueAsList(issue, "FindSecBugs"));
    rule.setOwasp(getCustomFieldValueAsList(issue, "OWASP"));
    rule.setPmd(getCustomFieldValueAsList(issue, "PMD"));
    rule.setCheckstyle(getCustomFieldValueAsList(issue, "Checkstyle"));
    rule.setPhpFig(getCustomFieldValueAsList(issue, "PHP-FIG"));
    rule.setResharper(getCustomFieldValueAsList(issue, "ReSharper"));
    rule.setCppCheck(getCustomFieldValueAsList(issue, "CPPCheck"));
    rule.setPylint(getCustomFieldValueAsList(issue, "Pylint"));
  }

  protected static void setSqale(Rule rule, JSONObject issue) {

    rule.setSqaleCharac(getCustomFieldValue(issue, "SQALE Characteristic"));
    JSONObject sqaleCharMap = getJsonField(issue, "SQALE Characteristic");
    if (sqaleCharMap != null) {
      Object o = sqaleCharMap.get("child");
      setSubcharacteristic(rule, (String) ((Map<String, Object>) o).get(VALUE));
    }

    setRemediationFunction(rule, getCustomFieldValue(issue, "SQALE Remediation Function"));
    rule.setSqaleConstantCostOrLinearThreshold(getCustomFieldValue(issue, "SQALE Constant Cost or Linear Threshold"));
    rule.setSqaleLinearArgDesc(getCustomFieldValue(issue, "SQALE Linear Argument Description"));
    rule.setSqaleLinearFactor(getCustomFieldValue(issue, "SQALE Linear Factor"));
    rule.setSqaleLinearOffset(getCustomFieldValue(issue, "SQALE Linear Offset"));
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

  public static void setSubcharacteristic(Rule rule, String candidate) {
    if (! Strings.isNullOrEmpty(candidate)) {
      for (Rule.Subcharacteristic subchar : Rule.Subcharacteristic.values()) {
        if (subchar.name().equals(candidate) || subchar.getRspecName().equals(candidate)) {
          rule.setSqaleSubCharac(subchar);
          return;
        }
      }
    }
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

  public static List<Parameter> handleParameterList(String paramString, String language) {
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
}
