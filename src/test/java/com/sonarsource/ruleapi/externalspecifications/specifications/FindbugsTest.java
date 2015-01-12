/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.externalspecifications.specifications;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class FindbugsTest {

  FindBugs fb = new FindBugs();

  @Test
  public void testFormatLine() {

    String result = String.format("  yo                9   37.67%%%n");
    assertThat(fb.formatLine("yo", 9, (float)37.671293)).isEqualTo(result);
  }

}
