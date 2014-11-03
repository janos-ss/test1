/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.File;

public class MarkdownConverterTest extends TestCase {

  private MarkdownConverter mc = new MarkdownConverter();


  public void testTransformNullMarkdown() throws Exception {
      Assert.assertEquals(null, mc.transform(null, "Java"));
  }

  public void testCode() throws Exception {
    String markdown = "{code}\r\nfor (int i = 0; i < 10; i++) {\r\n  // ...\r\n}\r\n{code}";
    String html = "<pre>\nfor (int i = 0; i &lt; 10; i++) {\n  // ...\n}\n</pre>\n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testUnclosedCode() throws Exception {
    String markdown = "{code}\r\nfor (int i = 0; i < 10; i++) {\r\n  // ...\r\n}\r\n";
    String html = "<pre>\nfor (int i = 0; i &lt; 10; i++) {\n  // ...\n}\n</pre>\n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testMultipleCodeSampleLanguages() throws Exception {
    java.net.URL url = this.getClass().getResource("/utilities");
    String markdown = new java.util.Scanner(new File(url.getPath() + "/MultipleCodeSampleLanguagesSource.txt"),"UTF8").useDelimiter("\\Z").next();
    String html =     new java.util.Scanner(new File(url.getPath() + "/MultipleCodeSampleLanguagesResult.txt"),"UTF8").useDelimiter("\\Z").next();

    Assert.assertEquals(html, mc.transform(markdown, "ABAP"));
  }

  public void testHeading() throws Exception {
    String markdown = "h2. Noncompliant Code Example";
    String html = "<h2>Noncompliant Code Example</h2>\n";

    Assert.assertEquals(html, mc.handleHeading(markdown));
  }

  public void testEntitiess() throws Exception {
    String markdown = "<?php & // Noncompliant; file comment missing";
    String html = "&lt;?php &amp; // Noncompliant; file comment missing";
    Assert.assertEquals(html, mc.handleEntities(markdown));
  }

  public void testTable() throws Exception {
    String markdown = "|a|b|c|";
    String html = "<table>\n<tr><td>a</td><td>b</td><td>c</td></tr>\n</table>\n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testTableWithHeader() throws Exception {
    String markdown = "||a||b||c||\r\n|a1|b1|c1|";
    String html = "<table>\n<tr><th>a</th><th>b</th><th>c</th></tr>\n<tr><td>a1</td><td>b1</td><td>c1</td></tr>\n</table>\n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testTableWithHeaderMultipleRows() throws Exception {
    String markdown = "||a||b||c||\r\n|a1|b1|c1|\r\n|a2|b2|c2|";
    String html = "<table>\n<tr><th>a</th><th>b</th><th>c</th></tr>\n<tr><td>a1</td><td>b1</td><td>c1</td></tr>\n<tr><td>a2</td><td>b2</td><td>c2</td></tr>\n</table>\n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testBold() throws Exception {
    String markdown = "*Now* is the time for *all* good people *to come to the aid of their country*";
    String html = "<strong>Now</strong> is the time for <strong>all</strong> good people <strong>to come to the aid of their country</strong>";

    Assert.assertEquals(html, mc.handleBold(markdown));
  }

  public void testItal() throws Exception {
    String markdown = "_Now_ is the time for _all_ good people to come to the _aid_ of their _country_";
    String html = "<em>Now</em> is the time for <em>all</em> good people to come to the <em>aid</em> of their <em>country</em>";

    Assert.assertEquals(html, mc.handleItal(markdown));
  }

  public void testSimpleUl() throws Exception {
    String markdown = "* a\r\n* b\r\n* c\r\n";
    String html = "<ul>\n<li> a</li>\n<li> b</li>\n<li> c</li>\n</ul>\n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testNestedUl() throws Exception {
    String markdown = "* a\r\n** a1\r\n** a1\r\n* b\r\n* c\r\n";
    String html = "<ul>\n<li> a</li>\n<ul>\n<li> a1</li>\n<li> a1</li>\n</ul>\n<li> b</li>\n<li> c</li>\n</ul>\n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testSimpleOl() throws Exception {
    String markdown = "# a\r\n# b\r\n# c\r\n";
    String html = "<ol>\n<li> a</li>\n<li> b</li>\n<li> c</li>\n</ol>\n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testNestedOl() throws Exception {
    String markdown = "# a\r\n## a1\r\n## a1\r\n# b\r\n# c\r\n";
    String html = "<ol>\n<li> a</li>\n<ol>\n<li> a1</li>\n<li> a1</li>\n</ol>\n<li> b</li>\n<li> c</li>\n</ol>\n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testMixedList() throws Exception {
    String markdown = "# a\r\n#* a1\r\n#* a1\r\n# b\r\n# c\r\n";
    String html = "<ol>\n<li> a</li>\n<ul>\n<li> a1</li>\n<li> a1</li>\n</ul>\n<li> b</li>\n<li> c</li>\n</ol>\n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testSequentialList() throws Exception {
    String markdown = "The following Javadoc elements are required:\r\n* Parameters, using <code>@param parameterName</code>.\r\n* Method return values, using <code>@return</code>.\r\n* Generic types, using <code>@param <T></code>.\r\n\r\nThe following public methods and constructors are not taken into account by this rule:\r\n* Getters and setters.\r\n* Methods with @Override annotation.\r\n* Empty constructors.\r\n* Static constants.\r\n";
    String html = "<p>The following Javadoc elements are required:</p>\n<ul>\n<li> Parameters, using <code>@param parameterName</code>.</li>\n<li> Method return values, using <code>@return</code>.</li>\n<li> Generic types, using <code>@param &lt;T&gt;</code>.</li>\n</ul>\n<p>The following public methods and constructors are not taken into account by this rule:</p>\n<ul>\n<li> Getters and setters.</li>\n<li> Methods with @Override annotation.</li>\n<li> Empty constructors.</li>\n<li> Static constants.</li>\n</ul>\n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testTt() throws Exception {
    String markdown = "This is {{code}}, isn't it?";
    String html = "This is <code>code</code>, isn't it?";

    Assert.assertEquals(html, mc.handleTt(markdown));
  }

  public void testHref() throws Exception {
    String markdown = "* [MITRE, CWE-459|http://cwe.mitre.org/data/definitions/459.html] - Incomplete [a|b] Cleanup";
    String html = "* <a href=\"http://cwe.mitre.org/data/definitions/459.html\">MITRE, CWE-459</a> - Incomplete [a|b] Cleanup";

    Assert.assertEquals(html, mc.handleHref(markdown));
  }

  public void testBq() throws Exception {
    String markdown = "bq. Now is the time for all good men to come to the aid of their country.";
    String html = "<blockquote>Now is the time for all good men to come to the aid of their country.</blockquote>";

    Assert.assertEquals(html, mc.handleBq(markdown));
  }
}
