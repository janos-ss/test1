/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services.badge;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class FontProviderTest {

  @Test
  public void testComputeWidth() throws Exception {
    FontProvider fp = new FontProvider();

    assertThat(fp.computeWidth("foo")).isGreaterThan(0);

  }
}
