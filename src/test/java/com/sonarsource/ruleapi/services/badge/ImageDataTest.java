/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services.badge;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class ImageDataTest {

  @Test
  public void testImageData() throws Exception {
    String label = "foo";
    String value = "bar";

    ImageData data = new ImageData(new FontProvider(), label, value);

    assertThat(data.labelText()).isEqualTo(label);
    assertThat(data.valueText()).isEqualTo(value);

    assertThat(Integer.valueOf(data.labelWidth())).isGreaterThan(0);
    assertThat(Integer.valueOf(data.valueWidth())).isGreaterThan(0);

    assertThat(Integer.valueOf(data.labelMidpoint())).isLessThan(Integer.valueOf(data.labelWidth()));
    assertThat(Integer.valueOf(data.valueMidPoint())).isGreaterThan(Integer.valueOf(data.labelMidpoint()));

    assertThat(Integer.valueOf(data.totalWidth())).isEqualTo(Integer.valueOf(data.labelWidth()) + Integer.valueOf(data.valueWidth()));

  }

}
