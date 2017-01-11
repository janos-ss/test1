/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.Profile;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.utilities.Language;
import org.fest.assertions.api.Assertions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class RuleMakerTest {

  private JSONParser parser = new JSONParser();

  @Test
  public void testGetRuleByKey(){

    Rule rule = RuleMaker.getRuleByKey("S2210", "Java");
    assertThat(rule).isNotNull();
    assertThat(rule.getKey()).isEqualTo("RSPEC-2210");
    assertThat(rule.getLookupKey()).isEqualTo("S2210");
  }


  @Test
  public void testPrivateConstructors() {
    final Constructor<?>[] constructors = RuleMaker.class.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }
  }

  @Test
  public void testIsLanguageMatchEasyTrue() throws Exception {
    assertThat(RuleMaker.isLanguageMatch("Java", "Java")).isTrue();
  }

  @Test
  public void testIsLanguageMatchEasyFalse() throws Exception {
    assertThat(RuleMaker.isLanguageMatch("RPG", "Java")).isFalse();
  }

  @Test
  public void testIsLanguageMatchFalse() throws Exception {
    assertThat(RuleMaker.isLanguageMatch("Java", "JavaScript")).isFalse();
  }

  @Test
  public void testIsLanguageMatchTrue() throws Exception {
    assertThat(RuleMaker.isLanguageMatch("Java", "Java: ...")).isTrue();
  }



  @Test
  public void testSetFullDescriptionNull() throws Exception {
    Rule rule = new Rule("Java");
    RuleMaker.setDescription(rule, null, true);
    assertThat(rule.getFullDescription()).isNull();
  }

  @Test
  public void testSetMarkdownDescription() throws Exception {
    String markdown = "Even if all browsers are fault-tolerant, HTML tags should be closed to prevent any unexpected behavior.";
    String html = "<p>Even if all browsers are fault-tolerant, HTML tags should be closed to prevent any unexpected behavior.</p>\n";

    Rule rule = new Rule("HTML");
    RuleMaker.setDescription(rule, markdown, true);
    assertThat(rule.getDescription()).isEqualTo(html);
  }

  @Test
  public void testSetMarkdownNonCompliant() throws Exception {
    String markdown = "h2. Noncompliant Code Example\r\n{code}\r\n<html>\r\n  <head>\r\n    <title>Test Page    <!-- Noncompliant; title not closed -->\r\n  <!-- Noncompliant; head not closed -->\r\n  <body>\r\n    <em>Emphasized Text  <!-- Noncompliant; em not closed -->\r\n  <!-- Noncompliant; body not closed -->\r\n</html>\r\n{code}\r\n";
    String html = "\n<h2>Noncompliant Code Example</h2>\n<pre>\n&lt;html&gt;\n  &lt;head&gt;\n    &lt;title&gt;Test Page    &lt;!-- Noncompliant; title not closed --&gt;\n  &lt;!-- Noncompliant; head not closed --&gt;\n  &lt;body&gt;\n    &lt;em&gt;Emphasized Text  &lt;!-- Noncompliant; em not closed --&gt;\n  &lt;!-- Noncompliant; body not closed --&gt;\n&lt;/html&gt;\n</pre>\n";

    Rule rule = new Rule("HTML");
    RuleMaker.setDescription(rule, markdown, true);

    assertThat(rule.getNonCompliant()).isEqualTo(html);
  }

  @Test
  public void testSetMarkdownCompliant() throws Exception {
    String markdown = "h2. Compliant Solution\r\n{code}\r\n<html>\r\n  <head>\r\n    <title>Test Page</title>\r\n  </head>\r\n  <body>\r\n    <em>Emphasized Text</em>\r\n  </body>\r\n</html>\r\n{code}\r\n";
    String html = "\n<h2>Compliant Solution</h2>\n<pre>\n&lt;html&gt;\n  &lt;head&gt;\n    &lt;title&gt;Test Page&lt;/title&gt;\n  &lt;/head&gt;\n  &lt;body&gt;\n    &lt;em&gt;Emphasized Text&lt;/em&gt;\n  &lt;/body&gt;\n&lt;/html&gt;\n</pre>\n";

    Rule rule = new Rule("HTML");
    RuleMaker.setDescription(rule, markdown,true);

    assertThat(rule.getCompliant()).isEqualTo(html);
  }

  @Test
  public void testSetMarkdownExceptions() throws Exception {
    String markdown = "h2.Exceptions\r\n<code>InterruptedException</code>, <code>NumberFormatException</code>, <code>ParseException</code> and <code>MalformedURLException</code> exceptions are arguably used to indicate nonexceptional outcomes.\r\nBecause they are part of Java, developers have no choice but to deal with them. This rule does not verify that those particular exceptions are correctly handled.\r\n\r\n{code}\r\nint myInteger;\r\ntry {\r\n  myInteger = Integer.parseInt(myString);\r\n} catch (NumberFormatException e) {\r\n  // It is perfectly acceptable to not handle \"e\" here\r\n  myInteger = 0;\r\n}\r\n{code}\r\n\r\n";
    String html = "\n<h2>Exceptions</h2>\n<p><code>InterruptedException</code>, <code>NumberFormatException</code>, <code>ParseException</code> and <code>MalformedURLException</code> exceptions are arguably used to indicate nonexceptional outcomes.</p>\n<p>Because they are part of Java, developers have no choice but to deal with them. This rule does not verify that those particular exceptions are correctly handled.</p>\n<pre>\nint myInteger;\ntry {\n  myInteger = Integer.parseInt(myString);\n} catch (NumberFormatException e) {\n  // It is perfectly acceptable to not handle \"e\" here\n  myInteger = 0;\n}\n</pre>\n";

    Rule rule = new Rule("Java");
    RuleMaker.setDescription(rule, markdown,true);

    assertThat(rule.getExceptions()).isEqualTo(html);
  }

  @Test
  public void testSetMarkdownReferences() throws Exception {
    String markdown = "h2. See\r\n* MISRA C++:2008, 2-13-4 \r\n* MISRA C:2012, 7.3\r\n";
    String html = "\n<h2>See</h2>\n" +
            "<ul>\n" +
            "<li> MISRA C++:2008, 2-13-4 \n" +
            "</li><li> MISRA C:2012, 7.3\n" +
            "</li></ul>\n";

    Rule rule = new Rule("C");
    RuleMaker.setDescription(rule, markdown, true);

    assertThat(rule.getReferences()).isEqualTo(html);
  }

  @Test
  public void testSetMarkdownNonsense() throws Exception {
    String markdown = "Even if all browsers are fault-tolerant, HTML tags should be closed to prevent any unexpected behavior.\r\nh2. Nconmpliant Code Example\r\n{code}blah{code}";
    String html = "<p>Even if all browsers are fault-tolerant, HTML tags should be closed to prevent any unexpected behavior.</p>\n";

    Rule rule = new Rule("HTML");
    RuleMaker.setDescription(rule, markdown, true);

    assertThat(rule.getHtmlDescription()).isEqualTo(html);
  }

  @Test
  public void testSetHtmlDescription() throws Exception {
    java.net.URL url = this.getClass().getResource("/");
    String html = new java.util.Scanner(new File(url.getPath() + "/FullDescriptionHtml.html"),"UTF8").useDelimiter("\\Z").next();

    Rule rule = new Rule("Java");
    RuleMaker.setDescription(rule, html, false);

    assertThat(rule.getHtmlDescription()).isEqualTo(html);
  }

  @Test
  public void testEmptyDescription() {
    Rule rule = new Rule("Java");
    RuleMaker.setDescription(rule, "", true);
    assertThat(rule.getDescription()).hasSize(0);
  }

  @Test
  public void testNullDescription() {
    Rule rule = new Rule("Java");
    RuleMaker.setDescription(rule, null, true);
    assertThat(rule.getDescription()).hasSize(0);
  }

  @Test
  public void testFleshOutRuleNullIssue() {
    Rule rule = new Rule("");
    RuleMaker.fleshOutRule(new JiraFetcherImpl(), rule, null);
    assertThat(rule.getTitle()).isNull();
  }

  @Test
  public void testFleshOutRuleNullValues() {
    String json = "{\"id\":\"19078\",\"names\":{\"summary\":\"Summary\",\"issuetype\":\"Issue Type\",\"customfield_10243\":\"issueFunction\",\"customfield_10232\":\"Completeness\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10246\":\"Checkstyle\",\"customfield_10242\":\"Template Rule\",\"reporter\":\"Reporter\",\"customfield_10330\":\"Implementation details\",\"updated\":\"Updated\",\"created\":\"Created\",\"description\":\"Description\",\"customfield_10001\":\"Targeted languages\",\"issuelinks\":\"Linked Issues\",\"customfield_10004\":\"Covered Languages\",\"subtasks\":\"Sub-Tasks\",\"status\":\"Status\",\"customfield_10007\":\"Default Severity\",\"labels\":\"Labels\",\"customfield_10005\":\"List of parameters\",\"customfield_10256\":\"Linear Argument\",\"workratio\":\"Work Ratio\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10255\":\"CERT\",\"customfield_10253\":\"OWASP\",\"customfield_10250\":\"PHP-FIG\",\"customfield_10251\":\"CWE\",\"project\":\"Project\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10014\":\"Linear Offset\",\"lastViewed\":\"Last Viewed\",\"customfield_10015\":\"Legacy Key\",\"customfield_10012\":\"Constant Cost or Linear Threshold\",\"customfield_10013\":\"Linear Factor\",\"comment\":\"Comment\",\"customfield_10010\":\"Characteristic\",\"customfield_10011\":\"Remediation Function\",\"votes\":\"Votes\",\"resolution\":\"Resolution\",\"resolutiondate\":\"Resolved\",\"creator\":\"Creator\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10021\":\"Activated by default\",\"watches\":\"Watchers\",\"assignee\":\"Assignee\",\"customfield_10131\":\"Applicability\",\"customfield_10130\":\"Outdated Languages\",\"customfield_10030\":\"Message\"},\"key\":\"RSPEC-2210\",\"fields\":{\"summary\":\"Anntest dummy rule asdf\",\"customfield_10244\":\"ASDF-PDQ\",\"customfield_10257\":\"findsecbuts\",\"customfield_10255\":\"cert...\",\"customfield_10253\":\"CWE-123\",\"customfield_10251\":\"CWE-123\",\"customfield_10249\":\"8.9\",\"customfield_10248\":\"0.0\",\"customfield_10258\":\"0-0-0\",\"customfield_10245\":\"pmd\",\"customfield_10246\":\"checkstyle\",\"customfield_10250\":\"mission-fig\"}}";

    Rule rule = new Rule("");
    try {
      RuleMaker.fleshOutRule(new JiraFetcherImpl(), rule, (JSONObject) parser.parse(json));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    assertThat(rule.getSeverity()).isNull();
  }

  @Test
  public void testCFamilyLanguageMatch() {
    assertThat(RuleMaker.isLanguageMatch("C","C-Family")).isTrue();
    assertThat(RuleMaker.isLanguageMatch("C++","Objective-C")).isTrue();

    assertThat(RuleMaker.isLanguageMatch("C", "Charlie")).isFalse();
    assertThat(RuleMaker.isLanguageMatch("C", "C#")).isFalse();

    assertThat(RuleMaker.isLanguageMatch("Java", "CPP")).isFalse();
  }

  @Test
  public void testGetProfiles() {

    String json = "[{\"key\":\"java-android-lint-48163\",\"name\":\"Android Lint\",\"lang\":\"java\"},{\"key\":\"php-drupal-68026\",\"name\":\"Drupal\",\"lang\":\"php\"},{\"key\":\"web-nemo-web-profile-50403\",\"name\":\"Nemo Web profile\",\"lang\":\"web\"},{\"key\":\"php-psr-2-29775\",\"name\":\"PSR-2\",\"lang\":\"php\"},{\"key\":\"cs-sonar-c-way-58434\",\"name\":\"Sonar C# Way\",\"lang\":\"cs\"},{\"key\":\"abap-sonar-way-38370\",\"name\":\"Sonar way\",\"lang\":\"abap\"},{\"key\":\"c-sonar-way-44762\",\"name\":\"Sonar way\",\"lang\":\"c\"},{\"key\":\"cobol-sonar-way-41769\",\"name\":\"Sonar way\",\"lang\":\"cobol\"},{\"key\":\"cpp-sonar-way-81587\",\"name\":\"Sonar way\",\"lang\":\"cpp\"},{\"key\":\"cs-sonar-way-31865\",\"name\":\"Sonar way\",\"lang\":\"cs\"},{\"key\":\"css-sonar-way-50956\",\"name\":\"Sonar way\",\"lang\":\"css\"},{\"key\":\"flex-sonar-way-91920\",\"name\":\"Sonar way\",\"lang\":\"flex\"},{\"key\":\"grvy-sonar-way-20404\",\"name\":\"Sonar way\",\"lang\":\"grvy\"},{\"key\":\"java-sonar-way-45126\",\"name\":\"Sonar way\",\"lang\":\"java\"},{\"key\":\"js-sonar-way-56838\",\"name\":\"Sonar way\",\"lang\":\"js\"},{\"key\":\"objc-sonar-way-83399\",\"name\":\"Sonar way\",\"lang\":\"objc\"},{\"key\":\"php-sonar-way-05918\",\"name\":\"Sonar way\",\"lang\":\"php\"},{\"key\":\"pli-sonar-way-95331\",\"name\":\"Sonar way\",\"lang\":\"pli\"},{\"key\":\"plsql-sonar-way-37514\",\"name\":\"Sonar way\",\"lang\":\"plsql\"},{\"key\":\"py-sonar-way-67511\",\"name\":\"Sonar way\",\"lang\":\"py\"},{\"key\":\"rpg-sonar-way-64226\",\"name\":\"Sonar way\",\"lang\":\"rpg\"},{\"key\":\"swift-sonar-way-89786\",\"name\":\"Sonar way\",\"lang\":\"swift\"},{\"key\":\"vb-sonar-way-21338\",\"name\":\"Sonar way\",\"lang\":\"vb\"},{\"key\":\"vbnet-sonar-way-31082\",\"name\":\"Sonar way\",\"lang\":\"vbnet\"},{\"key\":\"web-sonar-way-50375\",\"name\":\"Sonar way\",\"lang\":\"web\"},{\"key\":\"xml-sonar-way-06052\",\"name\":\"Sonar way\",\"lang\":\"xml\"},{\"key\":\"java-java-security-quality-profile-60308\",\"name\":\"SonarQube Security way\",\"lang\":\"java\"}]";

    try {
      List<Profile> profiles = RuleMaker.getProfiles(Language.JAVA, (List<JSONObject>) parser.parse(json));

      Assertions.assertThat(profiles).hasSize(3);
    } catch (ParseException e) {
      fail("Unexpected exception thrown");
    }

  }

  @Test
  public void testAddProfilesToRules() {

    Rule s1194 = new Rule("");
    s1194.setKey("RSPEC-1194");

    Rule s1234 = new Rule("");
    s1234.setKey("RSPEC-1234");

    List<Rule> allRules = new ArrayList<>();
    allRules.add(s1194);
    allRules.add(s1234);

    Profile profile = new Profile("Sonar way");

    String json = "[{\"key\":\"squid:S1194\",\"internalKey\":\"S1194\"},{\"key\":\"squid:S2078\",\"internalKey\":\"S2078\"},{\"key\":\"squid:S2077\"},{\"key\":\"squid:S1193\",\"internalKey\":\"S1193\"}]";

    try {
      List<JSONObject> profileRules = (List<JSONObject>) parser.parse(json);

      RuleMaker.addProfilesToSonarQubeRules(allRules, profile, profileRules);

      assertThat(s1194.getDefaultProfiles()).contains(profile);
      assertThat(s1234.getDefaultProfiles()).isEmpty();

    } catch (ParseException e) {
      fail("Unexpected exception thrown");
    }
  }

}
