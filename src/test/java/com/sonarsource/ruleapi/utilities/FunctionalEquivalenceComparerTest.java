/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.utilities;

import org.junit.Test;
import static org.fest.assertions.Assertions.assertThat;


public class FunctionalEquivalenceComparerTest {

  @Test
  public void testIsTextFunctionallyEquivalentEasy() throws Exception {
    String ruleTitle = "Methods should not be empty";

    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(ruleTitle, ruleTitle, true)).isTrue();
  }

  @Test
  public void testIsTextFunctionallyEquivalentBothNull () throws Exception {

    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(null, null, true)).isTrue();
  }

  @Test
  public void testIsTextFunctionallyEquivalentOneNull () throws Exception {

    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent("test", null, true)).isFalse();
  }

  @Test
  public void testIsTextFunctionallyEquivalentOtherNull () throws Exception {

    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(null, "test", true)).isFalse();
  }


  @Test
  public void testIsTextFunctionallyEquivalentSimple() throws Exception {
    String ruleTitle = "Methods should not be empty";
    String specTitle = "[Methods|functions|procedures] should not be empty";

    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(ruleTitle, specTitle, true)).isTrue();
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

    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(specDescription, ruleDescription, true)).isTrue();
  }

  @Test
  public void testIsTextFunctionallyEquivalentFalse() throws Exception {
    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent("Now is the time", "Four score and seven years ago", true)).isFalse();
  }

  @Test
  public void testTextIdenticalButForLinebreaks() {

    String s1 = "<p>Content that doesn't change or that doesn't change often should be included using a mechanism which won't try to interpret it. Specifically, <code>&lt;%@ include file=\"...\" %&gt;</code>, which includes the file in the JSP servlet translation phase (i.e. it happens once), should be used instead of <code>&lt;jsp:include page=\"...\" /&gt;</code>, which includes the page on the file, when the content is being served to the user.</p>";
    String s2 = "<p>\n" +
            "  Content that doesn't change or that doesn't change often should be included using a mechanism which won't try to interpret it.\n" +
            "  Specifically, <code>&lt;%@ include file=\"...\" %&gt;</code>, which includes the file in the JSP servlet translation phase (i.e. it happens once),\n" +
            "  should be used instead of <code>&lt;jsp:include page=\"...\" /&gt;</code>, which includes the page on the file, when the content is being served to the user.\n" +
            "</p>";

    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(s1, s2, true)).isTrue();
    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(s1, s2, false)).isFalse();
  }

  @Test
  public void optionalWords() {
    String rspec = "[Local] Variables should not be (declared|set) and then immediately returned [or thrown].";
    String with = "Local variables should not be declared and then immediately returned or thrown.";
    String without = "Variables should not be declared and then immediately returned.";
    String tooLong = "Variables should not be declared and then immediately returned on Sundays.";
    String different = "Vars should not be set and then immediately returned.";

    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(rspec, with, true)).isTrue();
    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(rspec, without, true)).isTrue();
    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(rspec, tooLong, true)).isFalse();
    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(rspec, different, true)).isFalse();
  }

  @Test
  public void phraseChoices() {
    String rspec = "Nested [<code>if</code>, <code>for</code>, <code>while</code>, <code>switch</code> and <code>try</code>|<code>IF</code>, <code>CASE</code>, <code>DO</code>, <code>LOOP</code>, <code>SELECT</code>, <code>WHILE</code> and <code>PROVIDE</code>] statements is a key ingredient for making what's known as \"Spaghetti code\".";
    String match = "Nested <code>IF</code>, <code>CASE</code>, <code>DO</code>, <code>LOOP</code>, <code>SELECT</code>, <code>WHILE</code> and <code>PROVIDE</code> statements is a key ingredient for making what's known as \"Spaghetti code\".";
    String wrong = "Nested <code>IF</code>, <code>CASE</code>, <code>DO</code>, <code>LOOP</code>, <code>SELECT</code>, and <code>PROVIDE</code> statements is a key ingredient for making what's known as \"Spaghetti code\".";

    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(rspec, match, true)).isTrue();
    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(rspec, wrong, true)).isFalse();
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

    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(rspec,impl, true)).isTrue();
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

    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(rspec, impl, true)).isTrue();
  }

  @Test
  public void handleEntitiesAndAmpersands() {

    String rspec = "<h2>Compliant Solution</h2>\n" +
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

    assertThat(FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(rspec, impl, true)).isTrue();
  }

}
