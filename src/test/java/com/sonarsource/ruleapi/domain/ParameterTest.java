/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.domain;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ParameterTest {

  private static Parameter full;

  @BeforeClass
  public static void setUp() throws Exception
  {
    full = new Parameter();
    full.setKey("fullKey");
    full.setDescription("This is the description of the fully-described parameter");
    full.setType("Text");
    full.setDefaultVal("*.*");
  }

  @Test
  public void testEqualsSameRef() throws Exception {

    assertThat(full.equals(full)).isTrue();
  }

  @Test
  public void testEqualsSameValues () throws Exception {
    Parameter copy = new Parameter();
    copy.setDefaultVal(full.getDefaultVal());
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    assertThat(full.equals(copy)).isTrue();
  }

  @Test
  public void testEqualsNeqKey() throws Exception {
    Parameter copy = new Parameter();
    copy.setDefaultVal("copyKey");
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    assertThat(full.equals(copy)).isFalse();
  }

  @Test
  public void testEqualsNeqDescription () throws Exception {
    Parameter copy = new Parameter();
    copy.setDefaultVal(full.getDefaultVal());
    copy.setKey(full.getKey());
    copy.setDescription("New description");

    assertThat(full.equals(copy)).isFalse();
  }

  @Test
  public void testEqualsNeqDefault () throws Exception {
    Parameter copy = new Parameter();
    copy.setDefaultVal("**/*.*");
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    assertThat(full.equals(copy)).isFalse();
  }

  @Test
  public void testEqualsNeqNullDefault () throws Exception {
    Parameter copy = new Parameter();
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    assertThat(full.equals(copy)).isFalse();
  }

  @Test
  public void testEqualsNeqOtherNullDefault () throws Exception {
    Parameter copy = new Parameter();
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    assertThat(full.equals(copy)).isFalse();
  }

  @Test
  public void testHashCodeDefault () throws Exception {

    assertThat(full.hashCode()).isEqualTo(-1130130840);
  }

  @Test
  public void testHashCodeNullDefault () throws Exception {
    Parameter copy = new Parameter();
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    assertThat(copy.hashCode()).isEqualTo(-1130172670);
  }
}
