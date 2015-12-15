/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class ComparisonUtilitiesTest {

  @Test
  public void testIsTextFunctionallyEquivalentEasy() throws Exception {
    String ruleTitle = "Methods should not be empty";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(ruleTitle, ruleTitle)).isTrue();
  }

  @Test
  public void testIsTextFunctionallyEquivalentBothNull () throws Exception {

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(null, null)).isTrue();
  }

  @Test
  public void testIsTextFunctionallyEquivalentOneNull () throws Exception {

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent("test", null)).isFalse();
  }

  @Test
  public void testIsTextFunctionallyEquivalentOtherNull () throws Exception {

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(null, "test")).isFalse();
  }


  @Test
  public void testIsTextFunctionallyEquivalentSimple() throws Exception {
    String ruleTitle = "Methods should not be empty";
    String specTitle = "[Methods|functions|procedures] should not be empty";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(ruleTitle, specTitle)).isTrue();
  }

  @Test
  public void testIsTextFunctionallyEquivalentHarder() throws Exception {
    String specDescription = "A [function|method|module|subroutine] that grows too large tends to aggregate too many responsibilities.\n" +
            "Such [function|method|module|subroutine] inevitably become harder to understand and therefore harder to maintain.\n" +
            "Above a specific threshold, it is strongly advised to refactor into smaller [function|method|module|subroutine] which focus on well-defined tasks.\n" +
            "Those smaller [function|method|module|subroutine] will not only be easier to understand, but also probably easier to test.";
    String ruleDescription = "A module that grows too large tends to aggregate too many responsibilities.\n" +
            "Such module inevitably become harder to understand and therefore harder to maintain.\n" +
            "Above a specific threshold, it is strongly advised to refactor into smaller module which focus on well-defined tasks.\n" +
            "Those smaller module will not only be easier to understand, but also probably easier to test.";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(specDescription, ruleDescription)).isTrue();
  }

  @Test
  public void testIsTextFunctionallyEquivalentFalse() throws Exception {
    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent("Now is the time", "Four score and seven years ago")).isFalse();
  }

  @Test
  public void testTextIdenticalButForLinebreaks() {

    String s1 = "<p>Content that doesn't change or that doesn't change often should be included using a mechanism which won't try to interpret it. Specifically, <code>&lt;%@ include file=\"...\" %&gt;</code>, which includes the file in the JSP servlet translation phase (i.e. it happens once), should be used instead of <code>&lt;jsp:include page=\"...\" /&gt;</code>, which includes the page on the file, when the content is being served to the user.</p>";
    String s2 = "<p>\n" +
            "  Content that doesn't change or that doesn't change often should be included using a mechanism which won't try to interpret it.\n" +
            "  Specifically, <code>&lt;%@ include file=\"...\" %&gt;</code>, which includes the file in the JSP servlet translation phase (i.e. it happens once),\n" +
            "  should be used instead of <code>&lt;jsp:include page=\"...\" /&gt;</code>, which includes the page on the file, when the content is being served to the user.\n" +
            "</p>";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(s1, s2)).isTrue();
  }

  @Test
  public void optionalWords() {
    String rspec = "[Local] Variables should not be (declared|set) and then immediately returned [or thrown].";
    String with = "Local variables should not be declared and then immediately returned or thrown.";
    String without = "Variables should not be declared and then immediately returned.";
    String tooLong = "Variables should not be declared and then immediately returned on Sundays.";
    String different = "Vars should not be set and then immediately returned.";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, with)).isTrue();
    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, without)).isTrue();
    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, tooLong)).isFalse();
    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, different)).isFalse();
  }

  @Test
  public void phraseChoices() {
    String rspec = "Nested [<code>if</code>, <code>for</code>, <code>while</code>, <code>switch</code> and <code>try</code>|<code>IF</code>, <code>CASE</code>, <code>DO</code>, <code>LOOP</code>, <code>SELECT</code>, <code>WHILE</code> and <code>PROVIDE</code>] statements is a key ingredient for making what's known as \"Spaghetti code\".";
    String match = "Nested <code>IF</code>, <code>CASE</code>, <code>DO</code>, <code>LOOP</code>, <code>SELECT</code>, <code>WHILE</code> and <code>PROVIDE</code> statements is a key ingredient for making what's known as \"Spaghetti code\".";
    String wrong = "Nested <code>IF</code>, <code>CASE</code>, <code>DO</code>, <code>LOOP</code>, <code>SELECT</code>, and <code>PROVIDE</code> statements is a key ingredient for making what's known as \"Spaghetti code\".";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, match)).isTrue();
    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, wrong)).isFalse();
  }

  @Test
  public void ignoreBreakTags() {
    String rspec = "<p>The ABAP documentation is pretty clear on this subject :</p>\n" +
            "<blockquote>\n" +
            "<p>This statement is only for </p>\n" +
            "<p>!!! Internal use in SAP Basis development !!! </p>\n" +
            "<p>Even within SAP Basis, it may only be used in programs within the ABAP+GUI development group. </p>\n" +
            "<p>Its use is subject to various restrictions, not all of which may be listed in the documentation. This documentation is intended for internal SAP use within the Basis development group ABAP+GUI. </p>\n" +
            "<p>Changes and further development, which may be incompatible, may occur at any time, without warning or notice! </p>\n" +
            "</blockquote>\n";
    String impl = "<p>The ABAP documentation is pretty clear on this subject :</p>\n" +
            "<blockquote>\n" +
            "This statement is only for\n" +
            "<br/>\n" +
            "!!! Internal use in SAP Basis development !!!\n" +
            "<br/>\n" +
            "Even within SAP Basis, it may only be used in programs within the ABAP+GUI development group.\n" +
            "<br/>\n" +
            "Its use is subject to various restrictions, not all of which may be listed in the documentation. This documentation is intended for internal SAP use within the Basis development group ABAP+GUI.\n" +
            "<br/>\n" +
            "Changes and further development, which may be incompatible, may occur at any time, without warning or notice!\n" +
            "</blockquote>";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, impl)).isTrue();
  }

  @Test
  public void handleEntities() {

    String rspec = "<h2>Compliant Solution</h2>\n" +
            "\n" +
            "<pre>\n" +
            "  try.\n" +
            "    if ABS( NUMBER ) &gt; 100.\n" +
            "      write / 'Number is large'.\n" +
            "    endif.\n" +
            "  catch CX_SY_ARITHMETIC_ERROR into OREF.\n" +
            "    write / OREF-&gt;GET_TEXT( ).\n" +
            "  endtry.\n" +
            "</pre>\n";
    String impl = "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "  try.\n" +
            "    if ABS( NUMBER ) > 100.\n" +
            "      write / 'Number is large'.\n" +
            "    endif.\n" +
            "  catch CX_SY_ARITHMETIC_ERROR into OREF.\n" +
            "    write / OREF->GET_TEXT( ).\n" +
            "  endtry.\n" +
            "</pre>\n";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, impl)).isTrue();
  }

  @Test
  public void handleEntitiesAndAmpersands() {

    String spec = "<h2>Compliant Solution</h2>\n" +
            "\n" +
            "<pre>\n" +
            "NEW cl_sql_statement( )-&gt;execute_ddl(\n" +
            "      `CREATE TABLE ` &amp;&amp; dbname   &amp;&amp;\n" +
            "      `( val1 char(10) NOT NULL,` &amp;&amp;\n" +
            "      `  val2 char(10) NOT NULL,` &amp;&amp;\n" +
            "      `  PRIMARY KEY (val1) )` ).\n" +
            "</pre>\n";

    String impl = "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "NEW cl_sql_statement( )->execute_ddl(\n" +
            "      `CREATE TABLE ` && dbname   &&\n" +
            "      `( val1 char(10) NOT NULL,` &&\n" +
            "      `  val2 char(10) NOT NULL,` &&\n" +
            "      `  PRIMARY KEY (val1) )` ).\n" +
            "</pre>\n";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl)).isTrue();


    spec = "(<code>~</code> and <code>&lt;&lt;</code>)";
    impl = "(<code>~</code> and <code><<</code>)";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl)).isTrue();


  }

  @Test
  public void testCompareStrings() {

    String test = "test";
    assertThat(ComparisonUtilities.compareStrings(test, test)).isEqualTo(0);
    assertThat(ComparisonUtilities.compareStrings(null, test)).isEqualTo(-1);
  }

  @Test
  public void testCompareFunctionalEquivalence() {

    String test = "test";
    assertThat(ComparisonUtilities.compareTextFunctionalEquivalence(null, test)).isEqualTo(1);
    assertThat(ComparisonUtilities.compareTextFunctionalEquivalence(test, null)).isEqualTo(-1);
  }

  @Test
  public void testConservativeTokenExpansionInPre() {
    String rspec = "<h2>Noncompliant Code Example</h2>\n" +
            "\n" +
            "<pre>\n" +
            "for (int i = 1; i &lt;= 10; i++) {     // Noncompliant - 2 continue - one might be tempted to add some logic in between\n" +
            "  if (i % 2 == 0) {\n" +
            "    continue;\n" +
            "  }\n" +
            "\n" +
            "  if (i % 3 == 0) {\n" +
            "    continue;\n" +
            "  }\n" +
            "\n" +
            "  System.out.println(\"i = \" + i);\n" +
            "}\n" +
            "</pre>";
    String impl = "<h2>Noncompliant Code Example</h2>\n" +
            "\n" +
            "<pre>\n" +
            "for (int i = 1; i <= 10; i++) {     // Noncompliant - 2 continue - one might be tempted to add some logic in between\n" +
            "  if (i % 2 == 0) {\n" +
            "    continue;\n" +
            "  }\n" +
            "\n" +
            "  if (i % 3 == 0) {\n" +
            "    continue;\n" +
            "  }\n" +
            "\n" +
            "  System.out.println(\"i = \" + i);\n" +
            "}\n" +
            "</pre>";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, impl)).isTrue();

  }

  @Test
  public void testSpacesAfterTags() {

    String rspec = "<p>Hardcoding an IP address into source code is a bad idea for several reasons:</p>\n" +
            "<ul>\n" +
            "<li> a recompile is required if the address changes</li>\n" +
            "<li> it forces the same address to be used in every environment (dev, sys, qa, prod)</li>\n" +
            "<li> it places the responsibility of setting the value to use in production on the shoulders of the developer</li>\n" +
            "</ul>";
    String impl = "<p>Hardcoding an IP address into source code is a bad idea for several reasons:</p>\n" +
            "\n" +
            "<ul>\n" +
            "  <li>a recompile is required if the address changes</li>\n" +
            "  <li>it forces the same address to be used in every environment (dev, sys, qa, prod)</li>\n" +
            "  <li>it places the responsibility of setting the value to use in production on the shoulders of the developer</li>\n" +
            "</ul>\n";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, impl)).isTrue();
  }

  @Test
  public void testLinebreakInHeading() {
    String rspec = "<h2>Compliant Solution</h2>\n" +
            "\n" +
            "<pre>\n" +
            "switch (myVariable) {\n" +
            "  case 1:                              \n" +
            "    foo();\n" +
            "    break;\n" +
            "  case 2: \n" +
            "    doSomething();\n" +
            "    break;\n" +
            "  default:                               \n" +
            "    doSomethingElse();\n" +
            "    break;\n" +
            "}\n" +
            "</pre>";
    String impl = "<h2>\n" +
            "Compliant Solution\n" +
            "</h2>\n" +
            "\n" +
            "<pre>\n" +
            "switch (myVariable) {\n" +
            "  case 1:                              \n" +
            "    foo();\n" +
            "    break;\n" +
            "  case 2: \n" +
            "    doSomething();\n" +
            "    break;\n" +
            "  default:                               \n" +
            "    doSomethingElse();\n" +
            "    break;\n" +
            "}\n" +
            "</pre>";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, impl)).isTrue();

  }

  @Test
  public void testNoParagraph() {

    String rspec = "<p>Having a class and some of its methods sharing the same name is misleading, and leaves others to wonder whether it was done that way on purpose, or was the methods supposed to be a constructor.</p>\n";
    String impl = "Having a class and some of its methods sharing the same name is misleading, and leaves others to wonder whether it was done that way on purpose, or was the methods supposed to be a constructor.\n";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, impl)).isTrue();
  }

  @Test
  public void testOptionsInParens() {

    String rspec = "Unused (private fields|variables) should be removed";
    String impl = "Unused private fields should be removed";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(rspec, impl)).isTrue();
  }

  @Test
  public void testStripHtml() {

    String html = "<ul><li><a href=\"blah.com\">blah</a></li></ul>";
    assertThat(ComparisonUtilities.stripHtml(html)).isEqualTo("blah");
  }

  @Test
  public void testIsEquivalentEntityIgnoreBrackets() {
    String rspecTok = "Now is the time.";
    String implTok = "Now is [the] time.";

    assertThat(ComparisonUtilities.isEquivalentEntityIgnoreBrackets(rspecTok, implTok)).isTrue();
  }

  @Test
  public void testIsPhraseInOptions() {
    String rspecTok = "Now is the time";
    String implTok = "Now is the time";

    List<String> implTokens = new ArrayList<>();

    assertThat(ComparisonUtilities.isPhraseInOptions(rspecTok, implTok, implTokens)).isTrue();
  }

  @Test
  public void testAssembleExtendedImplToken () {

    String seed = "Now is the time";
    List<String> implTokens = new ArrayList(Arrays.asList(seed.split(" ")));
    String implTok = implTokens.remove(0);
    String phraseToMatch = "Now is the time for all good people";

    assertThat(ComparisonUtilities.assembleExtendedImplToken(implTokens, implTok, phraseToMatch)).isEqualTo(seed);
  }

  @Test
  public void testIsOptional() {
    assertThat(ComparisonUtilities.isOptional("[yes]")).isTrue();
    assertThat(ComparisonUtilities.isOptional("yes")).isFalse();
    assertThat(ComparisonUtilities.isOptional("[yes|no]")).isFalse();
  }

  @Test
  public void testRuleLinks() {
    String spec = "<li>See also {rule:squid:S1234}</li>";
    String impl = "<li>See also <a href=\"/coding_rules#rule_key=squid%3AS1234\">S1234</a></li>";
    String impl2 = "<li>See also <a href=\"/sonar/coding_rules#rule_key=squid%3AS1234\">S1234</a></li>";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl)).isTrue();
    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl2)).isTrue();
  }

  @Test
  public void testSpaceAroundBlockquote() {

    String spec = "<p>The <code>eval</code> function is a way to run arbitrary code at run-time. </p>\n" +
            "<p>According to the PHP documentation</p>\n" +
            "<blockquote>\n" +
            "<p>The eval() language construct is very dangerous because it allows execution of arbitrary PHP code. Its use thus is discouraged. If you have carefully verified that there is no other option than to use this construct, pay special attention not to pass any user provided data into it without properly validating it beforehand.</p>\n" +
            "</blockquote>\n";

    String impl = "<p>\n" +
            "  The <code>eval</code> function is a way to run arbitrary code at run-time.\n" +
            "</p>\n" +
            "<p>According to the PHP documentation</p>\n" +
            "<blockquote>The eval() language construct is very dangerous because it allows execution of arbitrary PHP code. Its use thus is discouraged. If you have carefully verified that there is no other option than to use this construct, pay special attention not to pass any user provided data into it without properly validating it beforehand.</blockquote>\n" +
            "\n";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl)).isTrue();
  }

  @Test
  public void testQuoteVsQuoteEntity() {

    String spec = "The maximum number of lines allowed in an \"IF\" block";
    String impl = "The maximum number of lines allowed in an &quot;IF&quot; block";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl)).isTrue();

  }

  @Test
  public void testParenPipesInCodeTags() {
    String spec = "<p>Most bitwise operators (<code>~</code>, <code>&gt;&gt;</code>, <code>&gt;&gt;=</code>, <code>&amp;</code>, <code>&amp;=</code>, <code>^</code>, <code>^=</code>, <code>|</code>, and <code>|=</code>) have implementation-dependent results when performed on signed operands, and bitwise left shift (<code>&lt;&lt;</code> and <code>&lt;&lt;=</code>) has undefined behavior when performed on negative operands. </p>\n";
    String impl = "<p>\n" +
            "  Most bitwise operators (<code>~</code>, <code>>></code>, <code>>>=</code>, <code>&amp;</code>, <code>&amp;=</code>, <code>^</code>, <code>^=</code>, <code>|</code>, and <code>|=</code>)\n" +
            "  have implementation-dependent results when performed on signed operands, and bitwise left shift (<code><<</code> and <code><<=</code>)\n" +
            "  has undefined behavior when performed on negative operands.\n" +
            "</p>\n" +
            "\n";
    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl)).isTrue();
  }

  @Test
  public void testCFamilyRuleDeprecation(){

    String spec = "<h2>Deprecated</h2>\n" +
            "\n" +
            "<p>This rule is deprecated, use {rule:c:S1103} instead.</p>\n";

    String impl = "<h2>Deprecated</h2>\n" +
            "<p>\n" +
            "  This rule is deprecated, use c:<a href='/coding_rules#rule_key=c:S1103'>S1103</a> - cpp:<a href='/coding_rules#rule_key=cpp:S1103'>S1103</a> - objc:<a href='/coding_rules#rule_key=objc:S1103'>S1103</a> instead.\n" +
            "</p>";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl)).isTrue();

    impl = "<h2>Deprecated</h2>\n" +
            "<p>\n" +
            "  This rule is deprecated, use C:<a href='/coding_rules#rule_key=c:S1103'>S1103</a> instead.\n" +
            "</p>";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl)).isTrue();

    impl = "<h2>Deprecated</h2>\n" +
            "<p>\n" +
            "  This rule is deprecated, use objc:<a href='/coding_rules#rule_key=objc:S1103'>S1103</a> instead.\n" +
            "</p>";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl)).isTrue();

    impl = "<h2>Deprecated</h2>\n" +
            "<p>\n" +
            "  This rule is deprecated, use Objc:<a href='/coding_rules#rule_key=objc:S1103'>S1103</a> - c:<a href='/coding_rules#rule_key=c:S1103'>S1103</a> - cpp:<a href='/coding_rules#rule_key=cpp:S1103'>S1103</a> instead.\n" +
            "</p>";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl)).isTrue();

  }

  @Test
  public void testNegativeLinkMatching(){
    String spec = "<h2>Deprecated</h2>\n" +
            "\n" +
            "<p>This rule is deprecated, use {rule:c:S1103} instead.</p>\n";

    String impl = "<h2>Deprecated</h2>\n" +
            "<p>\n" +
            "  This rule is deprecated, use something else instead.\n" +
            "</p>";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl)).isFalse();

    impl = "<h2>Deprecated</h2>\n" +
            "<p>\n" +
            "  This rule is deprecated, use Objc:<a href='/coding_rules#rule_key=objc:S000'>S000</a> instead.\n" +
            "</p>";

    assertThat(ComparisonUtilities.isTextFunctionallyEquivalent(spec, impl)).isFalse();

  }

  @Test
  public void testCompareCamelCaseMostlyMatch() {

    String a = "legalTrailingCommentPattern";
    String b = "legalCommentPattern";
    String c = "illegalCommentPattern";
    String d = "green";

    assertThat(ComparisonUtilities.compareCamelCaseMostlyMatch(a, b)).isEqualTo(0);
    assertThat(ComparisonUtilities.compareCamelCaseMostlyMatch(b, a)).isEqualTo(0);
    assertThat(ComparisonUtilities.compareCamelCaseMostlyMatch(a, a)).isEqualTo(0);
    assertThat(ComparisonUtilities.compareCamelCaseMostlyMatch(b, c)).isEqualTo(3);
    assertThat(ComparisonUtilities.compareCamelCaseMostlyMatch(c, d)).isEqualTo(2);
    assertThat(ComparisonUtilities.compareCamelCaseMostlyMatch(d, c)).isEqualTo(-2);

  }

}
