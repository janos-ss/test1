/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class JiraHelperTest {

  private static final String FULL_JSON = "{\"id\":\"18166\",\"names\":{\"summary\":\"Summary\",\"issuetype\":\"Issue Type\",\"customfield_10243\":\"issueFunction\",\"customfield_10232\":\"Completeness\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10246\":\"Checkstyle\",\"customfield_10242\":\"Template Rule\",\"reporter\":\"Reporter\",\"customfield_10330\":\"Implementation details\",\"updated\":\"Updated\",\"created\":\"Created\",\"description\":\"Description\",\"customfield_10001\":\"Targeted languages\",\"issuelinks\":\"Linked Issues\",\"customfield_10004\":\"Covered Languages\",\"subtasks\":\"Sub-Tasks\",\"status\":\"Status\",\"customfield_10007\":\"Default Severity\",\"labels\":\"Labels\",\"customfield_10005\":\"List of parameters\",\"customfield_10256\":\"SQALE Linear Argument\",\"workratio\":\"Work Ratio\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10255\":\"CERT\",\"customfield_10253\":\"OWASP\",\"customfield_10250\":\"PHP-FIG\",\"customfield_10251\":\"CWE\",\"project\":\"Project\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10014\":\"SQALE Linear Offset\",\"lastViewed\":\"Last Viewed\",\"customfield_10015\":\"Legacy Key\",\"customfield_10012\":\"SQALE Constant Cost or Linear Threshold\",\"customfield_10013\":\"SQALE Linear Factor\",\"comment\":\"Comment\",\"customfield_10010\":\"SQALE Characteristic\",\"customfield_10011\":\"SQALE Remediation Function\",\"votes\":\"Votes\",\"resolution\":\"Resolution\",\"resolutiondate\":\"Resolved\",\"creator\":\"Creator\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10021\":\"Activated by default\",\"watches\":\"Watchers\",\"assignee\":\"Assignee\",\"customfield_10131\":\"Applicability\",\"customfield_10130\":\"Outdated Languages\",\"customfield_10030\":\"Message\"},\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/issue\\/18166\",\"key\":\"RSPEC-1967\",\"fields\":{\"summary\":\"Values should only be moved to variables large enough to hold them without truncation\",\"issuetype\":{\"subtask\":false,\"id\":\"7\",\"description\":\"Rule Specification\",\"name\":\"Specification\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/issuetypes\\/documentation.png\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issuetype\\/7\"},\"customfield_10243\":null,\"customfield_10232\":{\"id\":\"10324\",\"value\":\"Full\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10324\"},\"customfield_10244\":null,\"customfield_10245\":null,\"customfield_10246\":null,\"customfield_10242\":null,\"reporter\":{\"name\":\"ann.campbell.2\",\"active\":true,\"emailAddress\":\"ann.campbell@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=ann.campbell.2\",\"displayName\":\"Ann Campbell\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\"}},\"customfield_10330\":null,\"updated\":\"2014-10-29T13:23:19.000+0000\",\"created\":\"2014-08-22T18:51:55.000+0000\",\"description\":\"Moving a large value into a small field will result in data truncation for both numeric and alphabetic values. In general, alphabetic values are truncated from the right, while numeric values are truncated from the left. However, in the case of floating point values, when the target field has too little precision to hold the value being moved to it, decimals will be truncated (not rounded!) from the right.\\r\\n\\r\\nIn any case, data loss is always the result when too-large values are moved to too-small fields.\\r\\n\\r\\nh2. Noncompliant Code Example\\r\\n{code}\\r\\n01 NUM-A   PIC 9(2)V9.\\r\\n01 ALPHA   PIC X(4).\\r\\n*> ...\\r\\n\\r\\n    MOVE 88.89   TO NUM-A  *> Noncompliant. Becomes 88.8\\r\\n    MOVE 178.7   TO NUM-A  *> Noncompliant. Becomes 78.7\\r\\n    MOVE 999.99 TO NUM-A  *> Noncompliant. Truncated on both ends; becomes 99.9\\r\\n    MOVE \\\"Now is the time\\\" TO ALPHA *> Noncompliant. Becomes \\\"Now \\\"\\r\\n{code}\\r\\n\\r\\nh2. Compliant Solution\\r\\n{code}\\r\\n01 NUM-A   PIC 9(3)V99.\\r\\n01 ALPHA   PIC X(15).\\r\\n*> ...\\r\\n\\r\\n    MOVE 88.89   TO NUM-A\\r\\n    MOVE 178.7   TO NUM-A\\r\\n    MOVE 999.99 TO NUM-A\\r\\n    MOVE \\\"Now is the time\\\" TO ALPHA\\r\\n{code}\\r\\n\\r\\nh2. See\\r\\n* [MITRE, CWE-704|http:\\/\\/cwe.mitre.org\\/data\\/definitions\\/704] - Incorrect Type Conversion or Cast\",\"customfield_10001\":[{\"id\":\"10010\",\"value\":\"Cobol\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10010\"}],\"issuelinks\":[{\"id\":\"12993\",\"inwardIssue\":{\"id\":\"18509\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/18509\",\"key\":\"COBOL-1131\",\"fields\":{\"summary\":\"Rule \\\"Values should only be moved to variables large enough to hold them without truncation\\\"\",\"issuetype\":{\"subtask\":false,\"id\":\"2\",\"description\":\"A new feature of the product, which has yet to be developed.\",\"name\":\"New Feature\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/issuetypes\\/newfeature.png\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issuetype\\/2\"},\"status\":{\"id\":\"6\",\"description\":\"The issue is considered finished, the resolution is correct. Issues which are closed can be reopened.\",\"name\":\"Closed\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/statuses\\/closed.png\",\"statusCategory\":{\"id\":3,\"colorName\":\"green\",\"name\":\"Complete\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/statuscategory\\/3\",\"key\":\"done\"},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/status\\/6\"},\"priority\":{\"id\":\"3\",\"name\":\"Major\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/priorities\\/major.png\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/priority\\/3\"}}},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issueLink\\/12993\",\"type\":{\"id\":\"10010\",\"outward\":\"implements\",\"inward\":\"is implemented by\",\"name\":\"Rule specification\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issueLinkType\\/10010\"}}],\"customfield_10004\":null,\"subtasks\":[],\"status\":{\"id\":\"10000\",\"description\":\"Active Quality Rule\",\"name\":\"Active\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/statuses\\/open.png\",\"statusCategory\":{\"id\":2,\"colorName\":\"blue-gray\",\"name\":\"New\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/statuscategory\\/2\",\"key\":\"new\"},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/status\\/10000\"},\"customfield_10007\":{\"id\":\"10041\",\"value\":\"Critical\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10041\"},\"labels\":[\"bug\"],\"customfield_10005\":\"* key: onlyLiteralValues\\r\\n** description: True to apply the rule only to literal values\\r\\n** default: false\\r\\n* key: ignoredDataItemRegex\\r\\n** description: Regular expression describing sending fields to ignore \",\"customfield_10256\":null,\"workratio\":-1,\"customfield_10257\":null,\"customfield_10255\":null,\"customfield_10253\":null,\"customfield_10250\":null,\"customfield_10251\":\"704\",\"project\":{\"id\":\"10120\",\"name\":\"Rules Repository\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/project\\/10120\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=xsmall&pid=10120&avatarId=10011\",\"24x24\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=small&pid=10120&avatarId=10011\",\"32x32\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=medium&pid=10120&avatarId=10011\",\"48x48\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?pid=10120&avatarId=10011\"},\"key\":\"RSPEC\"},\"customfield_10249\":null,\"customfield_10248\":null,\"customfield_10014\":null,\"lastViewed\":null,\"customfield_10015\":null,\"customfield_10012\":\"30min\",\"customfield_10013\":null,\"comment\":{\"total\":1,\"startAt\":0,\"comments\":[{\"id\":\"20121\",\"body\":\"@Ann, perhaps we could associate this rule to http:\\/\\/cwe.mitre.org\\/data\\/definitions\\/704.html ? This is a bit controversial as CWE-704 is Weakness Class.\",\"author\":{\"name\":\"freddy.mallet\",\"active\":true,\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=freddy.mallet\",\"displayName\":\"Freddy Mallet\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\"}},\"updated\":\"2014-09-19T15:42:06.000+0000\",\"created\":\"2014-09-19T15:42:06.000+0000\",\"updateAuthor\":{\"name\":\"freddy.mallet\",\"active\":true,\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=freddy.mallet\",\"displayName\":\"Freddy Mallet\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\"}},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/18166\\/comment\\/20121\"}],\"maxResults\":1},\"customfield_10010\":{\"child\":{\"id\":\"10073\",\"value\":\"Data related reliability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10073\"},\"id\":\"10071\",\"value\":\"Reliability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10071\"},\"customfield_10011\":{\"id\":\"10086\",\"value\":\"Constant\\/Issue\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10086\"},\"votes\":{\"hasVoted\":false,\"votes\":0,\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/RSPEC-1967\\/votes\"},\"resolution\":null,\"resolutiondate\":null,\"creator\":{\"name\":\"ann.campbell.2\",\"active\":true,\"emailAddress\":\"ann.campbell@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=ann.campbell.2\",\"displayName\":\"Ann Campbell\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\"}},\"customfield_10258\":null,\"customfield_10021\":{\"id\":\"10113\",\"value\":\"Yes\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10113\"},\"watches\":{\"watchCount\":2,\"isWatching\":false,\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/RSPEC-1967\\/watchers\"},\"assignee\":null,\"customfield_10131\":[{\"id\":\"10241\",\"value\":\"Sources\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10241\"},{\"id\":\"10242\",\"value\":\"Tests\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10242\"}],\"customfield_10130\":null,\"customfield_10030\":\"Increase the size of \\\"YYY\\\" or do not \\\"MOVE\\\" (\\\"XXX\\\"|this literal value) to it.\"}}";
  private static final String SQ_JSON_NO_INTERNAL_KEY = "{\"key\":\"php:S1996\",\"repo\":\"php\",\"name\":\"Files should contain only one class or interface each\",\"createdAt\":\"2014-11-21T07:03:46+0000\",\"severity\":\"MAJOR\",\"status\":\"READY\",\"isTemplate\":false,\"tags\":[],\"sysTags\":[\"brain-overload\"],\"lang\":\"php\",\"langName\":\"PHP\",\"htmlDesc\":\"<p>\\n  A file that grows too much tends to aggregate too many responsibilities\\n  and inevitably becomes harder to understand and therefore to maintain. This is doubly true for a file with multiple independent classes and interfaces. It is strongly advised to divide the file into one independent class or interface per file.\\n</p>\",\"defaultDebtChar\":\"MAINTAINABILITY\",\"defaultDebtSubChar\":\"UNDERSTANDABILITY\",\"debtChar\":\"MAINTAINABILITY\",\"debtSubChar\":\"UNDERSTANDABILITY\",\"debtCharName\":\"Maintainability\",\"debtSubCharName\":\"Understandability\",\"defaultDebtRemFnType\":\"LINEAR\",\"defaultDebtRemFnCoeff\":\"10min\",\"debtOverloaded\":false,\"debtRemFnType\":\"LINEAR\",\"debtRemFnCoeff\":\"10min\",\"params\":[]}";


  JSONParser parser = new JSONParser();



  @Test
  public void testCustomFieldValueSadPath() {

    try {
      assertThat(JiraHelper.getCustomFieldValue((JSONObject) parser.parse("{}"), "foo")).isNull();
      assertThat(JiraHelper.getCustomFieldValue((JSONObject) parser.parse(FULL_JSON), null)).isNull();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testListFromJsonFieldValueNull() {

    try {
      assertThat(JiraHelper.getListFromJsonFieldValue((JSONObject) parser.parse("{}"), "foo")).isEmpty();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetJsonField() {

    try {
      assertThat(JiraHelper.getJsonField((JSONObject) parser.parse(FULL_JSON), "customfield_10007")).isNotNull();
      assertThat(JiraHelper.getJsonField((JSONObject) parser.parse(FULL_JSON), "SQALE Characteristic")).isNotNull();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetJsonFieldSadPath() {

    try {
      assertThat(JiraHelper.getJsonField((JSONObject) parser.parse("{}"), "foo")).isNull();
      assertThat(JiraHelper.getJsonField((JSONObject) parser.parse(FULL_JSON), null)).isNull();
      assertThat(JiraHelper.getJsonField((JSONObject) parser.parse(FULL_JSON), "foo")).isNull();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetJsonFieldValueNulls() {

    try {
      assertThat(JiraHelper.getJsonFieldValue((JSONObject) parser.parse("{}"), "foo")).isNull();
      assertThat(JiraHelper.getJsonFieldValue((JSONObject) parser.parse(FULL_JSON), null)).isNull();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testHandleParameterListNullString() throws Exception {
    List<Parameter> empty = new ArrayList<Parameter>();
    assertThat(JiraHelper.handleParameterList(null, "Java")).hasSize(0);
  }

  @Test
  public void testHandleParameterList() throws Exception {
    String paramString = "* key: complexity_threshold\r\n* type = text\r\n** Description: The minimum complexity at which this rule will be triggered.\r\n** Default: 250";
    List<Parameter> paramList = JiraHelper.handleParameterList(paramString, "Java");
    assertThat(paramList).hasSize(1);
  }

  @Test
  public void testHandleParameterListEmptyString() throws Exception {
    List<Parameter> paramList = JiraHelper.handleParameterList("", "Java");
    assertThat(paramList).hasSize(0);
  }

  @Test
  public void testHandleParameterListMultilanguage() throws Exception {
    String paramString = "Key: format \r\nDescription: Regular expression used to check the names against. \r\nDefault Value for Java : ^[a-z][a-zA-Z0-9]*$ \r\nDefault Value for Flex : ^[_a-z][a-zA-Z0-9]*$";
    List<Parameter> paramList = JiraHelper.handleParameterList(paramString, "Java");
    assertThat(paramList.get(0).getDefaultVal()).isEqualTo("^[a-z][a-zA-Z0-9]*$");
  }

  @Test
  public void testHandleParameterListNoKeyLabel() throws Exception {
    String paramString = "* indentSize \r\n** Description: Number of white-spaces of an indent. If this property is not set, we just check that the code is indented. \r\n** Default value: none \r\n* tabWidth \r\n** Description: Equivalent number of spaces of a tabulation \r\n** Default value: 2\r\n";
    List<Parameter> paramList = JiraHelper.handleParameterList(paramString, "Java");
    assertThat(paramList).hasSize(2);
    assertThat(paramList.get(0).getKey()).isEqualTo("indentSize");
  }

  @Test
  public void testHandleParameterListUnknownLabel() throws Exception {
    String paramString = "Key: format \r\nDescription: Regular expression used to check the names against. \r\nDefault Value for Java : ^[a-z][a-zA-Z0-9]*$ \r\nDefault Value for Flex : ^[_a-z][a-zA-Z0-9]*$\r\ntpye:text";
    List<Parameter> paramList = JiraHelper.handleParameterList(paramString, "Java");
    assertThat(paramList.get(0).getType()).isNull();
  }

  @Test
  public void testTidyParamLabel() throws Exception {

    assertThat(JiraHelper.tidyParamLabel(null)).isNull();
  }


  @Test
  public void testStringToListNull() {

    assertThat(JiraHelper.stringToList(null)).hasSize(0);
  }

  @Test
  public void testStringToListEmpty() {

    assertThat(JiraHelper.stringToList("")).hasSize(0);
  }

  @Test
  public void testStringToListOne() {

    assertThat(JiraHelper.stringToList("yo")).hasSize(1);
  }

  @Test
  public void testStringToListComma() {

    assertThat(JiraHelper.stringToList("hello, you")).hasSize(2);
  }

  @Test
  public void testStringToListAmps() {

    assertThat(JiraHelper.stringToList("hello && you")).hasSize(2);
  }

  @Test
  public void testStringToListAmp() {

    assertThat(JiraHelper.stringToList("hello & you")).hasSize(2);
  }

  @Test
  public void testStringToListAmpAndComma() {

    assertThat(JiraHelper.stringToList("hello, you & you")).hasSize(3);
  }


  @Test
  public void testSetReferences() {

    String json = "{\"id\":\"19078\",\"names\":{\"summary\":\"Summary\",\"issuetype\":\"Issue Type\",\"customfield_10243\":\"issueFunction\",\"customfield_10232\":\"Completeness\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10246\":\"Checkstyle\",\"customfield_10242\":\"Template Rule\",\"reporter\":\"Reporter\",\"customfield_10330\":\"Implementation details\",\"updated\":\"Updated\",\"created\":\"Created\",\"description\":\"Description\",\"customfield_10001\":\"Targeted languages\",\"issuelinks\":\"Linked Issues\",\"customfield_10004\":\"Covered Languages\",\"subtasks\":\"Sub-Tasks\",\"status\":\"Status\",\"customfield_10007\":\"Default Severity\",\"labels\":\"Labels\",\"customfield_10005\":\"List of parameters\",\"customfield_10256\":\"SQALE Linear Argument\",\"workratio\":\"Work Ratio\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10255\":\"CERT\",\"customfield_10253\":\"OWASP\",\"customfield_10250\":\"PHP-FIG\",\"customfield_10251\":\"CWE\",\"project\":\"Project\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10014\":\"SQALE Linear Offset\",\"lastViewed\":\"Last Viewed\",\"customfield_10015\":\"Legacy Key\",\"customfield_10012\":\"SQALE Constant Cost or Linear Threshold\",\"customfield_10013\":\"SQALE Linear Factor\",\"comment\":\"Comment\",\"customfield_10010\":\"SQALE Characteristic\",\"customfield_10011\":\"SQALE Remediation Function\",\"votes\":\"Votes\",\"resolution\":\"Resolution\",\"resolutiondate\":\"Resolved\",\"creator\":\"Creator\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10021\":\"Activated by default\",\"watches\":\"Watchers\",\"assignee\":\"Assignee\",\"customfield_10131\":\"Applicability\",\"customfield_10130\":\"Outdated Languages\",\"customfield_10030\":\"Message\"},\"key\":\"RSPEC-2210\",\"fields\":{\"summary\":\"Anntest dummy rule asdf\",\"customfield_10244\":\"ASDF-PDQ\",\"customfield_10257\":\"findsecbuts\",\"customfield_10255\":\"cert...\",\"customfield_10253\":\"CWE-123\",\"customfield_10251\":\"CWE-123\",\"customfield_10249\":\"8.9\",\"customfield_10248\":\"0.0\",\"customfield_10258\":\"0-0-0\",\"customfield_10245\":\"pmd\",\"customfield_10246\":\"checkstyle\",\"customfield_10250\":\"mission-fig\"}}";

    Rule rule = new Rule("");
    try {
      JiraHelper.setReferences(rule, (JSONObject) parser.parse(json));
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
      JiraHelper.setSqale(rule, (JSONObject) parser.parse(json));
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
  public void testSetDefaultProfiles() {

    String json = "{\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"id\":\"19078\",\"self\":\"http://jira.sonarsource.com/rest/api/latest/issue/19078\",\"key\":\"RSPEC-2210\",\"fields\":{\"issuetype\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issuetype/7\",\"id\":\"7\",\"description\":\"Rule Specification\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/issuetypes/documentation.png\",\"name\":\"Specification\",\"subtask\":false},\"customfield_10030\":\"message\",\"project\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/project/10120\",\"id\":\"10120\",\"key\":\"RSPEC\",\"name\":\"Rules Repository\",\"avatarUrls\":{\"48x48\":\"http://jira.sonarsource.com/secure/projectavatar?pid=10120&avatarId=10011\",\"24x24\":\"http://jira.sonarsource.com/secure/projectavatar?size=small&pid=10120&avatarId=10011\",\"16x16\":\"http://jira.sonarsource.com/secure/projectavatar?size=xsmall&pid=10120&avatarId=10011\",\"32x32\":\"http://jira.sonarsource.com/secure/projectavatar?size=medium&pid=10120&avatarId=10011\"}},\"customfield_10232\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10324\",\"value\":\"Full\",\"id\":\"10324\"},\"customfield_10430\":\"reeee-SHARper!\",\"resolution\":null,\"customfield_10431\":null,\"customfield_10630\":\"Porky Pig\",\"customfield_10432\":null,\"customfield_10433\":null,\"customfield_10631\":\"Pylint\",\"resolutiondate\":null,\"workratio\":-1,\"lastViewed\":null,\"watches\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/RSPEC-2210/watchers\",\"watchCount\":2,\"isWatching\":false},\"created\":\"2014-11-04T19:17:19.000+0000\",\"customfield_10021\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10114\",\"value\":\"No\",\"id\":\"10114\"},\"labels\":[\"bug\",\"cert\",\"clumsy\",\"cwe\",\"misra\",\"obsolete\",\"owasp-a6\",\"performance\",\"pitfall\",\"security\"],\"customfield_10258\":\"8.9\",\"issuelinks\":[],\"assignee\":null,\"updated\":\"2015-05-21T20:02:08.000+0000\",\"status\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Quality Rule\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"New\"}},\"customfield_10250\":\"mission-fig\",\"description\":\"aldjfl asjd lkjva;lskjeljz fnvw;uzoin j valiue nvakej\\r\\n\\r\\nh2. See \\r\\n* [MITRE, CWE-123|http://cwe.toto.com] - Title\\r\\n* [OWASP Top Ten 2013 Category A6|https://www.owasp.org/index.php/Top_10_2013-A6-Sensitive_Data_Exposure] - Sensitive Data Exposure\",\"customfield_10251\":\"CWE-123\",\"customfield_10010\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10049\",\"value\":\"Portability\",\"id\":\"10049\",\"child\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10050\",\"value\":\"Compiler related portability\",\"id\":\"10050\"}},\"customfield_10131\":[{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10241\",\"value\":\"Sources\",\"id\":\"10241\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10242\",\"value\":\"Tests\",\"id\":\"10242\"}],\"customfield_10011\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10086\",\"value\":\"Constant/Issue\",\"id\":\"10086\"},\"customfield_10330\":null,\"customfield_10253\":\"A12-Blah, A6\",\"customfield_10012\":\"2d\",\"customfield_10013\":null,\"customfield_10255\":\"cert...\",\"customfield_10530\":null,\"customfield_10014\":null,\"customfield_10256\":null,\"customfield_10015\":null,\"customfield_10257\":\"findsecbuts\",\"customfield_10730\":null,\"customfield_10005\":\"* key: key\\r\\n* default: default\\r\\n* description: description\",\"customfield_10248\":\"0.0\",\"customfield_10007\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10040\",\"value\":\"Blocker\",\"id\":\"10040\"},\"customfield_10249\":\"0-0-0\",\"summary\":\"Anntest dummy rule asdf\",\"creator\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=ann.campbell.2\",\"name\":\"ann.campbell.2\",\"emailAddress\":\"ann.campbell@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\"},\"displayName\":\"Ann Campbell\",\"active\":true},\"subtasks\":[],\"reporter\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=ann.campbell.2\",\"name\":\"ann.campbell.2\",\"emailAddress\":\"ann.campbell@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\"},\"displayName\":\"Ann Campbell\",\"active\":true},\"customfield_10242\":null,\"customfield_10001\":null,\"customfield_10243\":null,\"customfield_10244\":\"yowza!\",\"customfield_10245\":\"pmd\",\"customfield_10004\":null,\"customfield_10246\":\"checkstyle\"," +
            "\"customfield_10830\":[" +
            "{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10620\",\"value\":\"Bogus Way\",\"id\":\"10622\"}," +
            "{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10620\",\"value\":\"SonarQube Way\",\"id\":\"10620\"}," +
            "{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10621\",\"value\":\"Security Way\",\"id\":\"10621\"}],\"customfield_10434\":null,\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":null,\"customfield_10437\":null,\"customfield_10438\":[],\"comment\":{\"startAt\":0,\"maxResults\":3,\"total\":3,\"comments\":[{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/19078/comment/22389\",\"id\":\"22389\",\"author\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"body\":\"Can we remove this RSPEC @Ann ? :)\",\"updateAuthor\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"created\":\"2015-01-29T13:27:47.000+0000\",\"updated\":\"2015-01-29T13:27:47.000+0000\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/19078/comment/22390\",\"id\":\"22390\",\"author\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=ann.campbell.2\",\"name\":\"ann.campbell.2\",\"emailAddress\":\"ann.campbell@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\"},\"displayName\":\"Ann Campbell\",\"active\":true},\"body\":\"I know it's irritating, but I'd like to keep it for just a little longer [~freddy.mallet]\",\"updateAuthor\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=ann.campbell.2\",\"name\":\"ann.campbell.2\",\"emailAddress\":\"ann.campbell@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\"},\"displayName\":\"Ann Campbell\",\"active\":true},\"created\":\"2015-01-29T13:37:35.000+0000\",\"updated\":\"2015-01-29T13:37:35.000+0000\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/19078/comment/22591\",\"id\":\"22591\",\"author\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"body\":\"Ok, no problem [~ann.campbell.2]\",\"updateAuthor\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"created\":\"2015-02-04T09:53:57.000+0000\",\"updated\":\"2015-02-04T09:53:57.000+0000\"}]},\"votes\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/RSPEC-2210/votes\",\"votes\":0,\"hasVoted\":false}},\"names\":{\"issuetype\":\"Issue Type\",\"customfield_10030\":\"Message\",\"project\":\"Project\",\"customfield_10232\":\"Completeness\",\"customfield_10430\":\"ReSharper\",\"resolution\":\"Resolution\",\"customfield_10431\":\"Customer Request Type\",\"customfield_10630\":\"CPPCheck\",\"customfield_10432\":\"Time to resolution\",\"customfield_10433\":\"Golden customer\",\"customfield_10631\":\"Pylint\",\"resolutiondate\":\"Resolved\",\"workratio\":\"Work Ratio\",\"lastViewed\":\"Last Viewed\",\"watches\":\"Watchers\",\"created\":\"Created\",\"customfield_10021\":\"Activated by default\",\"labels\":\"Labels\",\"customfield_10258\":\"MISRA C 2012\",\"issuelinks\":\"Linked Issues\",\"assignee\":\"Assignee\",\"updated\":\"Updated\",\"status\":\"Status\",\"customfield_10250\":\"PHP-FIG\",\"description\":\"Description\",\"customfield_10251\":\"CWE\",\"customfield_10010\":\"SQALE Characteristic\",\"customfield_10131\":\"Applicability\",\"customfield_10011\":\"SQALE Remediation Function\",\"customfield_10330\":\"Implementation details\",\"customfield_10253\":\"OWASP\",\"customfield_10012\":\"SQALE Constant Cost or Linear Threshold\",\"customfield_10013\":\"SQALE Linear Factor\",\"customfield_10255\":\"CERT\",\"customfield_10530\":\"Waiting for customer\",\"customfield_10014\":\"SQALE Linear Offset\",\"customfield_10256\":\"SQALE Linear Argument Description\",\"customfield_10015\":\"Legacy Key\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10730\":\"Issue in trouble\",\"customfield_10005\":\"List of parameters\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10007\":\"Default Severity\",\"customfield_10249\":\"MISRA C++ 2008\",\"summary\":\"Summary\",\"creator\":\"Creator\",\"subtasks\":\"Sub-Tasks\",\"reporter\":\"Reporter\",\"customfield_10242\":\"Template Rule\",\"customfield_10001\":\"Targeted languages\",\"customfield_10243\":\"issueFunction\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10004\":\"Covered Languages\",\"customfield_10246\":\"Checkstyle\",\"customfield_10830\":\"Default Quality Profiles\",\"customfield_10434\":\"Time to answer\",\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":\"Irrelevant for Languages\",\"customfield_10437\":\"SonarQube version\",\"customfield_10438\":\"Request participants\",\"comment\":\"Comment\",\"votes\":\"Votes\"}}";

    JSONObject issue = null;
    try {
      issue = (JSONObject) parser.parse(json);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(JiraHelper.getCustomFieldStoredAsList(issue, "Default Quality Profiles")).hasSize(3);

    Rule rule = new Rule("Java");
    RuleMaker.populateFields(rule, issue);
    assertThat(rule.getDefaultProfiles()).hasSize(2);
    assertThat(rule.getDefaultProfiles()).contains(Rule.Profile.SECURITY);
    assertThat(rule.getDefaultProfiles()).contains(Rule.Profile.SONARQUBE);
  }
}
