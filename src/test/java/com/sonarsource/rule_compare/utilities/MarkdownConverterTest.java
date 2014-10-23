/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.rule_compare.utilities;

import com.sonarsource.rule_compare.utilities.MarkdownConverter;
import junit.framework.Assert;
import junit.framework.TestCase;

public class MarkdownConverterTest extends TestCase {

  MarkdownConverter mc = new MarkdownConverter();

  public void testCode() throws Exception {
    String markdown = "{code}\r\nfor (int i = 0; i < 10; i++) {\r\n  // ...\r\n}\r\n{code}";
    String html = "<pre>%nfor (int i = 0; i < 10; i++) {%n  // ...%n}%n</pre>%n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testHeading() throws Exception {
    String markdown = "h2. Noncompliant Code Example";
    String html = "<h2>Noncompliant Code Example</h2>%n";

    Assert.assertEquals(html, mc.handleHeading(markdown));
  }

  public void testTable() throws Exception {
    String markdown = "|a|b|c|";
    String html = "<table>%n<tr><td>a</td><td>b</td><td>c</td></tr>%n</table>%n";

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
    String html = "<ul>%n<li> a</li>%n<li> b</li>%n<li> c</li>%n</ul>%n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testNestedUl() throws Exception {
    String markdown = "* a\r\n** a1\r\n** a1\r\n* b\r\n* c\r\n";
    String html = "<ul>%n<li> a</li>%n<ul>%n<li> a1</li>%n<li> a1</li>%n</ul>%n<li> b</li>%n<li> c</li>%n</ul>%n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testSimpleOl() throws Exception {
    String markdown = "# a\r\n# b\r\n# c\r\n";
    String html = "<ol>%n<li> a</li>%n<li> b</li>%n<li> c</li>%n</ol>%n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testNestedOl() throws Exception {
    String markdown = "# a\r\n## a1\r\n## a1\r\n# b\r\n# c\r\n";
    String html = "<ol>%n<li> a</li>%n<ol>%n<li> a1</li>%n<li> a1</li>%n</ol>%n<li> b</li>%n<li> c</li>%n</ol>%n";

    Assert.assertEquals(html, mc.transform(markdown, "Java"));
  }

  public void testMixedList() throws Exception {
    String markdown = "# a\r\n#* a1\r\n#* a1\r\n# b\r\n# c\r\n";
    String html = "<ol>%n<li> a</li>%n<ul>%n<li> a1</li>%n<li> a1</li>%n</ul>%n<li> b</li>%n<li> c</li>%n</ol>%n";

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
