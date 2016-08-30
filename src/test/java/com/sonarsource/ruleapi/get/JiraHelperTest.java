/*
 * Copyright (C) 2014-2016 SonarSource SA
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class JiraHelperTest {

  private static final String FULL_JSON = "{\"id\":\"18166\",\"names\":{\"summary\":\"Summary\",\"issuetype\":\"Issue Type\",\"customfield_10243\":\"issueFunction\",\"customfield_10232\":\"Completeness\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10246\":\"Checkstyle\",\"customfield_10242\":\"Template Rule\",\"reporter\":\"Reporter\",\"customfield_10330\":\"Implementation details\",\"updated\":\"Updated\",\"created\":\"Created\",\"description\":\"Description\",\"customfield_10001\":\"Targeted languages\",\"issuelinks\":\"Linked Issues\",\"customfield_10004\":\"Covered Languages\",\"subtasks\":\"Sub-Tasks\",\"status\":\"Status\",\"customfield_10007\":\"Default Severity\",\"labels\":\"Labels\",\"customfield_10005\":\"List of parameters\",\"customfield_10256\":\"Linear Argument\",\"workratio\":\"Work Ratio\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10255\":\"CERT\",\"customfield_10253\":\"OWASP\",\"customfield_10250\":\"PHP-FIG\",\"customfield_10251\":\"CWE\",\"project\":\"Project\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10014\":\"Linear Offset\",\"lastViewed\":\"Last Viewed\",\"customfield_10015\":\"Legacy Key\",\"customfield_10012\":\"Constant Cost\",\"customfield_10013\":\"Linear Factor\",\"comment\":\"Comment\",\"customfield_10010\":\"Characteristic\",\"customfield_10011\":\"Remediation Function\",\"votes\":\"Votes\",\"resolution\":\"Resolution\",\"resolutiondate\":\"Resolved\",\"creator\":\"Creator\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10021\":\"Activated by default\",\"watches\":\"Watchers\",\"assignee\":\"Assignee\",\"customfield_10131\":\"Applicability\",\"customfield_10130\":\"Outdated Languages\",\"customfield_10030\":\"Message\"},\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/issue\\/18166\",\"key\":\"RSPEC-1967\",\"fields\":{\"summary\":\"Values should only be moved to variables large enough to hold them without truncation\",\"issuetype\":{\"subtask\":false,\"id\":\"7\",\"description\":\"Rule Specification\",\"name\":\"Specification\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/issuetypes\\/documentation.png\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issuetype\\/7\"},\"customfield_10243\":null,\"customfield_10232\":{\"id\":\"10324\",\"value\":\"Full\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10324\"},\"customfield_10244\":null,\"customfield_10245\":null,\"customfield_10246\":null,\"customfield_10242\":null,\"reporter\":{\"name\":\"ann.campbell.2\",\"active\":true,\"emailAddress\":\"ann.campbell@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=ann.campbell.2\",\"displayName\":\"Ann Campbell\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\"}},\"customfield_10330\":null,\"updated\":\"2014-10-29T13:23:19.000+0000\",\"created\":\"2014-08-22T18:51:55.000+0000\",\"description\":\"Moving a large value into a small field will result in data truncation for both numeric and alphabetic values. In general, alphabetic values are truncated from the right, while numeric values are truncated from the left. However, in the case of floating point values, when the target field has too little precision to hold the value being moved to it, decimals will be truncated (not rounded!) from the right.\\r\\n\\r\\nIn any case, data loss is always the result when too-large values are moved to too-small fields.\\r\\n\\r\\nh2. Noncompliant Code Example\\r\\n{code}\\r\\n01 NUM-A   PIC 9(2)V9.\\r\\n01 ALPHA   PIC X(4).\\r\\n*> ...\\r\\n\\r\\n    MOVE 88.89   TO NUM-A  *> Noncompliant. Becomes 88.8\\r\\n    MOVE 178.7   TO NUM-A  *> Noncompliant. Becomes 78.7\\r\\n    MOVE 999.99 TO NUM-A  *> Noncompliant. Truncated on both ends; becomes 99.9\\r\\n    MOVE \\\"Now is the time\\\" TO ALPHA *> Noncompliant. Becomes \\\"Now \\\"\\r\\n{code}\\r\\n\\r\\nh2. Compliant Solution\\r\\n{code}\\r\\n01 NUM-A   PIC 9(3)V99.\\r\\n01 ALPHA   PIC X(15).\\r\\n*> ...\\r\\n\\r\\n    MOVE 88.89   TO NUM-A\\r\\n    MOVE 178.7   TO NUM-A\\r\\n    MOVE 999.99 TO NUM-A\\r\\n    MOVE \\\"Now is the time\\\" TO ALPHA\\r\\n{code}\\r\\n\\r\\nh2. See\\r\\n* [MITRE, CWE-704|http:\\/\\/cwe.mitre.org\\/data\\/definitions\\/704] - Incorrect Type Conversion or Cast\",\"customfield_10001\":[{\"id\":\"10010\",\"value\":\"Cobol\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10010\"}],\"issuelinks\":[{\"id\":\"12993\",\"inwardIssue\":{\"id\":\"18509\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/18509\",\"key\":\"COBOL-1131\",\"fields\":{\"summary\":\"Rule \\\"Values should only be moved to variables large enough to hold them without truncation\\\"\",\"issuetype\":{\"subtask\":false,\"id\":\"2\",\"description\":\"A new feature of the product, which has yet to be developed.\",\"name\":\"New Feature\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/issuetypes\\/newfeature.png\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issuetype\\/2\"},\"status\":{\"id\":\"6\",\"description\":\"The issue is considered finished, the resolution is correct. Issues which are closed can be reopened.\",\"name\":\"Closed\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/statuses\\/closed.png\",\"statusCategory\":{\"id\":3,\"colorName\":\"green\",\"name\":\"Complete\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/statuscategory\\/3\",\"key\":\"done\"},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/status\\/6\"},\"priority\":{\"id\":\"3\",\"name\":\"Major\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/priorities\\/major.png\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/priority\\/3\"}}},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issueLink\\/12993\",\"type\":{\"id\":\"10010\",\"outward\":\"implements\",\"inward\":\"is implemented by\",\"name\":\"Rule specification\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issueLinkType\\/10010\"}}],\"customfield_10004\":null,\"subtasks\":[],\"status\":{\"id\":\"10000\",\"description\":\"Active Quality Rule\",\"name\":\"Active\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/statuses\\/open.png\",\"statusCategory\":{\"id\":2,\"colorName\":\"blue-gray\",\"name\":\"New\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/statuscategory\\/2\",\"key\":\"new\"},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/status\\/10000\"},\"customfield_10007\":{\"id\":\"10041\",\"value\":\"Critical\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10041\"},\"labels\":[\"bug\"],\"customfield_10005\":\"* key: onlyLiteralValues\\r\\n** description: True to apply the rule only to literal values\\r\\n** default: false\\r\\n* key: ignoredDataItemRegex\\r\\n** description: Regular expression describing sending fields to ignore \",\"customfield_10256\":null,\"workratio\":-1,\"customfield_10257\":null,\"customfield_10255\":null,\"customfield_10253\":null,\"customfield_10250\":null,\"customfield_10251\":\"704\",\"project\":{\"id\":\"10120\",\"name\":\"Rules Repository\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/project\\/10120\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=xsmall&pid=10120&avatarId=10011\",\"24x24\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=small&pid=10120&avatarId=10011\",\"32x32\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=medium&pid=10120&avatarId=10011\",\"48x48\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?pid=10120&avatarId=10011\"},\"key\":\"RSPEC\"},\"customfield_10249\":null,\"customfield_10248\":null,\"customfield_10014\":null,\"lastViewed\":null,\"customfield_10015\":null,\"customfield_10012\":\"30min\",\"customfield_10013\":null,\"comment\":{\"total\":1,\"startAt\":0,\"comments\":[{\"id\":\"20121\",\"body\":\"@Ann, perhaps we could associate this rule to http:\\/\\/cwe.mitre.org\\/data\\/definitions\\/704.html ? This is a bit controversial as CWE-704 is Weakness Class.\",\"author\":{\"name\":\"freddy.mallet\",\"active\":true,\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=freddy.mallet\",\"displayName\":\"Freddy Mallet\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\"}},\"updated\":\"2014-09-19T15:42:06.000+0000\",\"created\":\"2014-09-19T15:42:06.000+0000\",\"updateAuthor\":{\"name\":\"freddy.mallet\",\"active\":true,\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=freddy.mallet\",\"displayName\":\"Freddy Mallet\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\"}},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/18166\\/comment\\/20121\"}],\"maxResults\":1},\"customfield_10010\":{\"child\":{\"id\":\"10073\",\"value\":\"Data related reliability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10073\"},\"id\":\"10071\",\"value\":\"Reliability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10071\"},\"customfield_10011\":{\"id\":\"10086\",\"value\":\"Constant\\/Issue\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10086\"},\"votes\":{\"hasVoted\":false,\"votes\":0,\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/RSPEC-1967\\/votes\"},\"resolution\":null,\"resolutiondate\":null,\"creator\":{\"name\":\"ann.campbell.2\",\"active\":true,\"emailAddress\":\"ann.campbell@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=ann.campbell.2\",\"displayName\":\"Ann Campbell\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\"}},\"customfield_10258\":null,\"customfield_10021\":{\"id\":\"10113\",\"value\":\"Yes\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10113\"},\"watches\":{\"watchCount\":2,\"isWatching\":false,\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/RSPEC-1967\\/watchers\"},\"assignee\":null,\"customfield_10131\":[{\"id\":\"10241\",\"value\":\"Sources\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10241\"},{\"id\":\"10242\",\"value\":\"Tests\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10242\"}],\"customfield_10130\":null,\"customfield_10030\":\"Increase the size of \\\"YYY\\\" or do not \\\"MOVE\\\" (\\\"XXX\\\"|this literal value) to it.\"}}";

  private static final String FULL_VULN_DEFAULT_PROFILE_JSON = "{\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"id\":\"19078\",\"self\":\"http://jira.sonarsource.com/rest/api/latest/issue/19078\",\"key\":\"RSPEC-2210\",\"fields\":{\"issuetype\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issuetype/7\",\"id\":\"7\",\"description\":\"Rule Specification\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/issuetypes/documentation.png\",\"name\":\"Specification\",\"subtask\":false},\"customfield_10030\":\"message\",\"project\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/project/10120\",\"id\":\"10120\",\"key\":\"RSPEC\",\"name\":\"Rules Repository\",\"avatarUrls\":{\"48x48\":\"http://jira.sonarsource.com/secure/projectavatar?pid=10120&avatarId=10011\",\"24x24\":\"http://jira.sonarsource.com/secure/projectavatar?size=small&pid=10120&avatarId=10011\",\"16x16\":\"http://jira.sonarsource.com/secure/projectavatar?size=xsmall&pid=10120&avatarId=10011\",\"32x32\":\"http://jira.sonarsource.com/secure/projectavatar?size=medium&pid=10120&avatarId=10011\"}},\"customfield_10232\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10324\",\"value\":\"Full\",\"id\":\"10324\"},\"customfield_10430\":\"reeee-SHARper!\",\"resolution\":null,\"customfield_10431\":null,\"customfield_10630\":\"Porky Pig\",\"customfield_10432\":null,\"customfield_10433\":null,\"customfield_10631\":\"Pylint\",\"resolutiondate\":null,\"workratio\":-1,\"lastViewed\":null,\"watches\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/RSPEC-2210/watchers\",\"watchCount\":2,\"isWatching\":false},\"created\":\"2014-11-04T19:17:19.000+0000\",\"customfield_10021\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10114\",\"value\":\"No\",\"id\":\"10114\"},\"labels\":[\"cert\",\"clumsy\",\"cwe\",\"misra\",\"obsolete\",\"owasp-a6\",\"performance\",\"pitfall\",\"security\"],\"customfield_10258\":\"8.9\",\"issuelinks\":[],\"assignee\":null,\"updated\":\"2015-05-21T20:02:08.000+0000\",\"status\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Quality Rule\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"New\"}},\"customfield_10250\":\"mission-fig\",\"description\":\"aldjfl asjd lkjva;lskjeljz fnvw;uzoin j valiue nvakej\\r\\n\\r\\nh2. See \\r\\n* [MITRE, CWE-123|http://cwe.toto.com] - Title\\r\\n* [OWASP Top Ten 2013 Category A6|https://www.owasp.org/index.php/Top_10_2013-A6-Sensitive_Data_Exposure] - Sensitive Data Exposure\",\"customfield_10251\":\"CWE-123\",\"customfield_10010\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10049\",\"value\":\"Portability\",\"id\":\"10049\",\"child\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10050\",\"value\":\"Compiler related portability\",\"id\":\"10050\"}},\"customfield_10131\":[{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10241\",\"value\":\"Sources\",\"id\":\"10241\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10242\",\"value\":\"Tests\",\"id\":\"10242\"}],\"customfield_10011\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10086\",\"value\":\"Constant/Issue\",\"id\":\"10086\"},\"customfield_10330\":null,\"customfield_10253\":\"A12-Blah, A6\",\"customfield_10012\":\"2d\",\"customfield_10013\":null,\"customfield_10255\":\"cert...\",\"customfield_10530\":null,\"customfield_10014\":null,\"customfield_10256\":null,\"customfield_10015\":null,\"customfield_10257\":\"findsecbuts\",\"customfield_10730\":null,\"customfield_10005\":\"* key: key\\r\\n* default: default\\r\\n* description: description\",\"customfield_10248\":\"0.0\",\"customfield_10007\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10040\",\"value\":\"Blocker\",\"id\":\"10040\"},\"customfield_10249\":\"0-0-0\",\"summary\":\"Anntest dummy rule asdf\",\"creator\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=ann.campbell.2\",\"name\":\"ann.campbell.2\",\"emailAddress\":\"ann.campbell@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\"},\"displayName\":\"Ann Campbell\",\"active\":true},\"subtasks\":[],\"reporter\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=ann.campbell.2\",\"name\":\"ann.campbell.2\",\"emailAddress\":\"ann.campbell@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\"},\"displayName\":\"Ann Campbell\",\"active\":true},\"customfield_10242\":null,\"customfield_10001\":null,\"customfield_10243\":null,\"customfield_10244\":\"yowza!\",\"customfield_10245\":\"pmd\",\"customfield_10004\":null,\"customfield_10246\":\"checkstyle\"," +
          "\"customfield_10830\":[" +
          "{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10620\",\"value\":\"Bogus Way\",\"id\":\"10622\"}," +
          "{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10620\",\"value\":\"SonarQube Way\",\"id\":\"10620\"}," +
          "{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10621\",\"value\":\"Security Way\",\"id\":\"10621\"}],\"customfield_10434\":null,\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":null,\"customfield_10437\":null,\"customfield_10438\":[],\"comment\":{\"startAt\":0,\"maxResults\":3,\"total\":3,\"comments\":[{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/19078/comment/22389\",\"id\":\"22389\",\"author\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"body\":\"Can we remove this RSPEC @Ann ? :)\",\"updateAuthor\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"created\":\"2015-01-29T13:27:47.000+0000\",\"updated\":\"2015-01-29T13:27:47.000+0000\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/19078/comment/22390\",\"id\":\"22390\",\"author\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=ann.campbell.2\",\"name\":\"ann.campbell.2\",\"emailAddress\":\"ann.campbell@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\"},\"displayName\":\"Ann Campbell\",\"active\":true},\"body\":\"I know it's irritating, but I'd like to keep it for just a little longer [~freddy.mallet]\",\"updateAuthor\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=ann.campbell.2\",\"name\":\"ann.campbell.2\",\"emailAddress\":\"ann.campbell@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/e6e098cbbcdbd6ba253f335e1407b574?d=mm&s=32\"},\"displayName\":\"Ann Campbell\",\"active\":true},\"created\":\"2015-01-29T13:37:35.000+0000\",\"updated\":\"2015-01-29T13:37:35.000+0000\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/19078/comment/22591\",\"id\":\"22591\",\"author\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"body\":\"Ok, no problem [~ann.campbell.2]\",\"updateAuthor\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"created\":\"2015-02-04T09:53:57.000+0000\",\"updated\":\"2015-02-04T09:53:57.000+0000\"}]},\"votes\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/RSPEC-2210/votes\",\"votes\":0,\"hasVoted\":false}},\"names\":{\"issuetype\":\"Issue Type\",\"customfield_10030\":\"Message\",\"project\":\"Project\",\"customfield_10232\":\"Completeness\",\"customfield_10430\":\"ReSharper\",\"resolution\":\"Resolution\",\"customfield_10431\":\"Customer Request Type\",\"customfield_10630\":\"CPPCheck\",\"customfield_10432\":\"Time to resolution\",\"customfield_10433\":\"Golden customer\",\"customfield_10631\":\"Pylint\",\"resolutiondate\":\"Resolved\",\"workratio\":\"Work Ratio\",\"lastViewed\":\"Last Viewed\",\"watches\":\"Watchers\",\"created\":\"Created\",\"customfield_10021\":\"Activated by default\",\"labels\":\"Labels\",\"customfield_10258\":\"MISRA C 2012\",\"issuelinks\":\"Linked Issues\",\"assignee\":\"Assignee\",\"updated\":\"Updated\",\"status\":\"Status\",\"customfield_10250\":\"PHP-FIG\",\"description\":\"Description\",\"customfield_10251\":\"CWE\",\"customfield_10010\":\"Characteristic\",\"customfield_10131\":\"Applicability\",\"customfield_10011\":\"Remediation Function\",\"customfield_10330\":\"Implementation details\",\"customfield_10253\":\"OWASP\",\"customfield_10012\":\"Constant Cost\",\"customfield_10013\":\"Linear Factor\",\"customfield_10255\":\"CERT\",\"customfield_10530\":\"Waiting for customer\",\"customfield_10014\":\"Linear Offset\",\"customfield_10256\":\"Linear Argument Description\",\"customfield_10015\":\"Legacy Key\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10730\":\"Issue in trouble\",\"customfield_10005\":\"List of parameters\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10007\":\"Default Severity\",\"customfield_10249\":\"MISRA C++ 2008\",\"summary\":\"Summary\",\"creator\":\"Creator\",\"subtasks\":\"Sub-Tasks\",\"reporter\":\"Reporter\",\"customfield_10242\":\"Template Rule\",\"customfield_10001\":\"Targeted languages\",\"customfield_10243\":\"issueFunction\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10004\":\"Covered Languages\",\"customfield_10246\":\"Checkstyle\",\"customfield_10830\":\"Default Quality Profiles\",\"customfield_10434\":\"Time to answer\",\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":\"Irrelevant for Languages\",\"customfield_10437\":\"SonarQube version\",\"customfield_10438\":\"Request participants\",\"comment\":\"Comment\",\"votes\":\"Votes\"}}";


  private static final String FULL_CODE_SMELL_TEMPLATE_JSON = "{\"id\":\"15463\",\"names\":{\"summary\":\"Summary\",\"issuetype\":\"Issue Type\",\"customfield_10243\":\"issueFunction\",\"customfield_10244\":\"FindBugs\",\"customfield_10232\":\"Completeness\",\"customfield_10245\":\"PMD\",\"customfield_10246\":\"Checkstyle\",\"customfield_10242\":\"Template Rule\",\"customfield_10430\":\"ReSharper\",\"reporter\":\"Reporter\",\"customfield_10431\":\"Customer Request Type\",\"customfield_10432\":\"Time to resolution\",\"customfield_10330\":\"Implementation details\",\"customfield_10433\":\"Golden customer\",\"updated\":\"Updated\",\"created\":\"Created\",\"description\":\"Description\",\"customfield_10001\":\"Targeted languages\",\"customfield_10004\":\"Covered Languages\",\"issuelinks\":\"Linked Issues\",\"subtasks\":\"Sub-Tasks\",\"status\":\"Status\",\"customfield_10007\":\"Default Severity\",\"labels\":\"Labels\",\"customfield_10005\":\"List of parameters\",\"customfield_10256\":\"Linear Argument Description\",\"workratio\":\"Work Ratio\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10255\":\"CERT\",\"customfield_10530\":\"Waiting for customer\",\"customfield_10253\":\"OWASP\",\"customfield_10250\":\"PHP-FIG\",\"customfield_10251\":\"CWE\",\"project\":\"Project\",\"customfield_10249\":\"MISRA C++ 2008\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10014\":\"Linear Offset\",\"lastViewed\":\"Last Viewed\",\"customfield_10015\":\"Legacy Key\",\"customfield_10012\":\"Constant Cost\",\"customfield_10013\":\"Linear Factor\",\"comment\":\"Comment\",\"customfield_10010\":\"Characteristic\",\"customfield_10011\":\"Remediation Function\",\"votes\":\"Votes\",\"resolution\":\"Resolution\",\"resolutiondate\":\"Resolved\",\"creator\":\"Creator\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10021\":\"Activated by default\",\"watches\":\"Watchers\",\"customfield_10436\":\"Irrelevant for Languages\",\"customfield_10437\":\"SonarQube version\",\"customfield_10434\":\"Time to answer\",\"customfield_10435\":\"fb-contrib\",\"customfield_10438\":\"Request participants\",\"assignee\":\"Assignee\",\"customfield_10131\":\"Applicability\",\"customfield_10130\":\"Outdated Languages\",\"customfield_10030\":\"Message\"},\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/issue\\/15463\",\"key\":\"RSPEC-1212\",\"fields\":{\"summary\":\"Architectural constraint\",\"issuetype\":{\"subtask\":false,\"id\":\"7\",\"description\":\"Rule Specification\",\"name\":\"Specification\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/issuetypes\\/documentation.png\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issuetype\\/7\"},\"customfield_10243\":null,\"customfield_10244\":null,\"customfield_10232\":null,\"customfield_10245\":null,\"customfield_10246\":null,\"customfield_10242\":[{\"id\":\"10329\",\"value\":\"Yes\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10329\"}],\"customfield_10430\":null,\"reporter\":{\"name\":\"freddy.mallet\",\"active\":true,\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=freddy.mallet\",\"displayName\":\"Freddy Mallet\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\"}},\"customfield_10431\":null,\"customfield_10432\":null,\"customfield_10330\":null,\"customfield_10433\":null,\"updated\":\"2015-02-11T14:50:01.000+0000\",\"created\":\"2013-08-20T14:19:20.000+0000\",\"description\":\"A source code comply to an architectural model when it fully adheres to a set of architectural constraints. A constraint allows to deny references between classes by pattern.\\r\\n\\r\\nYou can for instance use this rule to :\\r\\n\\r\\n* forbid access to {{**.web.**}} from {{**.dao.**}} classes\\r\\n* forbid access to {{java.util.Vector}}, {{java.util.Hashtable}} and {{java.util.Enumeration}} from any classes\\r\\n* forbid access to {{java.sql.**}} from {{**.ui.**}} and {{**.web.**}} classes\",\"customfield_10001\":null,\"customfield_10004\":[{\"id\":\"10029\",\"value\":\"Java\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10029\"}],\"issuelinks\":[],\"subtasks\":[],\"status\":{\"id\":\"10000\",\"description\":\"Active Quality Rule\",\"name\":\"Active\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/statuses\\/open.png\",\"statusCategory\":{\"id\":2,\"colorName\":\"blue-gray\",\"name\":\"New\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/statuscategory\\/2\",\"key\":\"new\"},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/status\\/10000\"},\"customfield_10007\":{\"id\":\"10042\",\"value\":\"Major\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10042\"},\"labels\":[],\"customfield_10005\":\"* Key: fromClasses\\r\\n* Description: Optional. If this property is not defined, all classes should adhere to this constraint. Ex : **.web.**\\r\\n\\r\\n* Key: toClasses\\r\\n* Description: Mandatory. Ex : java.util.Vector, java.util.Hashtable, java.util.Enumeration\",\"customfield_10256\":null,\"workratio\":-1,\"customfield_10257\":null,\"customfield_10255\":null,\"customfield_10530\":null,\"customfield_10253\":null,\"customfield_10250\":null,\"customfield_10251\":null,\"project\":{\"id\":\"10120\",\"name\":\"Rules Repository\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/project\\/10120\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=xsmall&pid=10120&avatarId=10011\",\"24x24\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=small&pid=10120&avatarId=10011\",\"32x32\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?size=medium&pid=10120&avatarId=10011\",\"48x48\":\"http:\\/\\/jira.sonarsource.com\\/secure\\/projectavatar?pid=10120&avatarId=10011\"},\"key\":\"RSPEC\"},\"customfield_10249\":null,\"customfield_10248\":null,\"customfield_10014\":null,\"lastViewed\":null,\"customfield_10015\":\"ArchitecturalConstraint\",\"customfield_10012\":null,\"customfield_10013\":null,\"comment\":{\"total\":0,\"startAt\":0,\"comments\":[],\"maxResults\":0},\"customfield_10010\":null,\"customfield_10011\":null,\"votes\":{\"hasVoted\":false,\"votes\":0,\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/RSPEC-1212\\/votes\"},\"resolution\":null,\"resolutiondate\":null,\"creator\":{\"name\":\"freddy.mallet\",\"active\":true,\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/user?username=freddy.mallet\",\"displayName\":\"Freddy Mallet\",\"avatarUrls\":{\"16x16\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"24x24\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"32x32\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\",\"48x48\":\"http:\\/\\/www.gravatar.com\\/avatar\\/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\"}},\"customfield_10258\":null,\"customfield_10021\":{\"id\":\"10114\",\"value\":\"No\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10114\"},\"watches\":{\"watchCount\":1,\"isWatching\":false,\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/issue\\/RSPEC-1212\\/watchers\"},\"customfield_10436\":null,\"customfield_10437\":null,\"customfield_10434\":null,\"customfield_10435\":null,\"customfield_10438\":[],\"assignee\":null,\"customfield_10131\":null,\"customfield_10130\":[{\"id\":\"10227\",\"value\":\"Java\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10227\"}],\"customfield_10030\":null}}";

  JSONParser parser = new JSONParser();


  @Test()
  public void testPrivateConstructors() {
    final Constructor<?>[] constructors = JiraHelper.class.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }
  }


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
    List<Parameter> empty = new ArrayList<>();
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

    String json = "{\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"id\":\"19078\",\"self\":\"https://jira.sonarsource.com/rest/api/latest/issue/19078\",\"key\":\"RSPEC-2210\",\"fields\":{\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/7\",\"id\":\"7\",\"description\":\"Rule Specification\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/issuetypes/documentation.png\",\"name\":\"Specification\",\"subtask\":false},\"customfield_11041\":\"0|i007rb:\",\"customfield_10030\":\"message\",\"customfield_11042\":\"bobby\",\"customfield_10232\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10324\",\"value\":\"Full\",\"id\":\"10324\"},\"customfield_10430\":\"reeee-SHARper!\",\"resolution\":null,\"customfield_10431\":null,\"customfield_10630\":\"Porky Pig\",\"customfield_10432\":null,\"customfield_10433\":null,\"customfield_10631\":\"Pylint\",\"resolutiondate\":null,\"workratio\":-1,\"lastViewed\":null,\"created\":\"2014-11-04T19:17:19.000+0100\",\"customfield_11030\":\"dog-hair\",\"customfield_11031\":null,\"customfield_11032\":null,\"customfield_11230\":\"misualSmudio\",\"customfield_11033\":null,\"customfield_11231\":\"unicorn vomit\",\"customfield_11233\":null,\"customfield_11036\":null,\"customfield_11234\":\"bibbity, bobbity, boof!\",\"labels\":[\"cert\",\"cwe\",\"misra\",\"owasp-a6\"],\"customfield_11038\":null,\"customfield_10258\":\"8.9\",\"customfield_10930\":null,\"customfield_10931\":null,\"customfield_10932\":null,\"issuelinks\":[      ],\"updated\":\"2016-03-17T14:40:08.000+0100\",\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Rule\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"To Do\"}},\"customfield_11140\":null,\"customfield_10250\":\"mission-fig\",\"customfield_11141\":null,\"description\":\"aldjfl asjd lkjva;lskjeljz fnvw;uzoin j valiue nvakej\\r\\n\\r\\nh2. See \\r\\n* [OWASP Top Ten 2013 Category A6|https://www.owasp.org/index.php/Top_10_2013-A6-Sensitive_Data_Exposure] - Sensitive Data Exposure\",\"customfield_10251\":\"CWE-123\",\"customfield_10010\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10049\",\"value\":\"Portability\",\"id\":\"10049\",\"child\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10050\",\"value\":\"Compiler related portability\",\"id\":\"10050\"}},\"customfield_10011\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10086\",\"value\":\"Constant/Issue\",\"id\":\"10086\"},\"customfield_10330\":null,\"customfield_10253\":\"A12-Blah, A6\",\"customfield_10012\":\"2d\",\"customfield_10013\":null,\"customfield_10255\":\"cert...\",\"customfield_10530\":null,\"customfield_10014\":null,\"customfield_10256\":null,\"customfield_10015\":null,\"customfield_10257\":\"findsecbuts\",\"customfield_10730\":null,\"customfield_10005\":\"* key: key\\r\\n* default: default\\r\\n* description: description\",\"customfield_11138\":null,\"customfield_10248\":\"0.0\",\"customfield_11139\":null,\"customfield_10007\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10042\",\"value\":\"Major\",\"id\":\"10042\"},\"customfield_10249\":\"0-0-0\",\"summary\":\"Anntest dummy rule should asdf\",\"subtasks\":[{\"id\":\"39080\",\"key\":\"RSPEC-3006\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issue/39080\",\"fields\":{\"summary\":\"Javascript\",\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Rule\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"To Do\"}},\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/10\",\"id\":\"10\",\"description\":\"Type used to overload a rule specification for a language\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/issuetypes/subtask_alternate.png\",\"name\":\"Language-Specification\",\"subtask\":true}}},{\"id\":\"39081\",\"key\":\"RSPEC-3007\",\"self\":\"https://jira.sonarsource.com/rest/api/2/issue/39081\",\"fields\":{\"summary\":\"Java\",\"status\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/status/10000\",\"description\":\"Active Rule\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Active\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"To Do\"}},\"issuetype\":{\"self\":\"https://jira.sonarsource.com/rest/api/2/issuetype/10\",\"id\":\"10\",\"description\":\"Type used to overload a rule specification for a language\",\"iconUrl\":\"https://jira.sonarsource.com/images/icons/issuetypes/subtask_alternate.png\",\"name\":\"Language-Specification\",\"subtask\":true}}}],\"customfield_11130\":null,\"customfield_11131\":null,\"customfield_11132\":null,\"customfield_10242\":null,\"customfield_11133\":null,\"customfield_10001\":null,\"customfield_10243\":null,\"customfield_11134\":null,\"customfield_10244\":\"ASDF-PDQ\",\"customfield_10245\":\"pmd\",\"customfield_10004\":null,\"customfield_10246\":\"checkstyle\",\"customfield_10830\":[{\"self\":\"https://jira.sonarsource.com/rest/api/2/customFieldOption/10820\",\"value\":\"Drupal\",\"id\":\"10820\"}],\"customfield_10434\":null,\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":null,\"customfield_10437\":null,\"customfield_10438\":[      ]},\"names\":{\"issuetype\":\"Issue Type\",\"customfield_11041\":\"Rank\",\"customfield_10030\":\"Message\",\"customfield_11042\":\"FxCop\",\"customfield_10232\":\"Completeness\",\"customfield_10430\":\"ReSharper\",\"resolution\":\"Resolution\",\"customfield_10431\":\"Customer Request Type\",\"customfield_10630\":\"CPPCheck\",\"customfield_10432\":\"Time to resolution\",\"customfield_10433\":\"Golden customer\",\"customfield_10631\":\"Pylint\",\"resolutiondate\":\"Resolved\",\"workratio\":\"Work Ratio\",\"lastViewed\":\"Last Viewed\",\"created\":\"Created\",\"customfield_11030\":\"PC-Lint\",\"customfield_11031\":\"Quick-fixes\",\"customfield_11032\":\"Flagged\",\"customfield_11230\":\"VisualStudio\",\"customfield_11033\":\"Epic/Theme\",\"customfield_11231\":\"PVS-Studio\",\"customfield_11233\":\"Qualification\",\"customfield_11036\":\"Sprint\",\"customfield_11234\":\"MSFT Roslyn\",\"labels\":\"Labels\",\"customfield_11038\":\"Epic Link\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10930\":\"Source ID\",\"customfield_10931\":\"Testcase included\",\"customfield_10932\":\"Patch Submitted\",\"issuelinks\":\"Linked Issues\",\"updated\":\"Updated\",\"status\":\"Status\",\"customfield_11140\":\"Waiting for customer - 7 days\",\"customfield_10250\":\"PHP-FIG\",\"customfield_11141\":\"Waiting for customer - 9 days\",\"description\":\"Description\",\"customfield_10251\":\"CWE\",\"customfield_10010\":\"Characteristic\",\"customfield_10011\":\"Remediation Function\",\"customfield_10330\":\"Implementation details\",\"customfield_10253\":\"OWASP\",\"customfield_10012\":\"Constant Cost\",\"customfield_10013\":\"Linear Factor\",\"customfield_10255\":\"CERT\",\"customfield_10530\":\"Waiting for customer\",\"customfield_10014\":\"Linear Offset\",\"customfield_10256\":\"Linear Argument Description\",\"customfield_10015\":\"Legacy Key\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10730\":\"Issue in trouble\",\"customfield_10005\":\"List of parameters\",\"customfield_11138\":\"Signatories\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_11139\":\"Waiting for customer new\",\"customfield_10007\":\"Default Severity\",\"customfield_10249\":\"MISRA C++ 2008\",\"summary\":\"Summary\",\"subtasks\":\"Sub-Tasks\",\"customfield_11130\":\"First customer SLA\",\"customfield_11131\":\"Second customer SLA\",\"customfield_11132\":\"Third customer SLA\",\"customfield_10242\":\"Template Rule\",\"customfield_11133\":\"Test SLA\",\"customfield_10001\":\"Targeted languages\",\"customfield_10243\":\"issueFunction\",\"customfield_11134\":\"Highlighting\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10004\":\"Covered Languages\",\"customfield_10246\":\"Checkstyle\",\"customfield_10830\":\"Default Quality Profiles\",\"customfield_10434\":\"Time to answer\",\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":\"Irrelevant for Languages\",\"customfield_10437\":\"SonarQube version\",\"customfield_10438\":\"Request participants\"}}";

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

    assertThat(JiraHelper.getCustomFieldStoredAsList(issue, "Default Quality Profiles")).hasSize(3);

    Rule rule = new Rule("Java");
    JiraHelper.populateFields(rule, issue);
    assertThat(rule.getDefaultProfiles()).hasSize(3);
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
    assertThat(rule.getSeverity()).isEqualTo(Rule.Severity.CRITICAL);
    assertThat(rule.getStatus()).isEqualTo(Rule.Status.READY);
    assertThat(rule.getTags().size()).isEqualTo(1);
    assertThat(rule.getTags().iterator().next()).isEqualTo("bug");
    assertThat(rule.getTargetedLanguages()).hasSize(1);
  }

  @Test
  public void testPopulateFieldsSecurityType() {

    Rule rule = new Rule("");
    try {
      JiraHelper.populateFields(rule, (JSONObject) parser.parse(FULL_VULN_DEFAULT_PROFILE_JSON));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(rule.getType()).isEqualTo(Rule.Type.VULNERABILITY);
  }

  @Test
  public void testPopulateFieldsCodeSmellType() {
    Rule rule = new Rule("");
    try {
      JiraHelper.populateFields(rule, (JSONObject) parser.parse(FULL_CODE_SMELL_TEMPLATE_JSON));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(rule.getType()).isEqualTo(Rule.Type.CODE_SMELL);
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
  public void testDeprecatedStatusWithoutDeprecatedLinks() {
// @formatter:off
    String json = "{\"expand\":\"renderedFields,names,schema,transitions,operations,editmeta,changelog\",\"id\":\"14720\",\"self\":\"http://jira.sonarsource.com/rest/api/latest/issue/14720\",\"key\":\"RSPEC-888\",\"fields\":{\"issuetype\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issuetype/7\",\"id\":\"7\",\"description\":\"Rule Specification\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/issuetypes/documentation.png\",\"name\":\"Specification\",\"subtask\":false},\"customfield_10030\":\"Replace 'xx' operator with one of '<=', '>=', '<', or '>' comparison operators.\",\"project\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/project/10120\",\"id\":\"10120\",\"key\":\"RSPEC\",\"name\":\"Rules Repository\",\"avatarUrls\":{\"48x48\":\"http://jira.sonarsource.com/secure/projectavatar?pid=10120&avatarId=10011\",\"24x24\":\"http://jira.sonarsource.com/secure/projectavatar?size=small&pid=10120&avatarId=10011\",\"16x16\":\"http://jira.sonarsource.com/secure/projectavatar?size=xsmall&pid=10120&avatarId=10011\",\"32x32\":\"http://jira.sonarsource.com/secure/projectavatar?size=medium&pid=10120&avatarId=10011\"}},\"customfield_10232\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10324\",\"value\":\"Full\",\"id\":\"10324\"},\"customfield_10430\":null,\"resolution\":null,\"customfield_10431\":null,\"customfield_10630\":null,\"customfield_10432\":null,\"customfield_10433\":null,\"customfield_10631\":null,\"resolutiondate\":null,\"workratio\":-1,\"lastViewed\":null,\"watches\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/RSPEC-888/watchers\",\"watchCount\":3,\"isWatching\":false},\"created\":\"2013-05-14T11:57:49.000+0000\",\"labels\":[\"bug\",\"cert\",\"cwe\",\"misra\"],\"customfield_10258\":null,\"customfield_10930\":null,\"customfield_10931\":null,\"customfield_10932\":null,\"issuelinks\":[{\"id\":\"19107\",\"self\":\"http://jira.sonarsource.com/rest/api/2/issueLink/19107\",\"type\":{\"id\":\"10010\",\"name\":\"Rule specification\",\"inward\":\"is implemented by\",\"outward\":\"implements\",\"self\":\"http://jira.sonarsource.com/rest/api/2/issueLinkType/10010\"},\"inwardIssue\":{\"id\":\"39712\",\"key\":\"SONARCSANA-157\",\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/39712\",\"fields\":{\"summary\":\"Rule: Equality operators should not be used in \\\"for\\\" loop termination conditions\",\"status\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/status/1\",\"description\":\"The issue is open and ready for the assignee to start work on it.\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Open\",\"id\":\"1\",\"statusCategory\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"New\"}},\"priority\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/priority/3\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/priorities/major.png\",\"name\":\"Major\",\"id\":\"3\"},\"issuetype\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issuetype/2\",\"id\":\"2\",\"description\":\"A new feature of the product, which has yet to be developed.\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/issuetypes/newfeature.png\",\"name\":\"New Feature\",\"subtask\":false}}}}],\"assignee\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"updated\":\"2015-06-22T11:39:22.000+0000\",\"status\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/status/10301\",\"description\":\"Deprecated Rules\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/statuses/generic.png\",\"name\":\"Deprecated\",\"id\":\"10301\",\"statusCategory\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"New\"}},\"customfield_10250\":null,\"description\":\"Testing {{for}} loop termination using an equality operator ({{==}} and {{!=}}) is dangerous, because it could set up an infinite loop. Using a broader relational operator instead casts a wider net, and makes it harder (but not impossible) to accidentally write an infinite loop.\\r\\n\\r\\nh2. Noncompliant Code Example\\r\\n{code}\\r\\nfor (int i = 1; i != 10; i += 2)  // Noncompliant. Infinite; i goes from 9 straight to 11.\\r\\n{\\r\\n  //...\\r\\n} \\r\\n{code}\\r\\n\\r\\nh2. Compliant Solution\\r\\n{code}\\r\\nfor (int i = 1; i <= 10; i += 2)  // Compliant\\r\\n{\\r\\n  //...\\r\\n} \\r\\n{code}\\r\\n\\r\\nh2. Exceptions\\r\\nEquality operators are ignored if the loop counter is not modified within the body of the loop and either:\\r\\n* starts below the ending value and is incremented by 1 on each iteration.\\r\\n* starts above the ending value and is decremented by 1 on each iteration.\\r\\n\\r\\nEquality operators are also ignored when the test is against {{null}}.\\r\\n{code}\\r\\nfor (int i = 0; arr[i] != null; i++) {\\r\\n  // ...\\r\\n}\\r\\n\\r\\nfor (int i = 0; (item = arr[i]) != null; i++) {\\r\\n  // ...\\r\\n}\\r\\n{code}\\r\\n\\r\\nh2. See\\r\\n* MISRA C++:2008, 6-5-2\\r\\n* [MITRE, CWE-835|http://cwe.mitre.org/data/definitions/835] - Loop with Unreachable Exit Condition ('Infinite Loop')\\r\\n* [CERT, MSC21-C|https://www.securecoding.cert.org/confluence/x/EwDJAQ] - Use robust loop termination conditions\\r\\n* [CERT, MSC21-CPP|https://www.securecoding.cert.org/confluence/x/GwDJAQ] - Use inequality to terminate a loop whose counter changes by more than one\\r\\n\",\"customfield_10251\":\"CWE-835\",\"customfield_10010\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10071\",\"value\":\"Reliability\",\"id\":\"10071\",\"child\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10077\",\"value\":\"Logic related reliability\",\"id\":\"10077\"}},\"customfield_10131\":[{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10241\",\"value\":\"Sources\",\"id\":\"10241\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10242\",\"value\":\"Tests\",\"id\":\"10242\"}],\"customfield_10011\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10086\",\"value\":\"Constant/Issue\",\"id\":\"10086\"},\"customfield_10330\":[\"swift-top\"],\"customfield_10253\":null,\"customfield_10012\":\"2min\",\"customfield_10013\":null,\"customfield_10255\":\"MSC21-C, MSC21-CPP\",\"customfield_10530\":null,\"customfield_10014\":null,\"customfield_10256\":null,\"customfield_10015\":null,\"customfield_10257\":null,\"customfield_10730\":null,\"customfield_10005\":null,\"customfield_10248\":null,\"customfield_10007\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10041\",\"value\":\"Critical\",\"id\":\"10041\"},\"customfield_10249\":\"6-5-2\",\"summary\":\"Equality operators should not be used in \\\"for\\\" loop termination conditions\",\"creator\":null,\"subtasks\":[{\"id\":\"21554\",\"key\":\"RSPEC-2790\",\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/21554\",\"fields\":{\"summary\":\"JavaScript\",\"status\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/status/1\",\"description\":\"The issue is open and ready for the assignee to start work on it.\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/statuses/open.png\",\"name\":\"Open\",\"id\":\"1\",\"statusCategory\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"New\"}},\"issuetype\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issuetype/10\",\"id\":\"10\",\"description\":\"Type used to overload a rule specification for a language\",\"iconUrl\":\"http://jira.sonarsource.com/images/icons/issuetypes/subtask_alternate.png\",\"name\":\"Language-Specification\",\"subtask\":true}}}],\"reporter\":null,\"customfield_10242\":null,\"customfield_10001\":[{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10021\",\"value\":\"C#\",\"id\":\"10021\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10124\",\"value\":\"C\",\"id\":\"10124\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10000\",\"value\":\"C++\",\"id\":\"10000\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10013\",\"value\":\"Flex\",\"id\":\"10013\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10014\",\"value\":\"Groovy\",\"id\":\"10014\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10331\",\"value\":\"Objective-C\",\"id\":\"10331\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10007\",\"value\":\"PHP\",\"id\":\"10007\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10009\",\"value\":\"Python\",\"id\":\"10009\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10426\",\"value\":\"Swift\",\"id\":\"10426\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10019\",\"value\":\"VB.Net\",\"id\":\"10019\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10020\",\"value\":\"VB6\",\"id\":\"10020\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10015\",\"value\":\"Web\",\"id\":\"10015\"}],\"customfield_10243\":null,\"customfield_10244\":null,\"customfield_10245\":null,\"customfield_10004\":[{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10029\",\"value\":\"Java\",\"id\":\"10029\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10030\",\"value\":\"JavaScript\",\"id\":\"10030\"}],\"customfield_10246\":null,\"customfield_10830\":[{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10620\",\"value\":\"SonarQube Way\",\"id\":\"10620\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/customFieldOption/10621\",\"value\":\"Security Way\",\"id\":\"10621\"}],\"customfield_10434\":null,\"customfield_10435\":null,\"customfield_10436\":null,\"customfield_10437\":null,\"customfield_10438\":[],\"duedate\":\"2015-06-26\",\"comment\":{\"startAt\":0,\"maxResults\":3,\"total\":3,\"comments\":[{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/14720/comment/20115\",\"id\":\"20115\",\"author\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"body\":\"@Ann, I would:\\r\\n* Associate this rule to CWE-835 :http://cwe.mitre.org/data/definitions/835.html\\r\\n* Support the exception MSC21-EX1\",\"updateAuthor\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"created\":\"2014-09-19T14:02:15.000+0000\",\"updated\":\"2014-09-19T14:02:15.000+0000\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/14720/comment/20286\",\"id\":\"20286\",\"author\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=evgeny.mandrikov\",\"name\":\"evgeny.mandrikov\",\"emailAddress\":\"evgeny.mandrikov@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/723e72feef04f30303971d206e8b1af1?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/723e72feef04f30303971d206e8b1af1?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/723e72feef04f30303971d206e8b1af1?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/723e72feef04f30303971d206e8b1af1?d=mm&s=32\"},\"displayName\":\"Evgeny Mandrikov\",\"active\":true},\"body\":\"IMO both title and description can be improved to make it clear that this rule applicable only to for-loops, but not to all kinds of loops.\",\"updateAuthor\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=evgeny.mandrikov\",\"name\":\"evgeny.mandrikov\",\"emailAddress\":\"evgeny.mandrikov@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/723e72feef04f30303971d206e8b1af1?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/723e72feef04f30303971d206e8b1af1?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/723e72feef04f30303971d206e8b1af1?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/723e72feef04f30303971d206e8b1af1?d=mm&s=32\"},\"displayName\":\"Evgeny Mandrikov\",\"active\":true},\"created\":\"2014-09-28T16:50:05.000+0000\",\"updated\":\"2014-09-28T16:50:05.000+0000\"},{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/14720/comment/26594\",\"id\":\"26594\",\"author\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"body\":\"@Ann, I've the feeling that this rule title is a bit misleading as \\\"equality\\\" operator is also a \\\"relational\\\" operator: http://en.wikipedia.org/wiki/Relational_operator\",\"updateAuthor\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/user?username=freddy.mallet\",\"name\":\"freddy.mallet\",\"emailAddress\":\"freddy.mallet@sonarsource.com\",\"avatarUrls\":{\"48x48\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=48\",\"24x24\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=24\",\"16x16\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=16\",\"32x32\":\"http://www.gravatar.com/avatar/f45a92cf06e1e375faebefc1bb81cf71?d=mm&s=32\"},\"displayName\":\"Freddy Mallet\",\"active\":true},\"created\":\"2015-04-15T16:35:27.000+0000\",\"updated\":\"2015-04-15T16:35:27.000+0000\"}]},\"votes\":{\"self\":\"http://jira.sonarsource.com/rest/api/2/issue/RSPEC-888/votes\",\"votes\":0,\"hasVoted\":false}},\"names\":{\"issuetype\":\"Issue Type\",\"customfield_10030\":\"Message\",\"project\":\"Project\",\"customfield_10232\":\"Completeness\",\"customfield_10430\":\"ReSharper\",\"resolution\":\"Resolution\",\"customfield_10431\":\"Customer Request Type\",\"customfield_10630\":\"CPPCheck\",\"customfield_10432\":\"Time to resolution\",\"customfield_10433\":\"Golden customer\",\"customfield_10631\":\"Pylint\",\"resolutiondate\":\"Resolved\",\"workratio\":\"Work Ratio\",\"lastViewed\":\"Last Viewed\",\"watches\":\"Watchers\",\"created\":\"Created\",\"labels\":\"Labels\",\"customfield_10258\":\"MISRA C 2012\",\"customfield_10930\":\"Source ID\",\"customfield_10931\":\"Testcase included\",\"customfield_10932\":\"Patch Submitted\",\"issuelinks\":\"Linked Issues\",\"assignee\":\"Assignee\",\"updated\":\"Updated\",\"status\":\"Status\",\"customfield_10250\":\"PHP-FIG\",\"description\":\"Description\",\"customfield_10251\":\"CWE\",\"customfield_10010\":\"Characteristic\",\"customfield_10131\":\"Applicability\",\"customfield_10011\":\"Remediation Function\",\"customfield_10330\":\"Implementation details\",\"customfield_10253\":\"OWASP\",\"customfield_10012\":\"Constant Cost\",\"customfield_10013\":\"Linear Factor\",\"customfield_10255\":\"CERT\",\"customfield_10530\":\"Waiting for customer\",\"customfield_10014\":\"Linear Offset\",\"customfield_10256\":\"Linear Argument Description\",\"customfield_10015\":\"Legacy Key\",\"customfield_10257\":\"FindSecBugs\",\"customfield_10730\":\"Issue in trouble\",\"customfield_10005\":\"List of parameters\",\"customfield_10248\":\"MISRA C 2004\",\"customfield_10007\":\"Default Severity\",\"customfield_10249\":\"MISRA C++ 2008\",\"summary\":\"Summary\",\"creator\":\"Creator\",\"subtasks\":\"Sub-Tasks\",\"reporter\":\"Reporter\",\"customfield_10242\":\"Template Rule\",\"customfield_10001\":\"Targeted languages\",\"customfield_10243\":\"issueFunction\",\"customfield_10244\":\"FindBugs\",\"customfield_10245\":\"PMD\",\"customfield_10004\":\"Covered Languages\",\"customfield_10246\":\"Checkstyle\",\"customfield_10830\":\"Default Quality Profiles\",\"customfield_10434\":\"Time to answer\",\"customfield_10435\":\"fb-contrib\",\"customfield_10436\":\"Irrelevant for Languages\",\"customfield_10437\":\"SonarQube version\",\"customfield_10438\":\"Request participants\",\"duedate\":\"Due Date\",\"comment\":\"Comment\",\"votes\":\"Votes\"}}";
// @formatter:on

    Rule rule = new Rule("Java");
    try {
      JiraHelper.populateFields(rule, (JSONObject) parser.parse(json));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(rule.getStatus()).isEqualTo(Rule.Status.READY);
    assertThat(rule.getReplacementLinks()).hasSize(0);
    assertThat(rule.getDeprecation()).doesNotContain("This rule is deprecated");
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


}
