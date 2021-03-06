/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


public class JiraHelperTest {

  private static final String FULL_JSON = "{\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"id\":\"18166\",\"self\":\"https://jira.sonarsource.com/rest/api/latest/issue/18166\",\"key\":\"RSPEC-1967\",\"fields\":{\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/10403\",\"id\":\"10403\",\"description\":\"\",\"iconUrl\":\"https://jira.sonarsource.com/secure/viewavatar?size=xsmall&avatarId=10386&avatarType=issuetype\",\"name\":\"Bug Detection\",\"subtask\":false,\"avatarId\":10386},\"customfield_11041\":\"0|i006dj:\",\"customfield_10030\":\"Increase the size of \\\"YYY\\\" or do not \\\"MOVE\\\" (\\\"XXX\\\"|this literal value) to it.\",\"customfield_11042\":null,\"customfield_10232\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10324\",\"value\":\"Full\",\"id\":\"10324\"},\"customfield_10430\":null,\"resolution\":null,\"customfield_10431\":null,\"customfield_10630\":null,\"customfield_10432\":null,\"customfield_10433\":null,\"customfield_10631\":null,\"resolutiondate\":null,\"workratio\":-1,\"lastViewed\":null,\"created\":\"2014-08-22T18:51:55.000+0200\",\"customfield_11030\":null,\"customfield_11031\":null,\"customfield_11032\":null,\"customfield_11230\":null,\"customfield_11033\":null,\"customfield_11231\":null,\"customfield_11233\":null,\"customfield_11036\":null,\"customfield_11234\":null,\"labels\":[\"bug\",\"cwe\"],\"customfield_11038\":null,\"customfield_10258\":null,\"customfield_10930\":null,\"customfield_10931\":null,\"customfield_10932\":null,\"issuelinks\":[{\"id\":\"12993\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issueLink/12993\",\"type\":{\"id\":\"10010\",\"name\":\"Rule specification\",\"inward\":\"is implemented by\",\"outward\":\"implements\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issueLinkType/10010\"},\"inwardIssue\":{\"id\":\"18509\",\"key\":\"COBOL-1131\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issue/18509\",\"fields\":{\"summary\":\"Rule \\\"Values should only be moved to variables large enough to hold them without truncation\\\"\",\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/6\",\"description\":\"The issue is considered finished, the resolution is correct. Issues which are closed can be reopened.\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/closed.png\",\"name\":\"Closed\",\"id\":\"6\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/3\",\"id\":3,\"key\":\"done\",\"colorName\":\"green\",\"name\":\"Done\"}},\"priority\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/priority/3\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/priorities/major.png\",\"name\":\"Major\",\"id\":\"3\"},\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/2\",\"id\":\"2\",\"description\":\"A new feature of the product, which has yet to be developed.\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/issuetypes/newfeature.png\",\"name\":\"New Feature\",\"subtask\":false}}}}],\"updated\":\"2016-08-02T21:02:46.000+0200\",\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Rule\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"To Do\"}},\"customfield_11140\":null,\"customfield_10250\":null,\"customfield_11141\":null,\"customfield_11340\":\"Low\",\"description\":\"Moving a large value into a small field will result in data truncation for both numeric and alphabetic values. In general, alphabetic values are truncated from the right, while numeric values are truncated from the left. However, in the case of floating point values, when the target field has too little precision to hold the value being moved to it, decimals will be truncated (not rounded!) from the right.\\r\\n\\r\\nIn any case, data loss is always the result when too-large values are moved to too-small fields.\\r\\n\\r\\nh2. Noncompliant Code Example\\r\\n{code}\\r\\n01 NUM-A   PIC 9(2)V9.\\r\\n01 ALPHA   PIC X(4).\\r\\n*> ...\\r\\n\\r\\n    MOVE 88.89   TO NUM-A  *> Noncompliant. Becomes 88.8\\r\\n    MOVE 178.7   TO NUM-A  *> Noncompliant. Becomes 78.7\\r\\n    MOVE 999.99 TO NUM-A  *> Noncompliant. Truncated on both ends; becomes 99.9\\r\\n    MOVE \\\"Now is the time\\\" TO ALPHA *> Noncompliant. Becomes \\\"Now \\\"\\r\\n{code}\\r\\n\\r\\nh2. Compliant Solution\\r\\n{code}\\r\\n01 NUM-A   PIC 9(3)V99.\\r\\n01 ALPHA   PIC X(15).\\r\\n*> ...\\r\\n\\r\\n    MOVE 88.89   TO NUM-A\\r\\n    MOVE 178.7   TO NUM-A\\r\\n    MOVE 999.99 TO NUM-A\\r\\n    MOVE \\\"Now is the time\\\" TO ALPHA\\r\\n{code}\\r\\n\\r\\nh2. See\\r\\n* [MITRE, CWE-704|http://cwe.mitre.org/data/definitions/704] - Incorrect Type Conversion or Cast\",\"customfield_10251\":\"CWE-704\",\"customfield_10011\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10086\",\"value\":\"Constant/Issue\",\"id\":\"10086\"},\"customfield_10330\":null,\"customfield_10253\":null,\"customfield_11341\":\"High\",\"customfield_10012\":\"30min\",\"customfield_10013\":null,\"customfield_10255\":null,\"customfield_10530\":null,\"customfield_10014\":null,\"customfield_10256\":null,\"customfield_10015\":null,\"customfield_10257\":null,\"customfield_10730\":null,\"customfield_10005\":\"* key: onlyLiteralValues\\r\\n** description: True to apply the rule only to literal values\\r\\n** default: false\\r\\n* key: ignoredDataItemRegex\\r\\n** description: Regular expression describing sending fields to ignore \",\"customfield_11138\":null,\"customfield_11337\":null,\"customfield_10248\":null,\"customfield_11139\":null,\"customfield_11336\":null,\"customfield_10007\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10042\",\"value\":\"Major\",\"id\":\"10042\"},\"customfield_10249\":null,\"customfield_11338\":null,\"summary\":\"Values should only be moved to variables large enough to hold them without truncation\",\"subtasks\":[],\"customfield_11130\":null,\"customfield_11131\":null,\"customfield_11132\":null,\"customfield_11331\":null,\"customfield_10242\":null,\"customfield_11133\":null,\"customfield_11330\":null,\"customfield_10001\":null,\"customfield_10243\":null,\"customfield_11134\":null,\"customfield_11333\":null,\"customfield_10244\":null,\"customfield_11332\":null,\"customfield_10245\":null,\"customfield_11335\":null,\"customfield_10004\":[{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10026\",\"value\":\"Cobol\",\"id\":\"10026\"}],\"customfield_10246\":null,\"customfield_11334\":null,\"customfield_10830\":[{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10620\",\"value\":\"Sonar way\",\"id\":\"10620\"}],\"customfield_10434\":null,\"customfield_10435\":null,\"customfield_10436\":null,\"customfield_10437\":null,\"customfield_10438\":[]},\"names\":{\"issuetype\":\"Issue Type\",\"customfield_11041\":\"Rank\",\"customfield_10030\":\"Message\",\"customfield_11042\":\"FxCop\",\"customfield_10232\":\"Completeness\",\"customfield_10430\":\"ReSharper\",\"resolution\":\"Resolution\",\"customfield_10431\":\"Customer Request Type\",\"customfield_10630\":\"CPPCheck\",\"customfield_10432\":\"Time to resolution\",\"customfield_10433\":\"Golden customer\",\"customfield_10631\":\"Pylint\",\"resolutiondate\":\"Resolved\",\"workratio\":\"Work Ratio\",\"lastViewed\":\"Last Viewed\",\"created\":\"Created\",\"customfield_11030\":\"PC-Lint\",\"customfield_11031\":\"Quick-fixes\",\"customfield_11032\":\"Flagged\",\"customfield_11230\":\"VisualStudio\",\"customfield_11033\":\"Epic/Theme\",\"customfield_11231\":\"PVS-Studio\",\"customfield_11233\":\"Qualification\",\"customfield_11036\":\"Sprint\",\"customfield_11234\":\"MSFT Roslyn\",\"labels\":\"Labels\",\"customfield_11038\":\"Epic Link\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10930\":\"Source ID\",\"customfield_10931\":\"Testcase included\",\"customfield_10932\":\"Patch Submitted\",\"issuelinks\":\"Linked Issues\",\"updated\":\"Updated\",\"status\":\"Status\",\"customfield_11140\":\"Waiting for customer - 7 days\",\"customfield_10250\":\"PHP-FIG\",\"customfield_11141\":\"Waiting for customer - 9 days\",\"customfield_11340\":\"Impact\",\"description\":\"Description\",\"customfield_10251\":\"CWE\",\"customfield_10011\":\"SQALE Remediation Function\",\"customfield_10330\":\"Implementation details\",\"customfield_10253\":\"OWASP\",\"customfield_11341\":\"Likelihood\",\"customfield_10012\":\"SQALE Constant Cost\",\"customfield_10013\":\"SQALE Linear Factor\",\"customfield_10255\":\"CERT\",\"customfield_10530\":\"Waiting for customer\",\"customfield_10014\":\"SQALE Linear Offset\",\"customfield_10256\":\"SQALE Linear Argument Description\",\"customfield_10015\":\"Legacy Key\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10730\":\"Issue in trouble\",\"customfield_10005\":\"List of parameters\",\"customfield_11138\":\"Signatories\",\"customfield_11337\":\"Working Place\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_11139\":\"Waiting for customer new\",\"customfield_11336\":\"Email\",\"customfield_10007\":\"Default Severity\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_11338\":\"Quantity\",\"summary\":\"Summary\",\"subtasks\":\"Sub-Tasks\",\"customfield_11130\":\"First customer SLA\",\"customfield_11131\":\"Second customer SLA\",\"customfield_11132\":\"Third customer SLA\",\"customfield_11331\":\"ESLint\",\"customfield_10242\":\"Template Rule\",\"customfield_11133\":\"Test SLA\",\"customfield_11330\":\"Analysis Scope\",\"customfield_10001\":\"Targeted languages\",\"customfield_10243\":\"issueFunction\",\"customfield_11134\":\"Highlighting\",\"customfield_11333\":\"CppCoreGuidelines\",\"customfield_10244\":\"FindBugs\",\"customfield_11332\":\"JSHint\",\"customfield_10245\":\"PMD\",\"customfield_11335\":\"Surname\",\"customfield_10004\":\"Covered Languages\",\"customfield_10246\":\"Checkstyle\",\"customfield_11334\":\"First Name\",\"customfield_10830\":\"Default Quality Profiles\",\"customfield_10434\":\"Time to answer\",\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":\"Irrelevant for Languages\",\"customfield_10437\":\"SonarQube version\",\"customfield_10438\":\"Request participants\"}}";

  private static final String FULL_VULN_DEFAULT_PROFILE_JSON = "{\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"id\":\"49965\",\"self\":\"https://jira.sonarsource.com/rest/api/latest/issue/49965\",\"key\":\"RSPEC-3649\",\"fields\":{\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/10406\",\"id\":\"10406\",\"description\":\"\",\"iconUrl\":\"https://jira.sonarsource.com/secure/viewavatar?size=xsmall&avatarId=10386&avatarType=issuetype\",\"name\":\"Vulnerability Detection\",\"subtask\":false,\"avatarId\":10386},\"customfield_11041\":\"0|i05njv:\",\"customfield_10030\":\"* \\\"xxx\\\" is provided by the user and not sanitized before use\",\"customfield_11042\":null,\"customfield_10232\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10324\",\"value\":\"Full\",\"id\":\"10324\"},\"customfield_10430\":null,\"resolution\":null,\"customfield_10431\":null,\"customfield_10630\":null,\"customfield_10432\":null,\"customfield_10433\":null,\"customfield_10631\":null,\"resolutiondate\":null,\"workratio\":-1,\"lastViewed\":null,\"created\":\"2016-06-17T14:07:40.000+0200\",\"customfield_11030\":null,\"customfield_11031\":null,\"customfield_11032\":null,\"customfield_11230\":null,\"customfield_11033\":null,\"customfield_11231\":null,\"customfield_11233\":null,\"customfield_11036\":null,\"customfield_11234\":null,\"labels\":[\"cert\",\"cwe\",\"owasp-a1\",\"sans-top25-insecure\",\"security\",\"sql\"],\"customfield_11038\":null,\"customfield_10258\":null,\"customfield_10930\":null,\"customfield_10931\":null,\"customfield_10932\":null,\"issuelinks\":[{\"id\":\"24423\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issueLink/24423\",\"type\":{\"id\":\"10121\",\"name\":\"Supercedes\",\"inward\":\"is superceded by\",\"outward\":\"supercedes\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issueLinkType/10121\"},\"outwardIssue\":{\"id\":\"18641\",\"key\":\"RSPEC-2077\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issue/18641\",\"fields\":{\"summary\":\"SQL binding mechanisms should be used\",\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Rule\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"To Do\"}},\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/10406\",\"id\":\"10406\",\"description\":\"\",\"iconUrl\":\"https://jira.sonarsource.com/secure/viewavatar?size=xsmall&avatarId=10386&avatarType=issuetype\",\"name\":\"Vulnerability Detection\",\"subtask\":false,\"avatarId\":10386}}}}],\"updated\":\"2016-08-22T16:41:21.000+0200\",\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Rule\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"To Do\"}},\"customfield_11140\":null,\"customfield_10250\":null,\"customfield_11141\":null,\"customfield_11340\":\"High\",\"description\":\"Applications that execute SQL commands should neutralize any externally-provided values used in those commands. Failure to do so could allow an attacker to include input that changes the query so that unintended commands are executed, or sensitive data is exposed.\\r\\n\\r\\nThis rule raises an issue when user-provided values are concatenated into SQL statements.\\r\\n\\r\\nh2. Noncompliant Code Example\\r\\n{code}\\r\\nprotected void saveUserData(Connection con, HttpServletResponse response) {\\r\\n\\r\\n  Map<String,String[]> params = request.getParameterMap();\\r\\n\\r\\n  Statement stmt = con.createStatement();\\r\\n  ResultSet rs = stmt.executeQuery(\\\"select FNAME, LNAME, SSN \\\" +\\r\\n                 \\\"from USERS where UNAME=\\\" + params.get(\\\"user\\\");  // Noncompliant; user-provided parameter concatenated directly into query\\r\\n{code}\\r\\n\\r\\nh2. Compliant Solution\\r\\n{code}\\r\\nprotected void saveUserData(Connection con, HttpServletResponse response) {\\r\\n\\r\\n  Map<String,String[]> params = request.getParameterMap();\\r\\n\\r\\n  PreparedStatement pstmt = con.PrepareStatement(\\\"select FNAME, LNAME, SSN \\\" +\\r\\n                 \\\"from USERS where UNAME=?\\\");\\r\\n  pstmt.setString(1, params.get(\\\"user\\\");\\r\\n  ResultSet rs = pstmt.executeQuery();\\r\\n{code}\\r\\n\\r\\nh2. See\\r\\n* [MITRE, CWE-89|http://cwe.mitre.org/data/definitions/89] - Improper Neutralization of Special Elements used in an SQL Command\\r\\n* [MITRE, CWE-564|http://cwe.mitre.org/data/definitions/564.html] - SQL Injection: Hibernate\\r\\n* [MITRE, CWE-20|http://cwe.mitre.org/data/definitions/20.html] - Improper Input Validation\\r\\n* [MITRE, CWE-943|http://cwe.mitre.org/data/definitions/943.html] - Improper Neutralization of Special Elements in Data Query Logic\\r\\n* [CERT, IDS00-J.|https://wiki.sei.cmu.edu/confluence/x/PgIRAg] - Prevent SQL injection\\r\\n* [OWASP Top Ten 2013 Category A1|https://www.owasp.org/index.php/Top_10_2013-A1-Injection] - Injection\\r\\n* [SANS Top 25|http://www.sans.org/top25-software-errors/] - Insecure Interaction Between Components\\r\\n* Derived from FindSecBugs rules [Potential SQL/JPQL Injection (JPA)|http://h3xstream.github.io/find-sec-bugs/bugs.htm#SQL_INJECTION_JPA], [Potential SQL/JDOQL Injection (JDO)|http://h3xstream.github.io/find-sec-bugs/bugs.htm#SQL_INJECTION_JDO], [Potential SQL/HQL Injection (Hibernate)|http://h3xstream.github.io/find-sec-bugs/bugs.htm#SQL_INJECTION_HIBERNATE]\",\"customfield_10251\":\"CWE-89, CWE-564, CWE-20, CWE-943\",\"customfield_10011\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10086\",\"value\":\"Constant/Issue\",\"id\":\"10086\"},\"customfield_10330\":[\"x-proc\"],\"customfield_10253\":\"A1\",\"customfield_11341\":\"High\",\"customfield_10012\":\"20min\",\"customfield_10013\":null,\"customfield_10255\":\"IDS00-J.\",\"customfield_10530\":null,\"customfield_10014\":null,\"customfield_10256\":null,\"customfield_10015\":null,\"customfield_10257\":null,\"customfield_10730\":null,\"customfield_10005\":null,\"customfield_11138\":null,\"customfield_11337\":null,\"customfield_10248\":null,\"customfield_11139\":null,\"customfield_11336\":null,\"customfield_10007\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10040\",\"value\":\"Blocker\",\"id\":\"10040\"},\"customfield_10249\":null,\"customfield_11338\":null,\"summary\":\"User-provided values should be sanitized before use in SQL statements\",\"subtasks\":[],\"customfield_11130\":null,\"customfield_11131\":null,\"customfield_11132\":null,\"customfield_11331\":null,\"customfield_10242\":null,\"customfield_11133\":null,\"customfield_11330\":[{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/11020\",\"value\":\"Main Sources\",\"id\":\"11020\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/11021\",\"value\":\"Test Sources\",\"id\":\"11021\"}],\"customfield_10001\":[{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10122\",\"value\":\"ABAP\",\"id\":\"10122\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10021\",\"value\":\"C#\",\"id\":\"10021\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10124\",\"value\":\"C\",\"id\":\"10124\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10000\",\"value\":\"C++\",\"id\":\"10000\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10010\",\"value\":\"Cobol\",\"id\":\"10010\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10013\",\"value\":\"Flex\",\"id\":\"10013\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10014\",\"value\":\"Groovy\",\"id\":\"10014\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10001\",\"value\":\"Java\",\"id\":\"10001\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10008\",\"value\":\"JavaScript\",\"id\":\"10008\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10331\",\"value\":\"Objective-C\",\"id\":\"10331\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10007\",\"value\":\"PHP\",\"id\":\"10007\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10018\",\"value\":\"PL/I\",\"id\":\"10018\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10017\",\"value\":\"PL/SQL\",\"id\":\"10017\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10009\",\"value\":\"Python\",\"id\":\"10009\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10238\",\"value\":\"RPG\",\"id\":\"10238\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10426\",\"value\":\"Swift\",\"id\":\"10426\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10019\",\"value\":\"VB.Net\",\"id\":\"10019\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10020\",\"value\":\"VB6\",\"id\":\"10020\"}],\"customfield_10243\":null,\"customfield_11134\":\"var name where it's passed into SQL \",\"customfield_11333\":null,\"customfield_10244\":null,\"customfield_11332\":null,\"customfield_10245\":null,\"customfield_11335\":null,\"customfield_10004\":null,\"customfield_10246\":null,\"customfield_11334\":null,\"customfield_10830\":[{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10620\",\"value\":\"Sonar way\",\"id\":\"10620\"}],\"customfield_10434\":null,\"customfield_10435\":null,\"customfield_10436\":[{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10522\",\"value\":\"CSS\",\"id\":\"10522\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10433\",\"value\":\"Web\",\"id\":\"10433\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10446\",\"value\":\"XML\",\"id\":\"10446\"}],\"customfield_10437\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10922\",\"value\":\" \",\"id\":\"10922\"},\"customfield_10438\":[]},\"names\":{\"issuetype\":\"Issue Type\",\"customfield_11041\":\"Rank\",\"customfield_10030\":\"Message\",\"customfield_11042\":\"FxCop\",\"customfield_10232\":\"Completeness\",\"customfield_10430\":\"ReSharper\",\"resolution\":\"Resolution\",\"customfield_10431\":\"Customer Request Type\",\"customfield_10630\":\"CPPCheck\",\"customfield_10432\":\"Time to resolution\",\"customfield_10433\":\"Golden customer\",\"customfield_10631\":\"Pylint\",\"resolutiondate\":\"Resolved\",\"workratio\":\"Work Ratio\",\"lastViewed\":\"Last Viewed\",\"created\":\"Created\",\"customfield_11030\":\"PC-Lint\",\"customfield_11031\":\"Quick-fixes\",\"customfield_11032\":\"Flagged\",\"customfield_11230\":\"VisualStudio\",\"customfield_11033\":\"Epic/Theme\",\"customfield_11231\":\"PVS-Studio\",\"customfield_11233\":\"Qualification\",\"customfield_11036\":\"Sprint\",\"customfield_11234\":\"MSFT Roslyn\",\"labels\":\"Labels\",\"customfield_11038\":\"Epic Link\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10930\":\"Source ID\",\"customfield_10931\":\"Testcase included\",\"customfield_10932\":\"Patch Submitted\",\"issuelinks\":\"Linked Issues\",\"updated\":\"Updated\",\"status\":\"Status\",\"customfield_11140\":\"Waiting for customer - 7 days\",\"customfield_10250\":\"PHP-FIG\",\"customfield_11141\":\"Waiting for customer - 9 days\",\"customfield_11340\":\"Impact\",\"description\":\"Description\",\"customfield_10251\":\"CWE\",\"customfield_10011\":\"SQALE Remediation Function\",\"customfield_10330\":\"Implementation details\",\"customfield_10253\":\"OWASP\",\"customfield_11341\":\"Likelihood\",\"customfield_10012\":\"SQALE Constant Cost\",\"customfield_10013\":\"SQALE Linear Factor\",\"customfield_10255\":\"CERT\",\"customfield_10530\":\"Waiting for customer\",\"customfield_10014\":\"SQALE Linear Offset\",\"customfield_10256\":\"SQALE Linear Argument Description\",\"customfield_10015\":\"Legacy Key\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10730\":\"Issue in trouble\",\"customfield_10005\":\"List of parameters\",\"customfield_11138\":\"Signatories\",\"customfield_11337\":\"Working Place\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_11139\":\"Waiting for customer new\",\"customfield_11336\":\"Email\",\"customfield_10007\":\"Default Severity\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_11338\":\"Quantity\",\"summary\":\"Summary\",\"subtasks\":\"Sub-Tasks\",\"customfield_11130\":\"First customer SLA\",\"customfield_11131\":\"Second customer SLA\",\"customfield_11132\":\"Third customer SLA\",\"customfield_11331\":\"ESLint\",\"customfield_10242\":\"Template Rule\",\"customfield_11133\":\"Test SLA\",\"customfield_11330\":\"Analysis Scope\",\"customfield_10001\":\"Targeted languages\",\"customfield_10243\":\"issueFunction\",\"customfield_11134\":\"Highlighting\",\"customfield_11333\":\"CppCoreGuidelines\",\"customfield_10244\":\"FindBugs\",\"customfield_11332\":\"JSHint\",\"customfield_10245\":\"PMD\",\"customfield_11335\":\"Surname\",\"customfield_10004\":\"Covered Languages\",\"customfield_10246\":\"Checkstyle\",\"customfield_11334\":\"First Name\",\"customfield_10830\":\"Default Quality Profiles\",\"customfield_10434\":\"Time to answer\",\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":\"Irrelevant for Languages\",\"customfield_10437\":\"SonarQube version\",\"customfield_10438\":\"Request participants\"}}";


  private static final String FULL_CODE_SMELL_TEMPLATE_JSON = "{\"id\":\"15463\",\"names\":{\"summary\":\"Summary\",\"issuetype\":\"Issue Type\",\"customfield_10243\":\"issueFunction\",\"customfield_10244\":\"FindBugs\",\"customfield_10232\":\"Completeness\",\"customfield_10245\":\"PMD\",\"customfield_10246\":\"Checkstyle\",\"customfield_10242\":\"Template Rule\",\"customfield_10430\":\"ReSharper\",\"reporter\":\"Reporter\",\"customfield_10431\":\"Customer Request Type\",\"customfield_10432\":\"Time to resolution\",\"customfield_10330\":\"Implementation details\",\"customfield_10433\":\"Golden customer\",\"updated\":\"Updated\",\"created\":\"Created\",\"description\":\"Description\",\"customfield_10001\":\"Targeted languages\",\"customfield_10004\":\"Covered Languages\",\"issuelinks\":\"Linked Issues\",\"subtasks\":\"Sub-Tasks\",\"status\":\"Status\",\"customfield_10007\":\"Default Severity\",\"labels\":\"Labels\",\"customfield_10005\":\"List of parameters\",\"customfield_10256\":\"SQALE Linear Argument Description\",\"workratio\":\"Work Ratio\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10255\":\"CERT\",\"customfield_10530\":\"Waiting for customer\",\"customfield_10253\":\"OWASP\",\"customfield_10250\":\"PHP-FIG\",\"customfield_10251\":\"CWE\",\"project\":\"Project\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10014\":\"SQALE Linear Offset\",\"lastViewed\":\"Last Viewed\",\"customfield_10015\":\"Legacy Key\",\"customfield_10012\":\"SQALE Constant Cost\",\"customfield_10013\":\"SQALE Linear Factor\",\"comment\":\"Comment\",\"customfield_10010\":\"SQALE Characteristic\",\"customfield_10011\":\"SQALE Remediation Function\",\"votes\":\"Votes\",\"resolution\":\"Resolution\",\"resolutiondate\":\"Resolved\",\"creator\":\"Creator\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10021\":\"Activated by default\",\"watches\":\"Watchers\",\"customfield_10436\":\"Irrelevant for Languages\",\"customfield_10437\":\"SonarQube version\",\"customfield_10434\":\"Time to answer\",\"customfield_10435\":\"fb-contrib\",\"customfield_10438\":\"Request participants\",\"assignee\":\"Assignee\",\"customfield_10131\":\"Applicability\",\"customfield_10130\":\"Outdated Languages\",\"customfield_10030\":\"Message\"},\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/issue\\/15463\",\"key\":\"RSPEC-1212\",\"fields\":{\"summary\":\"Architectural constraint\",\"issuetype\":{\"subtask\":false,\"id\":\"7\",\"description\":\"Rule Specification\",\"name\":\"Specification\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/issuetypes\\/documentation.png\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issuetype\\/7\"},\"customfield_10243\":null,\"customfield_10244\":null,\"customfield_10232\":null,\"customfield_10245\":null,\"customfield_10246\":null,\"customfield_10242\":[{\"id\":\"10329\",\"value\":\"Yes\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10329\"}],\"customfield_10430\":null,\"reporter\":{\"name\":\"freddy.mallet\",\"active\":true,\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=freddy.mallet\",\"displayName\":\"Freddy Mallet\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\"}},\"customfield_10431\":null,\"customfield_10432\":null,\"customfield_10330\":null,\"customfield_10433\":null,\"updated\":\"2015-02-11T14:50:01.000+0000\",\"created\":\"2013-08-20T14:19:20.000+0000\",\"description\":\"A source code comply to an architectural model when it fully adheres to a set of architectural constraints. A constraint allows to deny references between classes by pattern.\\r\\n\\r\\nYou can for instance use this rule to :\\r\\n\\r\\n* forbid access to {{**.web.**}} from {{**.dao.**}} classes\\r\\n* forbid access to {{java.util.Vector}}, {{java.util.Hashtable}} and {{java.util.Enumeration}} from any classes\\r\\n* forbid access to {{java.sql.**}} from {{**.ui.**}} and {{**.web.**}} classes\",\"customfield_10001\":null,\"customfield_10004\":[{\"id\":\"10029\",\"value\":\"Java\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10029\"}],\"issuelinks\":[],\"subtasks\":[],\"status\":{\"id\":\"10000\",\"description\":\"Active Quality Rule\",\"name\":\"Active\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/statuses\\/open.png\",\"statusCategory\":{\"id\":2,\"colorName\":\"blue-gray\",\"name\":\"New\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/statuscategory\\/2\",\"key\":\"new\"},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/status\\/10000\"},\"customfield_10007\":{\"id\":\"10042\",\"value\":\"Major\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10042\"},\"labels\":[],\"customfield_10005\":\"* Key: fromClasses\\r\\n* Description: Optional. If this property is not defined, all classes should adhere to this constraint. Ex : **.web.**\\r\\n\\r\\n* Key: toClasses\\r\\n* Description: Mandatory. Ex : java.util.Vector, java.util.Hashtable, java.util.Enumeration\",\"customfield_10256\":null,\"workratio\":-1,\"customfield_10257\":null,\"customfield_10255\":null,\"customfield_10530\":null,\"customfield_10253\":null,\"customfield_10250\":null,\"customfield_10251\":null,\"project\":{\"id\":\"10120\",\"name\":\"Rules Repository\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/project\\/10120\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=xsmall&pid=10120&avatarId=10011\",\"24x24\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=small&pid=10120&avatarId=10011\",\"32x32\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=medium&pid=10120&avatarId=10011\",\"48x48\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?pid=10120&avatarId=10011\"},\"key\":\"RSPEC\"},\"customfield_10249\":null,\"customfield_10248\":null,\"customfield_10014\":null,\"lastViewed\":null,\"customfield_10015\":\"ArchitecturalConstraint\",\"customfield_10012\":null,\"customfield_10013\":null,\"comment\":{\"total\":0,\"startAt\":0,\"comments\":[],\"maxResults\":0},\"customfield_10010\":null,\"customfield_10011\":null,\"votes\":{\"hasVoted\":false,\"votes\":0,\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/RSPEC-1212\\/votes\"},\"resolution\":null,\"resolutiondate\":null,\"creator\":{\"name\":\"freddy.mallet\",\"active\":true,\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=freddy.mallet\",\"displayName\":\"Freddy Mallet\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\"}},\"customfield_10258\":null,\"customfield_10021\":{\"id\":\"10114\",\"value\":\"No\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10114\"},\"watches\":{\"watchCount\":1,\"isWatching\":false,\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/RSPEC-1212\\/watchers\"},\"customfield_10436\":null,\"customfield_10437\":null,\"customfield_10434\":null,\"customfield_10435\":null,\"customfield_10438\":[],\"assignee\":null,\"customfield_10131\":null,\"customfield_10130\":[{\"id\":\"10227\",\"value\":\"Java\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10227\"}],\"customfield_10030\":null}}";

  private static final String FULL_SECURITY_HOTSPOT_JSON = "{\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"id\":\"74623\",\"self\":\"https://jira.sonarsource.com/rest/api/latest/issue/74623\",\"key\":\"RSPEC-4721\",\"fields\":{\"customfield_11041\":\"0|i09rnz:\",\"customfield_11042\":null,\"resolution\":null,\"customfield_10630\":null,\"customfield_10631\":null,\"lastViewed\":\"2018-07-23T17:54:26.899+0200\",\"customfield_11030\":null,\"customfield_11031\":null,\"customfield_11032\":null,\"customfield_11033\":null,\"customfield_11430\":null,\"customfield_11432\":null,\"customfield_11036\":null,\"customfield_11431\":null,\"labels\":[\"cwe\",\"sans-top25-insecure\"],\"customfield_11038\":null,\"issuelinks\":[{\"id\":\"38818\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issueLink/38818\",\"type\":{\"id\":\"10120\",\"name\":\"Related\",\"inward\":\"is related to\",\"outward\":\"relates to\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issueLinkType/10120\"},\"outwardIssue\":{\"id\":\"18640\",\"key\":\"RSPEC-2076\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issue/18640\",\"fields\":{\"summary\":\"OS commands should not be vulnerable to injection attacks\",\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Rule\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"To Do\"}},\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/10406\",\"id\":\"10406\",\"description\":\"\",\"iconUrl\":\"https://jira.sonarsource.com/secure/viewavatar?size=xsmall&avatarId=10386&avatarType=issuetype\",\"name\":\"Vulnerability Detection\",\"subtask\":false,\"avatarId\":10386}}}},{\"id\":\"38857\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issueLink/38857\",\"type\":{\"id\":\"10010\",\"name\":\"Rule specification\",\"inward\":\"is implemented by\",\"outward\":\"implements\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issueLinkType/10010\"},\"inwardIssue\":{\"id\":\"74694\",\"key\":\"SONARPHP-806\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issue/74694\",\"fields\":{\"summary\":\"Rule S4721: Executing OS commands is security-sensitive\",\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/10002\",\"description\":\"Ticket whose relating Pull Request should be reviewed\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/information.png\",\"name\":\"In Review\",\"id\":\"10002\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/4\",\"id\":4,\"key\":\"indeterminate\",\"colorName\":\"yellow\",\"name\":\"In Progress\"}},\"priority\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/priority/3\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/priorities/major.png\",\"name\":\"Major\",\"id\":\"3\"},\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/2\",\"id\":\"2\",\"description\":\"A new feature of the product, which has yet to be developed.\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/issuetypes/newfeature.png\",\"name\":\"New Feature\",\"subtask\":false}}}}],\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Rule\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"To Do\"}},\"customfield_11140\":null,\"customfield_11141\":null,\"customfield_10330\":null,\"customfield_10730\":null,\"customfield_11535\":[{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/11336\",\"value\":\"Syntactic Analysis\",\"id\":\"11336\"}],\"customfield_11139\":null,\"subtasks\":[],\"customfield_11130\":null,\"customfield_11131\":null,\"customfield_11132\":null,\"customfield_11133\":null,\"customfield_11134\":null,\"customfield_11531\":null,\"customfield_11530\":null,\"customfield_11533\":null,\"customfield_10830\":[{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10620\",\"value\":\"Sonar way\",\"id\":\"10620\"}],\"customfield_10434\":null,\"customfield_10435\":null,\"customfield_10436\":null,\"customfield_10437\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10922\",\"value\":\" \",\"id\":\"10922\"},\"customfield_10438\":[],\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/10500\",\"id\":\"10500\",\"description\":\"\",\"iconUrl\":\"https://jira.sonarsource.com/secure/viewavatar?size=xsmall&avatarId=10386&avatarType=issuetype\",\"name\":\"Security Hotspot Detection\",\"subtask\":false,\"avatarId\":10386},\"customfield_10030\":\"Make sure that executing this OS command is safe here.\",\"customfield_10430\":null,\"customfield_10431\":null,\"customfield_10432\":null,\"customfield_10433\":null,\"customfield_11634\":null,\"customfield_11635\":\"Insecure Interaction Between Components\",\"resolutiondate\":null,\"workratio\":-1,\"created\":\"2018-07-11T10:38:40.000+0200\",\"customfield_11231\":null,\"customfield_11233\":null,\"customfield_11234\":null,\"customfield_11632\":null,\"customfield_10258\":null,\"customfield_11348\":null,\"customfield_10930\":null,\"customfield_11347\":null,\"customfield_10931\":null,\"customfield_10932\":null,\"updated\":\"2018-07-23T16:50:47.000+0200\",\"customfield_10250\":null,\"customfield_11340\":\"High\",\"description\":\"OS commands are security-sensitive. For example, their use has led in the past to the following vulnerabilities:\\r\\n* [CVE-2018-12465|http://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2018-12465]\\r\\n* [CVE-2018-7187|http://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2018-7187]\\r\\n\\r\\nApplications that execute operating system commands or execute commands that interact with the underlying system should neutralize any externally-provided input used to construct those commands. Failure to do so could allow an attacker to execute unexpected or dangerous commands, potentially leading to loss of confidentiality, integrity or availability.\\r\\n \\r\\nh2. Ask Yourself Whether\\r\\n* the executed command is constructed by input that is externally-influenced, for example, user\\r\\ninput (attacker) (*)\\r\\n* the command execution is not restricted to the right users (*)\\r\\n* the application can be redesigned to not rely on external input to execute the command\\r\\n\\r\\n(*) You are at risk if you answered yes to any of those questions.\\r\\n \\r\\nh2. Recommended Secure Coding Practices\\r\\nRestrict the control given to the user over the executed command:\\r\\n* make the executed command part of a whitelist and reject all commands not part of this list\\r\\n* sanitize the user input\\r\\n \\r\\nRestrict which users can have access to the command\\r\\n* use a firewall to protect the process running the code, and to protect the network from the command.\\r\\n* authenticate the user and allow only some users to run the command\\r\\n\\r\\nReduce the damage the command can do:\\r\\n* execute the code in a sandbox environment that enforces strict boundaries between the operating system and the process. For example: a \\\"jail\\\"\\r\\n* refuse to run the command if the process has too many privileges. For example: forbid running the code as \\\"root\\\"\\r\\n \\r\\nh2. See\\r\\n* [MITRE, CWE-78|http://cwe.mitre.org/data/definitions/78] - Improper Neutralization of Special Elements used in an OS Command\\r\\n* OWASP Top 10 2017 Category A1 - Injection\\r\\n* [SANS Top 25|https://www.sans.org/top25-software-errors/#cat1] - Insecure Interaction Between Components\\r\\n\\r\\n\",\"customfield_10251\":\"CWE-78\",\"customfield_10011\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10086\",\"value\":\"Constant/Issue\",\"id\":\"10086\"},\"customfield_10253\":null,\"customfield_11341\":\"Low\",\"customfield_10012\":\"30min\",\"customfield_11344\":null,\"customfield_10013\":null,\"customfield_10255\":null,\"customfield_10530\":null,\"customfield_11343\":null,\"customfield_10014\":null,\"customfield_10256\":null,\"customfield_11346\":null,\"customfield_10015\":null,\"customfield_10257\":null,\"customfield_11345\":null,\"customfield_10005\":null,\"customfield_11337\":null,\"customfield_10248\":null,\"customfield_10007\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10041\",\"value\":\"Critical\",\"id\":\"10041\"},\"customfield_10249\":null,\"customfield_11338\":null,\"summary\":\"Executing OS commands is security-sensitive\",\"customfield_11331\":null,\"customfield_10242\":null,\"customfield_11330\":[{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/11020\",\"value\":\"Main Sources\",\"id\":\"11020\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/11021\",\"value\":\"Test Sources\",\"id\":\"11021\"}],\"customfield_10001\":[{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10021\",\"value\":\"C#\",\"id\":\"10021\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10001\",\"value\":\"Java\",\"id\":\"10001\"},{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10007\",\"value\":\"PHP\",\"id\":\"10007\"}],\"customfield_10243\":null,\"customfield_11333\":null,\"customfield_10244\":null,\"customfield_11332\":null,\"customfield_10245\":null,\"customfield_11335\":null,\"customfield_10004\":null,\"customfield_10246\":null,\"customfield_11334\":null},\"names\":{\"customfield_11041\":\"Rank\",\"customfield_11042\":\"FxCop\",\"resolution\":\"Resolution\",\"customfield_10630\":\"CPPCheck\",\"customfield_10631\":\"Pylint\",\"lastViewed\":\"Last Viewed\",\"customfield_11030\":\"PC-Lint\",\"customfield_11031\":\"Quick-fixes\",\"customfield_11032\":\"Flagged\",\"customfield_11033\":\"Epic/Theme\",\"customfield_11430\":\"TSLint\",\"customfield_11432\":\"SwiftLint\",\"customfield_11036\":\"Sprint\",\"customfield_11431\":\"TSLint-SonarTS\",\"labels\":\"Labels\",\"customfield_11038\":\"Epic Link\",\"issuelinks\":\"Linked Issues\",\"status\":\"Status\",\"customfield_11140\":\"Waiting for customer - 7 days\",\"customfield_11141\":\"Waiting for customer - 9 days\",\"customfield_10330\":\"Implementation details\",\"customfield_10730\":\"Issue in trouble\",\"customfield_11535\":\"Analysis Level\",\"customfield_11139\":\"Waiting for customer new\",\"subtasks\":\"Sub-Tasks\",\"customfield_11130\":\"First customer SLA\",\"customfield_11131\":\"Second customer SLA\",\"customfield_11132\":\"Third customer SLA\",\"customfield_11133\":\"Test SLA\",\"customfield_11134\":\"Highlighting\",\"customfield_11531\":\"Mono Gendarme\",\"customfield_11530\":\"Common Rule\",\"customfield_11533\":\"ESLint-SonarJS\",\"customfield_10830\":\"Default Quality Profiles\",\"customfield_10434\":\"Time to answer\",\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":\"Irrelevant for Languages\",\"customfield_10437\":\"SonarQube version\",\"customfield_10438\":\"Request participants\",\"issuetype\":\"Issue Type\",\"customfield_10030\":\"Message\",\"customfield_10430\":\"ReSharper\",\"customfield_10431\":\"Customer Request Type\",\"customfield_10432\":\"Time to resolution\",\"customfield_10433\":\"Golden customer\",\"customfield_11634\":\"Fortify\",\"customfield_11635\":\"SANS Top 25\",\"resolutiondate\":\"Resolved\",\"workratio\":\"Work Ratio\",\"created\":\"Created\",\"customfield_11231\":\"PVS-Studio\",\"customfield_11233\":\"Qualification\",\"customfield_11234\":\"MSFT Roslyn\",\"customfield_11632\":\"Stylelint\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_11348\":\"SF Account Edition\",\"customfield_10930\":\"Source ID\",\"customfield_11347\":\"SF Account URL\",\"customfield_10931\":\"Testcase included\",\"customfield_10932\":\"Patch Submitted\",\"updated\":\"Updated\",\"customfield_10250\":\"PHP-FIG\",\"customfield_11340\":\"Impact\",\"description\":\"Description\",\"customfield_10251\":\"CWE\",\"customfield_10011\":\"Remediation Function\",\"customfield_10253\":\"OWASP\",\"customfield_11341\":\"Likelihood\",\"customfield_10012\":\"Constant Cost\",\"customfield_11344\":\"Email\",\"customfield_10013\":\"Linear Factor\",\"customfield_10255\":\"CERT\",\"customfield_10530\":\"Waiting for customer\",\"customfield_11343\":\"Task type\",\"customfield_10014\":\"Linear Offset\",\"customfield_10256\":\"Linear Argument Description\",\"customfield_11346\":\"SF Account Owner\",\"customfield_10015\":\"Legacy Key\",\"customfield_10257\":\"FindSecBugs\",\"customfield_11345\":\"Team\",\"customfield_10005\":\"List of parameters\",\"customfield_11337\":\"Working Place\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10007\":\"Default Severity\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_11338\":\"Quantity\",\"summary\":\"Summary\",\"customfield_11331\":\"ESLint\",\"customfield_10242\":\"Template Rule\",\"customfield_11330\":\"Analysis Scope\",\"customfield_10001\":\"Targeted languages\",\"customfield_10243\":\"issueFunction\",\"customfield_11333\":\"CppCoreGuidelines\",\"customfield_10244\":\"FindBugs\",\"customfield_11332\":\"JSHint\",\"customfield_10245\":\"PMD\",\"customfield_11335\":\"Surname\",\"customfield_10004\":\"Covered Languages\",\"customfield_10246\":\"Checkstyle\",\"customfield_11334\":\"First Name\"}}";

  JSONParser parser = new JSONParser();


  @Test()
  public void testPrivateConstructors() {
    final Constructor<?>[] constructors = JiraHelper.class.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }
  }


  @Test
  public void testCustomFieldValueSadPath() throws Exception {
    assertThat(JiraHelper.getCustomFieldValue((JSONObject) parser.parse("{}"), "foo")).isNull();
    assertThat(JiraHelper.getCustomFieldValue((JSONObject) parser.parse(FULL_JSON), null)).isNull();
  }

  @Test
  public void testListFromJsonFieldValueNull() throws Exception {
    assertThat(JiraHelper.getListFromJsonFieldValue((JSONObject) parser.parse("{}"), "foo")).isEmpty();
  }

  @Test
  public void testGetJsonFieldValueNulls() throws Exception {
    assertThat(JiraHelper.getJsonFieldValue((JSONObject) parser.parse("{}"), "foo")).isNull();
    assertThat(JiraHelper.getJsonFieldValue((JSONObject) parser.parse(FULL_JSON), null)).isNull();
  }

  @Test
  public void testHandleParameterListNullString() throws Exception {
    assertThat(JiraHelper.handleParameterList(null, "Java")).isEmpty();
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
    assertThat(paramList).isEmpty();
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

    String json = "{\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"id\":\"19078\",\"self\":\"https://jira.sonarsource.com/rest/api/latest/issue/19078\",\"key\":\"RSPEC-2210\",\"fields\":{\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/10403\",\"id\":\"10403\",\"description\":\"\",\"iconUrl\":\"https://jira.sonarsource.com/secure/viewavatar?size=xsmall&avatarId=10386&avatarType=issuetype\",\"name\":\"Bug Detection\",\"subtask\":false,\"avatarId\":10386},\"customfield_11041\":\"0|i007rb:\",\"customfield_10030\":\"message\",\"customfield_11042\":\"bobby\",\"customfield_10232\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10324\",\"value\":\"Full\",\"id\":\"10324\"},\"customfield_10430\":\"reeee-SHARper!\",\"resolution\":null,\"customfield_10431\":null,\"customfield_10630\":\"Porky Pig\",\"customfield_10432\":null,\"customfield_10433\":null,\"customfield_10631\":\"Pylint\",\"resolutiondate\":null,\"workratio\":-1,\"lastViewed\":null,\"created\":\"2014-11-04T19:17:19.000+0100\",\"customfield_11030\":\"dog-hair\",\"customfield_11031\":null,\"customfield_11032\":null,\"customfield_11230\":\"misualSmudio\",\"customfield_11033\":null,\"customfield_11231\":\"unicorn vomit\",\"customfield_11233\":null,\"customfield_11036\":null,\"customfield_11234\":\"bibbity, bobbity, boof!\",\"labels\":[\"bug\",\"cert\",\"cwe\",\"misra\",\"owasp-a6\",\"suspicious\"],\"customfield_11038\":null,\"customfield_10258\":\"8.9\",\"customfield_10930\":null,\"customfield_10931\":null,\"customfield_10932\":null,\"issuelinks\":[],\"updated\":\"2016-08-25T14:39:14.000+0200\",\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Rule\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"To Do\"}},\"customfield_11140\":null,\"customfield_10250\":\"mission-fig\",\"customfield_11141\":null,\"customfield_11340\":\"Low\",\"description\":\"aldjfl asjd lkjva;lskjeljz fnvw;uzoin j valiue nvakej\\r\\n\\r\\nh2. See \\r\\n* [OWASP Top Ten 2013 Category A6|https://www.owasp.org/index.php/Top_10_2013-A6-Sensitive_Data_Exposure] - Sensitive Data Exposure\",\"customfield_10251\":\"CWE-123\",\"customfield_10011\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10086\",\"value\":\"Constant/Issue\",\"id\":\"10086\"},\"customfield_10330\":null,\"customfield_10253\":\"A12-Blah, A6\",\"customfield_11341\":\"Low\",\"customfield_10012\":\"2d\",\"customfield_10013\":\"blah blah blah\",\"customfield_10255\":\"cert...\",\"customfield_10530\":null,\"customfield_10014\":\"3d\",\"customfield_10256\":\"4h\",\"customfield_10015\":null,\"customfield_10257\":\"findsecbuts\",\"customfield_10730\":null,\"customfield_10005\":\"* key: key\\r\\n* default: default\\r\\n* description: description\",\"customfield_11138\":null,\"customfield_11337\":null,\"customfield_10248\":\"0.0\",\"customfield_11139\":null,\"customfield_11336\":null,\"customfield_10007\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10043\",\"value\":\"Minor\",\"id\":\"10043\"},\"customfield_10249\":\"0-0-0\",\"customfield_11338\":null,\"summary\":\"Anntest dummy rule should asdf\",\"subtasks\":[{\"id\":\"39080\",\"key\":\"RSPEC-3006\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issue/39080\",\"fields\":{\"summary\":\"Javascript\",\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Rule\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"To Do\"}},\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/10\",\"id\":\"10\",\"description\":\"Type used to overload a rule specification for a language\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/issuetypes/subtask_alternate.png\",\"name\":\"Language-Specification\",\"subtask\":true}}},{\"id\":\"39081\",\"key\":\"RSPEC-3007\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issue/39081\",\"fields\":{\"summary\":\"Java\",\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Rule\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"To Do\"}},\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/10\",\"id\":\"10\",\"description\":\"Type used to overload a rule specification for a language\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/issuetypes/subtask_alternate.png\",\"name\":\"Language-Specification\",\"subtask\":true}}}],\"customfield_11130\":null,\"customfield_11131\":null,\"customfield_11132\":null,\"customfield_11331\":\"esLint\",\"customfield_10242\":null,\"customfield_11133\":null,\"customfield_11330\":null,\"customfield_10001\":null,\"customfield_10243\":null,\"customfield_11134\":null,\"customfield_11333\":\"cppCore\",\"customfield_10244\":\"ASDF-PDQ\",\"customfield_11332\":\"hint...\",\"customfield_10245\":\"pmd\",\"customfield_11335\":null,\"customfield_10004\":null,\"customfield_10246\":\"checkstyle\",\"customfield_11334\":null,\"customfield_10830\":[{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10820\",\"value\":\"Drupal\",\"id\":\"10820\"}],\"customfield_10434\":null,\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":null,\"customfield_10437\":null,\"customfield_10438\":[]},\"names\":{\"issuetype\":\"Issue Type\",\"customfield_11041\":\"Rank\",\"customfield_10030\":\"Message\",\"customfield_11042\":\"FxCop\",\"customfield_10232\":\"Completeness\",\"customfield_10430\":\"ReSharper\",\"resolution\":\"Resolution\",\"customfield_10431\":\"Customer Request Type\",\"customfield_10630\":\"CPPCheck\",\"customfield_10432\":\"Time to resolution\",\"customfield_10433\":\"Golden customer\",\"customfield_10631\":\"Pylint\",\"resolutiondate\":\"Resolved\",\"workratio\":\"Work Ratio\",\"lastViewed\":\"Last Viewed\",\"created\":\"Created\",\"customfield_11030\":\"PC-Lint\",\"customfield_11031\":\"Quick-fixes\",\"customfield_11032\":\"Flagged\",\"customfield_11230\":\"VisualStudio\",\"customfield_11033\":\"Epic/Theme\",\"customfield_11231\":\"PVS-Studio\",\"customfield_11233\":\"Qualification\",\"customfield_11036\":\"Sprint\",\"customfield_11234\":\"MSFT Roslyn\",\"labels\":\"Labels\",\"customfield_11038\":\"Epic Link\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10930\":\"Source ID\",\"customfield_10931\":\"Testcase included\",\"customfield_10932\":\"Patch Submitted\",\"issuelinks\":\"Linked Issues\",\"updated\":\"Updated\",\"status\":\"Status\",\"customfield_11140\":\"Waiting for customer - 7 days\",\"customfield_10250\":\"PHP-FIG\",\"customfield_11141\":\"Waiting for customer - 9 days\",\"customfield_11340\":\"Impact\",\"description\":\"Description\",\"customfield_10251\":\"CWE\",\"customfield_10011\":\"Remediation Function\",\"customfield_10330\":\"Implementation details\",\"customfield_10253\":\"OWASP\",\"customfield_11341\":\"Likelihood\",\"customfield_10012\":\"Constant Cost\",\"customfield_10013\":\"Linear Factor\",\"customfield_10255\":\"CERT\",\"customfield_10530\":\"Waiting for customer\",\"customfield_10014\":\"Linear Offset\",\"customfield_10256\":\"Linear Argument Description\",\"customfield_10015\":\"Legacy Key\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10730\":\"Issue in trouble\",\"customfield_10005\":\"List of parameters\",\"customfield_11138\":\"Signatories\",\"customfield_11337\":\"Working Place\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_11139\":\"Waiting for customer new\",\"customfield_11336\":\"Email\",\"customfield_10007\":\"Default Severity\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_11338\":\"Quantity\",\"summary\":\"Summary\",\"subtasks\":\"Sub-Tasks\",\"customfield_11130\":\"First customer SLA\",\"customfield_11131\":\"Second customer SLA\",\"customfield_11132\":\"Third customer SLA\",\"customfield_11331\":\"ESLint\",\"customfield_10242\":\"Template Rule\",\"customfield_11133\":\"Test SLA\",\"customfield_11330\":\"Analysis Scope\",\"customfield_10001\":\"Targeted languages\",\"customfield_10243\":\"issueFunction\",\"customfield_11134\":\"Highlighting\",\"customfield_11333\":\"CppCoreGuidelines\",\"customfield_10244\":\"FindBugs\",\"customfield_11332\":\"JSHint\",\"customfield_10245\":\"PMD\",\"customfield_11335\":\"Surname\",\"customfield_10004\":\"Covered Languages\",\"customfield_10246\":\"Checkstyle\",\"customfield_11334\":\"First Name\",\"customfield_10830\":\"Default Quality Profiles\",\"customfield_10434\":\"Time to answer\",\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":\"Irrelevant for Languages\",\"customfield_10437\":\"SonarQube version\",\"customfield_10438\":\"Request participants\"}}";

    Rule rule = new Rule("");
    try {
      JiraHelper.setReferences(rule, (JSONObject) parser.parse(json));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    assertThat(rule.getCert().size()).isEqualTo(1);
    assertThat(rule.getCert().get(0)).isEqualTo("cert...");

    assertThat(rule.getCheckstyle().size()).isEqualTo(1);
    assertThat(rule.getCheckstyle().get(0)).isEqualTo("checkstyle");

    assertThat(rule.getCppCheck().size()).isEqualTo(1);
    assertThat(rule.getCppCheck().get(0)).isEqualTo("Porky Pig");

    assertThat(rule.getCwe().size()).isEqualTo(1);
    assertThat(rule.getCwe().get(0)).isEqualTo("CWE-123");

    assertThat(rule.getFbContrib().size()).isEqualTo(1);
    assertThat(rule.getFbContrib().get(0)).isEqualTo("fb-contrib");

    assertThat(rule.getFindbugs().size()).isEqualTo(1);
    assertThat(rule.getFindbugs().get(0)).isEqualTo("ASDF-PDQ");

    assertThat(rule.getFindSecBugs().size()).isEqualTo(1);
    assertThat(rule.getFindSecBugs().get(0)).isEqualTo("findsecbuts");

    assertThat(rule.getFxCop().size()).isEqualTo(1);
    assertThat(rule.getFxCop().get(0)).isEqualTo("bobby");

    assertThat(rule.getMisraC04().size()).isEqualTo(1);
    assertThat(rule.getMisraC04().get(0)).isEqualTo("0.0");

    assertThat(rule.getMisraC12().size()).isEqualTo(1);
    assertThat(rule.getMisraC12().get(0)).isEqualTo("8.9");

    assertThat(rule.getMisraCpp().size()).isEqualTo(1);
    assertThat(rule.getMisraCpp().get(0)).isEqualTo("0-0-0");

    assertThat(rule.getMsftRoslyn().size()).isEqualTo(3);
    assertThat(rule.getMsftRoslyn().get(0)).isEqualTo("bibbity");

    assertThat(rule.getOwasp().size()).isEqualTo(2);
    assertThat(rule.getOwasp().get(0)).isEqualTo("A12-Blah");

    assertThat(rule.getPcLint().size()).isEqualTo(1);
    assertThat(rule.getPcLint().get(0)).isEqualTo("dog-hair");

    assertThat(rule.getPhpFig().size()).isEqualTo(1);
    assertThat(rule.getPhpFig().get(0)).isEqualTo("mission-fig");

    assertThat(rule.getPmd().size()).isEqualTo(1);
    assertThat(rule.getPmd().get(0)).isEqualTo("pmd");

    assertThat(rule.getPylint().size()).isEqualTo(1);
    assertThat(rule.getPylint().get(0)).isEqualTo("Pylint");

    assertThat(rule.getResharper().size()).isEqualTo(1);
    assertThat(rule.getResharper().get(0)).isEqualTo("reeee-SHARper!");

    assertThat(rule.getEsLint().size()).isEqualTo(1);
    assertThat(rule.getEsLint().get(0)).isEqualTo("esLint");

  }

  @Test
  public void testSetRemediation() {
    String json = "{\"id\":\"19078\",\"names\":{\"summary\":\"Summary\",\"issuetype\":\"Issue Type\",\"customfield_10243\":\"issueFunction\",\"customfield_10232\":\"Completeness\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10246\":\"Checkstyle\",\"customfield_10242\":\"Template Rule\",\"reporter\":\"Reporter\",\"customfield_10330\":\"Implementation details\",\"updated\":\"Updated\",\"created\":\"Created\",\"description\":\"Description\",\"customfield_10001\":\"Targeted languages\",\"issuelinks\":\"Linked Issues\",\"customfield_10004\":\"Covered Languages\",\"subtasks\":\"Sub-Tasks\",\"status\":\"Status\",\"customfield_10007\":\"Default Severity\",\"labels\":\"Labels\",\"customfield_10005\":\"List of parameters\",\"customfield_10256\":\"Linear Argument\",\"workratio\":\"Work Ratio\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10255\":\"CERT\",\"customfield_10253\":\"OWASP\",\"customfield_10250\":\"PHP-FIG\",\"customfield_10251\":\"CWE\",\"project\":\"Project\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10014\":\"Linear Offset\",\"lastViewed\":\"Last Viewed\",\"customfield_10015\":\"Legacy Key\",\"customfield_10012\":\"Constant Cost\",\"customfield_10013\":\"Linear Factor\",\"comment\":\"Comment\",\"customfield_10010\":\"Characteristic\",\"customfield_10011\":\"Remediation Function\",\"votes\":\"Votes\",\"resolution\":\"Resolution\",\"resolutiondate\":\"Resolved\",\"creator\":\"Creator\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10021\":\"Activated by default\",\"watches\":\"Watchers\",\"assignee\":\"Assignee\",\"customfield_10131\":\"Applicability\",\"customfield_10130\":\"Outdated Languages\",\"customfield_10030\":\"Message\"},\"key\":\"RSPEC-2210\",\"fields\":{\"summary\":\"Anntest dummy rule asdf\",\"customfield_10010\":{\"child\":{\"id\":\"10050\",\"value\":\"Compiler related portability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10050\"},\"id\":\"10049\",\"value\":\"Portability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10049\"},\"customfield_10011\":{\"id\":\"10086\",\"value\":\"Constant\\/Issue\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10086\"},\"customfield_10012\":\"2d\",\"customfield_10256\":null,\"customfield_10013\":null,\"customfield_10014\":null,\"customfield_10015\":null}}";

    Rule rule = new Rule("");
    try {
      JiraHelper.setRemediation(rule, (JSONObject) parser.parse(json));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(rule.getRemediationFunction()).isEqualTo(Rule.RemediationFunction.CONSTANT_ISSUE);
    assertThat(rule.getConstantCostOrLinearThreshold()).isEqualTo("2d");
    assertThat(rule.getLinearArgDesc()).isNull();
    assertThat(rule.getLinearFactor()).isNull();
    assertThat(rule.getLinearOffset()).isNull();
  }

  @Test
  public void testSetDefaultProfiles() {

    JSONObject issue = null;
    try {
      issue = (JSONObject) parser.parse(FULL_VULN_DEFAULT_PROFILE_JSON);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(JiraHelper.getCustomFieldStoredAsList(issue, "Default Quality Profiles")).hasSize(1);

    Rule rule = new Rule("Java");
    JiraHelper.populateFields(rule, issue);
    assertThat(rule.getDefaultProfiles()).hasSize(1);
  }


  @Test
  public void testPopulateFieldsBugType() {

    Rule rule = new Rule("");
    try {
      JiraHelper.populateFields(rule, (JSONObject) parser.parse(FULL_JSON));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(rule.getType()).isEqualTo(Rule.Type.BUG);
    assertThat(rule.getSeverity()).isEqualTo(Rule.Severity.MAJOR);
    assertThat(rule.getStatus()).isEqualTo(Rule.Status.READY);
    assertThat(rule.getTags().size()).isEqualTo(2);
    assertThat(rule.getTags()).contains("bug").contains("cwe");
    assertThat(rule.getTargetedLanguages()).hasSize(0);
  }

  @Test
  public void testPopulateFieldsSecurityType() throws Exception {

    Rule rule = new Rule("");
      JiraHelper.populateFields(rule, (JSONObject) parser.parse(FULL_VULN_DEFAULT_PROFILE_JSON));

    assertThat(rule.getType()).isEqualTo(Rule.Type.VULNERABILITY);
  }

  @Test
  public void testPopulateFieldsCodeSmellType() throws Exception {
    Rule rule = new Rule("");
    JiraHelper.populateFields(rule, (JSONObject) parser.parse(FULL_CODE_SMELL_TEMPLATE_JSON));

    assertThat(rule.getType()).isEqualTo(Rule.Type.CODE_SMELL);
  }

  @Test
  public void testSqkeys() throws Exception {

    Rule ruleWLegacyKey = new Rule("");
    JiraHelper.populateFields(ruleWLegacyKey, (JSONObject) parser.parse(FULL_CODE_SMELL_TEMPLATE_JSON));
    assertThat(ruleWLegacyKey.getSqKey()).isEqualTo("ArchitecturalConstraint");


    Rule ruleWSKey = new Rule("");
    JiraHelper.populateFields(ruleWSKey, (JSONObject) parser.parse(FULL_VULN_DEFAULT_PROFILE_JSON));
    assertThat(ruleWSKey.getSqKey()).isEqualTo("S3649");
  }

  @Test
  public void testSetTemplate() {


    Rule rule = new Rule("Java");
    try {
      JiraHelper.populateFields(rule, (JSONObject) parser.parse(FULL_CODE_SMELL_TEMPLATE_JSON));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(rule.isTemplate()).isTrue();
  }

  @Test
  public void testDeprecatedRule(){
    Rule rule = new Rule("Java");
    rule.setStatus(Rule.Status.DEPRECATED);

    List<Rule> implementedReplacements = new ArrayList<>();

    JiraHelper.validateRuleDeprecation(rule, implementedReplacements);
    assertThat(rule.getStatus()).isEqualTo(Rule.Status.DEPRECATED);


    Rule r2 = new Rule("Java");
    r2.getCoveredLanguages().add("C");
    r2.setKey("RSPEC-9999");
    implementedReplacements.add(r2);
    rule.setStatus(Rule.Status.DEPRECATED);

    JiraHelper.validateRuleDeprecation(rule, implementedReplacements);
    assertThat(rule.getStatus()).isEqualTo(Rule.Status.READY);

    r2.getCoveredLanguages().add("Java");
    rule.setStatus(Rule.Status.DEPRECATED);

    JiraHelper.validateRuleDeprecation(rule, implementedReplacements);
    assertThat(rule.getStatus()).isEqualTo(Rule.Status.DEPRECATED);

  }


  @Test
  public void testSetDeprecationMessage(){
    Rule rule = new Rule("Java");
    rule.setStatus(Rule.Status.DEPRECATED);
    List<String> replacementIds = new ArrayList<>();

    replacementIds.add("RSPEC-1234");
    replacementIds.add("RSPEC-2345");

    JiraHelper.setDeprecationMessage(rule, replacementIds);

    assertThat(rule.getStatus()).isEqualTo(Rule.Status.DEPRECATED);
    assertThat(rule.getDeprecation()).contains("This rule is deprecated");
    assertThat(rule.getHtmlDescription()).contains("This rule is deprecated");

  }

  @Test
  public void testDeprecationMessage(){
    Rule rule = new Rule("Java");
    rule.setStatus(Rule.Status.DEPRECATED);
    List<String> implementedReplacements = new ArrayList<>();

    JiraHelper.setDeprecationMessage(rule, implementedReplacements);

    assertThat(rule.getDeprecation()).isEqualTo("\n" +
            "<h2>Deprecated</h2>\n" +
            "<p>This rule is deprecated, and will eventually be removed.</p>\n");

    implementedReplacements.add("S123");
    implementedReplacements.add("S234");

    JiraHelper.setDeprecationMessage(rule, implementedReplacements);

    assertThat(rule.getDeprecation()).isEqualTo("\n" +
            "<h2>Deprecated</h2>\n" +
            "<p>This rule is deprecated; use {rule:squid:S123}, {rule:squid:S234} instead.</p>\n");
  }

  @Test
  public void shouldAddExtraSectionsAndLogIfUnknown() throws Exception {
    String json = "{\"id\": \"19078\",\"names\": {\"summary\": \"Summary\",\"issuetype\": \"Issue Type\",\"customfield_10243\": \"issueFunction\",\"customfield_10232\": \"Completeness\",\"customfield_10244\": \"FindBugs\",\"customfield_10245\": \"PMD\",\"customfield_10246\": \"Checkstyle\",\"customfield_10242\": \"Template Rule\",\"reporter\": \"Reporter\",\"customfield_10330\": \"Implementation details\",\"updated\": \"Updated\",\"created\": \"Created\",\"description\": \"Description\",\"customfield_10001\": \"Targeted languages\",\"issuelinks\": \"Linked Issues\",\"customfield_10004\": \"Covered Languages\",\"subtasks\": \"Sub-Tasks\",\"status\": \"Status\",\"customfield_10007\": \"Default Severity\",\"labels\": \"Labels\",\"customfield_10005\": \"List of parameters\",\"customfield_10256\": \"Linear Argument\",\"workratio\": \"Work Ratio\",\"customfield_10257\": \"FindSecBugs\",\"customfield_10255\": \"CERT\",\"customfield_10253\": \"OWASP\",\"customfield_10250\": \"PHP-FIG\",\"customfield_10251\": \"CWE\",\"project\": \"Project\",\"customfield_10249\": \"MISRA C++ 2008\",\"customfield_10248\": \"MISRA C 2004\",\"customfield_10014\": \"Linear Offset\",\"lastViewed\": \"Last Viewed\",\"customfield_10015\": \"Legacy Key\",\"customfield_10012\": \"Constant Cost\",\"customfield_10013\": \"Linear Factor\",\"comment\": \"Comment\",\"customfield_10010\": \"Characteristic\",\"customfield_10011\": \"Remediation Function\",\"votes\": \"Votes\",\"resolution\": \"Resolution\",\"resolutiondate\": \"Resolved\",\"creator\": \"Creator\",\"customfield_10258\": \"MISRA C 2012\",\"customfield_10021\": \"Activated by default\",\"watches\": \"Watchers\",\"assignee\": \"Assignee\",\"customfield_10131\": \"Applicability\",\"customfield_10130\": \"Outdated Languages\",\"customfield_10030\": \"Message\"},\"key\": \"RSPEC-2210\",\"fields\": {\"summary\": \"Anntest dummy rule asdf\",\"customfield_10010\": {  \"child\": {    \"id\": \"10050\",    \"value\": \"Compiler related portability\",    \"self\": \"http://jira.sonarsource.com/rest/api/2/customFieldOption/10050\"  },  \"id\": \"10049\",  \"value\": \"Portability\",  \"self\": \"http://jira.sonarsource.com/rest/api/2/customFieldOption/10049\"},\"customfield_10011\": {  \"id\": \"10086\",  \"value\": \"Constant/Issue\",  \"self\": \"http://jira.sonarsource.com/rest/api/2/customFieldOption/10086\"},\"description\": \"This is a nice rule.\\r\\n\\r\\nh2. Exceptions\\r\\nDoes not apply on code that was written by a baboon.\\r\\n\\r\\nh2. Deprecated\\r\\nThis rule should be deprecated.\\r\\n\\r\\nh2. Cross\\r\\nWell that went a bit too far.\",\"customfield_10012\": \"2d\",\"customfield_10256\": null,\"customfield_10013\": null,\"customfield_10014\": null,\"customfield_10015\": null}}";

    Rule rule = new Rule("");
    JiraHelper.populateFields(rule, (JSONObject) parser.parse(json));

    assertThat(rule.getExceptions()).isEqualTo("\n<h2>Exceptions</h2>\n<p>Does not apply on code that was written by a baboon.</p>\n");
    assertThat(rule.getDeprecation()).isEqualTo("\n<h2>Deprecated</h2>\n<p>This rule should be deprecated.</p>\n");
  }

  @Test
  public void shouldAcceptSecurityHotspot() throws Exception {
    Rule rule = new Rule("");
    JiraHelper.populateFields(rule, (JSONObject) parser.parse(FULL_SECURITY_HOTSPOT_JSON));
    assertThat(rule.getType()).isEqualTo(Rule.Type.SECURITY_HOTSPOT);
    assertThat(rule.getSansTop25()).isNotEmpty();
    assertThat(rule.getAskYourself()).isNotEmpty();
    assertThat(rule.getRecommended()).isNotEmpty();
  }
}
