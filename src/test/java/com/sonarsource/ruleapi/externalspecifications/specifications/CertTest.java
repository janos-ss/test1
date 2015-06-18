/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class CertTest {

  Cert cert = new Cert();

  @Test
  public void testBadFormatNoUpdates() {

    Rule rule = new Rule("");

    List<String> updates = new ArrayList<>();

    assertThat(cert.doesReferenceNeedUpdating("ABC01-Q",updates, rule.getKey())).isFalse();
    assertThat(updates).hasSize(1);

    assertThat(cert.doesReferenceNeedUpdating("ABC01-CPP",updates, rule.getKey())).isFalse();
    assertThat(updates).hasSize(2);

    assertThat(cert.doesReferenceNeedUpdating("413",updates, rule.getKey())).isFalse();
    assertThat(updates).hasSize(3);

    assertThat(cert.doesReferenceNeedUpdating("ABC43-Java.",updates, rule.getKey())).isTrue();
    assertThat(updates).hasSize(4);

  }
}
