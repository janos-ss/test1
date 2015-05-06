/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class RuleMakerTest {

  private static final String JSON = "{\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10071\",\"value\":\"Reliability\",\"id\":\"10071\",\"child\":{\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10073\",\"value\":\"Data related reliability\",\"id\":\"10073\"}}";
  private static final String FULL_JSON = "{\"id\":\"18166\",\"names\":{\"summary\":\"Summary\",\"issuetype\":\"Issue Type\",\"customfield_10243\":\"issueFunction\",\"customfield_10232\":\"Completeness\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10246\":\"Checkstyle\",\"customfield_10242\":\"Template Rule\",\"reporter\":\"Reporter\",\"customfield_10330\":\"Implementation details\",\"updated\":\"Updated\",\"created\":\"Created\",\"description\":\"Description\",\"customfield_10001\":\"Targeted languages\",\"issuelinks\":\"Linked Issues\",\"customfield_10004\":\"Covered Languages\",\"subtasks\":\"Sub-Tasks\",\"status\":\"Status\",\"customfield_10007\":\"Default Severity\",\"labels\":\"Labels\",\"customfield_10005\":\"List of parameters\",\"customfield_10256\":\"SQALE Linear Argument\",\"workratio\":\"Work Ratio\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10255\":\"CERT\",\"customfield_10253\":\"OWASP\",\"customfield_10250\":\"PHP-FIG\",\"customfield_10251\":\"CWE\",\"project\":\"Project\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10014\":\"SQALE Linear Offset\",\"lastViewed\":\"Last Viewed\",\"customfield_10015\":\"Legacy Key\",\"customfield_10012\":\"SQALE Constant Cost or Linear Threshold\",\"customfield_10013\":\"SQALE Linear Factor\",\"comment\":\"Comment\",\"customfield_10010\":\"SQALE Characteristic\",\"customfield_10011\":\"SQALE Remediation Function\",\"votes\":\"Votes\",\"resolution\":\"Resolution\",\"resolutiondate\":\"Resolved\",\"creator\":\"Creator\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10021\":\"Activated by default\",\"watches\":\"Watchers\",\"assignee\":\"Assignee\",\"customfield_10131\":\"Applicability\",\"customfield_10130\":\"Outdated Languages\",\"customfield_10030\":\"Message\"},\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/issue\\/18166\",\"key\":\"RSPEC-1967\",\"fields\":{\"summary\":\"Values should only be moved to variables large enough to hold them without truncation\",\"issuetype\":{\"subtask\":false,\"id\":\"7\",\"description\":\"Rule Specification\",\"name\":\"Specification\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/issuetypes\\/documentation.png\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issuetype\\/7\"},\"customfield_10243\":null,\"customfield_10232\":{\"id\":\"10324\",\"value\":\"Full\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10324\"},\"customfield_10244\":null,\"customfield_10245\":null,\"customfield_10246\":null,\"customfield_10242\":null,\"reporter\":{\"name\":\"ann.campbell.2\",\"active\":true,\"emailAddress\":\"ann.campbell@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=ann.campbell.2\",\"displayName\":\"Ann Campbell\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\"}},\"customfield_10330\":null,\"updated\":\"2014-10-29T13:23:19.000+0000\",\"created\":\"2014-08-22T18:51:55.000+0000\",\"description\":\"Moving a large value into a small field will result in data truncation for both numeric and alphabetic values. In general, alphabetic values are truncated from the right, while numeric values are truncated from the left. However, in the case of floating point values, when the target field has too little precision to hold the value being moved to it, decimals will be truncated (not rounded!) from the right.\\r\\n\\r\\nIn any case, data loss is always the result when too-large values are moved to too-small fields.\\r\\n\\r\\nh2. Noncompliant Code Example\\r\\n{code}\\r\\n01 NUM-A   PIC 9(2)V9.\\r\\n01 ALPHA   PIC X(4).\\r\\n*> ...\\r\\n\\r\\n    MOVE 88.89   TO NUM-A  *> Noncompliant. Becomes 88.8\\r\\n    MOVE 178.7   TO NUM-A  *> Noncompliant. Becomes 78.7\\r\\n    MOVE 999.99 TO NUM-A  *> Noncompliant. Truncated on both ends; becomes 99.9\\r\\n    MOVE \\\"Now is the time\\\" TO ALPHA *> Noncompliant. Becomes \\\"Now \\\"\\r\\n{code}\\r\\n\\r\\nh2. Compliant Solution\\r\\n{code}\\r\\n01 NUM-A   PIC 9(3)V99.\\r\\n01 ALPHA   PIC X(15).\\r\\n*> ...\\r\\n\\r\\n    MOVE 88.89   TO NUM-A\\r\\n    MOVE 178.7   TO NUM-A\\r\\n    MOVE 999.99 TO NUM-A\\r\\n    MOVE \\\"Now is the time\\\" TO ALPHA\\r\\n{code}\\r\\n\\r\\nh2. See\\r\\n* [MITRE, CWE-704|http:\\/\\/cwe.mitre.org\\/data\\/definitions\\/704] - Incorrect Type Conversion or Cast\",\"customfield_10001\":[{\"id\":\"10010\",\"value\":\"Cobol\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10010\"}],\"issuelinks\":[{\"id\":\"12993\",\"inwardIssue\":{\"id\":\"18509\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/18509\",\"key\":\"COBOL-1131\",\"fields\":{\"summary\":\"Rule \\\"Values should only be moved to variables large enough to hold them without truncation\\\"\",\"issuetype\":{\"subtask\":false,\"id\":\"2\",\"description\":\"A new feature of the product, which has yet to be developed.\",\"name\":\"New Feature\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/issuetypes\\/newfeature.png\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issuetype\\/2\"},\"status\":{\"id\":\"6\",\"description\":\"The issue is considered finished, the resolution is correct. Issues which are closed can be reopened.\",\"name\":\"Closed\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/statuses\\/closed.png\",\"statusCategory\":{\"id\":3,\"colorName\":\"green\",\"name\":\"Complete\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/statuscategory\\/3\",\"key\":\"done\"},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/status\\/6\"},\"priority\":{\"id\":\"3\",\"name\":\"Major\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/priorities\\/major.png\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/priority\\/3\"}}},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issueLink\\/12993\",\"type\":{\"id\":\"10010\",\"outward\":\"implements\",\"inward\":\"is implemented by\",\"name\":\"Rule specification\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issueLinkType\\/10010\"}}],\"customfield_10004\":null,\"subtasks\":[],\"status\":{\"id\":\"10000\",\"description\":\"Active Quality Rule\",\"name\":\"Active\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/statuses\\/open.png\",\"statusCategory\":{\"id\":2,\"colorName\":\"blue-gray\",\"name\":\"New\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/statuscategory\\/2\",\"key\":\"new\"},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/status\\/10000\"},\"customfield_10007\":{\"id\":\"10041\",\"value\":\"Critical\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10041\"},\"labels\":[\"bug\"],\"customfield_10005\":\"* key: onlyLiteralValues\\r\\n** description: True to apply the rule only to literal values\\r\\n** default: false\\r\\n* key: ignoredDataItemRegex\\r\\n** description: Regular expression describing sending fields to ignore \",\"customfield_10256\":null,\"workratio\":-1,\"customfield_10257\":null,\"customfield_10255\":null,\"customfield_10253\":null,\"customfield_10250\":null,\"customfield_10251\":\"704\",\"project\":{\"id\":\"10120\",\"name\":\"Rules Repository\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/project\\/10120\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=xsmall&pid=10120&avatarId=10011\",\"24x24\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=small&pid=10120&avatarId=10011\",\"32x32\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=medium&pid=10120&avatarId=10011\",\"48x48\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?pid=10120&avatarId=10011\"},\"key\":\"RSPEC\"},\"customfield_10249\":null,\"customfield_10248\":null,\"customfield_10014\":null,\"lastViewed\":null,\"customfield_10015\":null,\"customfield_10012\":\"30min\",\"customfield_10013\":null,\"comment\":{\"total\":1,\"startAt\":0,\"comments\":[{\"id\":\"20121\",\"body\":\"@Ann, perhaps we could associate this rule to http:\\/\\/cwe.mitre.org\\/data\\/definitions\\/704.html ? This is a bit controversial as CWE-704 is Weakness Class.\",\"author\":{\"name\":\"freddy.mallet\",\"active\":true,\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=freddy.mallet\",\"displayName\":\"Freddy Mallet\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\"}},\"updated\":\"2014-09-19T15:42:06.000+0000\",\"created\":\"2014-09-19T15:42:06.000+0000\",\"updateAuthor\":{\"name\":\"freddy.mallet\",\"active\":true,\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=freddy.mallet\",\"displayName\":\"Freddy Mallet\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\"}},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/18166\\/comment\\/20121\"}],\"maxResults\":1},\"customfield_10010\":{\"child\":{\"id\":\"10073\",\"value\":\"Data related reliability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10073\"},\"id\":\"10071\",\"value\":\"Reliability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10071\"},\"customfield_10011\":{\"id\":\"10086\",\"value\":\"Constant\\/Issue\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10086\"},\"votes\":{\"hasVoted\":false,\"votes\":0,\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/RSPEC-1967\\/votes\"},\"resolution\":null,\"resolutiondate\":null,\"creator\":{\"name\":\"ann.campbell.2\",\"active\":true,\"emailAddress\":\"ann.campbell@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=ann.campbell.2\",\"displayName\":\"Ann Campbell\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\"}},\"customfield_10258\":null,\"customfield_10021\":{\"id\":\"10113\",\"value\":\"Yes\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10113\"},\"watches\":{\"watchCount\":2,\"isWatching\":false,\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/RSPEC-1967\\/watchers\"},\"assignee\":null,\"customfield_10131\":[{\"id\":\"10241\",\"value\":\"Sources\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10241\"},{\"id\":\"10242\",\"value\":\"Tests\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10242\"}],\"customfield_10130\":null,\"customfield_10030\":\"Increase the size of \\\"YYY\\\" or do not \\\"MOVE\\\" (\\\"XXX\\\"|this literal value) to it.\"}}";
  private static final String SQ_JSON_NO_INTERNAL_KEY = "{\"key\":\"php:S1996\",\"repo\":\"php\",\"name\":\"Files should contain only one class or interface each\",\"createdAt\":\"2014-11-21T07:03:46+0000\",\"severity\":\"MAJOR\",\"status\":\"READY\",\"isTemplate\":false,\"tags\":[],\"sysTags\":[\"brain-overload\"],\"lang\":\"php\",\"langName\":\"PHP\",\"htmlDesc\":\"<p>\\n  A file that grows too much tends to aggregate too many responsibilities\\n  and inevitably becomes harder to understand and therefore to maintain. This is doubly true for a file with multiple independent classes and interfaces. It is strongly advised to divide the file into one independent class or interface per file.\\n</p>\",\"defaultDebtChar\":\"MAINTAINABILITY\",\"defaultDebtSubChar\":\"UNDERSTANDABILITY\",\"debtChar\":\"MAINTAINABILITY\",\"debtSubChar\":\"UNDERSTANDABILITY\",\"debtCharName\":\"Maintainability\",\"debtSubCharName\":\"Understandability\",\"defaultDebtRemFnType\":\"LINEAR\",\"defaultDebtRemFnCoeff\":\"10min\",\"debtOverloaded\":false,\"debtRemFnType\":\"LINEAR\",\"debtRemFnCoeff\":\"10min\",\"params\":[]}";
  private static final String SQ_JSON = "{\"key\":\"Web:ComplexityCheck\",\"repo\":\"Web\",\"name\":\"Files should not be too complex\",\"createdAt\":\"2013-06-19T05:34:52+0000\",\"severity\":\"MINOR\",\"status\":\"READY\",\"internalKey\":\"ComplexityCheck\",\"isTemplate\":false,\"tags\":[],\"sysTags\":[\"brain-overloaded\"],\"lang\":\"web\",\"langName\":\"Web\",\"htmlDesc\":\"<p>\\n  Checks cyclomatic complexity against a specified limit.\\n  The complexity is measured by counting decision tags (such as if and forEach) and boolean operators in expressions (\\\"&&\\\" and \\\"||\\\"), plus one for the body of the document.\\n  It is a measure of the minimum number of possible paths to render the page.\\n</p>\",\"debtOverloaded\":false,\"params\":[{\"key\":\"max\",\"htmlDesc\":\"Maximum allowed complexity\",\"type\":\"INTEGER\",\"defaultValue\":\"10\"}]}";

  JSONParser parser = new JSONParser();

  @Test
  public void testIsLangaugeMatchEasyTrue() throws Exception {
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
  public void testHandleParameterListNullString() throws Exception {
    List<Parameter> empty = new ArrayList<Parameter>();
    assertThat(RuleMaker.handleParameterList(null, "Java")).hasSize(0);
  }

  @Test
  public void testHandleParameterList() throws Exception {
    String paramString = "* key: complexity_threshold\r\n* type = text\r\n** Description: The minimum complexity at which this rule will be triggered.\r\n** Default: 250";
    List<Parameter> paramList = RuleMaker.handleParameterList(paramString, "Java");
    assertThat(paramList).hasSize(1);
  }

  @Test
  public void testHandleParameterListEmptyString() throws Exception {
    List<Parameter> paramList = RuleMaker.handleParameterList("", "Java");
    assertThat(paramList).hasSize(0);
  }

  @Test
  public void testHandleParameterListMultilanguage() throws Exception {
    String paramString = "Key: format \r\nDescription: Regular expression used to check the names against. \r\nDefault Value for Java : ^[a-z][a-zA-Z0-9]*$ \r\nDefault Value for Flex : ^[_a-z][a-zA-Z0-9]*$";
    List<Parameter> paramList = RuleMaker.handleParameterList(paramString, "Java");
    assertThat(paramList.get(0).getDefaultVal()).isEqualTo("^[a-z][a-zA-Z0-9]*$");
  }

  @Test
  public void testHandleParameterListNoKeyLabel() throws Exception {
    String paramString = "* indentSize \r\n** Description: Number of white-spaces of an indent. If this property is not set, we just check that the code is indented. \r\n** Default value: none \r\n* tabWidth \r\n** Description: Equivalent number of spaces of a tabulation \r\n** Default value: 2\r\n";
    List<Parameter> paramList = RuleMaker.handleParameterList(paramString, "Java");
    assertThat(paramList).hasSize(2);
    assertThat(paramList.get(0).getKey()).isEqualTo("indentSize");
  }

  @Test
  public void testHandleParameterListUnknownLabel() throws Exception {
    String paramString = "Key: format \r\nDescription: Regular expression used to check the names against. \r\nDefault Value for Java : ^[a-z][a-zA-Z0-9]*$ \r\nDefault Value for Flex : ^[_a-z][a-zA-Z0-9]*$\r\ntpye:text";
    List<Parameter> paramList = RuleMaker.handleParameterList(paramString, "Java");
    assertThat(paramList.get(0).getType()).isNull();
  }

  @Test
  public void testTidyParamLabel() throws Exception {

    assertThat(RuleMaker.tidyParamLabel(null)).isNull();
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
    String html = "<h2>Noncompliant Code Example</h2>\n\n<pre>\n&lt;html&gt;\n  &lt;head&gt;\n    &lt;title&gt;Test Page    &lt;!-- Noncompliant; title not closed --&gt;\n  &lt;!-- Noncompliant; head not closed --&gt;\n  &lt;body&gt;\n    &lt;em&gt;Emphasized Text  &lt;!-- Noncompliant; em not closed --&gt;\n  &lt;!-- Noncompliant; body not closed --&gt;\n&lt;/html&gt;\n</pre>\n";

    Rule rule = new Rule("HTML");
    RuleMaker.setDescription(rule, markdown, true);

    assertThat(rule.getNonCompliant()).isEqualTo(html);
  }

  @Test
  public void testSetMarkdownCompliant() throws Exception {
    String markdown = "h2. Compliant Solution\r\n{code}\r\n<html>\r\n  <head>\r\n    <title>Test Page</title>\r\n  </head>\r\n  <body>\r\n    <em>Emphasized Text</em>\r\n  </body>\r\n</html>\r\n{code}\r\n";
    String html = "<h2>Compliant Solution</h2>\n\n<pre>\n&lt;html&gt;\n  &lt;head&gt;\n    &lt;title&gt;Test Page&lt;/title&gt;\n  &lt;/head&gt;\n  &lt;body&gt;\n    &lt;em&gt;Emphasized Text&lt;/em&gt;\n  &lt;/body&gt;\n&lt;/html&gt;\n</pre>\n";

    Rule rule = new Rule("HTML");
    RuleMaker.setDescription(rule, markdown,true);

    assertThat(rule.getCompliant()).isEqualTo(html);
  }

  @Test
  public void testSetMarkdownExceptions() throws Exception {
    String markdown = "h2.Exceptions\r\n<code>InterruptedException</code>, <code>NumberFormatException</code>, <code>ParseException</code> and <code>MalformedURLException</code> exceptions are arguably used to indicate nonexceptional outcomes.\r\nBecause they are part of Java, developers have no choice but to deal with them. This rule does not verify that those particular exceptions are correctly handled.\r\n\r\n{code}\r\nint myInteger;\r\ntry {\r\n  myInteger = Integer.parseInt(myString);\r\n} catch (NumberFormatException e) {\r\n  // It is perfectly acceptable to not handle \"e\" here\r\n  myInteger = 0;\r\n}\r\n{code}\r\n\r\n";
    String html = "<h2>Exceptions</h2>\n\n<p><code>InterruptedException</code>, <code>NumberFormatException</code>, <code>ParseException</code> and <code>MalformedURLException</code> exceptions are arguably used to indicate nonexceptional outcomes.</p>\n<p>Because they are part of Java, developers have no choice but to deal with them. This rule does not verify that those particular exceptions are correctly handled.</p>\n<pre>\nint myInteger;\ntry {\n  myInteger = Integer.parseInt(myString);\n} catch (NumberFormatException e) {\n  // It is perfectly acceptable to not handle \"e\" here\n  myInteger = 0;\n}\n</pre>\n";

    Rule rule = new Rule("Java");
    RuleMaker.setDescription(rule, markdown,true);

    assertThat(rule.getExceptions()).isEqualTo(html);
  }

  @Test
  public void testSetMarkdownReferences() throws Exception {
    String markdown = "h2. See\r\n* MISRA C++:2008, 2-13-4 \r\n* MISRA C:2012, 7.3\r\n";
    String html = "<h2>See</h2>\n" +
            "\n" +
            "<ul>\n" +
            "<li> MISRA C++:2008, 2-13-4 \n" +
            "</li><li> MISRA C:2012, 7.3\n" +
            "</li></ul>\n";

    Rule rule = new Rule("C");
    RuleMaker.setDescription(rule, markdown,true);

    assertThat(rule.getReferences()).isEqualTo(html);
  }

  @Test
  public void testSetMarkdownNonsense() throws Exception {
    String markdown = "Even if all browsers are fault-tolerant, HTML tags should be closed to prevent any unexpected behavior.\r\nh2. Nconmpliant Code Example\r\n{code}blah{code}";
    String html = "<p>Even if all browsers are fault-tolerant, HTML tags should be closed to prevent any unexpected behavior.</p>\n";

    Rule rule = new Rule("HTML");
    RuleMaker.setDescription(rule, markdown,true);

    assertThat(rule.getHtmlDescription()).isEqualTo(html);
  }

  @Test
  public void testSetHtmlDescription() throws Exception {
    java.net.URL url = this.getClass().getResource("/");
    String html = new java.util.Scanner(new File(url.getPath() + "/FullDescriptionHtml.html"),"UTF8").useDelimiter("\\Z").next();

    Rule rule = new Rule("Java");
    RuleMaker.setDescription(rule, html,false);

    assertThat(rule.getHtmlDescription()).isEqualTo(html);
  }

  @Test
  public void testEmptyDescription() {
    Rule rule = new Rule("Java");
    RuleMaker.setDescription(rule, "",true);
    assertThat(rule.getDescription()).hasSize(0);
  }

  @Test
  public void testNullDescription() {
    Rule rule = new Rule("Java");
    RuleMaker.setDescription(rule, null,true);
    assertThat(rule.getDescription()).hasSize(0);
  }

  @Test
  public void testStringToListNull() {

    assertThat(RuleMaker.stringToList(null)).hasSize(0);
  }

  @Test
  public void testStringToListEmpty() {

    assertThat(RuleMaker.stringToList("")).hasSize(0);
  }

  @Test
  public void testStringToListOne() {

    assertThat(RuleMaker.stringToList("yo")).hasSize(1);
  }

  @Test
  public void testStringToListComma() {

    assertThat(RuleMaker.stringToList("hello, you")).hasSize(2);
  }

  @Test
  public void testStringToListAmps() {

    assertThat(RuleMaker.stringToList("hello && you")).hasSize(2);
  }

  @Test
  public void testStringToListAmp() {

    assertThat(RuleMaker.stringToList("hello & you")).hasSize(2);
  }

  @Test
  public void testStringToListAmpAndComma() {

    assertThat(RuleMaker.stringToList("hello, you & you")).hasSize(3);
  }

  @Test
  public void testFleshOutRuleNullIssue() {
    Rule rule = new Rule("");
    RuleMaker.fleshOutRule(new Fetcher(), rule, null);
    assertThat(rule.getTitle()).isNull();
  }

  @Test
  public void testFleshOutRuleNullValues() {
    String json = "{\"id\":\"19078\",\"names\":{\"summary\":\"Summary\",\"issuetype\":\"Issue Type\",\"customfield_10243\":\"issueFunction\",\"customfield_10232\":\"Completeness\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10246\":\"Checkstyle\",\"customfield_10242\":\"Template Rule\",\"reporter\":\"Reporter\",\"customfield_10330\":\"Implementation details\",\"updated\":\"Updated\",\"created\":\"Created\",\"description\":\"Description\",\"customfield_10001\":\"Targeted languages\",\"issuelinks\":\"Linked Issues\",\"customfield_10004\":\"Covered Languages\",\"subtasks\":\"Sub-Tasks\",\"status\":\"Status\",\"customfield_10007\":\"Default Severity\",\"labels\":\"Labels\",\"customfield_10005\":\"List of parameters\",\"customfield_10256\":\"SQALE Linear Argument\",\"workratio\":\"Work Ratio\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10255\":\"CERT\",\"customfield_10253\":\"OWASP\",\"customfield_10250\":\"PHP-FIG\",\"customfield_10251\":\"CWE\",\"project\":\"Project\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10014\":\"SQALE Linear Offset\",\"lastViewed\":\"Last Viewed\",\"customfield_10015\":\"Legacy Key\",\"customfield_10012\":\"SQALE Constant Cost or Linear Threshold\",\"customfield_10013\":\"SQALE Linear Factor\",\"comment\":\"Comment\",\"customfield_10010\":\"SQALE Characteristic\",\"customfield_10011\":\"SQALE Remediation Function\",\"votes\":\"Votes\",\"resolution\":\"Resolution\",\"resolutiondate\":\"Resolved\",\"creator\":\"Creator\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10021\":\"Activated by default\",\"watches\":\"Watchers\",\"assignee\":\"Assignee\",\"customfield_10131\":\"Applicability\",\"customfield_10130\":\"Outdated Languages\",\"customfield_10030\":\"Message\"},\"key\":\"RSPEC-2210\",\"fields\":{\"summary\":\"Anntest dummy rule asdf\",\"customfield_10244\":\"ASDF-PDQ\",\"customfield_10257\":\"findsecbuts\",\"customfield_10255\":\"cert...\",\"customfield_10253\":\"CWE-123\",\"customfield_10251\":\"CWE-123\",\"customfield_10249\":\"8.9\",\"customfield_10248\":\"0.0\",\"customfield_10258\":\"0-0-0\",\"customfield_10245\":\"pmd\",\"customfield_10246\":\"checkstyle\",\"customfield_10250\":\"mission-fig\"}}";

    Rule rule = new Rule("");
    try {
      RuleMaker.fleshOutRule(new Fetcher(), rule, (JSONObject) parser.parse(json));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    assertThat(rule.getSeverity()).isNull();
    assertThat(rule.getSqaleCharac()).isNull();
    assertThat(rule.getSqaleSubCharac()).isNull();
    assertThat(rule.getDefaultActive()).isNull();
  }

  @Test
  public void testSetReferences() {

    String json = "{\"id\":\"19078\",\"names\":{\"summary\":\"Summary\",\"issuetype\":\"Issue Type\",\"customfield_10243\":\"issueFunction\",\"customfield_10232\":\"Completeness\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10246\":\"Checkstyle\",\"customfield_10242\":\"Template Rule\",\"reporter\":\"Reporter\",\"customfield_10330\":\"Implementation details\",\"updated\":\"Updated\",\"created\":\"Created\",\"description\":\"Description\",\"customfield_10001\":\"Targeted languages\",\"issuelinks\":\"Linked Issues\",\"customfield_10004\":\"Covered Languages\",\"subtasks\":\"Sub-Tasks\",\"status\":\"Status\",\"customfield_10007\":\"Default Severity\",\"labels\":\"Labels\",\"customfield_10005\":\"List of parameters\",\"customfield_10256\":\"SQALE Linear Argument\",\"workratio\":\"Work Ratio\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10255\":\"CERT\",\"customfield_10253\":\"OWASP\",\"customfield_10250\":\"PHP-FIG\",\"customfield_10251\":\"CWE\",\"project\":\"Project\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10014\":\"SQALE Linear Offset\",\"lastViewed\":\"Last Viewed\",\"customfield_10015\":\"Legacy Key\",\"customfield_10012\":\"SQALE Constant Cost or Linear Threshold\",\"customfield_10013\":\"SQALE Linear Factor\",\"comment\":\"Comment\",\"customfield_10010\":\"SQALE Characteristic\",\"customfield_10011\":\"SQALE Remediation Function\",\"votes\":\"Votes\",\"resolution\":\"Resolution\",\"resolutiondate\":\"Resolved\",\"creator\":\"Creator\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10021\":\"Activated by default\",\"watches\":\"Watchers\",\"assignee\":\"Assignee\",\"customfield_10131\":\"Applicability\",\"customfield_10130\":\"Outdated Languages\",\"customfield_10030\":\"Message\"},\"key\":\"RSPEC-2210\",\"fields\":{\"summary\":\"Anntest dummy rule asdf\",\"customfield_10244\":\"ASDF-PDQ\",\"customfield_10257\":\"findsecbuts\",\"customfield_10255\":\"cert...\",\"customfield_10253\":\"CWE-123\",\"customfield_10251\":\"CWE-123\",\"customfield_10249\":\"8.9\",\"customfield_10248\":\"0.0\",\"customfield_10258\":\"0-0-0\",\"customfield_10245\":\"pmd\",\"customfield_10246\":\"checkstyle\",\"customfield_10250\":\"mission-fig\"}}";

    Rule rule = new Rule("");
    try {
      RuleMaker.setReferences(rule, (JSONObject)parser.parse(json));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    assertThat(rule.getCwe().size()).isEqualTo(1);
    assertThat(rule.getCwe().get(0)).isEqualTo("CWE-123");

    assertThat(rule.getCert().size()).isEqualTo(1);
    assertThat(rule.getCert().get(0)).isEqualTo("cert...");

    assertThat(rule.getMisraC04().size()).isEqualTo(1);
    assertThat(rule.getMisraC04().get(0)).isEqualTo("0.0");

    assertThat(rule.getMisraC12().size()).isEqualTo(1);
    assertThat(rule.getMisraC12().get(0)).isEqualTo("0-0-0");

    assertThat(rule.getMisraCpp().size()).isEqualTo(1);
    assertThat(rule.getMisraCpp().get(0)).isEqualTo("8.9");

    assertThat(rule.getFindbugs().size()).isEqualTo(1);
    assertThat(rule.getFindbugs().get(0)).isEqualTo("ASDF-PDQ");

    assertThat(rule.getFindSecBugs().size()).isEqualTo(1);
    assertThat(rule.getFindSecBugs().get(0)).isEqualTo("findsecbuts");

    assertThat(rule.getOwasp().size()).isEqualTo(1);
    assertThat(rule.getOwasp().get(0)).isEqualTo("CWE-123");

    assertThat(rule.getPmd().size()).isEqualTo(1);
    assertThat(rule.getPmd().get(0)).isEqualTo("pmd");

    assertThat(rule.getCheckstyle().size()).isEqualTo(1);
    assertThat(rule.getCheckstyle().get(0)).isEqualTo("checkstyle");

    assertThat(rule.getPhpFig().size()).isEqualTo(1);
    assertThat(rule.getPhpFig().get(0)).isEqualTo("mission-fig");
  }

  @Test
  public void testSetSquale() {
    String json = "{\"id\":\"19078\",\"names\":{\"summary\":\"Summary\",\"issuetype\":\"Issue Type\",\"customfield_10243\":\"issueFunction\",\"customfield_10232\":\"Completeness\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10246\":\"Checkstyle\",\"customfield_10242\":\"Template Rule\",\"reporter\":\"Reporter\",\"customfield_10330\":\"Implementation details\",\"updated\":\"Updated\",\"created\":\"Created\",\"description\":\"Description\",\"customfield_10001\":\"Targeted languages\",\"issuelinks\":\"Linked Issues\",\"customfield_10004\":\"Covered Languages\",\"subtasks\":\"Sub-Tasks\",\"status\":\"Status\",\"customfield_10007\":\"Default Severity\",\"labels\":\"Labels\",\"customfield_10005\":\"List of parameters\",\"customfield_10256\":\"SQALE Linear Argument\",\"workratio\":\"Work Ratio\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10255\":\"CERT\",\"customfield_10253\":\"OWASP\",\"customfield_10250\":\"PHP-FIG\",\"customfield_10251\":\"CWE\",\"project\":\"Project\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10014\":\"SQALE Linear Offset\",\"lastViewed\":\"Last Viewed\",\"customfield_10015\":\"Legacy Key\",\"customfield_10012\":\"SQALE Constant Cost or Linear Threshold\",\"customfield_10013\":\"SQALE Linear Factor\",\"comment\":\"Comment\",\"customfield_10010\":\"SQALE Characteristic\",\"customfield_10011\":\"SQALE Remediation Function\",\"votes\":\"Votes\",\"resolution\":\"Resolution\",\"resolutiondate\":\"Resolved\",\"creator\":\"Creator\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10021\":\"Activated by default\",\"watches\":\"Watchers\",\"assignee\":\"Assignee\",\"customfield_10131\":\"Applicability\",\"customfield_10130\":\"Outdated Languages\",\"customfield_10030\":\"Message\"},\"key\":\"RSPEC-2210\",\"fields\":{\"summary\":\"Anntest dummy rule asdf\",\"customfield_10010\":{\"child\":{\"id\":\"10050\",\"value\":\"Compiler related portability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10050\"},\"id\":\"10049\",\"value\":\"Portability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10049\"},\"customfield_10011\":{\"id\":\"10086\",\"value\":\"Constant\\/Issue\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10086\"},\"customfield_10012\":\"2d\",\"customfield_10256\":null,\"customfield_10013\":null,\"customfield_10014\":null,\"customfield_10015\":null}}";

    Rule rule = new Rule("");
    try {
      RuleMaker.setSqale(rule, (JSONObject)parser.parse(json));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(rule.getSqaleCharac()).isEqualTo("Portability");
    assertThat(rule.getSqaleSubCharac()).isEqualTo(Rule.Subcharacteristic.COMPILER_RELATED_PORTABILITY);
    assertThat(rule.getSqaleRemediationFunction()).isEqualTo(Rule.RemediationFunction.CONSTANT_ISSUE);
    assertThat(rule.getSqaleConstantCostOrLinearThreshold()).isEqualTo("2d");
    assertThat(rule.getSqaleLinearArgDesc()).isNull();
    assertThat(rule.getSqaleLinearFactor()).isNull();
    assertThat(rule.getSqaleLinearOffset()).isNull();
  }

  @Test
  public void testPopulateFields() {

    Rule rule = new Rule("");
    try {
      RuleMaker.populateFields(rule, (JSONObject)parser.parse(FULL_JSON));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(rule.getSeverity()).isEqualTo(Rule.Severity.CRITICAL);
    assertThat(rule.getDefaultActive()).isTrue();
    assertThat(rule.getStatus()).isEqualTo(Rule.Status.READY);
    assertThat(rule.getTags().size()).isEqualTo(1);
    assertThat(rule.getTags().get(0)).isEqualTo("bug");
    assertThat(rule.getTargetedLanguages()).hasSize(1);
  }

  @Test
  public void testCustomFieldValueSadPath() {

    try {
      assertThat(RuleMaker.getCustomFieldValue((JSONObject) parser.parse("{}"), "foo")).isNull();
      assertThat(RuleMaker.getCustomFieldValue((JSONObject) parser.parse(FULL_JSON), null)).isNull();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testListFromJsonFieldValueNull() {

    try {
      assertThat(RuleMaker.getListFromJsonFieldValue((JSONObject) parser.parse("{}"), "foo")).isEmpty();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetJsonField() {

    try {
      assertThat(RuleMaker.getJsonField((JSONObject)parser.parse(FULL_JSON),"customfield_10007")).isNotNull();
      assertThat(RuleMaker.getJsonField((JSONObject) parser.parse(FULL_JSON), "SQALE Characteristic")).isNotNull();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetJsonFieldSadPath() {

    try {
      assertThat(RuleMaker.getJsonField((JSONObject) parser.parse("{}"), "foo")).isNull();
      assertThat(RuleMaker.getJsonField((JSONObject) parser.parse(FULL_JSON), null)).isNull();
      assertThat(RuleMaker.getJsonField((JSONObject) parser.parse(FULL_JSON), "foo")).isNull();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetJsonFieldValueNulls() {

    try {
      assertThat(RuleMaker.getJsonFieldValue((JSONObject) parser.parse("{}"), "foo")).isNull();
      assertThat(RuleMaker.getJsonFieldValue((JSONObject) parser.parse(FULL_JSON), null)).isNull();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testPopulateFieldsFromSonarQube() {


    try {
      JSONObject jsonRule = (JSONObject) parser.parse(SQ_JSON);

      Rule rule = RuleMaker.populateFieldsFromSonarQube(jsonRule);

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

    try {
      JSONObject jsonRule = (JSONObject) parser.parse(SQ_JSON_NO_INTERNAL_KEY);

      Rule rule = RuleMaker.populateFieldsFromSonarQube(jsonRule);

      assertThat(rule.getKey()).isEqualTo("RSPEC-1996");
      assertThat(rule.getSeverity()).isEqualTo(Rule.Severity.MAJOR);
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testNormalizeKeyNoAction() {
    String key1 = "StrictMode";
    String key2 = "S1111";
    String key3 = "S000109";

    assertThat(RuleMaker.normalizeKey(key1)).isEqualTo(key1);
    assertThat(RuleMaker.normalizeKey(key2)).isEqualTo("RSPEC-1111");
    assertThat(RuleMaker.normalizeKey(key3)).isEqualTo("RSPEC-109");
  }

  @Test
  public void testSqaleConstantValueFromSqInstance() {

    Rule rule = new Rule("");
    String cost = "5h";

    rule.setSqaleRemediationFunction(Rule.RemediationFunction.LINEAR);
    RuleMaker.setSqaleConstantValueFromSqInstance(rule, cost);
    assertThat(rule.getSqaleConstantCostOrLinearThreshold()).isNull();
    assertThat(rule.getSqaleLinearOffset()).isNull();

    rule.setSqaleRemediationFunction(Rule.RemediationFunction.CONSTANT_ISSUE);
    RuleMaker.setSqaleConstantValueFromSqInstance(rule, cost);
    assertThat(rule.getSqaleConstantCostOrLinearThreshold()).isEqualTo(cost);
    assertThat(rule.getSqaleLinearOffset()).isNull();

    rule.setSqaleRemediationFunction(Rule.RemediationFunction.LINEAR_OFFSET);
    RuleMaker.setSqaleConstantValueFromSqInstance(rule, cost);
    assertThat(rule.getSqaleConstantCostOrLinearThreshold()).isNull();
    assertThat(rule.getSqaleLinearOffset()).isEqualTo(cost);

  }

  @Test
  public void testSetTemplate() {

    String json = "{\"id\":\"15463\",\"names\":{\"summary\":\"Summary\",\"issuetype\":\"Issue Type\",\"customfield_10243\":\"issueFunction\",\"customfield_10244\":\"FindBugs\",\"customfield_10232\":\"Completeness\",\"customfield_10245\":\"PMD\",\"customfield_10246\":\"Checkstyle\",\"customfield_10242\":\"Template Rule\",\"customfield_10430\":\"ReSharper\",\"reporter\":\"Reporter\",\"customfield_10431\":\"Customer Request Type\",\"customfield_10432\":\"Time to resolution\",\"customfield_10330\":\"Implementation details\",\"customfield_10433\":\"Golden customer\",\"updated\":\"Updated\",\"created\":\"Created\",\"description\":\"Description\",\"customfield_10001\":\"Targeted languages\",\"customfield_10004\":\"Covered Languages\",\"issuelinks\":\"Linked Issues\",\"subtasks\":\"Sub-Tasks\",\"status\":\"Status\",\"customfield_10007\":\"Default Severity\",\"labels\":\"Labels\",\"customfield_10005\":\"List of parameters\",\"customfield_10256\":\"SQALE Linear Argument Description\",\"workratio\":\"Work Ratio\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10255\":\"CERT\",\"customfield_10530\":\"Waiting for customer\",\"customfield_10253\":\"OWASP\",\"customfield_10250\":\"PHP-FIG\",\"customfield_10251\":\"CWE\",\"project\":\"Project\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10014\":\"SQALE Linear Offset\",\"lastViewed\":\"Last Viewed\",\"customfield_10015\":\"Legacy Key\",\"customfield_10012\":\"SQALE Constant Cost or Linear Threshold\",\"customfield_10013\":\"SQALE Linear Factor\",\"comment\":\"Comment\",\"customfield_10010\":\"SQALE Characteristic\",\"customfield_10011\":\"SQALE Remediation Function\",\"votes\":\"Votes\",\"resolution\":\"Resolution\",\"resolutiondate\":\"Resolved\",\"creator\":\"Creator\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10021\":\"Activated by default\",\"watches\":\"Watchers\",\"customfield_10436\":\"Irrelevant for Languages\",\"customfield_10437\":\"SonarQube version\",\"customfield_10434\":\"Time to answer\",\"customfield_10435\":\"fb-contrib\",\"customfield_10438\":\"Request participants\",\"assignee\":\"Assignee\",\"customfield_10131\":\"Applicability\",\"customfield_10130\":\"Outdated Languages\",\"customfield_10030\":\"Message\"},\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/issue\\/15463\",\"key\":\"RSPEC-1212\",\"fields\":{\"summary\":\"Architectural constraint\",\"issuetype\":{\"subtask\":false,\"id\":\"7\",\"description\":\"Rule Specification\",\"name\":\"Specification\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/issuetypes\\/documentation.png\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issuetype\\/7\"},\"customfield_10243\":null,\"customfield_10244\":null,\"customfield_10232\":null,\"customfield_10245\":null,\"customfield_10246\":null,\"customfield_10242\":[{\"id\":\"10329\",\"value\":\"Yes\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10329\"}],\"customfield_10430\":null,\"reporter\":{\"name\":\"freddy.mallet\",\"active\":true,\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=freddy.mallet\",\"displayName\":\"Freddy Mallet\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\"}},\"customfield_10431\":null,\"customfield_10432\":null,\"customfield_10330\":null,\"customfield_10433\":null,\"updated\":\"2015-02-11T14:50:01.000+0000\",\"created\":\"2013-08-20T14:19:20.000+0000\",\"description\":\"A source code comply to an architectural model when it fully adheres to a set of architectural constraints. A constraint allows to deny references between classes by pattern.\\r\\n\\r\\nYou can for instance use this rule to :\\r\\n\\r\\n* forbid access to {{**.web.**}} from {{**.dao.**}} classes\\r\\n* forbid access to {{java.util.Vector}}, {{java.util.Hashtable}} and {{java.util.Enumeration}} from any classes\\r\\n* forbid access to {{java.sql.**}} from {{**.ui.**}} and {{**.web.**}} classes\",\"customfield_10001\":null,\"customfield_10004\":[{\"id\":\"10029\",\"value\":\"Java\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10029\"}],\"issuelinks\":[],\"subtasks\":[],\"status\":{\"id\":\"10000\",\"description\":\"Active Quality Rule\",\"name\":\"Active\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/statuses\\/open.png\",\"statusCategory\":{\"id\":2,\"colorName\":\"blue-gray\",\"name\":\"New\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/statuscategory\\/2\",\"key\":\"new\"},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/status\\/10000\"},\"customfield_10007\":{\"id\":\"10042\",\"value\":\"Major\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10042\"},\"labels\":[],\"customfield_10005\":\"* Key: fromClasses\\r\\n* Description: Optional. If this property is not defined, all classes should adhere to this constraint. Ex : **.web.**\\r\\n\\r\\n* Key: toClasses\\r\\n* Description: Mandatory. Ex : java.util.Vector, java.util.Hashtable, java.util.Enumeration\",\"customfield_10256\":null,\"workratio\":-1,\"customfield_10257\":null,\"customfield_10255\":null,\"customfield_10530\":null,\"customfield_10253\":null,\"customfield_10250\":null,\"customfield_10251\":null,\"project\":{\"id\":\"10120\",\"name\":\"Rules Repository\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/project\\/10120\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=xsmall&pid=10120&avatarId=10011\",\"24x24\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=small&pid=10120&avatarId=10011\",\"32x32\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=medium&pid=10120&avatarId=10011\",\"48x48\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?pid=10120&avatarId=10011\"},\"key\":\"RSPEC\"},\"customfield_10249\":null,\"customfield_10248\":null,\"customfield_10014\":null,\"lastViewed\":null,\"customfield_10015\":\"ArchitecturalConstraint\",\"customfield_10012\":null,\"customfield_10013\":null,\"comment\":{\"total\":0,\"startAt\":0,\"comments\":[],\"maxResults\":0},\"customfield_10010\":null,\"customfield_10011\":null,\"votes\":{\"hasVoted\":false,\"votes\":0,\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/RSPEC-1212\\/votes\"},\"resolution\":null,\"resolutiondate\":null,\"creator\":{\"name\":\"freddy.mallet\",\"active\":true,\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=freddy.mallet\",\"displayName\":\"Freddy Mallet\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\"}},\"customfield_10258\":null,\"customfield_10021\":{\"id\":\"10114\",\"value\":\"No\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10114\"},\"watches\":{\"watchCount\":1,\"isWatching\":false,\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/RSPEC-1212\\/watchers\"},\"customfield_10436\":null,\"customfield_10437\":null,\"customfield_10434\":null,\"customfield_10435\":null,\"customfield_10438\":[],\"assignee\":null,\"customfield_10131\":null,\"customfield_10130\":[{\"id\":\"10227\",\"value\":\"Java\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10227\"}],\"customfield_10030\":null}}";
    Rule rule = new Rule("Java");
    try {
      RuleMaker.populateFields(rule, (JSONObject) parser.parse(json));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(rule.isTemplate()).isTrue();
  }

  @Test
  public void testCFamilyLanguageMatch() {
    assertThat(RuleMaker.isLanguageMatch("C","C-Family")).isTrue();
    assertThat(RuleMaker.isLanguageMatch("C++","Objective-C")).isTrue();

    assertThat(RuleMaker.isLanguageMatch("C", "Charlie")).isFalse();
    assertThat(RuleMaker.isLanguageMatch("C", "C#")).isFalse();

    assertThat(RuleMaker.isLanguageMatch("Java","CPP")).isFalse();
  }

  @Test
  public void testDeprecatedRuleFromJson() {

    String json = "{\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"id\":\"15297\",\"self\":\"http://jira.sonarsource.com/rest/api/latest/issue/15297\",\"key\":\"RSPEC-1127\",\"fields\":{\"issuetype\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issuetype/7\",\"id\":\"7\",\"description\":\"Rule Specification\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/issuetypes/documentation.png\",\"name\":\"Specification\",\"subtask\":false},\"customfield_10030\":\"Replace \\\"[==|!=]\\\" with \\\"!?[equals()|Equals()]\\\" to compare these strings.\",\"project\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/project/10120\",\"id\":\"10120\",\"key\":\"RSPEC\",\"name\":\"Rules Repository\",\"avatarUrls\":{\"48x48\":\"http://jira.sonarsource.com/secure/projectavatar?pid=10120&avatarId=10011\",\"24x24\":\"http://jira.sonarsource.com/secure/projectavatar?size=small&pid=10120&avatarId=10011\",\"16x16\":\"http://jira.sonarsource.com/secure/projectavatar?size=xsmall&pid=10120&avatarId=10011\",\"32x32\":\"http://jira.sonarsource.com/secure/projectavatar?size=medium&pid=10120&avatarId=10011\"}},\"customfield_10232\":null,\"customfield_10430\":null,\"resolution\":null,\"customfield_10431\":null,\"customfield_10630\":null,\"customfield_10432\":null,\"customfield_10433\":null,\"customfield_10631\":null,\"resolutiondate\":null,\"workratio\":-1,\"lastViewed\":null,\"watches\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/RSPEC-1127/watchers\",\"watchCount\":1,\"isWatching\":false},\"created\":\"2013-07-10T13:11:17.000+0000\",\"customfield_10021\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10113\",\"value\":\"Yes\",\"id\":\"10113\"},\"labels\":[\"bug\",\"cwe\"],\"customfield_10258\":null,\"issuelinks\":[{\"id\":\"14322\",\"self\":\"http://jira.sonarsource.com/rest/api/2/issueLink/14322\",\"type\":{\"id\":\"10020\",\"name\":\"Deprecate\",\"inward\":\"is deprecated by\",\"outward\":\"deprecates\",\"self\":\"http://jira.sonarsource.com/rest/api/2/issueLinkType/10020\"},\"inwardIssue\":{\"id\":\"16920\",\"key\":\"RSPEC-1698\",\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/16920\",\"fields\":{\"summary\":\"Objects should be compared with \\\"equals()\\\"\",\"status\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Quality Rule\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"New\"}},\"issuetype\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issuetype/7\",\"id\":\"7\",\"description\":\"Rule Specification\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/issuetypes/documentation.png\",\"name\":\"Specification\",\"subtask\":false}}}}],\"assignee\":null,\"updated\":\"2015-03-31T19:55:38.000+0000\",\"status\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Quality Rule\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"New\"}},\"customfield_10250\":null,\"description\":\"Strings, just like any other <code>Object</code>, should be compared using the <code>equals()|Equals()</code> method.\\r\\nUsing <code>==</code> and <code>!=</code> compares references rather than values, and usually does not work.\\r\\n\\r\\nThe following code:\\r\\n\\r\\nh2. Noncompliant Code Example\\r\\n{code}\\r\\nif (variable == \\\"foo\\\") { /* ... */ }\\r\\nif (variable != \\\"foo\\\") { /* ... */ }\\r\\n{code}\\r\\n\\r\\nh2. Compliant Solution\\r\\n\\r\\n{code title=\\\"Java\\\"}\\r\\nif (\\\"foo\\\".equals(variable)) { /* ... */ }\\r\\nif (!\\\"foo\\\".equals(variable)) { /* ... */ }\\r\\n{code}\\r\\n\\r\\n{code title=\\\"C#\\\"}\\r\\nif (\\\"foo\\\".Equals(variable)) { /* ... */ }\\r\\nif (!\\\"foo\\\".Equals(variable)) { /* ... */ }\\r\\n{code}\\r\\n\\r\\nh2. See\\r\\n* [MITRE, CWE-597|http://cwe.mitre.org/data/definitions/597.html] - Use of Wrong Operator in String Comparison\",\"customfield_10251\":\"CWE-597\",\"customfield_10010\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10071\",\"value\":\"Reliability\",\"id\":\"10071\",\"child\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10076\",\"value\":\"Instruction related reliability\",\"id\":\"10076\"}},\"customfield_10131\":null,\"customfield_10011\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10086\",\"value\":\"Constant/Issue\",\"id\":\"10086\"},\"customfield_10330\":null,\"customfield_10253\":null,\"customfield_10012\":\"5mn\",\"customfield_10013\":null,\"customfield_10255\":null,\"customfield_10530\":null,\"customfield_10014\":null,\"customfield_10256\":null,\"customfield_10015\":\"StringEqualityComparisonCheck\",\"customfield_10257\":null,\"customfield_10005\":null,\"customfield_10248\":null,\"customfield_10007\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10041\",\"value\":\"Critical\",\"id\":\"10041\"},\"customfield_10249\":null,\"summary\":\"Strings should be compared using \\\"[equals()|Equals()]\\\"\",\"creator\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=fabrice.bellingard\",\"name\":\"fabrice.bellingard\",\"emailAddress\":\"fabrice.bellingard@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/ac6d71d8c40f430483b6ec1f1fc9daa0?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/ac6d71d8c40f430483b6ec1f1fc9daa0?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/ac6d71d8c40f430483b6ec1f1fc9daa0?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/ac6d71d8c40f430483b6ec1f1fc9daa0?d=mm&s=32\"},\"displayName\":\"Fabrice Bellingard\",\"active\":true},\"subtasks\":[],\"reporter\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=fabrice.bellingard\",\"name\":\"fabrice.bellingard\",\"emailAddress\":\"fabrice.bellingard@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/ac6d71d8c40f430483b6ec1f1fc9daa0?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/ac6d71d8c40f430483b6ec1f1fc9daa0?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/ac6d71d8c40f430483b6ec1f1fc9daa0?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/ac6d71d8c40f430483b6ec1f1fc9daa0?d=mm&s=32\"},\"displayName\":\"Fabrice Bellingard\",\"active\":true},\"customfield_10242\":null,\"customfield_10001\":[{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10021\",\"value\":\"C#\",\"id\":\"10021\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10014\",\"value\":\"Groovy\",\"id\":\"10014\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10019\",\"value\":\"VB.Net\",\"id\":\"10019\"}],\"customfield_10243\":null,\"customfield_10244\":\"ES_COMPARING_PARAMETER_STRING_WITH_EQ, ES_COMPARING_STRINGS_WITH_EQ\",\"customfield_10245\":null,\"customfield_10004\":[{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10029\",\"value\":\"Java\",\"id\":\"10029\"}],\"customfield_10246\":null,\"customfield_10434\":null,\"customfield_10435\":null,\"customfield_10436\":null,\"customfield_10437\":null,\"customfield_10438\":[],\"comment\":{\"startAt\":0,\"maxResults\":3,\"total\":3,\"comments\":[{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/15297/comment/15777\",\"id\":\"15777\",\"author\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=dinesh.bolkensteyn\",\"name\":\"dinesh.bolkensteyn\",\"emailAddress\":\"dinesh.bolkensteyn@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=32\"},\"displayName\":\"Dinesh Bolkensteyn\",\"active\":true},\"body\":\"I'm changing the severity to Critical as this is most likely a bug that will be caught at runtime.\",\"updateAuthor\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=dinesh.bolkensteyn\",\"name\":\"dinesh.bolkensteyn\",\"emailAddress\":\"dinesh.bolkensteyn@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=32\"},\"displayName\":\"Dinesh Bolkensteyn\",\"active\":true},\"created\":\"2013-07-11T16:28:39.000+0000\",\"updated\":\"2013-07-11T16:28:39.000+0000\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/15297/comment/15778\",\"id\":\"15778\",\"author\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=dinesh.bolkensteyn\",\"name\":\"dinesh.bolkensteyn\",\"emailAddress\":\"dinesh.bolkensteyn@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=32\"},\"displayName\":\"Dinesh Bolkensteyn\",\"active\":true},\"body\":\"Implemented by https://jira.codehaus.org/browse/SONARJAVA-205\",\"updateAuthor\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=dinesh.bolkensteyn\",\"name\":\"dinesh.bolkensteyn\",\"emailAddress\":\"dinesh.bolkensteyn@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=32\"},\"displayName\":\"Dinesh Bolkensteyn\",\"active\":true},\"created\":\"2013-07-11T17:18:18.000+0000\",\"updated\":\"2013-07-11T17:18:18.000+0000\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/15297/comment/15779\",\"id\":\"15779\",\"author\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=dinesh.bolkensteyn\",\"name\":\"dinesh.bolkensteyn\",\"emailAddress\":\"dinesh.bolkensteyn@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=32\"},\"displayName\":\"Dinesh Bolkensteyn\",\"active\":true},\"body\":\"Fabrice, it looks like to me that this can also deprecate some Findbugs checks:\\r\\n\\r\\n ES_COMPARING_STRINGS_WITH_EQ\\r\\n ES_COMPARING_PARAMETER_STRING_WITH_EQ\\r\\n\\r\\nI did not really get the difference between those 2 however\",\"updateAuthor\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=dinesh.bolkensteyn\",\"name\":\"dinesh.bolkensteyn\",\"emailAddress\":\"dinesh.bolkensteyn@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/2c057238a03a3f64736707eee40f9be5?d=mm&s=32\"},\"displayName\":\"Dinesh Bolkensteyn\",\"active\":true},\"created\":\"2013-07-11T17:21:24.000+0000\",\"updated\":\"2013-07-11T17:21:24.000+0000\"}]},\"votes\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/RSPEC-1127/votes\",\"votes\":0,\"hasVoted\":false}},\"names\":{\"issuetype\":\"Issue Type\",\"customfield_10030\":\"Message\",\"project\":\"Project\",\"customfield_10232\":\"Completeness\",\"customfield_10430\":\"ReSharper\",\"resolution\":\"Resolution\",\"customfield_10431\":\"Customer Request Type\",\"customfield_10630\":\"CPPCheck\",\"customfield_10432\":\"Time to resolution\",\"customfield_10433\":\"Golden customer\",\"customfield_10631\":\"Pylint\",\"resolutiondate\":\"Resolved\",\"workratio\":\"Work Ratio\",\"lastViewed\":\"Last Viewed\",\"watches\":\"Watchers\",\"created\":\"Created\",\"customfield_10021\":\"Activated by default\",\"labels\":\"Labels\",\"customfield_10258\":\"MISRA C 2012\",\"issuelinks\":\"Linked Issues\",\"assignee\":\"Assignee\",\"updated\":\"Updated\",\"status\":\"Status\",\"customfield_10250\":\"PHP-FIG\",\"description\":\"Description\",\"customfield_10251\":\"CWE\",\"customfield_10010\":\"SQALE Characteristic\",\"customfield_10131\":\"Applicability\",\"customfield_10011\":\"SQALE Remediation Function\",\"customfield_10330\":\"Implementation details\",\"customfield_10253\":\"OWASP\",\"customfield_10012\":\"SQALE Constant Cost or Linear Threshold\",\"customfield_10013\":\"SQALE Linear Factor\",\"customfield_10255\":\"CERT\",\"customfield_10530\":\"Waiting for customer\",\"customfield_10014\":\"SQALE Linear Offset\",\"customfield_10256\":\"SQALE Linear Argument Description\",\"customfield_10015\":\"Legacy Key\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10005\":\"List of parameters\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10007\":\"Default Severity\",\"customfield_10249\":\"MISRA C++ 2008\",\"summary\":\"Summary\",\"creator\":\"Creator\",\"subtasks\":\"Sub-Tasks\",\"reporter\":\"Reporter\",\"customfield_10242\":\"Template Rule\",\"customfield_10001\":\"Targeted languages\",\"customfield_10243\":\"issueFunction\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10004\":\"Covered Languages\",\"customfield_10246\":\"Checkstyle\",\"customfield_10434\":\"Time to answer\",\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":\"Irrelevant for Languages\",\"customfield_10437\":\"SonarQube version\",\"customfield_10438\":\"Request participants\",\"comment\":\"Comment\",\"votes\":\"Votes\"}}";
    Rule rule = new Rule("Java");
    try {
      RuleMaker.populateFields(rule, (JSONObject) parser.parse(json));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(rule.getStatus()).isEqualTo(Rule.Status.DEPRECATED);
    assertThat(rule.getDeprecationLinks()).hasSize(1);
    assertThat(rule.getDeprecationLinks().get(0)).isEqualTo("RSPEC-1698");
    assertThat(rule.getDeprecation()).endsWith("<p>This rule is deprecated, use {rule:squid:S1698} instead.</p>\n");

  }
}
