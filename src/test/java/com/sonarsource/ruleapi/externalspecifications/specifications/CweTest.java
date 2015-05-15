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
    references.add("CWE-123");
    references.add("456");
    cwe.setRspecReferenceFieldValues(rule, references);

    Map<String, Object> updates = new HashMap<String, Object>();


    cwe.isFieldEntryFormatNeedUpdating(updates, rule);
    List<String> ups = (List<String>) updates.get("CWE");

    assertThat(updates).hasSize(1);
    assertThat(rule.getCwe()).hasSize(2).contains("CWE-123").contains("CWE-456");
    assertThat(rule.getCwe()).isEqualTo(ups);
  }

  @Test
  public void testIsCweEntryFormatValidNot() {

    Rule rule = new Rule("");
    rule.setKey("test");

    List<String> references = new ArrayList<String>();
    references.add("CWE123");
    references.add("456");
    cwe.setRspecReferenceFieldValues(rule, references);

    Map<String, Object> updates = new HashMap<String, Object>();

    cwe.isFieldEntryFormatNeedUpdating(updates, rule);
    List<String> ups = (List<String>) updates.get("CWE");

    assertThat(updates).hasSize(0);
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
    cwe1.setLanguage(Language.ABAP);

    Map<String,List<Rule>> cweRules = new TreeMap<String, List<Rule>>();

    assertThat(cwe1.generateReport("http://localhost:9000", cweRules)).isNull();

    Rule sq2 = new Rule("Java");
    Rule rspec2 = sq2;
    sq2.getCwe().add("CWE-123");
    sq2.getCwe().add("CWE-234");
    sq2.setKey("NonNormalKey");

    cwe1.populateStandardMap(cweRules, sq2, rspec2);

    String expectedReport = "<h2>ABAP coverage of CWE</h2>\n" +
            "<table>\n" +
            "<tr><td><a href='http://cwe.mitre.org/data/definitions/123' target='_blank'>CWE-123</a></td>\n" +
            "<td><a href='http://localhost:9000/coding_rules#rule_key=abap%3ANonNormalKey'>NonNormalKey</a> null<br/>\n" +
            "</td></tr>\n" +
            "<tr><td><a href='http://cwe.mitre.org/data/definitions/234' target='_blank'>CWE-234</a></td>\n" +
            "<td><a href='http://localhost:9000/coding_rules#rule_key=abap%3ANonNormalKey'>NonNormalKey</a> null<br/>\n" +
            "</td></tr>\n" +
            "</table>";

    assertThat(cwe1.generateReport("http://localhost:9000", cweRules)).isEqualTo(expectedReport);

  }

}
