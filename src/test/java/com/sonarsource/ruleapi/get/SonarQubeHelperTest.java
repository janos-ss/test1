/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.Rule;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.fest.assertions.Assertions.assertThat;

public class SonarQubeHelperTest {

  private static final String SQ_JSON = "{\"key\":\"Web:ComplexityCheck\",\"repo\":\"Web\",\"name\":\"Files should not be too complex\",\"createdAt\":\"2013-06-19T05:34:52+0000\",\"severity\":\"MINOR\",\"status\":\"READY\",\"internalKey\":\"ComplexityCheck\",\"isTemplate\":false,\"tags\":[],\"sysTags\":[\"brain-overloaded\"],\"lang\":\"web\",\"langName\":\"Web\",\"htmlDesc\":\"<p>\\n  Checks cyclomatic complexity against a specified limit.\\n  The complexity is measured by counting decision tags (such as if and forEach) and boolean operators in expressions (\\\"&&\\\" and \\\"||\\\"), plus one for the body of the document.\\n  It is a measure of the minimum number of possible paths to render the page.\\n</p>\",\"debtOverloaded\":false,\"params\":[{\"key\":\"max\",\"htmlDesc\":\"Maximum allowed complexity\",\"type\":\"INTEGER\",\"defaultValue\":\"10\"}]}";

  private JSONParser parser = new JSONParser();


  @Test()
  public void testPrivateConstructors() {
    final Constructor<?>[] constructors = SonarQubeHelper.class.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }
  }

  @Test
  public void testSqaleConstantValueFromSqInstance() {

    Rule rule = new Rule("");
    String cost = "5h";

    rule.setSqaleRemediationFunction(Rule.RemediationFunction.LINEAR);
    SonarQubeHelper.setSqaleConstantValueFromSqInstance(rule, cost);
    assertThat(rule.getSqaleConstantCostOrLinearThreshold()).isNull();
    assertThat(rule.getSqaleLinearOffset()).isNull();

    rule.setSqaleRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);
    SonarQubeHelper.setSqaleConstantValueFromSqInstance(rule, cost);
    assertThat(rule.getSqaleConstantCostOrLinearThreshold()).isEqualTo(cost);
    assertThat(rule.getSqaleLinearOffset()).isNull();

    rule.setSqaleRemediationFunction(Rule.RemediationFunction.LINEAR_OFFSET);
    SonarQubeHelper.setSqaleConstantValueFromSqInstance(rule, cost);
    assertThat(rule.getSqaleConstantCostOrLinearThreshold()).isNull();
    assertThat(rule.getSqaleLinearOffset()).isEqualTo(cost);

  }

  @Test
  public void testPopulateFieldsFromSonarQube() {


    try {
      JSONObject jsonRule = (JSONObject) parser.parse(SQ_JSON);

      Rule rule = SonarQubeHelper.populateFields(jsonRule);

      assertThat(rule.getKey()).isEqualTo("ComplexityCheck");
      assertThat(rule.getParameterList()).hasSize(1);
      assertThat(rule.getDescription()).isNotEmpty();
      assertThat(rule.getTags()).hasSize(1);
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }


  @Test
  public void testPopulateFieldsFromSonarQube2() {

    String json = "{\"key\":\"php:S1996\",\"repo\":\"php\",\"name\":\"Files should contain only one class or interface each\",\"createdAt\":\"2014-11-21T07:03:46+0000\",\"severity\":\"MAJOR\",\"status\":\"READY\",\"isTemplate\":false,\"tags\":[],\"sysTags\":[\"brain-overload\"],\"lang\":\"php\",\"langName\":\"PHP\",\"htmlDesc\":\"<p>\\n  A file that grows too much tends to aggregate too many responsibilities\\n  and inevitably becomes harder to understand and therefore to maintain. This is doubly true for a file with multiple independent classes and interfaces. It is strongly advised to divide the file into one independent class or interface per file.\\n</p>\",\"defaultDebtChar\":\"MAINTAINABILITY\",\"defaultDebtSubChar\":\"UNDERSTANDABILITY\",\"debtChar\":\"MAINTAINABILITY\",\"debtSubChar\":\"UNDERSTANDABILITY\",\"debtCharName\":\"Maintainability\",\"debtSubCharName\":\"Understandability\",\"defaultDebtRemFnType\":\"LINEAR\",\"defaultDebtRemFnCoeff\":\"10min\",\"debtOverloaded\":false,\"debtRemFnType\":\"LINEAR\",\"debtRemFnCoeff\":\"10min\",\"params\":[]}";

    try {
      JSONObject jsonRule = (JSONObject) parser.parse(json);

      Rule rule = SonarQubeHelper.populateFields(jsonRule);

      assertThat(rule.getKey()).isEqualTo("RSPEC-1996");
      assertThat(rule.getSeverity()).isEqualTo(Rule.Severity.MAJOR);
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

}
