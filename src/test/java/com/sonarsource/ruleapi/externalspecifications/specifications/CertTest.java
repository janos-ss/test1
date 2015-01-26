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


public class CertTest {

  Cert cert = new Cert();

  @Test
  public void testBadFormatNoUpdates() {

    Rule rule = new Rule("");
    List<String> refs = new ArrayList<String>();
    refs.add("ABC01-Q");
    refs.add("ABC01-CPP");

    cert.setRspecReferenceFieldValues(rule, refs);

    Map<String, Object> updates = new HashMap<String, Object>();

    assertThat(cert.isFieldEntryFormatNeedUpdating(updates, rule)).isFalse();
    assertThat(updates).isEmpty();

    refs.add("413");
    assertThat(cert.isFieldEntryFormatNeedUpdating(updates,rule)).isFalse();
    assertThat(updates).isEmpty();

    refs.add("ABC43-Java.");
    assertThat(cert.isFieldEntryFormatNeedUpdating(updates, rule)).isTrue();
    assertThat(updates).hasSize(1);

    List<String> ids = (List<String>) updates.get(cert.getRSpecReferenceFieldName());
    assertThat(ids).hasSize(4);
  }
}
