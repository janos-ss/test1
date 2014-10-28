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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RuleMaker {

  private static final String MARKDOWN_H2 = "h2.";
  private static final String HTML_H2 = "<h2>";
  private MarkdownConverter markdownConverter = new MarkdownConverter();
  private IssueFetcher fetcher = new IssueFetcher();

  public RuleMaker() {
  }

  public Rule makeRule(String key, String language) {

    Rule rule = new Rule(language);
    Issue issue = fetcher.fetch(key);

    if (issue != null) {
      populateFields(rule, issue, language);

      if (issue.getSubtasks() != null && language != null && language.length() > 0) {
        Issue subIssue = getSubtask(language, issue.getSubtasks());

        Rule subRule = new Rule(language);
        populateFields(subRule, subIssue, null);
        rule.merge(subRule);
      }
    }

    return rule;
  }

  private void populateFields(Rule rule, Issue issue, String language) {
    rule.setKey(issue.getKey());
    rule.setStatus(issue.getStatus());
    rule.setTitle(issue.getSummary());
    rule.setTags(issue.getLabels());

    setDescription(rule, issue.getDescription(), language);

    rule.setMessage(getFieldValue(issue,"Message"));
    rule.setDefaultActive(Boolean.valueOf(getFieldValue(issue,"Activated by default")));

    String tmp = getCustomFieldValue(issue, "Legacy Key");
    if (tmp != null) {
      rule.setLegacyKeys(tmp.split(","));
    }

    rule.setSqaleCharac(pullValueFromJson(getCustomFieldValue(issue, "SQALE Characteristic")));
    rule.setSqaleRemediation(pullValueFromJson(getCustomFieldValue(issue, "SQALE Remediation Function")));
    rule.setSqaleCost(pullValueFromJson(getCustomFieldValue(issue, "SQALE Constant Cost or Linear Threshold")));
// more sqale fields...

    rule.setSeverity(pullValueFromJson(getCustomFieldValue(issue, "Default Severity")));

    rule.setParameterList(handleParameterList(getCustomFieldValue(issue, "List of parameters"), language));

  }

  private Issue getSubtask(String language, Iterable<Subtask> tasks) {
    Iterator<Subtask> itr = tasks.iterator();
    while (itr.hasNext()) {
      Subtask subt = itr.next();
      if (isLanguageMatch(language, subt.getSummary().trim())) {
        return fetcher.fetch(subt.getIssueKey());
      }
    }
    return null;
  }

  private String getFieldValue(Issue issue, String fieldName) {
    if (issue != null && fieldName != null) {
      Field f = issue.getFieldByName(fieldName);
      if (f != null && f.getValue() != null) {
       return f.getValue().toString();
      }
    }
    return null;
  }

  protected boolean isLanguageMatch(String language, String candidate) {
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

  protected List<Parameter> handleParameterList(String paramString, String language) {
    List<Parameter> list = new ArrayList<Parameter>();
    Parameter param = null;
    if (paramString != null) {
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
            } else if (label.startsWith("default")) {
              param.setDefaultVal(extractParamValue(line));
            } else if (label.startsWith("description")) {
              param.setDescription(extractParamValue(line));
            } else if (label.startsWith("type")) {
              param.setType(extractParamValue(line));
            }
          }
        }
      }
    }
    return list;
  }

  private boolean isParamLanguageMatch(String label, String language) {
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

  private String stripLeading(String line, String toStrip) {
    String target = line.trim();
    if (target.startsWith(toStrip)) {
      return target.substring(toStrip.length());
    }
    return target;
  }

  private String extractParamValue(String line) {
    if (line.indexOf('=') > -1) {
      return line.substring(line.indexOf('=') + 1).trim();
    } else if (line.indexOf(':') > -1) {
      return line.substring(line.indexOf(':') + 1).trim();
    }
    return line;
  }

  private String extractParamLcLabel(String line) {
    if (line.indexOf('=') > -1) {
      return tidyParamLabel(line.substring(0, line.indexOf('=')));
    } else if (line.indexOf(':') > -1) {
      return tidyParamLabel(line.substring(0, line.indexOf(':')));
    }
    return null;
  }

  private String tidyParamLabel(String paramLabel) {
    if (paramLabel != null) {
      return paramLabel.trim().toLowerCase().replace(" value","");
    }
    return null;
  }

  protected String pullValueFromJson(String json) {
    if (json != null) {
      JSONParser jsonParser = new JSONParser();

      try {
        Object o = jsonParser.parse(json);
        if (o instanceof Map) {
          Map<String, String> m = (Map<String, String>) o;
          return m.get("value");
        }
      } catch (ParseException e) {
        // nothing to see here
      }
    }
    return null;
  }

  private String getCustomFieldValue(Issue issue, String name) {
    if (name != null) {
      Field f = issue.getFieldByName(name);
      if (f != null && f.getValue() != null) {
        return f.getValue().toString();
      }
    }
    return null;
  }

  private void setDescription(Rule rule, String fullDescription, String language) {

    rule.setFullDescription(fullDescription);
    if (fullDescription != null && fullDescription.length() > 0) {
      String[] markdownPieces = fullDescription.split(MARKDOWN_H2);
      String[] htmlPieces = fullDescription.split(HTML_H2);

      if (markdownPieces.length > 1 || htmlPieces.length == 1) {
        handleMarkdown(rule, markdownPieces, language);
      } else {
        handleHtml(rule, htmlPieces);
      }
    }
  }

  private void handleMarkdown(Rule rule, String[] pieces, String language) {

    rule.setDescription(markdownConverter.transform(pieces[0], language));

    for (int i = 1; i < pieces.length; i++) {

      String piece = pieces[i];
      if (piece.indexOf("Noncompliant Code Example") > -1) {
        rule.setNonCompliant(markdownConverter.transform(MARKDOWN_H2 + piece, language));

      } else if (piece.indexOf("Compliant Solution") > -1) {
        rule.setCompliant(markdownConverter.transform(MARKDOWN_H2 + piece, language));

      } else if (piece.indexOf("Exceptions") > -1) {
        rule.setExceptions(markdownConverter.transform(MARKDOWN_H2 + piece, language));

      }  else if (piece.indexOf("See") > -1) {
        rule.setReferences(markdownConverter.transform(MARKDOWN_H2 + piece, language));
      }
    }
  }

  private final void handleHtml(Rule rule, String[] pieces) {

    rule.setDescription(pieces[0]);
    for (String piece : pieces) {
      if (piece.indexOf("Noncompliant Code Example") > -1) {
        rule.setNonCompliant(HTML_H2 + piece);

      } else if (piece.indexOf("Compliant Solution") > -1) {
        rule.setNonCompliant(HTML_H2 + piece);

      }  else if (piece.indexOf("Exceptions") > -1) {
        rule.setExceptions(HTML_H2 + piece);

      } else if (piece.indexOf("See") > -1) {
        rule.setReferences(HTML_H2 + piece);
      }
    }
  }

}
