/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services.badge;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class FontProviderTest {

  @Test
  public void testComputeWidth() throws Exception {
    FontProvider fp = new FontProvider();

    assertThat(fp.computeWidth("foo")).isGreaterThan(0);

  }
}
