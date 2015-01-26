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

}
