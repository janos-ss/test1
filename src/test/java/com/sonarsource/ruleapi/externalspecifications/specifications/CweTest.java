/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.services.IntegrityEnforcementService;
import com.sonarsource.ruleapi.utilities.Language;
import org.junit.Test;

import java.util.*;

import static org.fest.assertions.Assertions.assertThat;


public class CweTest {

  Cwe cwe = new Cwe();

  @Test
  public void testParseCweFromSeeSection() {

    List<String> references = new ArrayList<String>();
    references.add("MITRE, CWE-123 - title");
    references.add("MITRE, 404 - RAH!");

    IntegrityEnforcementService enforcer = new IntegrityEnforcementService();
    List<String> refs = enforcer.parseReferencesFromStrings(cwe, references);

    assertThat(refs).hasSize(1).contains("CWE-123");
  }

  @Test
  public void testIsCweEntryFormatValid() {

    Rule rule = new Rule("");
    rule.setKey("test");

    List<String> references = new ArrayList<String>();
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
  public void testPopulateCweMap() {
    Cwe cwe1 = new Cwe();

    Map<String,List<Rule>> cweRules = new TreeMap<String, List<Rule>>();

    Rule sq = new Rule("Java");
    Rule rspec = sq;
    cwe1.populateStandardMap(cweRules, sq, rspec);

    assertThat(cweRules).isEmpty();

    sq.getCwe().add("CWE-234");
    cwe1.populateStandardMap(cweRules, sq, rspec);

    Rule sq2 = new Rule("Java");
    Rule rspec2 = sq2;
    sq2.getCwe().add("CWE-123");
    sq2.getCwe().add("CWE-234");

    cwe1.populateStandardMap(cweRules, sq2, rspec2);

    assertThat(cweRules.get("CWE-123")).isEqualTo(Arrays.asList(sq2));
    assertThat(cweRules.get("CWE-234")).contains(sq);
    assertThat(cweRules.values()).hasSize(2);
  }

  @Test
  public void testGetReport(){
    Cwe cwe1 = new Cwe();
    String instance = "http://localhost:9000";
    Map<String,List<Rule>> cweRules = new TreeMap<String, List<Rule>>();

    Rule sq2 = new Rule("Java");
    sq2.setRepo("abap");
    Rule rspec2 = sq2;
    sq2.getCwe().add("CWE-123");
    sq2.getCwe().add("CWE-234");
    sq2.setKey("NonNormalKey");

    assertThat(cwe1.generateReport(instance, cweRules)).isNull();

    cwe1.setLanguage(Language.ABAP);

    assertThat(cwe1.generateReport(instance, cweRules)).isNull();

    cwe1.setLanguage(null);
    cwe1.populateStandardMap(cweRules, sq2, rspec2);

    assertThat(cwe1.generateReport(instance, cweRules)).isNull();

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

    assertThat(cwe1.generateReport(instance, cweRules)).isEqualTo(expectedReport);

  }

}
