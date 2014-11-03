/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuleMakerTest extends TestCase{

  private RuleMaker rm = new RuleMaker();
  private static final String json = "{\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10071\",\"value\":\"Reliability\",\"id\":\"10071\",\"child\":{\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10073\",\"value\":\"Data related reliability\",\"id\":\"10073\"}}";

  public void testIsLangaugeMatchEasyTrue() throws Exception {
    Assert.assertTrue(rm.isLanguageMatch("Java", "Java"));
  }

  public void testIsLanguageMatchEasyFalse() throws Exception {
    Assert.assertFalse(rm.isLanguageMatch("RPG", "Java"));
  }

  public void testIsLanguageMatchFalse() throws Exception {
    Assert.assertFalse(rm.isLanguageMatch("Java", "JavaScript"));
  }

  public void testIsLanguageMatchTrue() throws Exception {
    Assert.assertTrue(rm.isLanguageMatch("Java", "Java: ..."));
  }

  public void testHandleParameterListNullString() throws Exception {
    List<Parameter> empty = new ArrayList<Parameter>();
    Assert.assertEquals(empty, rm.handleParameterList(null, "Java"));
  }

  public void testHandleParameterList() throws Exception{
    String paramString = "* key: complexity_threshold\r\n* type = text\r\n** Description: The minimum complexity at which this rule will be triggered.\r\n** Default: 250";
    List<Parameter> paramList = rm.handleParameterList(paramString, "Java");
    Assert.assertTrue(paramList.size() == 1);
  }

  public void testHandleParameterListEmptyString() throws Exception {
    List<Parameter> paramList = rm.handleParameterList("", "Java");
    Assert.assertTrue(paramList.size() == 0);
  }

  public void testHandleParameterListMultilanguage() throws Exception {
    String paramString = "Key: format \r\nDescription: Regular expression used to check the names against. \r\nDefault Value for Java : ^[a-z][a-zA-Z0-9]*$ \r\nDefault Value for Flex : ^[_a-z][a-zA-Z0-9]*$";
    List<Parameter> paramList = rm.handleParameterList(paramString, "Java");
    Assert.assertEquals("^[a-z][a-zA-Z0-9]*$", paramList.get(0).getDefaultVal());

  }

  public void testHandleParameterListNoKeyLabel() throws Exception {
    String paramString = "* indentSize \r\n** Description: Number of white-spaces of an indent. If this property is not set, we just check that the code is indented. \r\n** Default value: none \r\n* tabWidth \r\n** Description: Equivalent number of spaces of a tabulation \r\n** Default value: 2\r\n";
    List<Parameter> paramList = rm.handleParameterList(paramString, "Java");
    Assert.assertTrue(paramList.size() == 2 && "indentSize".equals(paramList.get(0).getKey()));
  }

  public void testHandleParameterListUnknownLabel() throws Exception {
    String paramString = "Key: format \r\nDescription: Regular expression used to check the names against. \r\nDefault Value for Java : ^[a-z][a-zA-Z0-9]*$ \r\nDefault Value for Flex : ^[_a-z][a-zA-Z0-9]*$\r\ntpye:text";
    List<Parameter> paramList = rm.handleParameterList(paramString, "Java");
    Assert.assertNull(paramList.get(0).getType());
  }

  public void testTidyParamLabel() throws Exception {

    Assert.assertNull(rm.tidyParamLabel(null));
  }

  public void testPullValueFromJson() throws Exception {
    Assert.assertEquals("Reliability", rm.pullValueFromJson(json));
  }

  public void testPullChildValueFromJson() throws Exception {
    Map<String,Object> sqaleCharMap = rm.getMapFromJson(json);
    Object o = sqaleCharMap.get("child");

    Assert.assertEquals("Data related reliability", rm.getValueFromMap((Map<String, Object>) o));
  }

  public void testPullValueFromJsonNullString() throws Exception {
    Assert.assertNull(rm.pullValueFromJson(null));
  }

  public void testSetFullDescriptionNull() throws Exception {
    Rule rule = new Rule("Java");
    rm.setDescription(rule, null);
    Assert.assertEquals(null, rule.getFullDescription());
  }

  public void testSetMarkdownDescription() throws Exception {
    String markdown = "Even if all browsers are fault-tolerant, HTML tags should be closed to prevent any unexpected behavior.";
    String html = "<p>Even if all browsers are fault-tolerant, HTML tags should be closed to prevent any unexpected behavior.</p>\n";

    Rule rule = new Rule("HTML");
    rm.setDescription(rule, markdown);

    Assert.assertEquals(html, rule.getDescription());
  }

  public void testSetMarkdownNonCompliant() throws Exception {
    String markdown = "h2. Noncompliant Code Example\r\n{code}\r\n<html>\r\n  <head>\r\n    <title>Test Page    <!-- Noncompliant; title not closed -->\r\n  <!-- Noncompliant; head not closed -->\r\n  <body>\r\n    <em>Emphasized Text  <!-- Noncompliant; em not closed -->\r\n  <!-- Noncompliant; body not closed -->\r\n</html>\r\n{code}\r\n";
    String html = "<h2>Noncompliant Code Example</h2>\n\n<pre>\n&lt;html&gt;\n  &lt;head&gt;\n    &lt;title&gt;Test Page    &lt;!-- Noncompliant; title not closed --&gt;\n  &lt;!-- Noncompliant; head not closed --&gt;\n  &lt;body&gt;\n    &lt;em&gt;Emphasized Text  &lt;!-- Noncompliant; em not closed --&gt;\n  &lt;!-- Noncompliant; body not closed --&gt;\n&lt;/html&gt;\n</pre>\n";

    Rule rule = new Rule("HTML");
    rm.setDescription(rule, markdown);

    Assert.assertEquals(html, rule.getNonCompliant());
  }

  public void testSetMarkdownCompliant() throws Exception {
    String markdown = "h2. Compliant Solution\r\n{code}\r\n<html>\r\n  <head>\r\n    <title>Test Page</title>\r\n  </head>\r\n  <body>\r\n    <em>Emphasized Text</em>\r\n  </body>\r\n</html>\r\n{code}\r\n";
    String html = "<h2>Compliant Solution</h2>\n\n<pre>\n&lt;html&gt;\n  &lt;head&gt;\n    &lt;title&gt;Test Page&lt;/title&gt;\n  &lt;/head&gt;\n  &lt;body&gt;\n    &lt;em&gt;Emphasized Text&lt;/em&gt;\n  &lt;/body&gt;\n&lt;/html&gt;\n</pre>\n";

    Rule rule = new Rule("HTML");
    rm.setDescription(rule, markdown);

    Assert.assertEquals(html, rule.getCompliant());
  }

  public void testSetMarkdownExceptions() throws Exception {
    String markdown = "h2.Exceptions\r\n<code>InterruptedException</code>, <code>NumberFormatException</code>, <code>ParseException</code> and <code>MalformedURLException</code> exceptions are arguably used to indicate nonexceptional outcomes.\r\nBecause they are part of Java, developers have no choice but to deal with them. This rule does not verify that those particular exceptions are correctly handled.\r\n\r\n{code}\r\nint myInteger;\r\ntry {\r\n  myInteger = Integer.parseInt(myString);\r\n} catch (NumberFormatException e) {\r\n  // It is perfectly acceptable to not handle \"e\" here\r\n  myInteger = 0;\r\n}\r\n{code}\r\n\r\n";
    String html = "<h2>Exceptions</h2>\n\n<p><code>InterruptedException</code>, <code>NumberFormatException</code>, <code>ParseException</code> and <code>MalformedURLException</code> exceptions are arguably used to indicate nonexceptional outcomes.</p>\n<p>Because they are part of Java, developers have no choice but to deal with them. This rule does not verify that those particular exceptions are correctly handled.</p>\n<pre>\nint myInteger;\ntry {\n  myInteger = Integer.parseInt(myString);\n} catch (NumberFormatException e) {\n  // It is perfectly acceptable to not handle \"e\" here\n  myInteger = 0;\n}\n</pre>\n";

    Rule rule = new Rule("Java");
    rm.setDescription(rule, markdown);

    Assert.assertEquals(html, rule.getExceptions());
  }

  public void testSetMarkdownReferences() throws Exception {
    String markdown = "h2. See\r\n* MISRA C++:2008, 2-13-4 \r\n* MISRA C:2012, 7.3\r\n";
    String html = "<h2>See</h2>\n\n<ul>\n<li> MISRA C++:2008, 2-13-4 </li>\n<li> MISRA C:2012, 7.3</li>\n</ul>\n";

    Rule rule = new Rule("C");
    rm.setDescription(rule, markdown);

    Assert.assertEquals(html, rule.getReferences());
  }

  public void testSetMarkdownNonsense() throws Exception {
    String markdown = "Even if all browsers are fault-tolerant, HTML tags should be closed to prevent any unexpected behavior.\r\nh2. Nconmpliant Code Example\r\n{code}blah{code}";
    String html = "<p>Even if all browsers are fault-tolerant, HTML tags should be closed to prevent any unexpected behavior.</p>\n";

    Rule rule = new Rule("HTML");
    rm.setDescription(rule, markdown);

    Assert.assertEquals(html, rule.getHtmlDescription());
  }

  public void testSetHtmlDescription() throws Exception {
    java.net.URL url = this.getClass().getResource("/");
    String html = new java.util.Scanner(new File(url.getPath() + "/FullDescriptionHtml.html"),"UTF8").useDelimiter("\\Z").next();

    Rule rule = new Rule("Java");
    rm.setDescription(rule, html);
    Assert.assertEquals(html, rule.getHtmlDescription());
  }
}
