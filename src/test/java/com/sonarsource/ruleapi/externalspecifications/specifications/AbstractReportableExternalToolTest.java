/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.externalspecifications.SupportedCodingStandard;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class AbstractReportableExternalToolTest {

  @Test
  public void testFormatLine() {

    FindBugs fb = (FindBugs) SupportedCodingStandard.FINDBUGS.getCodingStandard();

    String result = String.format("  yo                9   37.67%%%n");
    assertThat(fb.formatLine("yo", 9, (float)37.671293)).isEqualTo(result);
  }

}
