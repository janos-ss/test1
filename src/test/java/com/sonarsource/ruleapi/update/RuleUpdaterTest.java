/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.update;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Profile;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import org.fest.assertions.api.Assertions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class RuleUpdaterTest {

  private static final String FIELDS_META = "{\"summary\":{\"schema\":{\"system\":\"summary\",\"type\":\"string\"},\"name\":\"Summary\",\"operations\":[\"set\"],\"required\":true},\"customfield_10244\":{\"schema\":{\"customId\":10244,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"FindBugs\",\"operations\":[\"set\"],\"required\":false},\"customfield_10232\":{\"schema\":{\"customId\":10232,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:select\"},\"name\":\"Completeness\",\"operations\":[\"set\"],\"allowedValues\":[{\"id\":\"10323\",\"value\":\"Partial\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10323\"},{\"id\":\"10324\",\"value\":\"Full\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10324\"}],\"required\":false},\"customfield_10245\":{\"schema\":{\"customId\":10245,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"PMD\",\"operations\":[\"set\"],\"required\":false},\"customfield_10246\":{\"schema\":{\"customId\":10246,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"Checkstyle\",\"operations\":[\"set\"],\"required\":false},\"customfield_10242\":{\"schema\":{\"customId\":10242,\"items\":\"string\",\"type\":\"array\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:multicheckboxes\"},\"name\":\"Template Rule\",\"operations\":[\"add\",\"set\",\"remove\"],\"allowedValues\":[{\"id\":\"10329\",\"value\":\"Yes\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10329\"}],\"required\":false},\"customfield_10430\":{\"schema\":{\"customId\":10430,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"ReSharper\",\"operations\":[\"set\"],\"required\":false},\"customfield_10258\":{\"schema\":{\"customId\":10258,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"MISRA C 2012\",\"operations\":[\"set\"],\"required\":false},\"customfield_10830\":{\"schema\":{\"customId\":10830,\"items\":\"string\",\"type\":\"array\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:multiselect\"},\"name\":\"Default Quality Profiles\",\"operations\":[\"add\",\"set\",\"remove\"],\"allowedValues\":[{\"id\":\"10620\",\"value\":\"SonarQube Way\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10620\"},{\"id\":\"10621\",\"value\":\"Security Way\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10621\"}],\"required\":false},\"customfield_10330\":{\"schema\":{\"customId\":10330,\"items\":\"string\",\"type\":\"array\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:labels\"},\"autoCompleteUrl\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/1.0\\/labels\\/39034\\/suggest?customFieldId=10330&query=\",\"name\":\"Implementation details\",\"operations\":[\"add\",\"set\",\"remove\"],\"required\":false},\"description\":{\"schema\":{\"system\":\"description\",\"type\":\"string\"},\"name\":\"Description\",\"operations\":[\"set\"],\"required\":false},\"customfield_10001\":{\"schema\":{\"customId\":10001,\"items\":\"string\",\"type\":\"array\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:multiselect\"},\"name\":\"Targeted languages\",\"operations\":[\"add\",\"set\",\"remove\"],\"allowedValues\":[{\"id\":\"10122\",\"value\":\"ABAP\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10122\"},{\"id\":\"10021\",\"value\":\"C#\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10021\"},{\"id\":\"10124\",\"value\":\"C\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10124\"},{\"id\":\"10000\",\"value\":\"C++\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10000\"},{\"id\":\"10010\",\"value\":\"Cobol\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10010\"},{\"id\":\"10521\",\"value\":\"CSS\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10521\"},{\"id\":\"10013\",\"value\":\"Flex\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10013\"},{\"id\":\"10014\",\"value\":\"Groovy\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10014\"},{\"id\":\"10001\",\"value\":\"Java\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10001\"},{\"id\":\"10008\",\"value\":\"JavaScript\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10008\"},{\"id\":\"10331\",\"value\":\"Objective-C\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10331\"},{\"id\":\"10007\",\"value\":\"PHP\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10007\"},{\"id\":\"10018\",\"value\":\"PL\\/I\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10018\"},{\"id\":\"10017\",\"value\":\"PL\\/SQL\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10017\"},{\"id\":\"10009\",\"value\":\"Python\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10009\"},{\"id\":\"10238\",\"value\":\"RPG\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10238\"},{\"id\":\"10426\",\"value\":\"Swift\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10426\"},{\"id\":\"10019\",\"value\":\"VB.Net\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10019\"},{\"id\":\"10020\",\"value\":\"VB6\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10020\"},{\"id\":\"10015\",\"value\":\"Web\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10015\"},{\"id\":\"10016\",\"value\":\"XML\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10016\"}],\"required\":false},\"customfield_10004\":{\"schema\":{\"customId\":10004,\"items\":\"string\",\"type\":\"array\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:multiselect\"},\"name\":\"Covered Languages\",\"operations\":[\"add\",\"set\",\"remove\"],\"allowedValues\":[{\"id\":\"10121\",\"value\":\"ABAP\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10121\"},{\"id\":\"10024\",\"value\":\"C#\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10024\"},{\"id\":\"10125\",\"value\":\"C\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10125\"},{\"id\":\"10025\",\"value\":\"C++\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10025\"},{\"id\":\"10026\",\"value\":\"Cobol\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10026\"},{\"id\":\"10520\",\"value\":\"CSS\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10520\"},{\"id\":\"10027\",\"value\":\"Flex\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10027\"},{\"id\":\"10028\",\"value\":\"Groovy\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10028\"},{\"id\":\"10029\",\"value\":\"Java\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10029\"},{\"id\":\"10030\",\"value\":\"JavaScript\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10030\"},{\"id\":\"10332\",\"value\":\"Objective-C\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10332\"},{\"id\":\"10032\",\"value\":\"PHP\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10032\"},{\"id\":\"10033\",\"value\":\"PL\\/I\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10033\"},{\"id\":\"10034\",\"value\":\"PL\\/SQL\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10034\"},{\"id\":\"10035\",\"value\":\"Python\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10035\"},{\"id\":\"10239\",\"value\":\"RPG\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10239\"},{\"id\":\"10424\",\"value\":\"Swift\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10424\"},{\"id\":\"10036\",\"value\":\"VB.Net\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10036\"},{\"id\":\"10037\",\"value\":\"VB6\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10037\"},{\"id\":\"10038\",\"value\":\"Web\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10038\"},{\"id\":\"10039\",\"value\":\"XML\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10039\"}],\"required\":false},\"customfield_10436\":{\"schema\":{\"customId\":10436,\"items\":\"string\",\"type\":\"array\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:multiselect\"},\"name\":\"Irrelevant for Languages\",\"operations\":[\"add\",\"set\",\"remove\"],\"allowedValues\":[{\"id\":\"10427\",\"value\":\"ABAP\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10427\"},{\"id\":\"10428\",\"value\":\"C#\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10428\"},{\"id\":\"10447\",\"value\":\"C\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10447\"},{\"id\":\"10429\",\"value\":\"C++\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10429\"},{\"id\":\"10430\",\"value\":\"COBOL\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10430\"},{\"id\":\"10522\",\"value\":\"CSS\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10522\"},{\"id\":\"10431\",\"value\":\"Flex\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10431\"},{\"id\":\"10432\",\"value\":\"Groovy\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10432\"},{\"id\":\"10434\",\"value\":\"Java\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10434\"},{\"id\":\"10435\",\"value\":\"JavaScript\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10435\"},{\"id\":\"10437\",\"value\":\"Objective-C\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10437\"},{\"id\":\"10438\",\"value\":\"PHP\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10438\"},{\"id\":\"10439\",\"value\":\"PL\\/I\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10439\"},{\"id\":\"10440\",\"value\":\"PL\\/SQL\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10440\"},{\"id\":\"10441\",\"value\":\"Python\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10441\"},{\"id\":\"10442\",\"value\":\"RPG\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10442\"},{\"id\":\"10443\",\"value\":\"Swift\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10443\"},{\"id\":\"10444\",\"value\":\"VB.Net\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10444\"},{\"id\":\"10445\",\"value\":\"VB6\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10445\"},{\"id\":\"10433\",\"value\":\"Web\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10433\"},{\"id\":\"10446\",\"value\":\"XML\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10446\"}],\"required\":false},\"customfield_10435\":{\"schema\":{\"customId\":10435,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"fb-contrib\",\"operations\":[\"set\"],\"required\":false},\"customfield_10007\":{\"schema\":{\"customId\":10007,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:select\"},\"name\":\"Default Severity\",\"operations\":[\"set\"],\"allowedValues\":[{\"id\":\"10040\",\"value\":\"Blocker\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10040\"},{\"id\":\"10041\",\"value\":\"Critical\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10041\"},{\"id\":\"10042\",\"value\":\"Major\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10042\"},{\"id\":\"10043\",\"value\":\"Minor\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10043\"},{\"id\":\"10044\",\"value\":\"Info\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10044\"}],\"required\":false},\"labels\":{\"schema\":{\"system\":\"labels\",\"items\":\"string\",\"type\":\"array\"},\"autoCompleteUrl\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/1.0\\/labels\\/suggest?query=\",\"name\":\"Labels\",\"operations\":[\"add\",\"set\",\"remove\"],\"required\":false},\"customfield_10005\":{\"schema\":{\"customId\":10005,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textarea\"},\"name\":\"List of parameters\",\"operations\":[\"set\"],\"required\":false},\"customfield_10256\":{\"schema\":{\"customId\":10256,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"SQALE Linear Argument Description\",\"operations\":[\"set\"],\"required\":false},\"customfield_10257\":{\"schema\":{\"customId\":10257,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"FindSecBugs\",\"operations\":[\"set\"],\"required\":false},\"customfield_10255\":{\"schema\":{\"customId\":10255,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"CERT\",\"operations\":[\"set\"],\"required\":false},\"customfield_10253\":{\"schema\":{\"customId\":10253,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"OWASP\",\"operations\":[\"set\"],\"required\":false},\"customfield_10250\":{\"schema\":{\"customId\":10250,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"PHP-FIG\",\"operations\":[\"set\"],\"required\":false},\"customfield_10251\":{\"schema\":{\"customId\":10251,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"CWE\",\"operations\":[\"set\"],\"required\":false},\"customfield_10131\":{\"schema\":{\"customId\":10131,\"items\":\"string\",\"type\":\"array\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:multiselect\"},\"name\":\"Applicability\",\"operations\":[\"add\",\"set\",\"remove\"],\"allowedValues\":[{\"id\":\"10241\",\"value\":\"Sources\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10241\"},{\"id\":\"10242\",\"value\":\"Tests\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10242\"}],\"required\":false},\"customfield_10249\":{\"schema\":{\"customId\":10249,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"MISRA C++ 2008\",\"operations\":[\"set\"],\"required\":false},\"customfield_10248\":{\"schema\":{\"customId\":10248,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"MISRA C 2004\",\"operations\":[\"set\"],\"required\":false},\"customfield_10014\":{\"schema\":{\"customId\":10014,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"SQALE Linear Offset\",\"operations\":[\"set\"],\"required\":false},\"customfield_10630\":{\"schema\":{\"customId\":10630,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"CPPCheck\",\"operations\":[\"set\"],\"required\":false},\"customfield_10015\":{\"schema\":{\"customId\":10015,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"Legacy Key\",\"operations\":[\"set\"],\"required\":false},\"customfield_10631\":{\"schema\":{\"customId\":10631,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"Pylint\",\"operations\":[\"set\"],\"required\":false},\"customfield_10012\":{\"schema\":{\"customId\":10012,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"SQALE Constant Cost or Linear Threshold\",\"operations\":[\"set\"],\"required\":false},\"customfield_10030\":{\"schema\":{\"customId\":10030,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textarea\"},\"name\":\"Message\",\"operations\":[\"set\"],\"required\":false},\"customfield_10013\":{\"schema\":{\"customId\":10013,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:textfield\"},\"name\":\"SQALE Linear Factor\",\"operations\":[\"set\"],\"required\":false},\"comment\":{\"schema\":{\"system\":\"comment\",\"items\":\"comment\",\"type\":\"array\"},\"name\":\"Comment\",\"operations\":[\"add\",\"edit\",\"remove\"],\"required\":false},\"customfield_10010\":{\"schema\":{\"customId\":10010,\"items\":\"string\",\"type\":\"array\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:cascadingselect\"},\"name\":\"SQALE Characteristic\",\"operations\":[\"set\"],\"allowedValues\":[{\"id\":\"10420\",\"value\":\"Reusability\",\"children\":[{\"id\":\"10421\",\"value\":\"Modularity\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10421\"},{\"id\":\"10422\",\"value\":\"Transportability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10422\"},{\"id\":\"10461\",\"value\":\"Compliance\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10461\"}],\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10420\"},{\"id\":\"10049\",\"value\":\"Portability\",\"children\":[{\"id\":\"10050\",\"value\":\"Compiler related portability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10050\"},{\"id\":\"10051\",\"value\":\"Hardware related portability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10051\"},{\"id\":\"10052\",\"value\":\"Language related portability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10052\"},{\"id\":\"10053\",\"value\":\"OS related portability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10053\"},{\"id\":\"10054\",\"value\":\"Software related portability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10054\"},{\"id\":\"10055\",\"value\":\"Time zone related portability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10055\"},{\"id\":\"10462\",\"value\":\"Compliance\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10462\"}],\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10049\"},{\"id\":\"10056\",\"value\":\"Maintainability\",\"children\":[{\"id\":\"10057\",\"value\":\"Readability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10057\"},{\"id\":\"10058\",\"value\":\"Understandability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10058\"},{\"id\":\"10463\",\"value\":\"Compliance\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10463\"}],\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10056\"},{\"id\":\"10457\",\"value\":\"Usability\",\"children\":[{\"id\":\"10458\",\"value\":\"Accessibility\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10458\"},{\"id\":\"10459\",\"value\":\"Ease of Use\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10459\"},{\"id\":\"10460\",\"value\":\"Compliance\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10460\"}],\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10457\"},{\"id\":\"10059\",\"value\":\"Security\",\"children\":[{\"id\":\"10060\",\"value\":\"API abuse\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10060\"},{\"id\":\"10061\",\"value\":\"Errors\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10061\"},{\"id\":\"10062\",\"value\":\"Input validation and representation\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10062\"},{\"id\":\"10063\",\"value\":\"Security features\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10063\"},{\"id\":\"10464\",\"value\":\"Compliance\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10464\"}],\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10059\"},{\"id\":\"10064\",\"value\":\"Efficiency\",\"children\":[{\"id\":\"10065\",\"value\":\"Memory use\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10065\"},{\"id\":\"10423\",\"value\":\"Network use\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10423\"},{\"id\":\"10066\",\"value\":\"Processor use\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10066\"},{\"id\":\"10465\",\"value\":\"Compliance\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10465\"}],\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10064\"},{\"id\":\"10067\",\"value\":\"Changeability\",\"children\":[{\"id\":\"10068\",\"value\":\"Architecture related changeability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10068\"},{\"id\":\"10069\",\"value\":\"Data related changeability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10069\"},{\"id\":\"10070\",\"value\":\"Logic related changeability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10070\"},{\"id\":\"10466\",\"value\":\"Compliance\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10466\"}],\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10067\"},{\"id\":\"10071\",\"value\":\"Reliability\",\"children\":[{\"id\":\"10072\",\"value\":\"Architecture related reliability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10072\"},{\"id\":\"10073\",\"value\":\"Data related reliability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10073\"},{\"id\":\"10074\",\"value\":\"Exception handling\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10074\"},{\"id\":\"10075\",\"value\":\"Fault tolerance\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10075\"},{\"id\":\"10076\",\"value\":\"Instruction related reliability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10076\"},{\"id\":\"10077\",\"value\":\"Logic related reliability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10077\"},{\"id\":\"10078\",\"value\":\"Synchronization related reliability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10078\"},{\"id\":\"10079\",\"value\":\"Unit tests\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10079\"},{\"id\":\"10467\",\"value\":\"Compliance\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10467\"}],\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10071\"},{\"id\":\"10080\",\"value\":\"Testability\",\"children\":[{\"id\":\"10081\",\"value\":\"Unit level testability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10081\"},{\"id\":\"10082\",\"value\":\"Integration level testability\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10082\"},{\"id\":\"10468\",\"value\":\"Compliance\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10468\"}],\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10080\"}],\"required\":false},\"customfield_10011\":{\"schema\":{\"customId\":10011,\"type\":\"string\",\"custom\":\"com.atlassian.jira.plugin.system.customfieldtypes:select\"},\"name\":\"SQALE Remediation Function\",\"operations\":[\"set\"],\"allowedValues\":[{\"id\":\"10083\",\"value\":\"Linear\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10083\"},{\"id\":\"10084\",\"value\":\"Linear with offset\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10084\"},{\"id\":\"10085\",\"value\":\"Linear with threshold\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10085\"},{\"id\":\"10086\",\"value\":\"Constant\\/Issue\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/customFieldOption\\/10086\"}],\"required\":false}}\n";

  @Test
  public void testConstrainedValueHappyString () {

    JSONObject obj = null;

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);
      Map<String, Object> map = new HashMap<>();
      map.put("Default Severity", "Blocker");

      obj = RuleUpdater.prepareRequest(map, fieldsMeta);

    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(obj.toJSONString()).isEqualTo("{\"fields\":{\"customfield_10007\":{\"id\":\"10040\"}}}");
  }

  @Test
  public void testConstrainedValueHappyList () {
    JSONObject obj = null;

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);

      List<String> languages = new ArrayList<>();
      languages.add("C#");
      languages.add("ABAP");

      Map<String, Object> map = new HashMap<>();
      map.put("Targeted languages", languages);

      obj = RuleUpdater.prepareRequest(map, fieldsMeta);

    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(obj.toJSONString()).isEqualTo("{\"fields\":{\"customfield_10001\":[{\"id\":\"10021\"},{\"id\":\"10122\"}]}}");
  }

  @Test
  public void testConstrainedValueHappyEmpty () {
    JSONObject obj = null;

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);
      Map<String, Object> map = new HashMap<>();
      map.put("Default Severity", null);

      obj = RuleUpdater.prepareRequest(map, fieldsMeta);

    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(obj.toJSONString()).isEqualTo("{\"fields\":{\"customfield_10007\":{}}}");

  }

  @Test
  public void testConstrainedValueSad () {

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);
      Map<String, Object> map = new HashMap<>();
      map.put("Default Severity", "Green");

      RuleUpdater.prepareRequest(map, fieldsMeta);
      fail("RuleException expected");
    } catch (Exception e) {
      assertThat(e).isInstanceOf(RuleException.class);
    }
  }

  @Test
  public void testConstraintedValueNonString() {
    JSONObject obj = null;

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);

      List<Profile> profiles = new ArrayList<>();
      profiles.add(new Profile("Security Way"));
      profiles.add(new Profile("SonarQube Way"));

      Map<String, Object> map = new HashMap<>();
      map.put("Default Quality Profiles", profiles);

      obj = RuleUpdater.prepareRequest(map, fieldsMeta);
    } catch (Exception e) {
      fail("Unexpected exception thrown");
    }

    Assertions.assertThat(obj.toJSONString()).isEqualTo("{\"fields\":{\"customfield_10830\":[{\"id\":\"10621\"},{\"id\":\"10620\"}]}}");
  }

  @Test
  public void testFieldNameNotFound() {

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);
      Map<String, Object> map = new HashMap<>();
      map.put("Unknown Field", "Green");

      JSONObject obj = RuleUpdater.prepareRequest(map, fieldsMeta);
      Assertions.assertThat((JSONObject)obj.get("fields")).hasSize(0);

    } catch (Exception e) {
      fail("Unexpected exception thwon");
    }

  }

  @Test
  public void testConstrainedValueSadList () {
    JSONObject obj = null;

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);

      List<String> languages = new ArrayList<>();
      languages.add("ABAP");
      languages.add("Zulu");


      Map<String, Object> map = new HashMap<>();
      map.put("Targeted languages", languages);

      obj = RuleUpdater.prepareRequest(map, fieldsMeta);

    } catch (Exception e) {
      assertThat(e).isInstanceOf(RuleException.class);
    }
  }

  @Test
  public void testArrayValue () {
    JSONObject obj = null;

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);
      Map<String, Object> map = new HashMap<>();
      List<String> labels = new ArrayList<>();
      labels.add("bug");
      labels.add("clumsy");
      labels.add("pitfall");
      labels.add("security");
      labels.add("performance");
      labels.add("obsolete");
      map.put("Labels", labels);

      obj = RuleUpdater.prepareRequest(map, fieldsMeta);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(obj.toJSONString()).isEqualTo("{\"fields\":{\"labels\":[\"bug\",\"clumsy\",\"pitfall\",\"security\",\"performance\",\"obsolete\"]}}");
  }

  @Test
  public void testFreeEntryStringListMultiple () {
    JSONObject obj = null;

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);
      Map<String, Object> map = new HashMap<>();
      List<String> cwe = new ArrayList<>();
      cwe.add("bug");
      cwe.add("clumsy");
      cwe.add("pitfall");
      cwe.add("security");
      cwe.add("performance");
      cwe.add("obsolete");
      map.put("CWE", cwe);

      obj = RuleUpdater.prepareRequest(map, fieldsMeta);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(obj.toJSONString()).isEqualTo("{\"fields\":{\"customfield_10251\":\"bug, clumsy, pitfall, security, performance, obsolete\"}}");
  }

  @Test
  public void testFreeEntryStringListSingle () {
    JSONObject obj = null;

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);
      Map<String, Object> map = new HashMap<>();
      List<String> cwe = new ArrayList<>();
      cwe.add("bug");
      map.put("CWE", cwe);

      obj = RuleUpdater.prepareRequest(map, fieldsMeta);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(obj.toJSONString()).isEqualTo("{\"fields\":{\"customfield_10251\":\"bug\"}}");
  }

  @Test
  public void testFreeEntryString () {
    JSONObject obj = null;

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);
      Map<String, Object> map = new HashMap<>();
      map.put("Message", "this is the message...");

      obj = RuleUpdater.prepareRequest(map, fieldsMeta);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(obj.toJSONString()).isEqualTo("{\"fields\":{\"customfield_10030\":\"this is the message...\"}}");
  }

  @Test
  public void testFreeEntryParameterListOne () {
    JSONObject obj = null;

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);
      Map<String, Object> map = new HashMap<>();
      List<Parameter> params = new ArrayList<>();

      Parameter param = new Parameter();
      param.setKey("theKey");
      param.setDescription("description...");
      params.add(param);

      map.put("List of parameters", params);

      obj = RuleUpdater.prepareRequest(map, fieldsMeta);

    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(obj.toJSONString()).isEqualTo("{\"fields\":{\"customfield_10005\":\"* key = theKey\\n* description = description...\"}}");
  }

  @Test
  public void testFreeEntryParameterListMulti () {
    JSONObject obj = null;

    JSONParser parser = new JSONParser();
    try {
      JSONObject fieldsMeta = (JSONObject) parser.parse(FIELDS_META);
      Map<String, Object> map = new HashMap<>();
      List<Parameter> params = new ArrayList<>();

      Parameter param = new Parameter();
      param.setKey("theKey");
      param.setDescription("description...");
      params.add(param);

      param = new Parameter();
      param.setKey("anotherKey");
      param.setDescription("another description...");
      param.setDefaultVal("blah");
      params.add(param);

      map.put("List of parameters", params);

      obj = RuleUpdater.prepareRequest(map, fieldsMeta);

    } catch (ParseException e) {
      e.printStackTrace();
    }

    assertThat(obj.toJSONString()).isEqualTo("{\"fields\":{\"customfield_10005\":\"* key = theKey\\n* description = description..., * key = anotherKey\\n* description = another description...\\n* default = blah\"}}");
  }

  @Test
  public void testHandleArrayTypeString() {
    JSONArray jobj = RuleUpdater.handleArrayType("blah");
    assertThat(jobj.toJSONString()).isEqualTo("[\"blah\"]");
  }

  @Test
  public void testHandleArrayTypeEmpty() {

    JSONArray jobj = RuleUpdater.handleArrayType(null);
    assertThat(jobj.toJSONString()).isEqualTo("[]");
  }

  @Test
  public void testHandleFreeEntryEmpty() {
    String result = RuleUpdater.handleFreeEntry(null);
    Assertions.assertThat(result).isEqualTo("");
  }

  @Test
  public void testHandleFreeEntryEmptyList() {

    String result = RuleUpdater.handleFreeEntry(new ArrayList<String>());
    Assertions.assertThat(result).isEqualTo("");
  }

  @Test
  public void testPrepareTransitionRequest(){
    String raw = "{\"transitions\":[{\"to\":{\"id\":\"10301\",\"description\":\"Deprecated Rules\",\"name\":\"Deprecated\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/statuses\\/generic.png\",\"statusCategory\":{\"id\":2,\"colorName\":\"blue-gray\",\"name\":\"New\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/statuscategory\\/2\",\"key\":\"new\"},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/status\\/10301\"},\"id\":\"13\",\"name\":\"Deprecate Rule\",\"fields\":{}},{\"to\":{\"id\":\"6\",\"description\":\"The issue is considered finished, the resolution is correct. Issues which are closed can be reopened.\",\"name\":\"Closed\",\"iconUrl\":\"http:\\/\\/jira.sonarsource.com\\/images\\/icons\\/statuses\\/closed.png\",\"statusCategory\":{\"id\":3,\"colorName\":\"green\",\"name\":\"Complete\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/statuscategory\\/3\",\"key\":\"done\"},\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/2\\/status\\/6\"},\"id\":\"2\",\"name\":\"Close Issue\",\"fields\":{\"assignee\":{\"schema\":{\"system\":\"assignee\",\"type\":\"user\"},\"autoCompleteUrl\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/user\\/assignable\\/search?issueKey=RSPEC-2988&username=\",\"name\":\"Assignee\",\"operations\":[\"set\"],\"required\":false},\"resolution\":{\"schema\":{\"system\":\"resolution\",\"type\":\"resolution\"},\"name\":\"Resolution\",\"operations\":[\"set\"],\"allowedValues\":[{\"id\":\"1\",\"name\":\"Fixed\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/resolution\\/1\"},{\"id\":\"2\",\"name\":\"Won't Fix\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/resolution\\/2\"},{\"id\":\"3\",\"name\":\"Duplicate\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/resolution\\/3\"},{\"id\":\"4\",\"name\":\"Incomplete\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/resolution\\/4\"},{\"id\":\"5\",\"name\":\"Cannot Reproduce\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/resolution\\/5\"},{\"id\":\"10000\",\"name\":\"Done\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/resolution\\/10000\"},{\"id\":\"10100\",\"name\":\"Not an issue\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/resolution\\/10100\"},{\"id\":\"10200\",\"name\":\"Not A Bug\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/resolution\\/10200\"},{\"id\":\"10201\",\"name\":\"UNRESOLVED\",\"self\":\"http:\\/\\/jira.sonarsource.com\\/rest\\/api\\/latest\\/resolution\\/10201\"}],\"required\":true}}}],\"expand\":\"transitions\"}";
    JSONParser parser = new JSONParser();
    try {
      JSONObject jsonObject = (JSONObject) parser.parse(raw);
      Assertions.assertThat(RuleUpdater.prepareTransitionRequest(Rule.Status.DEPRECATED, jsonObject).toJSONString()).isEqualTo("{\"transition\":{\"id\":\"13\"}}");

    } catch (ParseException e) {
      Assert.fail();
    }
  }

  @Test
  public void testNoRuleUpdate() {

    Map<String, Object> updates = new HashMap<>();

    assertThat(RuleUpdater.updateRule("RSPEC-123", updates, "boo", "hoo")).isFalse();

    List<String> tags = new ArrayList<>();
    tags.add("foo");
    updates.put("Labels", tags);

    Assertions.assertThat(RuleUpdater.updateRule("S123", updates, "boo", "hoo")).isFalse();
  }

}
