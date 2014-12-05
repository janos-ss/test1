/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import org.junit.Test;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;


public class MarkdownConverterTest {

  private MarkdownConverter mc = new MarkdownConverter();


  @Test
  public void testTransformNullMarkdown() throws Exception {
    assertThat(mc.transform(null, "Java")).isNull();
  }

  @Test
  public void testCode() throws Exception {
    String markdown = "{code}\r\nfor (int i = 0; i < 10; i++) {\r\n  // ...\r\n}\r\n{code}";
    String html = "<pre>\nfor (int i = 0; i &lt; 10; i++) {\n  // ...\n}\n</pre>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testUnclosedCode() throws Exception {
    String markdown = "{code}\r\nfor (int i = 0; i < 10; i++) {\r\n  // ...\r\n}\r\n";
    String html = "<pre>\nfor (int i = 0; i &lt; 10; i++) {\n  // ...\n}\n</pre>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testMultipleCodeSampleLanguages() throws Exception {
    java.net.URL url = this.getClass().getResource("/utilities");
    String markdown = new java.util.Scanner(new File(url.getPath() + "/MultipleCodeSampleLanguagesSource.txt"),"UTF8").useDelimiter("\\Z").next();
    String html =     new java.util.Scanner(new File(url.getPath() + "/MultipleCodeSampleLanguagesResult.txt"),"UTF8").useDelimiter("\\Z").next();

    assertThat(mc.transform(markdown, "ABAP")).isEqualTo(html);
  }

  @Test
  public void testHeading() throws Exception {
    String markdown = "h2. Noncompliant Code Example";
    String html = "<h2>Noncompliant Code Example</h2>\n";

    assertThat(mc.handleHeading(markdown)).isEqualTo(html);
  }

  @Test
  public void testEntitiess() throws Exception {
    String markdown = "<?php & // Noncompliant; file comment missing";
    String html = "&lt;?php &amp; // Noncompliant; file comment missing";

    assertThat(mc.handleEntities(markdown)).isEqualTo(html);
  }

  @Test
  public void testTable() throws Exception {
    String markdown = "|a|b|c|";
    String html = "<table>\n<tr><td>a</td><td>b</td><td>c</td></tr>\n</table>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testTableWithHeader() throws Exception {
    String markdown = "||a||b||c||\r\n|a1|b1|c1|";
    String html = "<table>\n<tr><th>a</th><th>b</th><th>c</th></tr>\n<tr><td>a1</td><td>b1</td><td>c1</td></tr>\n</table>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testTableWithHeaderTextAfter() throws Exception {
    String markdown = "||a||b||c||\r\n|a1|b1|c1|\r\nNow is the time";
    String html = "<table>\n<tr><th>a</th><th>b</th><th>c</th></tr>\n<tr><td>a1</td><td>b1</td><td>c1</td></tr>\n</table>\n<p>Now is the time</p>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }


  @Test
  public void testTableWithHeaderMultipleRows() throws Exception {
    String markdown = "||a||b||c||\r\n|a1|b1|c1|\r\n|a2|b2|c2|";
    String html = "<table>\n<tr><th>a</th><th>b</th><th>c</th></tr>\n<tr><td>a1</td><td>b1</td><td>c1</td></tr>\n<tr><td>a2</td><td>b2</td><td>c2</td></tr>\n</table>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testBold() throws Exception {
    String markdown = "*Now* is the time for *all* good people *to come to the aid of their country*";
    String html = "<strong>Now</strong> is the time for <strong>all</strong> good people <strong>to come to the aid of their country</strong>";

    assertThat(mc.handleBold(markdown)).isEqualTo(html);
  }

  @Test
  public void testNotBold() {

    String markdown = "{{SELECT *}} should be avoided because it releases control of the returned columns and could therefore lead to errors and potentially to performance issues.";
    String html = "<p><code>SELECT *</code> should be avoided because it releases control of the returned columns and could therefore lead to errors and potentially to performance issues.</p>\n";

    assertThat(mc.transform(markdown, "")).isEqualTo(html);
  }

  @Test
  public void testItal() throws Exception {
    String markdown = "_Now_ is the time for _all_ good people to come to the _aid_ of their _country_";
    String html = "<em>Now</em> is the time for <em>all</em> good people to come to the <em>aid</em> of their <em>country</em>";

    assertThat(mc.handleItal(markdown)).isEqualTo(html);
  }

  @Test
  public void testNoItalInCode() {
    String markdown = "Because {{CX_ROOT}} is the _base_ exception type, catching it directly probably casts a wider net than you intended. Catching {{CX_ROOT}} could mask far more serious system errors that your {{CATCH}} logic was intended to deal with.\n" +
            "\n" +
            "Some smaller, more specific exception type should be caught instead.";
    String html = "<p>Because <code>CX_ROOT</code> is the <em>base</em> exception type, catching it directly probably casts a wider net than you intended. Catching <code>CX_ROOT</code> could mask far more serious system errors that your <code>CATCH</code> logic was intended to deal with.</p>\n" +
            "<p>Some smaller, more specific exception type should be caught instead.</p>\n";

    assertThat(mc.transform(markdown,"")).isEqualTo(html);
  }

  @Test
  public void testSimpleUl() throws Exception {
    String markdown = "* a\r\n* b\r\n* c\r\n";
    String html = "<ul>\n<li> a</li>\n<li> b</li>\n<li> c</li>\n</ul>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testNestedUl() throws Exception {
    String markdown = "* a\r\n** a1\r\n** a1\r\n* b\r\n* c\r\n";
    String html = "<ul>\n<li> a</li>\n<ul>\n<li> a1</li>\n<li> a1</li>\n</ul>\n<li> b</li>\n<li> c</li>\n</ul>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testSimpleOl() throws Exception {
    String markdown = "# a\r\n# b\r\n# c\r\n";
    String html = "<ol>\n<li> a</li>\n<li> b</li>\n<li> c</li>\n</ol>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testNestedOl() throws Exception {
    String markdown = "# a\r\n## a1\r\n## a1\r\n# b\r\n# c\r\n";
    String html = "<ol>\n<li> a</li>\n<ol>\n<li> a1</li>\n<li> a1</li>\n</ol>\n<li> b</li>\n<li> c</li>\n</ol>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testMixedList() throws Exception {
    String markdown = "# a\r\n#* a1\r\n#* a1\r\n# b\r\n# c\r\n";
    String html = "<ol>\n<li> a</li>\n<ul>\n<li> a1</li>\n<li> a1</li>\n</ul>\n<li> b</li>\n<li> c</li>\n</ol>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testSequentialList() throws Exception {
    String markdown = "The following Javadoc elements are required:\r\n* Parameters, using <code>@param parameterName</code>.\r\n* Method return values, using <code>@return</code>.\r\n* Generic types, using <code>@param <T></code>.\r\n\r\nThe following public methods and constructors are not taken into account by this rule:\r\n* Getters and setters.\r\n* Methods with @Override annotation.\r\n* Empty constructors.\r\n* Static constants.\r\n";
    String html = "<p>The following Javadoc elements are required:</p>\n<ul>\n<li> Parameters, using <code>@param parameterName</code>.</li>\n<li> Method return values, using <code>@return</code>.</li>\n<li> Generic types, using <code>@param &lt;T&gt;</code>.</li>\n</ul>\n<p>The following public methods and constructors are not taken into account by this rule:</p>\n<ul>\n<li> Getters and setters.</li>\n<li> Methods with @Override annotation.</li>\n<li> Empty constructors.</li>\n<li> Static constants.</li>\n</ul>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testTt() throws Exception {
    String markdown = "This is {{code}}, isn't it?";
    String html = "This is <code>code</code>, isn't it?";

    assertThat(mc.handleDoubleCurly(markdown)).isEqualTo(html);
  }

  @Test
  public void testHref() throws Exception {
    String markdown = "* [MITRE, CWE-459|http://cwe.mitre.org/data/definitions/459.html] - Incomplete [a|b] Cleanup";
    String html = "* <a href=\"http://cwe.mitre.org/data/definitions/459.html\">MITRE, CWE-459</a> - Incomplete [a|b] Cleanup";

    assertThat(mc.handleHref(markdown)).isEqualTo(html);
  }

  @Test
  public void testHandleHrefNoSpace() {
    String markdown = "([more on Wikipedia|http://en.wikipedia.org/wiki/Reentrancy_(computing)]).";
    String html = "(<a href=\"http://en.wikipedia.org/wiki/Reentrancy_(computing)\">more on Wikipedia</a>).";
    assertThat(mc.handleHref(markdown)).isEqualTo(html);
  }

  @Test
  public void testHrefEndOfLine() {
    String markdown = "* [MITRE, CWE-459|http://cwe.mitre.org/data/definitions/459.html]";
    String html = "* <a href=\"http://cwe.mitre.org/data/definitions/459.html\">MITRE, CWE-459</a>";

    assertThat(mc.handleHref(markdown)).isEqualTo(html);
  }

  @Test
  public void testBq() throws Exception {
    String markdown = "bq. Now is the time for all good men to come to the aid of their country.";
    String html = "<blockquote>Now is the time for all good men to come to the aid of their country.</blockquote>";

    assertThat(mc.handleBq(markdown)).isEqualTo(html);
  }

  @Test
  public void testQuoteTag()
  {
    String markdown = "According to the SAP documentation:\n" +
            "{quote}\n" +
            "System functions are only intended for internal usage. Incompatible changes and further development is possible at any time and without warning or notice.\n" +
            "{quote}\n" +
            "\n" +
            "So calling system C functions using a {{CALL}} statement should be avoided.\n";
    String html = "<p>According to the SAP documentation:</p>\n" +
            "<blockquote>\n" +
            "<p>System functions are only intended for internal usage. Incompatible changes and further development is possible at any time and without warning or notice.</p>\n" +
            "</blockquote>\n" +
            "<p>So calling system C functions using a <code>CALL</code> statement should be avoided.</p>\n";

    assertThat(mc.transform(markdown, "")).isEqualTo(html);
  }

}
