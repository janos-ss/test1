/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.CodingStandardRuleCoverage;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.services.IntegrityEnforcementService;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


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

    assertThat(cwe1.getRulesCoverage().get("CWE-123").getSpecifiedBy().get(0)).isEqualTo(sq2);

  }

  @Test
  public void testGetReportNullLanguage(){
    Cwe cwe1 = new Cwe();
    assertThat(cwe1.getReport(RuleManager.SONARQUBE_COM)).isNull();
  }


  @Test
  public void testGetReport(){
    Cwe cwe1 = new Cwe();
    String instance = "http://localhost:9000";

    Rule sq2 = new Rule("Java");
    sq2.setTitle("CWE rule");
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

    String report = cwe1.generateReport(instance);
    assertThat(report).contains(sq2.getTitle());
    assertThat(report).contains(Utilities.getFormattedDateString());

  }

}
