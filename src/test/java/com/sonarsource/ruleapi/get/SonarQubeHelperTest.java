/*
 * Copyright (C) 2014-2016 SonarSource SA
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

  private static final String SQ_JSON = "{\"key\":\"Web:ComplexityCheck\",\"repo\":\"Web\",\"name\":\"Files should not be too complex\",\"createdAt\":\"2013-06-19T07:34:52+0200\",\"htmlDesc\":\"<p>\\n  Checks cyclomatic complexity against a specified limit.\\n  The complexity is measured by counting decision tags (such as if and forEach) and boolean operators in expressions (&quot;&amp;&amp;&quot; and &quot;||&quot;),\\n  plus one for the body of the document. It is a measure of the minimum number of possible paths to render the page.\\n</p>\",\"mdDesc\":\"<p>\\n  Checks cyclomatic complexity against a specified limit.\\n  The complexity is measured by counting decision tags (such as if and forEach) and boolean operators in expressions (&quot;&amp;&amp;&quot; and &quot;||&quot;),\\n  plus one for the body of the document. It is a measure of the minimum number of possible paths to render the page.\\n</p>\",\"severity\":\"MAJOR\",\"status\":\"READY\",\"isTemplate\":false,\"tags\":[],\"sysTags\":[\"brain-overload\"],\"lang\":\"web\",\"langName\":\"Web\",\"params\":[{\"key\":\"max\",\"htmlDesc\":\"Maximum allowed complexity\",\"defaultValue\":\"10\",\"type\":\"INTEGER\"}],\"defaultDebtRemFnType\":\"LINEAR_OFFSET\",\"defaultDebtRemFnCoeff\":\"1min\",\"defaultDebtRemFnOffset\":\"30min\",\"effortToFixDescription\":\"per complexity point above the threshold\",\"debtOverloaded\":false,\"debtRemFnType\":\"LINEAR_OFFSET\",\"debtRemFnCoeff\":\"1min\",\"debtRemFnOffset\":\"30min\",\"defaultRemFnType\":\"LINEAR_OFFSET\",\"defaultRemFnGapMultiplier\":\"1min\",\"defaultRemFnBaseEffort\":\"30min\",\"remFnType\":\"LINEAR_OFFSET\",\"remFnGapMultiplier\":\"1min\",\"remFnBaseEffort\":\"30min\",\"remFnOverloaded\":false,\"gapDescription\":\"per complexity point above the threshold\",\"type\":\"CODE_SMELL\"}";

  private JSONParser parser = new JSONParser();

  @Test
  public void ruleType() throws Exception {

    String bugJson = "{\"key\":\"squid:S2111\",\"repo\":\"squid\",\"name\":\"\\\"BigDecimal(double)\\\" should not be used\",\"createdAt\":\"2015-01-08T13:39:42+0100\",\"htmlDesc\":\"<p>Because of floating point imprecision, you're unlikely to get the value you expect from the <code>BigDecimal(double)</code> constructor. </p>\\n<p>From <a href=\\\"http://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html#BigDecimal(double)\\\">the JavaDocs</a>:</p>\\n<blockquote>The results of this constructor can be somewhat unpredictable. One might assume that writing new BigDecimal(0.1) in Java creates a BigDecimal which is exactly equal to 0.1 (an unscaled value of 1, with a scale of 1), but it is actually equal to 0.1000000000000000055511151231257827021181583404541015625. This is because 0.1 cannot be represented exactly as a double (or, for that matter, as a binary fraction of any finite length). Thus, the value that is being passed in to the constructor is not exactly equal to 0.1, appearances notwithstanding.</blockquote>\\n<p>Instead, you should use <code>BigDecimal.valueOf</code>, which uses a string under the covers to eliminate floating point rounding errors.</p>\\n<h2>Noncompliant Code Example</h2>\\n\\n<pre>\\ndouble d = 1.1;\\n\\nBigDecimal bd1 = new BigDecimal(d); // Noncompliant; see comment above\\nBigDecimal bd2 = new BigDecimal(1.1); // Noncompliant; same result\\n</pre>\\n<h2>Compliant Solution</h2>\\n\\n<pre>\\ndouble d = 1.1;\\n\\nBigDecimal bd1 = BigDecimal.valueOf(d);\\nBigDecimal bd2 = BigDecimal.valueOf(1.1);\\n</pre>\\n<h2>See</h2>\\n\\n<ul>\\n<li> <a href=\\\"https://www.securecoding.cert.org/confluence/x/NQAVAg\\\">CERT, NUM10-J</a> - Do not construct BigDecimal objects from floating-point literals</li>\\n</ul>\",\"mdDesc\":\"<p>Because of floating point imprecision, you're unlikely to get the value you expect from the <code>BigDecimal(double)</code> constructor. </p>\\n<p>From <a href=\\\"http://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html#BigDecimal(double)\\\">the JavaDocs</a>:</p>\\n<blockquote>The results of this constructor can be somewhat unpredictable. One might assume that writing new BigDecimal(0.1) in Java creates a BigDecimal which is exactly equal to 0.1 (an unscaled value of 1, with a scale of 1), but it is actually equal to 0.1000000000000000055511151231257827021181583404541015625. This is because 0.1 cannot be represented exactly as a double (or, for that matter, as a binary fraction of any finite length). Thus, the value that is being passed in to the constructor is not exactly equal to 0.1, appearances notwithstanding.</blockquote>\\n<p>Instead, you should use <code>BigDecimal.valueOf</code>, which uses a string under the covers to eliminate floating point rounding errors.</p>\\n<h2>Noncompliant Code Example</h2>\\n\\n<pre>\\ndouble d = 1.1;\\n\\nBigDecimal bd1 = new BigDecimal(d); // Noncompliant; see comment above\\nBigDecimal bd2 = new BigDecimal(1.1); // Noncompliant; same result\\n</pre>\\n<h2>Compliant Solution</h2>\\n\\n<pre>\\ndouble d = 1.1;\\n\\nBigDecimal bd1 = BigDecimal.valueOf(d);\\nBigDecimal bd2 = BigDecimal.valueOf(1.1);\\n</pre>\\n<h2>See</h2>\\n\\n<ul>\\n<li> <a href=\\\"https://www.securecoding.cert.org/confluence/x/NQAVAg\\\">CERT, NUM10-J</a> - Do not construct BigDecimal objects from floating-point literals</li>\\n</ul>\",\"severity\":\"CRITICAL\",\"status\":\"READY\",\"internalKey\":\"S2111\",\"isTemplate\":false,\"tags\":[],\"sysTags\":[\"cert\"],\"lang\":\"java\",\"langName\":\"Java\",\"params\":[],\"defaultDebtRemFnType\":\"CONSTANT_ISSUE\",\"defaultDebtRemFnOffset\":\"5min\",\"debtOverloaded\":false,\"debtRemFnType\":\"CONSTANT_ISSUE\",\"debtRemFnOffset\":\"5min\",\"defaultRemFnType\":\"CONSTANT_ISSUE\",\"defaultRemFnBaseEffort\":\"5min\",\"remFnType\":\"CONSTANT_ISSUE\",\"remFnBaseEffort\":\"5min\",\"remFnOverloaded\":false,\"type\":\"BUG\"}";
    String vulnJson = "{\"key\":\"squid:S2250\",\"repo\":\"squid\",\"name\":\"\\\"ConcurrentLinkedQueue.size()\\\" should not be used\",\"createdAt\":\"2015-01-05T09:08:32+0100\",\"htmlDesc\":\"<p>For most collections the <code>size()</code> method requires constant time, but the time required to execute <code>ConcurrentLinkedQueue.size()</code> is directly proportional to the number of elements in the queue. When the queue is large, this could therefore be an expensive operation. Further, the results may be inaccurate if the queue is modified during execution.</p>\\n\\n<p>By the way, if the <code>size()</code> is used only to check that the collection is empty, then the <code>isEmpty()</code> method should be used.</p>\\n\\n<h2>Noncompliant Code Example</h2>\\n<pre>\\nConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();\\n//...\\nlog.info(\\\"Queue contains \\\" + queue.size() + \\\" elements\\\");\\n</pre>\",\"mdDesc\":\"<p>For most collections the <code>size()</code> method requires constant time, but the time required to execute <code>ConcurrentLinkedQueue.size()</code> is directly proportional to the number of elements in the queue. When the queue is large, this could therefore be an expensive operation. Further, the results may be inaccurate if the queue is modified during execution.</p>\\n\\n<p>By the way, if the <code>size()</code> is used only to check that the collection is empty, then the <code>isEmpty()</code> method should be used.</p>\\n\\n<h2>Noncompliant Code Example</h2>\\n<pre>\\nConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();\\n//...\\nlog.info(\\\"Queue contains \\\" + queue.size() + \\\" elements\\\");\\n</pre>\",\"severity\":\"CRITICAL\",\"status\":\"READY\",\"internalKey\":\"S2250\",\"isTemplate\":false,\"tags\":[],\"sysTags\":[\"performance\"],\"lang\":\"java\",\"langName\":\"Java\",\"params\":[],\"defaultDebtRemFnType\":\"CONSTANT_ISSUE\",\"defaultDebtRemFnOffset\":\"15min\",\"debtOverloaded\":false,\"debtRemFnType\":\"CONSTANT_ISSUE\",\"debtRemFnOffset\":\"15min\",\"defaultRemFnType\":\"CONSTANT_ISSUE\",\"defaultRemFnBaseEffort\":\"15min\",\"remFnType\":\"CONSTANT_ISSUE\",\"remFnBaseEffort\":\"15min\",\"remFnOverloaded\":false,\"type\":\"VULNERABILITY\"}";
    String smellJson = "{\"key\":\"squid:S1161\",\"repo\":\"squid\",\"name\":\"\\\"@Override\\\" annotation should be used on any method overriding (since Java 5) or implementing (since Java 6) another one\",\"createdAt\":\"2014-10-10T17:54:03+0200\",\"htmlDesc\":\"<p>Using the <code>@Override</code> annotation is useful for two reasons :</p>\\n<ul>\\n  <li>It elicits a warning from the compiler if the annotated method doesn't actually override anything, as in the case of a misspelling.</li>\\n  <li>It improves the readability of the source code by making it obvious that methods are overridden.</li>\\n</ul>\\n<h2>Noncompliant Code Example</h2>\\n<pre>\\nclass ParentClass {\\n  public boolean doSomething(){...}\\n}\\nclass FirstChildClass extends ParentClass {\\n  public boolean doSomething(){...}  // Noncompliant\\n}\\n</pre>\\n\\n<h2>Compliant Solution</h2>\\n<pre>\\nclass ParentClass {\\n  public boolean doSomething(){...}\\n}\\nclass FirstChildClass extends ParentClass {\\n  @Override\\n  public boolean doSomething(){...}  // Compliant\\n}\\n</pre>\",\"mdDesc\":\"<p>Using the <code>@Override</code> annotation is useful for two reasons :</p>\\n<ul>\\n  <li>It elicits a warning from the compiler if the annotated method doesn't actually override anything, as in the case of a misspelling.</li>\\n  <li>It improves the readability of the source code by making it obvious that methods are overridden.</li>\\n</ul>\\n<h2>Noncompliant Code Example</h2>\\n<pre>\\nclass ParentClass {\\n  public boolean doSomething(){...}\\n}\\nclass FirstChildClass extends ParentClass {\\n  public boolean doSomething(){...}  // Noncompliant\\n}\\n</pre>\\n\\n<h2>Compliant Solution</h2>\\n<pre>\\nclass ParentClass {\\n  public boolean doSomething(){...}\\n}\\nclass FirstChildClass extends ParentClass {\\n  @Override\\n  public boolean doSomething(){...}  // Compliant\\n}\\n</pre>\",\"severity\":\"MAJOR\",\"status\":\"READY\",\"internalKey\":\"S1161\",\"isTemplate\":false,\"tags\":[],\"sysTags\":[\"bad-practice\"],\"lang\":\"java\",\"langName\":\"Java\",\"params\":[],\"defaultDebtRemFnType\":\"CONSTANT_ISSUE\",\"defaultDebtRemFnOffset\":\"5min\",\"debtOverloaded\":false,\"debtRemFnType\":\"CONSTANT_ISSUE\",\"debtRemFnOffset\":\"5min\",\"defaultRemFnType\":\"CONSTANT_ISSUE\",\"defaultRemFnBaseEffort\":\"5min\",\"remFnType\":\"CONSTANT_ISSUE\",\"remFnBaseEffort\":\"5min\",\"remFnOverloaded\":false,\"type\":\"CODE_SMELL\"}";

    Rule rule;

    rule = SonarQubeHelper.populateFields((JSONObject) parser.parse(bugJson));
    assertThat(rule.getType()).isEqualTo(Rule.Type.BUG);
    assertThat(rule.getTags()).contains("bug");

    rule = SonarQubeHelper.populateFields((JSONObject) parser.parse(vulnJson));
    assertThat(rule.getType()).isEqualTo(Rule.Type.VULNERABILITY);
    assertThat(rule.getTags()).contains("security");

    rule = SonarQubeHelper.populateFields((JSONObject) parser.parse(smellJson));
    assertThat(rule.getType()).isEqualTo(Rule.Type.CODE_SMELL);
    assertThat(rule.getTags()).excludes("bug");
    assertThat(rule.getTags()).excludes("security");
  }

  @Test
  public void templateAndChild() throws Exception {
    String childOfTemplate = "{\"key\":\"squid:ArchitecturalConstraint_1394615069102\",\"repo\":\"squid\",\"name\":\"Classes from Web layer should not access DB classes\",\"createdAt\":\"2014-03-12T10:04:29+0100\",\"htmlDesc\":\"skdskldhslkdhsdsdsdsds\",\"mdDesc\":\"skdskldhslkdhsdsdsdsds\",\"severity\":\"MAJOR\",\"status\":\"DEPRECATED\",\"internalKey\":\"S1212\",\"isTemplate\":false,\"templateKey\":\"squid:ArchitecturalConstraint\",\"tags\":[],\"sysTags\":[],\"lang\":\"java\",\"langName\":\"Java\",\"params\":[{\"key\":\"fromClasses\",\"htmlDesc\":\"Optional. If this property is not defined, all classes should adhere to this constraint. Ex : *<strong>.web.</strong>*\",\"defaultValue\":\"**.web.**\",\"type\":\"STRING\"},{\"key\":\"toClasses\",\"htmlDesc\":\"Mandatory. Ex : java.util.Vector, java.util.Hashtable, java.util.Enumeration\",\"defaultValue\":\"**.db.**\",\"type\":\"STRING\"}],\"debtOverloaded\":false,\"remFnOverloaded\":false,\"type\":\"CODE_SMELL\"}";
    String template = "{\"key\":\"squid:ArchitecturalConstraint\",\"repo\":\"squid\",\"name\":\"Track breaches of architectural constraints\",\"createdAt\":\"2013-06-19T07:34:52+0200\",\"htmlDesc\":\"<p>A source code comply to an architectural model when it fully adheres to a set of architectural constraints. A constraint allows to deny references\\nbetween classes by pattern.</p>\\n<p>You can for instance use this rule to :</p>\\n<ul>\\n  <li> forbid access to <code>**.web.**</code> from <code>**.dao.**</code> classes </li>\\n  <li> forbid access to <code>java.util.Vector</code>, <code>java.util.Hashtable</code> and <code>java.util.Enumeration</code> from any classes </li>\\n  <li> forbid access to <code>java.sql.**</code> from <code>**.ui.**</code> and <code>**.web.**</code> classes </li>\\n</ul>\\n<h2>Deprecated</h2>\\n<p>This rule is deprecated, and will eventually be removed.</p>\",\"mdDesc\":\"<p>A source code comply to an architectural model when it fully adheres to a set of architectural constraints. A constraint allows to deny references\\nbetween classes by pattern.</p>\\n<p>You can for instance use this rule to :</p>\\n<ul>\\n  <li> forbid access to <code>**.web.**</code> from <code>**.dao.**</code> classes </li>\\n  <li> forbid access to <code>java.util.Vector</code>, <code>java.util.Hashtable</code> and <code>java.util.Enumeration</code> from any classes </li>\\n  <li> forbid access to <code>java.sql.**</code> from <code>**.ui.**</code> and <code>**.web.**</code> classes </li>\\n</ul>\\n<h2>Deprecated</h2>\\n<p>This rule is deprecated, and will eventually be removed.</p>\",\"severity\":\"MAJOR\",\"status\":\"DEPRECATED\",\"internalKey\":\"S1212\",\"isTemplate\":true,\"tags\":[],\"sysTags\":[],\"lang\":\"java\",\"langName\":\"Java\",\"params\":[{\"key\":\"fromClasses\",\"htmlDesc\":\"Optional. If this property is not defined, all classes should adhere to this constraint. Ex : *<strong>.web.</strong>*\",\"type\":\"STRING\"},{\"key\":\"toClasses\",\"htmlDesc\":\"Mandatory. Ex : java.util.Vector, java.util.Hashtable, java.util.Enumeration\",\"type\":\"STRING\"}],\"debtOverloaded\":false,\"remFnOverloaded\":false,\"type\":\"CODE_SMELL\"}";

    Rule rule;

    rule = SonarQubeHelper.populateFields((JSONObject) parser.parse(childOfTemplate));
    assertThat(rule.isTemplate()).isFalse();
    assertThat(rule.getKeyOfTemplate()).isNotEmpty();

    rule = SonarQubeHelper.populateFields((JSONObject) parser.parse(template));
    assertThat(rule.isTemplate()).isTrue();
    assertThat(rule.getKeyOfTemplate()).isNull();
  }

  @Test
  public void testPrivateConstructors() {
    final Constructor<?>[] constructors = SonarQubeHelper.class.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }
  }

  @Test
  public void testRemediationConstantValueFromSqInstance() {

    Rule rule = new Rule("");
    String cost = "5h";

    rule.setRemediationFunction(Rule.RemediationFunction.LINEAR);
    SonarQubeHelper.setRemediationConstantValueFromSqInstance(rule, cost);
    assertThat(rule.getConstantCostOrLinearThreshold()).isNull();
    assertThat(rule.getLinearOffset()).isNull();

    rule.setRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);
    SonarQubeHelper.setRemediationConstantValueFromSqInstance(rule, cost);
    assertThat(rule.getConstantCostOrLinearThreshold()).isEqualTo(cost);
    assertThat(rule.getLinearOffset()).isNull();

    rule.setRemediationFunction(Rule.RemediationFunction.LINEAR_OFFSET);
    SonarQubeHelper.setRemediationConstantValueFromSqInstance(rule, cost);
    assertThat(rule.getConstantCostOrLinearThreshold()).isNull();
    assertThat(rule.getLinearOffset()).isEqualTo(cost);

  }

  @Test
  public void testPopulateFieldsFromSonarQube() throws Exception {

    JSONObject jsonRule = (JSONObject) parser.parse(SQ_JSON);

    Rule rule = SonarQubeHelper.populateFields(jsonRule);

    assertThat(rule.getKey()).isEqualTo("ComplexityCheck");
    assertThat(rule.getParameterList()).hasSize(1);
    assertThat(rule.getDescription()).isNotEmpty();
    assertThat(rule.getTags()).hasSize(1);
  }

  @Test
  public void testPopulateFieldsFromSonarQube2() throws Exception {

    String json = "{\"key\":\"php:S1996\",\"repo\":\"php\",\"name\":\"Files should contain only one class or interface each\",\"createdAt\":\"2014-11-21T07:03:46+0000\",\"severity\":\"MAJOR\",\"status\":\"READY\",\"isTemplate\":false,\"tags\":[],\"sysTags\":[\"brain-overload\"],\"lang\":\"php\",\"langName\":\"PHP\",\"htmlDesc\":\"<p>\\n  A file that grows too much tends to aggregate too many responsibilities\\n  and inevitably becomes harder to understand and therefore to maintain. This is doubly true for a file with multiple independent classes and interfaces. It is strongly advised to divide the file into one independent class or interface per file.\\n</p>\",\"defaultDebtChar\":\"MAINTAINABILITY\",\"defaultDebtSubChar\":\"UNDERSTANDABILITY\",\"debtChar\":\"MAINTAINABILITY\",\"debtSubChar\":\"UNDERSTANDABILITY\",\"debtCharName\":\"Maintainability\",\"debtSubCharName\":\"Understandability\",\"defaultDebtRemFnType\":\"LINEAR\",\"defaultDebtRemFnCoeff\":\"10min\",\"debtOverloaded\":false,\"debtRemFnType\":\"LINEAR\",\"debtRemFnCoeff\":\"10min\",\"params\":[]}";

    JSONObject jsonRule = (JSONObject) parser.parse(json);

    Rule rule = SonarQubeHelper.populateFields(jsonRule);

    assertThat(rule.getKey()).isEqualTo("RSPEC-1996");
    assertThat(rule.getSeverity()).isEqualTo(Rule.Severity.MAJOR);
  }

}
