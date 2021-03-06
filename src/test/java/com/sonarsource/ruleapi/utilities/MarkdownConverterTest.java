/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


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
    String markdown = "h2. Noncompliant Code Example\n" +
            "\n" +
            "{code}\n" +
            "if (file != null) {\n" +
            "  if (file.isFile() || file.isDirectory()) {\n" +
            "    ...\n" +
            "  }\n" +
            "}\n" +
            "{code}\n" +
            "\n" +
            "{code:title=PHP}\n" +
            "if (condition1) {\n" +
            "  if (condition2) {\n" +
            "    ...\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "{code}\n" +
            "{code:title=ABAP}\n" +
            "IF something.\n" +
            "  IF somethingElse.\n" +
            "    WRITE / 'hello'.\n" +
            "  ENDIF.\n" +
            "ENDIF.\n" +
            "{code}\n" +
            "\n" +
            "{code:title=PL/SQL}\n" +
            "IF something THEN\n" +
            "  IF something_else THEN\n" +
            "    -- ...\n" +
            "  END IF;\n" +
            "END IF;\n" +
            "{code}\n" +
            "\n" +
            "{code:title=VB6}\n" +
            "If a And b Then\n" +
            "  If c And d Then\n" +
            "    doSomething()\n" +
            "  End If\n" +
            "End If\n" +
            "{code}";

    String html = "\n<h2>Noncompliant Code Example</h2>\n" +
            "<pre>\n" +
            "IF something.\n" +
            "  IF somethingElse.\n" +
            "    WRITE / 'hello'.\n" +
            "  ENDIF.\n" +
            "ENDIF.\n" +
            "</pre>\n";

    assertThat(mc.transform(markdown, "ABAP")).isEqualTo(html);
  }

  @Test
  public void testHeading() throws Exception {
    String markdown = "h2. Noncompliant Code Example";
    String html = "\n<h2>Noncompliant Code Example</h2>";

    assertThat(mc.handleHeading(markdown)).isEqualTo(html);
  }

  @Test
  public void testEntitiess() throws Exception {
    String markdown = "<?php & § // Noncompliant; file comment missing";
    String html = "&lt;?php &amp; &sect; // Noncompliant; file comment missing";

    assertThat(mc.handleEntities(markdown)).isEqualTo(html);
  }

  @Test
  public void testTable() throws Exception {
    String markdown = "|a|b|c|";
    String html = "<table>\n<tr><td>a</td><td>b</td><td>c</td></tr>\n</table>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testTableWithEscapedPipeInHeader() throws Exception {
    String markdown = "||\\|a||\\|||c\\|||";
    String html = "<table>\n<tr><th>|a</th><th>|</th><th>c|</th></tr>\n</table>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testTableWithEscapedPipeInBody() throws Exception {
    String markdown = "|\\|a|\\||c\\||";
    String html = "<table>\n<tr><td>|a</td><td>|</td><td>c|</td></tr>\n</table>\n";

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
  public void testEscapedChar() throws Exception {
    String markdown = "|_*?+{}^~-[] &#92; \\|\\_\\*\\?\\+\\{\\}\\^\\~\\-\\[\\]";
    String html = "|_*?+{}^~-[] \\ |_*?+{}^~-[]";

    assertThat(mc.handleUnescapeChar(markdown)).isEqualTo(html);
  }

  @Test
  public void testBoldWithEscapedChar() throws Exception {
    String markdown = "*Start \\* End* other part";
    String html = "<strong>Start * End</strong> other part";

    assertThat(mc.handleUnescapeChar(mc.handleBold(markdown))).isEqualTo(html);
  }

  @Test
  public void testItalWithEscapedChar() throws Exception {
    String markdown = "_Start\\_ End_ other part";
    String html = "<em>Start_ End</em> other part";

    assertThat(mc.handleUnescapeChar(mc.handleItal(markdown))).isEqualTo(html);
  }

  @Test
  public void testStriketrhoughWithEscapedChar() throws Exception {
    String markdown = "-Start\\- End- other part";
    String html = "<del>Start- End</del> other part";

    assertThat(mc.handleUnescapeChar(mc.handleStriketrhough(markdown))).isEqualTo(html);
  }

  @Test
  public void testNotBold() {

    String markdown = "{{SELECT *}} should be avoided because it releases control of the returned columns and could therefore lead to errors and potentially to performance issues.";
    String html = "<p><code>SELECT *</code> should be avoided because it releases control of the returned columns and could therefore lead to errors and potentially to performance issues.</p>\n";

    assertThat(mc.transform(markdown, "")).isEqualTo(html);

    markdown = "* MISRA C++:2008, 16-2-4 - The ', \", /* or // characters shall not occur in a header file name.";
    html = "<ul>\n" +
            "<li> MISRA C++:2008, 16-2-4 - The ', \", /* or // characters shall not occur in a header file name.\n" +
            "</li></ul>\n";

    assertThat(mc.transform(markdown, "")).isEqualTo(html);

    markdown = "This excludes the use of {{//}} C99 style comments and C++ style comments, since these are not permitted in C90. Many compilers support the {{//}} style of comments as an extension to C90. The use of {{//}} in preprocessor directives (e.g. {{#define}}) can vary. Also the mixing of {{/* ... */}} and {{//}} is not consistent. This is more than a style issue, since different (pre C99) compilers may behave differently.";
    html = "<p>This excludes the use of <code>//</code> C99 style comments and C++ style comments, since these are not permitted in C90. Many compilers support the <code>//</code> style of comments as an extension to C90. The use of <code>//</code> in preprocessor directives (e.g. <code>#define</code>) can vary. Also the mixing of <code>/* ... */</code> and <code>//</code> is not consistent. This is more than a style issue, since different (pre C99) compilers may behave differently.</p>\n";

    assertThat(mc.transform(markdown, "")).isEqualTo(html);

  }

  @Test
  public void testItal() throws Exception {
    String markdown1 = "_Now_ is the time for _all_ good people to come to the _aid_ of their _country_";
    String html1 = "<em>Now</em> is the time for <em>all</em> good people to come to the <em>aid</em> of their <em>country</em>";

    String markdown2 = "_Now_ is the time.";
    String html2 = "<em>Now</em> is the time.";

    String regex = "^([A-Z0-9_]*|[a-z0-9_]*)$";

    assertThat(mc.handleItal(markdown1)).isEqualTo(html1);
    assertThat(mc.handleItal(markdown2)).isEqualTo(html2);
    assertThat(mc.handleItal(regex)).isEqualTo(regex);

    markdown1 = "A possible programming error in C++ is to apply the {{sizeof}} operator to an expression and expect the expression to be evaluated. However, the expression is not evaluated because {{sizeof}} only acts on the _type_ of the expression. To avoid this error, {{sizeof}} should not be used on expressions that would contain side effects if they were used elsewhere, since the side effects will not occur.";
    html1 = "<p>A possible programming error in C++ is to apply the <code>sizeof</code> operator to an expression and expect the expression to be evaluated. However, the expression is not evaluated because <code>sizeof</code> only acts on the <em>type</em> of the expression. To avoid this error, <code>sizeof</code> should not be used on expressions that would contain side effects if they were used elsewhere, since the side effects will not occur.</p>\n";

    assertThat(mc.transform(markdown1,"")).isEqualTo(html1);
  }

  @Test
  public void testNoItalInCode() {
    String markdown = "Because {{CX_ROOT}} is the _base_ exception type, catching it directly probably casts a wider net than you intended. Catching {{CX_ROOT}} could mask far more serious system errors that your {{CATCH}} logic was intended to deal with.\n" +
            "\n" +
            "Some smaller, more specific exception type should be caught instead.";
    String html = "<p>Because <code>CX_ROOT</code> is the <em>base</em> exception type, catching it directly probably casts a wider net than you intended. Catching <code>CX_ROOT</code> could mask far more serious system errors that your <code>CATCH</code> logic was intended to deal with.</p>\n" +
            "<p>Some smaller, more specific exception type should be caught instead.</p>\n";

    assertThat(mc.transform(markdown, "")).isEqualTo(html);

    markdown = "*C99*\n" +
            "{{inline}}, {{restrict}}, {{_Bool}}, {{_Complex}}, {{_Noreturn}}, {{_Static_assert}}, {{_Thread_local}}";
    html = "<p><strong>C99</strong></p>\n" +
            "<p><code>inline</code>, <code>restrict</code>, <code>_Bool</code>, <code>_Complex</code>, <code>_Noreturn</code>, <code>_Static_assert</code>, <code>_Thread_local</code></p>\n";

    assertThat(mc.transform(markdown,"")).isEqualTo(html);

  }

  @Test
  public void testUnbalancedCodeTags() {
    String markdown = "Because {{CX_ROOT is the _base_ exception type, catching it directly probably casts a wider net than you intended.";
    String html = "<p>Because <code>CX_ROOT is the <em>base</em> exception type, catching it directly probably casts a wider net than you intended.</p>\n";

    assertThat(mc.transform(markdown,"")).isEqualTo(html);

    markdown = "Because the _base_ exception type is CX_ROOT}}, catching it directly probably casts a wider net than you intended. Catching {{CX_ROOT}} could mask far more serious system errors that your {{CATCH}} logic was intended to deal with.\n" +
            "\n" +
            "Some smaller, more specific exception type should be caught instead.";
    html = "<p>Because the <em>base</em> exception type is CX_ROOT</code>, catching it directly probably casts a wider net than you intended. Catching <code>CX_ROOT</code> could mask far more serious system errors that your <code>CATCH</code> logic was intended to deal with.</p>\n" +
            "<p>Some smaller, more specific exception type should be caught instead.</p>\n";

    assertThat(mc.transform(markdown,"")).isEqualTo(html);

  }

  @Test
  public void testSimpleUl() throws Exception {
    String markdown = "* a\r\n* b\r\n* c\r\n";
    String html = "<ul>\n" +
            "<li> a\n" +
            "</li><li> b\n" +
            "</li><li> c\n" +
            "</li></ul>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testNestedUl() throws Exception {
    String markdown = "* a\r\n" +
                        "** a1\r\n" +
                        "** a1\r\n" +
                      "* b\r\n" +
                      "* c\r\n";

    String html = "<ul>\n" +
            "<li> a\n" +
            "<ul>\n" +
            "<li> a1\n" +
            "</li><li> a1\n" +
            "</li></ul>\n" +
            "</li><li> b\n" +
            "</li><li> c\n" +
            "</li></ul>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testDeeplyNestedList() {

    String markdown = "* a\r\n" +
                        "** b\r\n" +
                        "*** c\r\n" +
                      "* d\r\n" +
                      "blah";
    String html = "<ul>\n" +
            "<li> a\n" +
            "<ul>\n" +
            "<li> b\n" +
            "<ul>\n" +
            "<li> c\n" +
            "</li></ul>\n" +
            "</li></ul>\n" +
            "</li><li> d\n" +
            "</li></ul>\n" +
            "<p>blah</p>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testBadDeeplyNestedList() {

    String markdown = "* a\r\n" +
            "*** c\r\n" +
            "blah";
    String html = "<ul>\n" +
            "<li> a\n" +
            "<ul>\n" +
            "<li> c\n" +
            "</li></ul>\n" +
            "</li></ul>\n" +
            "<p>blah</p>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testSimpleOl() throws Exception {
    String markdown = "# a\r\n" +
                      "# b\r\n" +
                      "# c\r\n";
    String html = "<ol>\n" +
                    "<li> a\n" +
                    "</li><li> b\n" +
                    "</li><li> c\n" +
                    "</li></ol>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testNestedOl() throws Exception {
    String markdown = "# a\r\n" +
                        "## a1\r\n" +
                        "## a1\r\n" +
                      "# b\r\n" +
                      "# c\r\n";
    String html = "<ol>\n" +
            "<li> a\n" +
            "<ol>\n" +
            "<li> a1\n" +
            "</li><li> a1\n" +
            "</li></ol>\n" +
            "</li><li> b\n" +
            "</li><li> c\n" +
            "</li></ol>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testMixedList() throws Exception {
    String markdown = "# a\r\n#* a1\r\n#* a1\r\n# b\r\n# c\r\n";
    String html = "<ol>\n" +
            "<li> a\n" +
            "<ul>\n" +
            "<li> a1\n" +
            "</li><li> a1\n" +
            "</li></ul>\n" +
            "</li><li> b\n" +
            "</li><li> c\n" +
            "</li></ol>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }

  @Test
  public void testSequentialList() throws Exception {
    String markdown = "The following Javadoc elements are required:\r\n* Parameters, using <code>@param parameterName</code>.\r\n* Method return values, using <code>@return</code>.\r\n* Generic types, using <code>@param <T></code>.\r\n\r\nThe following public methods and constructors are not taken into account by this rule:\r\n* Getters and setters.\r\n* Methods with @Override annotation.\r\n* Empty constructors.\r\n* Static constants.\r\n";
    String html = "<p>The following Javadoc elements are required:</p>\n" +
            "<ul>\n" +
            "<li> Parameters, using <code>@param parameterName</code>.\n" +
            "</li><li> Method return values, using <code>@return</code>.\n" +
            "</li><li> Generic types, using <code>@param &lt;T&gt;</code>.\n" +
            "</li></ul>\n" +
            "<p>The following public methods and constructors are not taken into account by this rule:</p>\n" +
            "<ul>\n" +
            "<li> Getters and setters.\n" +
            "</li><li> Methods with @Override annotation.\n" +
            "</li><li> Empty constructors.\n" +
            "</li><li> Static constants.\n" +
            "</li></ul>\n";

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
  public void testQuoteTag() {
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

  @Test
  public void testNoGapAfterList() {

    String markdown = "h2. Exceptions\n" +
            "The following cases are ignored\n" +
            "* overriding methods.\n" +
            "* all methods in classes implementing one or more interfaces.\n" +
            "* methods which are empty or where the body consists of a single comment or a single {{throw}} statement (i.e. where the intention is apparently to simulate an abstract class).\n" +
            "\n" +
            "\n" +
            "{code:title=Flex}\n" +
            "override function doSomething(a:int):void {    // ignored\n" +
            "  compute(a);\n" +
            "}\n" +
            "\n" +
            "...\n" +
            "\n" +
            "class AbstractSomething {\n" +
            "  public function doSomething(a:int) {  // ignored\n" +
            "    throw new IllegalOperationError(\"doSomething() is abstract\");\n" +
            "  }\n" +
            "\n" +
            "...\n" +
            "\n" +
            "interface I {\n" +
            "  function action(a:int, b:int);\n" +
            "}\n" +
            "\n" +
            "class C extends I {\n" +
            "  function action(a:int, b:int) { // ignored\n" +
            "    return doSomethignWith(a);\n" +
            "  }\n" +
            "}\n" +
            "{code}";
    String html = "\n<h2>Exceptions</h2>\n" +
            "<p>The following cases are ignored</p>\n" +
            "<ul>\n" +
            "<li> overriding methods.\n" +
            "</li><li> all methods in classes implementing one or more interfaces.\n" +
            "</li><li> methods which are empty or where the body consists of a single comment or a single <code>throw</code> statement (i.e. where the intention is apparently to simulate an abstract class).\n" +
            "</li></ul>\n" +
            "<pre>\n" +
            "override function doSomething(a:int):void {    // ignored\n" +
            "  compute(a);\n" +
            "}\n" +
            "\n" +
            "...\n" +
            "\n" +
            "class AbstractSomething {\n" +
            "  public function doSomething(a:int) {  // ignored\n" +
            "    throw new IllegalOperationError(\"doSomething() is abstract\");\n" +
            "  }\n" +
            "\n" +
            "...\n" +
            "\n" +
            "interface I {\n" +
            "  function action(a:int, b:int);\n" +
            "}\n" +
            "\n" +
            "class C extends I {\n" +
            "  function action(a:int, b:int) { // ignored\n" +
            "    return doSomethignWith(a);\n" +
            "  }\n" +
            "}\n" +
            "</pre>\n";

    assertThat(mc.transform(markdown, "Flex")).isEqualTo(html);
  }

  @Test
  public void testLanguageInQuotes() {
    String title = "{code:title=\"Java\"}";
    assertThat(mc.isCodeLanguageMatch(false, "Java", title)).isTrue();
  }

  @Test
  public void testLinebreakInTable() {

    String markdown = "When using Spring proxies, calling a method in the same class with an incompatible {{@Transactional}} requirement will result in runtime exceptions because Spring only \"sees\" the caller and makes no provisions for properly invoking the callee. \n" +
            "\n" +
            "Therefore, certain calls should never be made within the same class:\n" +
            "||From||To||\n" +
            "| non-{{@Transactional}} | {{@Transactional}} |\n" +
            "| {{@Transactional}} | {{@Transactional(propagation = Propagation.NEVER)}} |\n" +
            "| {{@Transactional(propagation = Propagation.MANDATORY)}}, \n" +
            "{{@Transactional(propagation = Propagation.NESTED)}}, \n" +
            "{{@Transactional(propagation = Propagation.REQUIRED)}} (the default), \n" +
            "{{@Transactional(propagation = Propagation.SUPPORTS)}} |  {{@Transactional(propagation = Propagation.REQUIRES_NEW)}}|\n" +
            "\n" +
            "h2. Noncompliant Code Example\n" +
            "{code}\n" +
            "\n" +
            "@Override\n" +
            "public void doTheThing() {\n" +
            "  // ...\n" +
            "  actuallyDoTheThing();  // Noncompliant\n" +
            "}\n" +
            "\n" +
            "@Override\n" +
            "@Transactional\n" +
            "public void actuallyDoTheThing() {\n" +
            "  // ...\n" +
            "}\n" +
            "{code}";

    String html = "<p>When using Spring proxies, calling a method in the same class with an incompatible <code>@Transactional</code> requirement will result in runtime exceptions because Spring only \"sees\" the caller and makes no provisions for properly invoking the callee. </p>\n" +
            "<p>Therefore, certain calls should never be made within the same class:</p>\n" +
            "<table>\n" +
            "<tr><th>From</th><th>To</th></tr>\n" +
            "<tr><td> non-<code>@Transactional</code> </td><td> <code>@Transactional</code> </td></tr>\n" +
            "<tr><td> <code>@Transactional</code> </td><td> <code>@Transactional(propagation = Propagation.NEVER)</code> </td></tr>\n" +
            "<tr><td> <code>@Transactional(propagation = Propagation.MANDATORY)</code>, <br/><code>@Transactional(propagation = Propagation.NESTED)</code>, <br/><code>@Transactional(propagation = Propagation.REQUIRED)</code> (the default), <br/><code>@Transactional(propagation = Propagation.SUPPORTS)</code> </td><td>  <code>@Transactional(propagation = Propagation.REQUIRES_NEW)</code></td></tr>\n" +
            "</table>\n" +
            "\n" +
            "<h2>Noncompliant Code Example</h2>\n" +
            "<pre>\n" +
            "\n" +
            "@Override\n" +
            "public void doTheThing() {\n" +
            "  // ...\n" +
            "  actuallyDoTheThing();  // Noncompliant\n" +
            "}\n" +
            "\n" +
            "@Override\n" +
            "@Transactional\n" +
            "public void actuallyDoTheThing() {\n" +
            "  // ...\n" +
            "}\n" +
            "</pre>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);

  }

  @Test
  public void testLinebreakInTableNotClosed() {

    String markdown = "When using Spring proxies, calling a method in the same class with an incompatible {{@Transactional}} requirement will result in runtime exceptions because Spring only \"sees\" the caller and makes no provisions for properly invoking the callee. \n" +
            "\n" +
            "Therefore, certain calls should never be made within the same class:\n" +
            "||From||To||\n" +
            "| non-{{@Transactional}} | {{@Transactional}} |\n" +
            "| {{@Transactional}} | {{@Transactional(propagation = Propagation.NEVER)}} |\n" +
            "| {{@Transactional(propagation = Propagation.MANDATORY)}}, \n" +
            "{{@Transactional(propagation = Propagation.NESTED)}}, \n" +
            "{{@Transactional(propagation = Propagation.REQUIRED)}} (the default)";
    String html = "<p>When using Spring proxies, calling a method in the same class with an incompatible <code>@Transactional</code> requirement will result in runtime exceptions because Spring only \"sees\" the caller and makes no provisions for properly invoking the callee. </p>\n" +
            "<p>Therefore, certain calls should never be made within the same class:</p>\n" +
            "<table>\n" +
            "<tr><th>From</th><th>To</th></tr>\n" +
            "<tr><td> non-<code>@Transactional</code> </td><td> <code>@Transactional</code> </td></tr>\n" +
            "<tr><td> <code>@Transactional</code> </td><td> <code>@Transactional(propagation = Propagation.NEVER)</code> </td></tr>\n" +
            "<tr><td> <code>@Transactional(propagation = Propagation.MANDATORY)</code>, <br/><code>@Transactional(propagation = Propagation.NESTED)</code>, <br/><code>@Transactional(propagation = Propagation.REQUIRED)</code> (the default)</td></tr>\n" +
            "</table>\n";
    assertThat(mc.transform(markdown, "Java")).isEqualTo(html);
  }


  @Test
  public void testMultipleUrlsOneLine() {

    String markdown = "Derived from FindSecBugs rules [Potential SQL/JPQL Injection (JPA)|http://h3xstream.github.io/find-sec-bugs/bugs.htm#SQL_INJECTION_JPA], [Potential SQL/JDOQL Injection (JDO)|http://h3xstream.github.io/find-sec-bugs/bugs.htm#SQL_INJECTION_JDO], [Potential SQL/HQL Injection (Hibernate)|http://h3xstream.github.io/find-sec-bugs/bugs.htm#SQL_INJECTION_HIBERNATE]";
    String html = "Derived from FindSecBugs rules <a href=\"http://h3xstream.github.io/find-sec-bugs/bugs.htm#SQL_INJECTION_JPA\">Potential SQL/JPQL Injection (JPA)</a>, <a href=\"http://h3xstream.github.io/find-sec-bugs/bugs.htm#SQL_INJECTION_JDO\">Potential SQL/JDOQL Injection (JDO)</a>, <a href=\"http://h3xstream.github.io/find-sec-bugs/bugs.htm#SQL_INJECTION_HIBERNATE\">Potential SQL/HQL Injection (Hibernate)</a>";

    assertThat(mc.handleHref(markdown)).isEqualTo(html);

  }

  @Test
  public void testUrlNoLinkText() {

    String markdown = "this is a test [http://blah.com]";
    String html = "this is a test <a href=\"http://blah.com\">http://blah.com</a>";

    assertThat(mc.handleHref(markdown)).isEqualTo(html);
  }


  @Test
  public void testRuleLinks() {
    String line1 = "* Rule S1656 - Implements a ES2015 check on {{=}}.";
    String line1Out1 = "* Rule {rule:squid:S1656} - Implements a ES2015 check on {{=}}.";
    String line1Out2 = "<ul>\n" +
            "<li> Rule {rule:squid:S1656} - Implements a ES2015 check on <code>=</code>.\n" +
            "</li></ul>\n";

    String line2 = "This is a test of the emergency broadcast system.";

    String line3 = "S123, S234, S345 blah de blah blah.";
    String line3Out = "{rule:squid:S123}, {rule:squid:S234}, {rule:squid:S345} blah de blah blah.";

    assertThat(mc.handleRuleLinks(line1, "Java")).isEqualTo(line1Out1);
    assertThat(mc.handleRuleLinks(line1, "")).isEqualTo(line1);
    assertThat(mc.transform(line1, "Java")).isEqualTo(line1Out2);

    assertThat(mc.handleRuleLinks(line2, "Java")).isEqualTo(line2);
    assertThat(mc.handleRuleLinks(line2, "")).isEqualTo(line2);

    assertThat(mc.handleRuleLinks(line3, "Java")).isEqualTo(line3Out);
    assertThat(mc.handleRuleLinks(line3, "")).isEqualTo(line3);

    line1Out1 = "* Rule {rule:javascript:S1656} - Implements a ES2015 check on {{=}}.";
    line1Out2 = "<ul>\n" +
            "<li> Rule {rule:javascript:S1656} - Implements a ES2015 check on <code>=</code>.\n" +
            "</li></ul>\n";

    assertThat(mc.handleRuleLinks(line1, "JavaScript")).isEqualTo(line1Out1);
    assertThat(mc.handleRuleLinks(line1, "")).isEqualTo(line1);
    assertThat(mc.transform(line1, "JavaScript")).isEqualTo(line1Out2);

  }

  @Test
  public void testHangingBlockquote() {

    String markdown = "Kennedy said\n" +
            "{quote}Now is the time for all good men to come to the aid of their country.";
    String expectedHtml = "<p>Kennedy said</p>\n" +
            "<blockquote><p>Now is the time for all good men to come to the aid of their country.</p>\n" +
            "</blockquote>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);
  }

  @Test
  public void testFindBefore() {
    String str = "this is a test";
    assertThat(Utilities.findBefore(str, 3, '*')).isEqualTo(-1);
  }

  @Test
  public void testAsterisksInCodeTags(){
    String markdown = "This *should be bold* and this should not: \"{{.*TODO.*}}\"";
    String expectedHtml = "<p>This <strong>should be bold</strong> and this should not: \"<code>.*TODO.*</code>\"</p>\n";
    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);

    markdown = " \"{{(?i).*TODO.*}}\" plus some *bold text.*";
    expectedHtml = "<p> \"<code>(?i).*TODO.*</code>\" plus some <strong>bold text.</strong></p>\n";
    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);

    markdown = "Some *bold* text before, some {{.*code.*}}, and some *bold* text after.";
    expectedHtml = "<p>Some <strong>bold</strong> text before, some <code>.*code.*</code>, and some <strong>bold</strong> text after.</p>\n";
    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);
  }

  @Test
  public void testIsIndicatorInsideCodeTags() {

    String line = "This is a line with no code";
    assertThat(mc.isIndicatorInsideCodeTags(line, 5)).isFalse();

  }

  @Test
  public void testCodePipesInTables() {

    String markdown = "| |{{+}}, {{-}}, {{*}}, {{/}}, {{%}}|{{<<}}, {{>>}}, {{>>>}}|{{&}}|{{^}}| {{|}} |\n" +
            "|{{+}}, {{-}}, {{*}}, {{/}}, {{%}}| |x|x|x|x|\n" +
            "|{{<<}}, {{>>}}, {{>>>}}|x| |x|x|x|\n" +
            "|{{&}}|x|x| |x|x|\n" +
            "|{{^}}|x|x|x| |x|\n" +
            "| {{|}} |x|x|x|x| |";
    String html = "<table>\n" +
            "<tr><td> </td><td><code>+</code>, <code>-</code>, <code>*</code>, <code>/</code>, <code>%</code></td><td><code>&lt;&lt;</code>, <code>&gt;&gt;</code>, <code>&gt;&gt;&gt;</code></td><td><code>&amp;</code></td><td><code>^</code></td><td> <code>|</code> </td></tr>\n" +
            "<tr><td><code>+</code>, <code>-</code>, <code>*</code>, <code>/</code>, <code>%</code></td><td> </td><td>x</td><td>x</td><td>x</td><td>x</td></tr>\n" +
            "<tr><td><code>&lt;&lt;</code>, <code>&gt;&gt;</code>, <code>&gt;&gt;&gt;</code></td><td>x</td><td> </td><td>x</td><td>x</td><td>x</td></tr>\n" +
            "<tr><td><code>&amp;</code></td><td>x</td><td>x</td><td> </td><td>x</td><td>x</td></tr>\n" +
            "<tr><td><code>^</code></td><td>x</td><td>x</td><td>x</td><td> </td><td>x</td></tr>\n" +
            "<tr><td> <code>|</code> </td><td>x</td><td>x</td><td>x</td><td>x</td><td> </td></tr>\n" +
            "</table>\n";

    assertThat(mc.transform(markdown, "")).isEqualTo(html);
  }

  @Test
  public void testTrickyAmpersands(){
    String markdown = "{code}" +
            "  localtime_r(&amp;unix_epoch_time, &local_time); // Compliant\n" +
            "  print_date_and_time(&local_time);\n" +
            "\n" +
            "  time(&amp;current_time);\n" +
            "\n" +
            "  localtime_r(&amp;current_time, &local_time); // Compliant\n" +
            "\n" +
            "  // As expected, this will print the current date and time, as expected\n" +
            "  print_date_and_time(&local_time);\n" +
            "\n" +
            "  return 0;\n" +
            "}\n" +
            "{code}";
    String expectedHtml = "<pre>" +
            "  localtime_r(&amp;unix_epoch_time, &amp;local_time); // Compliant\n" +
            "  print_date_and_time(&amp;local_time);\n" +
            "\n" +
            "  time(&amp;current_time);\n" +
            "\n" +
            "  localtime_r(&amp;current_time, &amp;local_time); // Compliant\n" +
            "\n" +
            "  // As expected, this will print the current date and time, as expected\n" +
            "  print_date_and_time(&amp;local_time);\n" +
            "\n" +
            "  return 0;\n" +
            "}\n" +
            "</pre>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);
  }

  @Test
  public void testNothingToDo() {

    String result = mc.transform("", "");
    assertThat(result).isEqualToIgnoringCase("");
  }

  @Test
  public void testCodeBlockInBlockquote() {

    String markdown = "According to the Flex documentation :\n" +
            "{quote}\n" +
            "In an ActionScript file, when you define component events or other aspects of a component that affect more than a single property, you add the metadata tag outside the class definition so that the metadata is bound to the entire class, as the following example shows:\n" +
            "\n" +
            "{code}\n" +
            "// Add the [Event] metadata tag outside of the class file. \n" +
            "[Event(name=\"enableChange\", type=\"flash.events.Event\")] \n" +
            "public class ModalText extends TextArea {\n" +
            "\n" +
            "    ...\n" +
            "\n" +
            "    // Define class properties/methods\n" +
            "    private var _enableTA:Boolean;\n" +
            "\n" +
            "    // Add the [Inspectable] metadata tag before the individual property. \n" +
            "    [Inspectable(defaultValue=\"false\")] \n" +
            "    public function set enableTA(val:Boolean):void {\n" +
            "        _enableTA = val;\n" +
            "        this.enabled = val;\n" +
            "    \n" +
            "        // Define event object, initialize it, then dispatch it. \n" +
            "        var eventObj:Event = new Event(\"enableChange\");\n" +
            "        dispatchEvent(eventObj);\n" +
            "    }\n" +
            "}\n" +
            "{code}\n" +
            "{quote}\n";

    String expectedHtml = "<p>According to the Flex documentation :</p>\n" +
            "<blockquote>\n" +
            "<p>In an ActionScript file, when you define component events or other aspects of a component that affect more than a single property, you add the metadata tag outside the class definition so that the metadata is bound to the entire class, as the following example shows:</p>\n" +
            "<pre>\n" +
            "// Add the [Event] metadata tag outside of the class file. \n" +
            "[Event(name=\"enableChange\", type=\"flash.events.Event\")] \n" +
            "public class ModalText extends TextArea {\n" +
            "\n" +
            "    ...\n" +
            "\n" +
            "    // Define class properties/methods\n" +
            "    private var _enableTA:Boolean;\n" +
            "\n" +
            "    // Add the [Inspectable] metadata tag before the individual property. \n" +
            "    [Inspectable(defaultValue=\"false\")] \n" +
            "    public function set enableTA(val:Boolean):void {\n" +
            "        _enableTA = val;\n" +
            "        this.enabled = val;\n" +
            "    \n" +
            "        // Define event object, initialize it, then dispatch it. \n" +
            "        var eventObj:Event = new Event(\"enableChange\");\n" +
            "        dispatchEvent(eventObj);\n" +
            "    }\n" +
            "}\n" +
            "</pre>\n" +
            "</blockquote>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);

  }

  @Test
  public void testStrikethrough() {

    String markdown = "{{file_uploads}} is an on-by-default PHP configuration that allows files to be uploaded to your " +
            "site. Since accepting -candy- files from strangers is inherently dangerous, this feature should be disabled " +
            "unless it is absolutely necessary for your site.";

    String expectedHtml = "<p><code>file_uploads</code> is an on-by-default PHP configuration that allows files to be " +
            "uploaded to your site. Since accepting <del>candy</del> files from strangers is inherently dangerous, this " +
            "feature should be disabled unless it is absolutely necessary for your site.</p>\n";

    assertThat(mc.transform(markdown, "PHP")).isEqualTo(expectedHtml);


    markdown = "blah -";
    expectedHtml = "<p>blah -</p>\n";

    assertThat(mc.transform(markdown, "PHP")).isEqualTo(expectedHtml);
  }

  @Test
  public void testNotStrikethrough() {

    String markdown = "Programmers *should not* comment- out code as it bloats programs and reduces readability.\n" +
            "Unused code should be deleted and -can be retrieved from source control history if required.\n" +
            "Random * asterisks *\n" +
            "\n" +
            "h2. See\n" +
            "* MISRA C:2004, 2.4 - Sections of code should not be \"commented out\".\n" +
            "* MISRA C++:2008, 2-7-2 - Sections of code shall not be \"commented out\" using C-style comments.\n" +
            "* MISRA C++:2008, 2-7-3 - Sections of code should not be \"commented out\" using C++ comments.\n" +
            "* MISRA C:2012, Dir. 4.4 - Sections of code should not be \"commented out\"";

    String expectedHtml = "<p>Programmers <strong>should not</strong> comment- out code as it bloats programs and reduces readability.</p>\n" +
            "<p>Unused code should be deleted and -can be retrieved from source control history if required.</p>\n" +
            "<p>Random * asterisks *</p>\n" +
            "\n" +
            "<h2>See</h2>\n" +
            "<ul>\n" +
            "<li> MISRA C:2004, 2.4 - Sections of code should not be \"commented out\".\n" +
            "</li><li> MISRA C++:2008, 2-7-2 - Sections of code shall not be \"commented out\" using C-style comments.\n" +
            "</li><li> MISRA C++:2008, 2-7-3 - Sections of code should not be \"commented out\" using C++ comments.\n" +
            "</li><li> MISRA C:2012, Dir. 4.4 - Sections of code should not be \"commented out\"\n" +
            "</li></ul>\n";

    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);
  }

  @Test
  public void hasCloseIndicator(){

    String line = "*Now ^is^ the time_";

    assertThat(mc.hasCloseIndicator(line, 0, '*')).isFalse();
    assertThat(mc.hasCloseIndicator(line, 18, '_')).isFalse();
    assertThat(mc.hasCloseIndicator(line, 5, '^')).isTrue();
  }

  @Test
  public void testCodeAsCodeValue(){
    String markdown =
            "||Element||Remediation Action||\n" +
            "|{{strike}}|use {{del}} or {{s}}|\n" +
            "|{{xmp}}|use {{pre}} or {{code}}, and escape \"<\" and \"&\" characters|";

    String expectedHtml = "<table>\n" +
            "<tr><th>Element</th><th>Remediation Action</th></tr>\n" +
            "<tr><td><code>strike</code></td><td>use <code>del</code> or <code>s</code></td></tr>\n" +
            "<tr><td><code>xmp</code></td><td>use <code>pre</code> or <code>code</code>, and escape \"&lt;\" and \"&amp;\" characters</td></tr>\n" +
            "</table>\n";

    assertThat(mc.transform(markdown, "Web")).isEqualTo(expectedHtml);
  }

  @Test
  public void testUtf8Characters(){
    String markdown = "&#123; &#125;";
    String expectedHtml = "<p>&#123; &#125;</p>\n";
    assertThat(mc.transform(markdown, "Web")).isEqualTo(expectedHtml);

    markdown = "{{&#123; &#125;}}";
    expectedHtml = "<p><code>&#123; &#125;</code></p>\n";
    assertThat(mc.transform(markdown, "Web")).isEqualTo(expectedHtml);

    markdown = "{code}\n&#123; &#125;\n{code}";
    expectedHtml = "<pre>\n&#123; &#125;\n</pre>\n"; // This is not how JIRA renders it. In JIRA & is escaped to &amp; inside code blocks
    assertThat(mc.transform(markdown, "Web")).isEqualTo(expectedHtml);
  }

  @Test
  public void shouldUnescapeBracesInCode() throws Exception {
    String markdown = "Something {{} }} something";
    String expectedHtml = "<p>Something <code>} </code> something</p>\n";
    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);

    markdown = "Something {{\\}}} something";
    expectedHtml = "<p>Something <code>}</code> something</p>\n";
    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);

    markdown = "Something {{\\!}} something";
    expectedHtml = "<p>Something <code>!</code> something</p>\n";
    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);

    markdown = "Something {{}}}}}}}}}} something";
    expectedHtml = "<p>Something <code>}}}}}}}}</code> something</p>\n";
    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);
  }

  @Test
  public void shouldUnescapeInlineCode() throws Exception {
    // Excerpt from RSPEC-2757
    String markdown = "The use of operators pairs ( {{=\\+}}, {{=\\-}} or {{=\\!}} ) where the reversed, " +
      "single operator was meant ({{+=}}, {{-=}} or {{\\!=}}) will compile and run, " +
      "but not produce the expected results.";
    String expectedHtml = "<p>The use of operators pairs ( <code>=+</code>, <code>=-</code> or <code>=!</code> ) where the reversed, " +
      "single operator was meant (<code>+=</code>, <code>-=</code> or <code>!=</code>) will compile and run, " +
      "but not produce the expected results.</p>\n";
    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);
  }

  @Test
  public void shouldPreserveEscapedDotInRegexp() throws Exception {
    // Excerpt from RSPEC-2410 (Java subtask of RSPEC-120)
    String markdown = "With the default regular expression {{^[a-z]+(\\.[a-z][a-z0-9]*)*$}}:";
    String expectedHtml = "<p>With the default regular expression <code>^[a-z]+(\\.[a-z][a-z0-9]*)*$</code>:</p>\n";
    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);
  }

  @Test
  public void shouldPreserveOctalEscape() throws Exception {
    // Excerpt from RSPEC-1314
    String markdown = "Octal constants (other than zero) and octal escape sequences (other than \"\\0\") shall not be used";
    String expectedHtml = "<p>Octal constants (other than zero) and octal escape sequences (other than \"\\0\") shall not be used</p>\n";
    assertThat(mc.transform(markdown, "Java")).isEqualTo(expectedHtml);
  }
}
