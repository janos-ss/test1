/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.utilities.Language;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class CertTest {

  Cert cert = new Cert();

  @Test
  public void testBadFormatNoUpdates() {

    Rule rule = new Rule("");

    List<String> updates = new ArrayList<>();

    assertThat(cert.doesReferenceNeedUpdating("ABC01-Q",updates, rule.getKey())).isFalse();
    assertThat(updates).hasSize(1);

    assertThat(cert.doesReferenceNeedUpdating("ABC01-CPP",updates, rule.getKey())).isFalse();
    assertThat(updates).hasSize(2);

    assertThat(cert.doesReferenceNeedUpdating("413",updates, rule.getKey())).isFalse();
    assertThat(updates).hasSize(3);

    assertThat(cert.doesReferenceNeedUpdating("ABC43-Java",updates, rule.getKey())).isFalse();
    assertThat(updates).hasSize(4);

  }

  @Test
  public void testGetCleanupReportBody(){

    Rule rule = new Rule("");
    List<Rule> list = new ArrayList<>();
    list.add(rule);

    String report = cert.getCleanupReportBody(list);
    assertThat(report).isEqualTo("");

    rule.setReferences("CERT, ASDF01-J");
    report = cert.getCleanupReportBody(list);
    assertThat(report).isEqualTo("Not found: null : CERT, ASDF01-J\n" +
            "https://www.securecoding.cert.org/confluence/rest/api/content?title=ASDF01-J\n");
  }

  @Test
  public void testGetReportBody(){

    Cert freshCert = new Cert();

    Rule rule = new Rule("");
    rule.setKey("ruleKey");
    List<Rule> list = new ArrayList<>();
    list.add(rule);

    Map<String, List<Rule>> standardRules = new HashMap<>();
    standardRules.put("PRE30-C.", list);

    String result = freshCert.generateReport(RuleManager.NEMO, standardRules);
    assertThat(result).isNull();


    Cert.CertRule certRule = new Cert.CertRule("PRE30-C.", "test title", "http://boo.com");
    Cert.CertRule certRule2 = new Cert.CertRule("MSC01-C.", "Miscellaney", "http://misc.com");
    Cert.CertRule[] certRules = {certRule, certRule2};

    result = freshCert.getReportBody(RuleManager.NEMO, standardRules, certRules);
    assertThat(result).isNull();

    freshCert.setLanguage(Language.C);
    result = freshCert.getReportBody(RuleManager.NEMO, standardRules, certRules);

    assertThat(result).isEqualTo("<h2>C coverage of CERT</h2>\n" +
            "<h3>Covered</h3><table>\n" +
            "<tr><td><a href='http://boo.com' target='_blank'>PRE30-C.</a>test title</td>\n" +
            "<td><a href='https://nemo.sonarqube.org/coding_rules#rule_key=null%3AruleKey'>ruleKey</a> null<br/>\n" +
            "</td></tr>\n" +
            "</table><h3>Uncovered</h3><table>\n" +
            "<tr><td><a href='http://misc.com' target='_blank'>MSC01-C.</a>Miscellaney</td></tr>\n" +
            "</table>");
  }

  @Test
  public void testGenerateReport() {
    Cert freshCert = new Cert();

    Rule rule = new Rule("");
    List<Rule> list = new ArrayList<>();
    list.add(rule);

    Map<String, List<Rule>> standardRules = new HashMap<>();
    standardRules.put("PRE30-C.", list);

    String result = freshCert.generateReport(RuleManager.NEMO, standardRules);
    assertThat(result).isNull();
  }

  @Test
  public void testLanguageSetting(){
    Cert freshCert = new Cert();
    freshCert.setLanguage(Language.ABAP);

    assertThat(freshCert.getLanguage()).isNull();

    freshCert.setLanguage(Language.CPP);
    assertThat(freshCert.getLanguage()).isEqualTo(Language.CPP);

    freshCert.setLanguage(Language.JAVA);
    assertThat(freshCert.getLanguage()).isEqualTo(Language.JAVA);

    freshCert.setLanguage(Language.C);
    assertThat(freshCert.getLanguage()).isEqualTo(Language.C);

  }

  @Test
  public void testGetSpecificReferences(){
    String references = "<ul>" +
            "<li><a href='http://blah.com>CERT ASD01-F.</a> - bibbity bobbity boo</li>\n" +
            "</ul>\n" +
            "<h3>See also</h3>\n" +
            "<ul>\n" +
            "<li>Foo</li>\n" +
            "</ul>\n";

    Rule rule = new Rule("");
    rule.setReferences(references);

    List<String> refs = cert.getSpecificReferences(rule);
    assertThat(refs.size()).isEqualTo(1);
  }

  @Test
  public void testExtractRulesFromChildPages() {
    Cert.CertType cppType = new Cert.CertType(Language.CPP, "146440541");

    String baseUrl = "http://foo.com";
    List<String> ids = new ArrayList<>();

    String dudResults = "[{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/pages\\/viewpage.action?pageId=146440556\",\"tinyui\":\"\\/x\\/bIG6C\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/146440556\"},\"id\":\"146440556\",\"type\":\"page\",\"title\":\"Rule 01. Declarations and Initialization (DCL)\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/146440556\\/child\",\"history\":\"\\/rest\\/api\\/content\\/146440556\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/146440556\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/pages\\/viewpage.action?pageId=146440561\",\"tinyui\":\"\\/x\\/cYG6C\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/146440561\"},\"id\":\"146440561\",\"type\":\"page\",\"title\":\"Rule 02. Expressions (EXP)\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/146440561\\/child\",\"history\":\"\\/rest\\/api\\/content\\/146440561\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/146440561\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/pages\\/viewpage.action?pageId=146440565\",\"tinyui\":\"\\/x\\/dYG6C\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/146440565\"},\"id\":\"146440565\",\"type\":\"page\",\"title\":\"Rule 03. Integers (INT)\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/146440565\\/child\",\"history\":\"\\/rest\\/api\\/content\\/146440565\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/146440565\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/pages\\/viewpage.action?pageId=146440573\",\"tinyui\":\"\\/x\\/fYG6C\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/146440573\"},\"id\":\"146440573\",\"type\":\"page\",\"title\":\"Rule 04. Containers (CTR)\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/146440573\\/child\",\"history\":\"\\/rest\\/api\\/content\\/146440573\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/146440573\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/pages\\/viewpage.action?pageId=146440577\",\"tinyui\":\"\\/x\\/gYG6C\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/146440577\"},\"id\":\"146440577\",\"type\":\"page\",\"title\":\"Rule 05. Characters and Strings (STR)\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/146440577\\/child\",\"history\":\"\\/rest\\/api\\/content\\/146440577\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/146440577\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/pages\\/viewpage.action?pageId=146440581\",\"tinyui\":\"\\/x\\/hYG6C\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/146440581\"},\"id\":\"146440581\",\"type\":\"page\",\"title\":\"Rule 06. Memory Management (MEM)\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/146440581\\/child\",\"history\":\"\\/rest\\/api\\/content\\/146440581\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/146440581\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/pages\\/viewpage.action?pageId=146440585\",\"tinyui\":\"\\/x\\/iYG6C\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/146440585\"},\"id\":\"146440585\",\"type\":\"page\",\"title\":\"Rule 07. Input Output (FIO)\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/146440585\\/child\",\"history\":\"\\/rest\\/api\\/content\\/146440585\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/146440585\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/pages\\/viewpage.action?pageId=146440597\",\"tinyui\":\"\\/x\\/lYG6C\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/146440597\"},\"id\":\"146440597\",\"type\":\"page\",\"title\":\"Rule 08. Exceptions and Error Handling (ERR)\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/146440597\\/child\",\"history\":\"\\/rest\\/api\\/content\\/146440597\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/146440597\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/pages\\/viewpage.action?pageId=146440601\",\"tinyui\":\"\\/x\\/mYG6C\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/146440601\"},\"id\":\"146440601\",\"type\":\"page\",\"title\":\"Rule 09. Object Oriented Programming (OOP)\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/146440601\\/child\",\"history\":\"\\/rest\\/api\\/content\\/146440601\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/146440601\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/pages\\/viewpage.action?pageId=178847757\",\"tinyui\":\"\\/x\\/DQCpCg\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/178847757\"},\"id\":\"178847757\",\"type\":\"page\",\"title\":\"Rule 10. Concurrency (CON)\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/178847757\\/child\",\"history\":\"\\/rest\\/api\\/content\\/178847757\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/178847757\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/pages\\/viewpage.action?pageId=146440610\",\"tinyui\":\"\\/x\\/ooG6C\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/146440610\"},\"id\":\"146440610\",\"type\":\"page\",\"title\":\"Rule 49. Miscellaneous (MSC)\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/146440610\\/child\",\"history\":\"\\/rest\\/api\\/content\\/146440610\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/146440610\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"}]";
    String goodResults = "[{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/display\\/cplusplus\\/DCL50-CPP.+Do+not+define+a+C-style+variadic+function\",\"tinyui\":\"\\/x\\/i4CW\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/9863307\"},\"id\":\"9863307\",\"type\":\"page\",\"title\":\"DCL50-CPP. Do not define a C-style variadic function\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/9863307\\/child\",\"history\":\"\\/rest\\/api\\/content\\/9863307\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/9863307\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/display\\/cplusplus\\/DCL51-CPP.+Do+not+declare+or+define+a+reserved+identifier\",\"tinyui\":\"\\/x\\/Qg8\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/3906\"},\"id\":\"3906\",\"type\":\"page\",\"title\":\"DCL51-CPP. Do not declare or define a reserved identifier\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/3906\\/child\",\"history\":\"\\/rest\\/api\\/content\\/3906\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/3906\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/display\\/cplusplus\\/DCL52-CPP.+Never+qualify+a+reference+type+with+const+or+volatile\",\"tinyui\":\"\\/x\\/VYFLAQ\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/21725525\"},\"id\":\"21725525\",\"type\":\"page\",\"title\":\"DCL52-CPP. Never qualify a reference type with const or volatile\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/21725525\\/child\",\"history\":\"\\/rest\\/api\\/content\\/21725525\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/21725525\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/display\\/cplusplus\\/DCL53-CPP.+Do+not+write+syntactically+ambiguous+declarations\",\"tinyui\":\"\\/x\\/zwCyAQ\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/28442831\"},\"id\":\"28442831\",\"type\":\"page\",\"title\":\"DCL53-CPP. Do not write syntactically ambiguous declarations\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/28442831\\/child\",\"history\":\"\\/rest\\/api\\/content\\/28442831\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/28442831\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/display\\/cplusplus\\/DCL54-CPP.+Overload+allocation+and+deallocation+functions+as+a+pair+in+the+same+scope\",\"tinyui\":\"\\/x\\/FYCpAQ\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/27885589\"},\"id\":\"27885589\",\"type\":\"page\",\"title\":\"DCL54-CPP. Overload allocation and deallocation functions as a pair in the same scope\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/27885589\\/child\",\"history\":\"\\/rest\\/api\\/content\\/27885589\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/27885589\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/display\\/cplusplus\\/DCL55-CPP.+Overloaded+postfix+increment+and+decrement+operators+should+return+a+const+object\",\"tinyui\":\"\\/x\\/mgCuAQ\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/28180634\"},\"id\":\"28180634\",\"type\":\"page\",\"title\":\"DCL55-CPP. Overloaded postfix increment and decrement operators should return a const object\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/28180634\\/child\",\"history\":\"\\/rest\\/api\\/content\\/28180634\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/28180634\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/display\\/cplusplus\\/DCL56-CPP.+Do+not+recursively+reenter+a+function+during+the+initialization+of+one+of+its+static+objects\",\"tinyui\":\"\\/x\\/1oAzAg\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/36929750\"},\"id\":\"36929750\",\"type\":\"page\",\"title\":\"DCL56-CPP. Do not recursively reenter a function during the initialization of one of its static objects\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/36929750\\/child\",\"history\":\"\\/rest\\/api\\/content\\/36929750\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/36929750\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/display\\/cplusplus\\/DCL57-CPP.+Functions+declared+with+%5B%5Bnoreturn%5D%5D+must+return+void\",\"tinyui\":\"\\/x\\/eICKC\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/143294584\"},\"id\":\"143294584\",\"type\":\"page\",\"title\":\"DCL57-CPP. Functions declared with [[noreturn]] must return void\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/143294584\\/child\",\"history\":\"\\/rest\\/api\\/content\\/143294584\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/143294584\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/display\\/cplusplus\\/DCL58-CPP.+Destructors+and+deallocation+functions+must+be+declared+noexcept\",\"tinyui\":\"\\/x\\/UwABCQ\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/151060563\"},\"id\":\"151060563\",\"type\":\"page\",\"title\":\"DCL58-CPP. Destructors and deallocation functions must be declared noexcept\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/151060563\\/child\",\"history\":\"\\/rest\\/api\\/content\\/151060563\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/151060563\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"},{\"extensions\":{\"position\":\"none\"},\"_links\":{\"webui\":\"\\/display\\/cplusplus\\/DCL59-CPP.+Do+not+define+an+unnamed+namespace+in+a+header+file\",\"tinyui\":\"\\/x\\/7gB9CQ\",\"self\":\"https:\\/\\/www.securecoding.cert.org\\/confluence\\/rest\\/api\\/content\\/159187182\"},\"id\":\"159187182\",\"type\":\"page\",\"title\":\"DCL59-CPP. Do not define an unnamed namespace in a header file\",\"_expandable\":{\"container\":\"\",\"metadata\":\"\",\"operations\":\"\",\"children\":\"\\/rest\\/api\\/content\\/159187182\\/child\",\"history\":\"\\/rest\\/api\\/content\\/159187182\\/history\",\"ancestors\":\"\",\"body\":\"\",\"version\":\"\",\"descendants\":\"\\/rest\\/api\\/content\\/159187182\\/descendant\",\"space\":\"\\/rest\\/api\\/space\\/cplusplus\"},\"status\":\"current\"}]";

    JSONParser parser = new JSONParser();
    try {
      JSONArray goodArray = (JSONArray) parser.parse(goodResults);
      cppType.extractRulesFromChildPages(baseUrl, ids, goodArray);
      assertThat(cppType.getCodingStandardRules().length).isEqualTo(10);

      JSONArray dudArray = (JSONArray) parser.parse(dudResults);
      cppType.extractRulesFromChildPages(baseUrl, ids, dudArray);
      assertThat(cppType.getCodingStandardRules().length).isEqualTo(10);


    } catch (ParseException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void testGetCertTypeStandardName() {

    Cert.CertType cppType = new Cert.CertType(Language.CPP, "146440541");

    assertThat(cppType.getStandardName()).isEqualTo(cert.getStandardName() + " " + cppType.getLanguage().getSq());

  }

}
