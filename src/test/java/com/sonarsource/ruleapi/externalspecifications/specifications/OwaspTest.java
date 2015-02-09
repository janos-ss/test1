/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
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

    List<String> refs = enforcer.parseReferencesFromStrings(owasp,references);

    assertThat(refs).hasSize(1).contains("A9");
  }

  @Test
  public void testBadFormatNoUpdates() {
    Rule rule = new Rule("");
    List<String> refs = new ArrayList<String>();
    refs.add("A1");
    refs.add("A2");

    owasp.setRspecReferenceFieldValues(rule, refs);

    Map<String, Object> updates = new HashMap<String, Object>();

    assertThat(owasp.isFieldEntryFormatNeedUpdating(updates, rule)).isFalse();
    assertThat(updates).isEmpty();

    refs.add("blah A3 blah");
    assertThat(owasp.isFieldEntryFormatNeedUpdating(updates, rule)).isTrue();
    assertThat(updates).hasSize(1);

  }

  @Test
  public void testReports() {
    OwaspTopTen owasp = new OwaspTopTen();
    owasp.populateRulesCoverageMap();

    String expectedSummaryReport = "OWASP Top Ten for Java\n" +
            "A1\tSpecified: 0\tImplemented: 0\n" +
            "A2\tSpecified: 0\tImplemented: 0\n" +
            "A3\tSpecified: 0\tImplemented: 0\n" +
            "A4\tSpecified: 0\tImplemented: 0\n" +
            "A5\tSpecified: 0\tImplemented: 0\n" +
            "A6\tSpecified: 0\tImplemented: 0\n" +
            "A7\tSpecified: 0\tImplemented: 0\n" +
            "A8\tSpecified: 0\tImplemented: 0\n" +
            "A9\tSpecified: 0\tImplemented: 0\n" +
            "A10\tSpecified: 0\tImplemented: 0\n";

    assertThat(owasp.getSummaryReport("")).isEqualTo(expectedSummaryReport);

    String expectedReport = "OWASP Top Ten for Java\n" +
            "A1 - Injection\n" +
            "\tSpecifying:   \n" +
            "\tImplementing: \n" +
            "\n" +
            "A2 - Broken Authentication and Session Management\n" +
            "\tSpecifying:   \n" +
            "\tImplementing: \n" +
            "\n" +
            "A3 - Cross-Site Scripting (XSS)\n" +
            "\tSpecifying:   \n" +
            "\tImplementing: \n" +
            "\n" +
            "A4 - Insecure Direct Object References\n" +
            "\tSpecifying:   \n" +
            "\tImplementing: \n" +
            "\n" +
            "A5 - Security Misconfiguration\n" +
            "\tSpecifying:   \n" +
            "\tImplementing: \n" +
            "\n" +
            "A6 - Sensitive Data Exposure\n" +
            "\tSpecifying:   \n" +
            "\tImplementing: \n" +
            "\n" +
            "A7 - Missing Function Level Access Control\n" +
            "\tSpecifying:   \n" +
            "\tImplementing: \n" +
            "\n" +
            "A8 - Cross-Site Request Forgery (CSRF)\n" +
            "\tSpecifying:   \n" +
            "\tImplementing: \n" +
            "\n" +
            "A9 - Using Components with Known Vulnerabilities\n" +
            "\tSpecifying:   \n" +
            "\tImplementing: \n" +
            "\n" +
            "A10 - Unvalidated Redirects and Forwards\n" +
            "\tSpecifying:   \n" +
            "\tImplementing: \n" +
            "\n";

    assertThat(owasp.getReport("")).isEqualTo(expectedReport);

  }

}
