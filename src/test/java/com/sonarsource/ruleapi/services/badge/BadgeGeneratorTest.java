/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services.badge;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class BadgeGeneratorTest {

  @Test
  public void testGetBadge() throws Exception {

    String label = "foo";
    String value = "bar";

    BadgeGenerator badger = new BadgeGenerator();

    String image = badger.getBadge(label, value);

    assertThat(image).doesNotContain("\n");
    assertThat(image).contains(label);
    assertThat(image).contains(value);
    assertThat(image).contains("svg");
  }

}
