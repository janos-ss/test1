/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.utilities.Language;
import java.util.HashMap;
import java.util.Map;
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

    rule.setReferences("CERT, ASDF01");
    report = cert.getCleanupReportBody(list);
    assertThat(report).isEqualTo("Not found: null : CERT, ASDF01\n" +
            "https://www.securecoding.cert.org/confluence/rest/api/content?title=ASDF01\n");
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

}
