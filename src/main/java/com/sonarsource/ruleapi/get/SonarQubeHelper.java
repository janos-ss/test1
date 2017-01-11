/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to translate SonarQube-specific JSON
 * into the fields of a Rule
 */
public class SonarQubeHelper {

  private static final String HTML_H2 = "<h2>";


  private SonarQubeHelper(){
    // this space intentionally left blank
  }


  protected static Rule populateFields(JSONObject jsonRule) {
    Rule rule = new Rule((String) jsonRule.get("langName"));

    rule.setType(Rule.Type.fromString((String) jsonRule.get("type")));

    String rawKey = ((String) jsonRule.get("key")).split(":")[1];
    rule.setKey(Utilities.normalizeKey(rawKey));
    rule.setRepo((String) jsonRule.get("repo"));

    rule.setLegacyKeys(new ArrayList<String>());
    rule.getLegacyKeys().add(rawKey);

    rule.setStatus(Rule.Status.valueOf((String) jsonRule.get("status")));
    rule.setSeverity(Rule.Severity.valueOf((String) jsonRule.get("severity")));

    rule.setTitle((String) jsonRule.get("name"));
    RuleMaker.setDescription(rule, (String) jsonRule.get("htmlDesc"), false);

    RuleMaker.setRemediationFunction(rule, (String) jsonRule.get("defaultDebtRemFnType"));
    setRemediationConstantValueFromSqInstance(rule, (String) jsonRule.get("defaultRemFnBaseEffort"));
    rule.setLinearFactor((String) jsonRule.get("defaultRemFnGapMultiplier"));
    rule.setLinearArgDesc((String) jsonRule.get("gapDescription"));

    rule.setTemplate((Boolean) jsonRule.get("isTemplate"));
    rule.setKeyOfTemplate((String) jsonRule.get("templateKey"));

    handleTags(jsonRule, rule);

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

  private static void handleTags(JSONObject jsonRule, Rule rule) {

    rule.setTags(new ArrayList<String>((JSONArray) jsonRule.get("sysTags")));
    if (rule.getType().equals(Rule.Type.BUG)) {
      rule.getTags().add("bug");
    } else if (rule.getType().equals(Rule.Type.VULNERABILITY)) {
      rule.getTags().add("security");
    }
  }


  static void handleHtml(Rule rule, String[] pieces) {

    rule.setDescription(pieces[0].replaceAll("&lt;", "<").replaceAll("&gt;", ">"));

    for (int i = 1; i < pieces.length; i++) {

      String piece = pieces[i];
      if (piece.contains("Noncompliant Code Example")) {
        rule.setNonCompliant(HTML_H2 + piece);

      } else if (piece.contains("Compliant Solution")) {
        rule.setCompliant(HTML_H2 + piece);

      } else if (piece.contains("Exceptions")) {
        rule.setExceptions(HTML_H2 + piece);

      } else if (piece.contains("See")) {
        rule.setReferences(HTML_H2 + piece);

      } else if (piece.contains("Deprecated")) {
        rule.setDeprecation(HTML_H2 + piece);
      }
    }
  }

  protected static void setRemediationConstantValueFromSqInstance(Rule rule, String value) {

    rule.setConstantCostOrLinearThreshold(null);
    rule.setLinearOffset(null);

    Rule.RemediationFunction remFun = rule.getRemediationFunction();
    if (remFun == null) {
      return;
    }

    if (remFun == Rule.RemediationFunction.LINEAR_OFFSET) {
      rule.setLinearOffset(value);

    } else if (remFun == Rule.RemediationFunction.CONSTANT_ISSUE) {
      rule.setConstantCostOrLinearThreshold(value);

    }
  }
}
