/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;


public class CweTest {

  Cwe cwe = new Cwe();

  @Test
  public void testParseCweFromSeeSection() {

    List<String> references = new ArrayList<String>();
    references.add("MITRE, CWE-123 - title");
    references.add("MITRE, 404 - RAH!");

    List<String> refs = cwe.parseReferencesFromStrings(references);

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


}
