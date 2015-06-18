/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.Rule;

public class SonarQubeHelper {

  private static final String HTML_H2 = "<h2>";


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

  protected static void setSqaleConstantValueFromSqInstance(Rule rule, String value) {

    rule.setSqaleConstantCostOrLinearThreshold(null);
    rule.setSqaleLinearOffset(null);

    Rule.RemediationFunction remFun = rule.getSqaleRemediationFunction();
    if (remFun == null) {
      return;
    }

    if (remFun == Rule.RemediationFunction.LINEAR_OFFSET) {
      rule.setSqaleLinearOffset(value);

    } else if (remFun == Rule.RemediationFunction.CONSTANT_ISSUE) {
      rule.setSqaleConstantCostOrLinearThreshold(value);

    }
  }
}
