/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.services.IntegrityEnforcementService;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.utilities.Language;
import org.junit.Test;

import java.util.*;

import static org.fest.assertions.Assertions.assertThat;


public class CweTest {

  Cwe cwe = new Cwe();

  @Test
  public void testParseCweFromSeeSection() {

    List<String> references = new ArrayList<>();
    references.add("MITRE, CWE-123 - title");
    references.add("MITRE, 404 - RAH!");

    List<String> refs = IntegrityEnforcementService.parseReferencesFromStrings(cwe, references);

    assertThat(refs).hasSize(1).contains("CWE-123");
  }

  @Test
  public void testIsCweEntryFormatValid() {

    Rule rule = new Rule("");
    rule.setKey("test");

    List<String> references = new ArrayList<>();
    cwe.setRspecReferenceFieldValues(rule, references);

    List<String> updates = new ArrayList<>();

    assertThat(cwe.doesReferenceNeedUpdating("CWE-123",updates, rule.getKey())).isFalse();
    assertThat(updates).hasSize(1).contains("CWE-123");

    assertThat(cwe.doesReferenceNeedUpdating("456",updates, rule.getKey())).isTrue();
    assertThat(updates).hasSize(2).contains("CWE-456");

    assertThat(cwe.doesReferenceNeedUpdating("CWE234",updates, rule.getKey())).isFalse();
    assertThat(updates).hasSize(3).contains("CWE234");

  }

  @Test
  public void testSetCodingStandardRuleCoverageImplementedBy(){

    Cwe cwe1 = new Cwe();
    cwe1.setLanguage(Language.JAVA);

    assertThat(cwe.getRulesCoverage()).isNull();

    Rule sq2 = new Rule("Java");
    sq2.getCwe().add("CWE-123");
    sq2.getCwe().add("CWE-234");

    cwe1.setCodingStandardRuleCoverageImplemented(sq2.getCwe(), sq2);

    assertThat(cwe1.getRulesCoverage()).hasSize(2);
    assertThat(cwe1.getRulesCoverage().get("CWE-123").getImplementedBy().get(0)).isEqualTo(sq2);

  }

  @Test
  public void testSetCodingStandardRuleCoverageSpecifiedBy(){

    Cwe cwe1 = new Cwe();
    cwe1.setLanguage(Language.JAVA);

    assertThat(cwe.getRulesCoverage()).isNull();

    Rule sq2 = new Rule("Java");
    sq2.getCwe().add("CWE-123");
    sq2.getCwe().add("CWE-234");

    cwe1.setCodingStandardRuleCoverageSpecifiedBy(sq2, sq2.getCwe());

    assertThat(cwe1.getRulesCoverage()).hasSize(2);
    assertThat(cwe1.getRulesCoverage().get("CWE-123").getSpecifiedBy().get(0)).isEqualTo(sq2);

  }

  @Test
  public void testGetReportNullLanguage(){
    Cwe cwe1 = new Cwe();
    assertThat(cwe1.getReport(RuleManager.NEMO)).isNull();
  }


  @Test
  public void testGetReport(){
    Cwe cwe1 = new Cwe();
    String instance = "http://localhost:9000";

    Rule sq2 = new Rule("Java");
    sq2.setRepo("abap");
    sq2.getCwe().add("CWE-123");
    sq2.getCwe().add("CWE-234");
    sq2.setKey("NonNormalKey");

    assertThat(cwe1.generateReport(instance)).isNull();

    cwe1.setLanguage(Language.ABAP);

    assertThat(cwe1.generateReport(instance)).isNull();

    cwe1.setLanguage(null);
    cwe1.populateRulesCoverageMap();

    CodingStandardRuleCoverage csrc = new CodingStandardRuleCoverage();
    csrc.setCodingStandardRuleId("CWE-123");
    cwe1.getRulesCoverage().put("CWE-123", csrc);

    csrc = new CodingStandardRuleCoverage();
    csrc.setCodingStandardRuleId("CWE-234");
    cwe1.getRulesCoverage().put("CWE-234", csrc);

    cwe1.setCodingStandardRuleCoverageImplemented(sq2.getCwe(), sq2);

    assertThat(cwe1.generateReport(instance)).isNull();

    cwe1.setLanguage(Language.ABAP);

    String expectedReport = "<h2>ABAP coverage of CWE</h2>\n" +
            "<table>\n" +
            "<tr><td><a href='http://cwe.mitre.org/data/definitions/123' target='_blank'>CWE-123</a></td>\n" +
            "<td><a href='http://localhost:9000/coding_rules#rule_key=abap%3ANonNormalKey'>NonNormalKey</a> null<br/>\n" +
            "</td></tr>\n" +
            "<tr><td><a href='http://cwe.mitre.org/data/definitions/234' target='_blank'>CWE-234</a></td>\n" +
            "<td><a href='http://localhost:9000/coding_rules#rule_key=abap%3ANonNormalKey'>NonNormalKey</a> null<br/>\n" +
            "</td></tr>\n" +
            "</table>";

    assertThat(cwe1.generateReport(instance)).isEqualTo(expectedReport);

  }

}
