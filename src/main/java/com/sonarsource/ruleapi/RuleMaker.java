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

/**
 * Created by ganncamp on 10/17/14.
 */
public class RuleMaker {

  private static final String MARKDOWN_H2 = "h2.";
  private static final String HTML_H2 = "<h2>";
  private MarkdownConverter markdownConverter = new MarkdownConverter();
  private IssueFetcher fetcher = new IssueFetcher();

  public RuleMaker() {
  }

  public Rule makeRule(String key, String language) {

    Rule rule = new Rule();
    Issue issue = fetcher.fetch(key);

    if (issue != null) {
      populateFields(rule, issue, language);

      if (issue.getSubtasks() != null && language != null && language.length() > 0) {
        Issue subIssue = getSubtask(language, issue.getSubtasks());

        // // TEST

        Rule subRule = new Rule();
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

    rule.setMessage(issue.getFieldByName("Message").getValue().toString());
    rule.setDefaultActive(Boolean.valueOf(issue.getFieldByName("Activated by default").getValue().toString()));

    String tmp = getCustomFieldValue(issue, "Legacy Key");
    if (tmp != null) {
      rule.setLegacyKeys(tmp.split(","));
    }

    rule.setSqaleCharac(pullValueFromJson(getCustomFieldValue(issue, "SQALE Characteristic")));
    rule.setSqaleRemediation(pullValueFromJson(getCustomFieldValue(issue, "SQALE Remediation Function")));
    rule.setSqaleCost(pullValueFromJson(getCustomFieldValue(issue, "SQALE Constant Cost or Linear Threshold")));
// more sqale fields...

    rule.setSeverity(pullValueFromJson(getCustomFieldValue(issue, "Default Severity")));

    rule.setParameterList(handleParameterList(getCustomFieldValue(issue, "List of parameters")));

  }

  private Issue getSubtask(String language, Iterable<Subtask> tasks) {
    Iterator<Subtask> itr = tasks.iterator();
    while (itr.hasNext()) {
      Subtask subt = itr.next();
      if (isLanguageMatch(language, subt.getSummary().trim()))
      {
        return fetcher.fetch(subt.getIssueKey());
      }
    }
    return null;
  }

  private boolean isLanguageMatch(String language, String candidate) {
    if (language.equals(candidate)) {
      return true;
    }
    if (!candidate.startsWith(language)) {
      return false;
    }
    if (candidate.matches(language + "\\W.*"))
    {
      // Java vs Javascript
      return true;
    }
    return false;
  }

  private List<Parameter> handleParameterList(String paramString) {
    List<Parameter> list = new ArrayList<Parameter>();
    Parameter param = null;
    if (paramString != null) {
      String[] lines = paramString.split("\r\n");
      for (String line : lines) {
        if (line.startsWith("key") || line.startsWith("Key")) {
          param = new Parameter();
          param.setKey(extractParamValue(line));
          list.add(param);
        } else if (line.startsWith("default") || line.startsWith("Default")) {
          param.setDefaultVal(extractParamValue(line));
        } else if (line.startsWith("description") || line.startsWith("Description")) {
          param.setDescription(extractParamValue(line));
        }
      }
    }
    return list;
  }

  private String extractParamValue(String line) {
    if (line.indexOf('=') > -1) {
      return line.substring(line.indexOf('=') + 1).trim();
    }
    else if (line.indexOf(':') > -1) {
      return line.substring(line.indexOf(':') + 1).trim();
    }
    return line;
  }

  private String pullValueFromJson(String json) {
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

      if (markdownPieces.length == 1 && htmlPieces.length == 1) {
        rule.setDescription(fullDescription);
      }
      if (markdownPieces.length > 1) {
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
