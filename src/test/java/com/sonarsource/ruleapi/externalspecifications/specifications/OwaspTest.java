/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;
import com.sonarsource.ruleapi.services.IntegrityEnforcementService;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class OwaspTest {

  OwaspTopTen owasp = new OwaspTopTen();

  @Test
  public void testParseOwaspFromSeeSection() {
    List<String> references = new ArrayList<>();
    references.add("MITRE, CWE-123 - title");
    references.add("OWASP Top Ten 2013 Category A9");

    List<String> refs = IntegrityEnforcementService.parseReferencesFromStrings(OwaspTopTen.StandardRule.A9,references);

    assertThat(refs).hasSize(1).contains("A9");
  }

  @Test
  public void testBadReferenceFormat() {
    Rule rule = new Rule("");

    TaggableStandard taggable = OwaspTopTen.StandardRule.A3;

    List<String> updates = new ArrayList<>();

    assertThat(taggable.doesReferenceNeedUpdating("A1",updates, rule.getKey())).isFalse();
    assertThat(updates).hasSize(1);

    assertThat(taggable.doesReferenceNeedUpdating("A2",updates, rule.getKey())).isFalse();
    assertThat(updates).hasSize(2);

    assertThat(taggable.doesReferenceNeedUpdating("blah A3 blah",updates, rule.getKey())).isTrue();
    assertThat(updates).hasSize(3);
  }

  @Test
  public void testSetFieldValues() {

    Rule rule = new Rule("");
    List<String> ids = new ArrayList<>();
    ids.add("A1");
    ids.add("A2");

    OwaspTopTen owasp = new OwaspTopTen();
    owasp.setRspecReferenceFieldValues(rule, ids);

    assertThat(rule.getOwasp()).isEqualTo(ids);

  }

  @Test
  public void testReports() {
    OwaspTopTen owasp = new OwaspTopTen();
    owasp.populateRulesCoverageMap();
    String linebreak = String.format("%n");

    String expectedSummaryReport = "OWASP Top Ten" + linebreak +
            "A1\tSpecified: 0" + linebreak +
            "A2\tSpecified: 0" + linebreak +
            "A3\tSpecified: 0" + linebreak +
            "A4\tSpecified: 0" + linebreak +
            "A5\tSpecified: 0" + linebreak +
            "A6\tSpecified: 0" + linebreak +
            "A7\tSpecified: 0" + linebreak +
            "A8\tSpecified: 0" + linebreak +
            "A9\tSpecified: 0" + linebreak +
            "A10\tSpecified: 0" + linebreak;

    assertThat(owasp.getSummaryReport("")).isEqualTo(expectedSummaryReport);

    String expectedReport = "OWASP Top Ten" + linebreak +
            "A1 - Injection" + linebreak +
            "\tSpecifying:   " + linebreak +
            linebreak +
            "A2 - Broken Authentication and Session Management" + linebreak +
            "\tSpecifying:   " + linebreak +
            linebreak +
            "A3 - Cross-Site Scripting (XSS)" + linebreak +
            "\tSpecifying:   " + linebreak +
            linebreak +
            "A4 - Insecure Direct Object References" + linebreak +
            "\tSpecifying:   " + linebreak +
            linebreak +
            "A5 - Security Misconfiguration" + linebreak +
            "\tSpecifying:   " + linebreak +
            linebreak +
            "A6 - Sensitive Data Exposure" + linebreak +
            "\tSpecifying:   " + linebreak +
            linebreak +
            "A7 - Missing Function Level Access Control" + linebreak +
            "\tSpecifying:   " + linebreak +
            linebreak +
            "A8 - Cross-Site Request Forgery (CSRF)" + linebreak +
            "\tSpecifying:   " + linebreak +
            linebreak +
            "A9 - Using Components with Known Vulnerabilities" + linebreak +
            "\tSpecifying:   " + linebreak +
            linebreak +
            "A10 - Unvalidated Redirects and Forwards" + linebreak +
            "\tSpecifying:   " + linebreak +
            linebreak;

    assertThat(owasp.getReport("")).isEqualTo(expectedReport);

  }

  @Test
  public void testAddTagIfMissing() {

    Rule rule = new Rule("Java");
    Map<String, Object> updates = new HashMap<>();

    // !hasTag && !needsTag
    OwaspTopTen.StandardRule.A1.addTagIfMissing(rule, updates);
    assertThat(updates).isEmpty();

    rule.getOwasp().add("A1");

    // !hasTag && needsTag
    OwaspTopTen.StandardRule.A1.addTagIfMissing(rule, updates);
    assertThat(updates).hasSize(1);
    assertThat(updates.containsKey("Labels")).isTrue();

    updates.clear();

    // previous run added tag to rule
    // hasTag && needsTag
    OwaspTopTen.StandardRule.A1.addTagIfMissing(rule, updates);
    assertThat(updates).isEmpty();

    rule.getOwasp().clear();
    updates.clear();

    // hasTag && !needsTag
    OwaspTopTen.StandardRule.A1.addTagIfMissing(rule, updates);
    assertThat(updates).hasSize(1);
    assertThat(updates.containsKey("Labels")).isTrue();
    assertThat((Set)updates.get("Labels")).isEmpty();

    rule.setStatus(Rule.Status.DEPRECATED);
    rule.getOwasp().add("A1");
    updates.clear();

    // !hasTag && needsTag BUT deprecated
    OwaspTopTen.StandardRule.A1.addTagIfMissing(rule, updates);
    assertThat(updates).hasSize(0);

  }

  @Test
  public void testGenerateReport(){

    String instance = "http://localhost:9000";

    OwaspTopTen owasp = new OwaspTopTen();
    Map<String, List<Rule>> standardRules = new TreeMap<>();

    Rule rule = new Rule("");
    List<String> list = owasp.getRspecReferenceFieldValues(rule);
    list.add("A1");
    list.add("A2");

    assertThat(owasp.generateReport(instance)).isNull();

    owasp.setLanguage(Language.C);
    assertThat(owasp.generateReport(instance)).isNull();

    owasp.setLanguage(null);
    owasp.setCodingStandardRuleCoverageImplemented(list, rule);

    assertThat(owasp.generateReport(instance)).isNull();

    owasp.setLanguage(Language.C);
    owasp.setCodingStandardRuleCoverageImplemented(list, rule);

    String report = owasp.generateReport(instance);

    assertThat(report).isNotNull();
    assertThat(report).contains("https://www.owasp.org/index.php/Top_10_2013-A1-Injection");
    assertThat(report).contains(Utilities.getFormattedDateString());

  }

  @Test
  public void testSetLanguageResetsRulesCoverageMap() {
    OwaspTopTen owasp = new OwaspTopTen();
    assertThat(owasp.getRulesCoverage()).isNull();

    owasp.setLanguage(Language.ABAP);
    assertThat(owasp.getRulesCoverage()).isNull();

    owasp.populateRulesCoverageMap();
    assertThat(owasp.getRulesCoverage()).isNotNull();

    owasp.setLanguage(Language.JAVA);
    assertThat(owasp.getRulesCoverage()).isNull();

  }

}
