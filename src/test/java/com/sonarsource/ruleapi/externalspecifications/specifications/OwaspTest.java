/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;
import com.sonarsource.ruleapi.services.IntegrityEnforcementService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;


public class OwaspTest {

  OwaspTopTen owasp = new OwaspTopTen();

  @Test
  public void testParseOwaspFromSeeSection() {
    List<String> references = new ArrayList<String>();
    references.add("MITRE, CWE-123 - title");
    references.add("OWASP Top Ten 2013 Category A9");

    IntegrityEnforcementService enforcer = new IntegrityEnforcementService();

    List<String> refs = enforcer.parseReferencesFromStrings(OwaspTopTen.StandardRule.A9,references);

    assertThat(refs).hasSize(1).contains("A9");
  }

  @Test
  public void testBadFormatNoUpdates() {
    Rule rule = new Rule("");
    List<String> refs = new ArrayList<String>();
    refs.add("A1");
    refs.add("A2");

    TaggableStandard taggable = OwaspTopTen.StandardRule.A3;

    taggable.setRspecReferenceFieldValues(rule, refs);

    Map<String, Object> updates = new HashMap<String, Object>();

    assertThat(taggable.isFieldEntryFormatNeedUpdating(updates, rule)).isFalse();
    assertThat(updates).isEmpty();

    refs.add("blah A3 blah");
    assertThat(taggable.isFieldEntryFormatNeedUpdating(updates, rule)).isTrue();
    assertThat(updates).hasSize(1);

  }

  @Test
  public void testReports() {
    OwaspTopTen owasp = new OwaspTopTen();
    owasp.populateRulesCoverageMap();
    String linebreak = String.format("%n");

    String expectedSummaryReport = "OWASP Top Ten for Java" + linebreak +
            "A1\tSpecified: 0\tImplemented: 0" + linebreak +
            "A2\tSpecified: 0\tImplemented: 0" + linebreak +
            "A3\tSpecified: 0\tImplemented: 0" + linebreak +
            "A4\tSpecified: 0\tImplemented: 0" + linebreak +
            "A5\tSpecified: 0\tImplemented: 0" + linebreak +
            "A6\tSpecified: 0\tImplemented: 0" + linebreak +
            "A7\tSpecified: 0\tImplemented: 0" + linebreak +
            "A8\tSpecified: 0\tImplemented: 0" + linebreak +
            "A9\tSpecified: 0\tImplemented: 0" + linebreak +
            "A10\tSpecified: 0\tImplemented: 0" + linebreak;

    assertThat(owasp.getSummaryReport("")).isEqualTo(expectedSummaryReport);

    String expectedReport = "OWASP Top Ten for Java" + linebreak +
            "A1 - Injection" + linebreak +
            "\tSpecifying:   " + linebreak +
            "\tImplementing: " + linebreak +
            linebreak +
            "A2 - Broken Authentication and Session Management" + linebreak +
            "\tSpecifying:   " + linebreak +
            "\tImplementing: " + linebreak +
            linebreak +
            "A3 - Cross-Site Scripting (XSS)" + linebreak +
            "\tSpecifying:   " + linebreak +
            "\tImplementing: " + linebreak +
            linebreak +
            "A4 - Insecure Direct Object References" + linebreak +
            "\tSpecifying:   " + linebreak +
            "\tImplementing: " + linebreak +
            linebreak +
            "A5 - Security Misconfiguration" + linebreak +
            "\tSpecifying:   " + linebreak +
            "\tImplementing: " + linebreak +
            linebreak +
            "A6 - Sensitive Data Exposure" + linebreak +
            "\tSpecifying:   " + linebreak +
            "\tImplementing: " + linebreak +
            linebreak +
            "A7 - Missing Function Level Access Control" + linebreak +
            "\tSpecifying:   " + linebreak +
            "\tImplementing: " + linebreak +
            linebreak +
            "A8 - Cross-Site Request Forgery (CSRF)" + linebreak +
            "\tSpecifying:   " + linebreak +
            "\tImplementing: " + linebreak +
            linebreak +
            "A9 - Using Components with Known Vulnerabilities" + linebreak +
            "\tSpecifying:   " + linebreak +
            "\tImplementing: " + linebreak +
            linebreak +
            "A10 - Unvalidated Redirects and Forwards" + linebreak +
            "\tSpecifying:   " + linebreak +
            "\tImplementing: " + linebreak +
            linebreak;

    assertThat(owasp.getReport("")).isEqualTo(expectedReport);

  }

}
