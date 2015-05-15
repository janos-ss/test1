/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.get.RuleMaker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


public class UtilitiesTest {

  @Test
  public void testNormalizeKey() {
    String key1 = "StrictMode";
    String key2 = "S1111";
    String key3 = "S000109";

    assertThat(Utilities.normalizeKey(key1)).isEqualTo(key1);
    assertThat(Utilities.normalizeKey(key2)).isEqualTo("RSPEC-1111");
    assertThat(Utilities.normalizeKey(key3)).isEqualTo("RSPEC-109");
  }

  @Test
  public void testDenormalizeKey() {
    String key1 = "StrictMode";
    String key2 = "RSPEC-1111";

    assertThat(Utilities.denormalizeKey(key1)).isEqualTo(key1);
    assertThat(Utilities.denormalizeKey(key2)).isEqualTo("S1111");
  }

  @Test
  public void testIsKeyNormal() {
    String key1 = "StrictMode";
    String key2 = "RSPEC-1111";
    String key3 = "S000109";

    assertThat(Utilities.isKeyNormal(key1)).isFalse();
    assertThat(Utilities.isKeyNormal(key2)).isTrue();
    assertThat(Utilities.isKeyNormal(key3)).isFalse();
    assertThat(Utilities.isKeyNormal(null)).isFalse();
  }

  @Test
  public void testListToString() {

    List<String> list = new ArrayList<>();

    assertThat(Utilities.listToString(list, true)).isEqualTo("");

    list.add("red");
    list.add("green");
    list.add("blue");

    assertThat(Utilities.listToString(list, true)).isEqualTo("red, green, blue");
    assertThat(Utilities.listToString(list, false)).isEqualTo("red green blue");
  }

  @Test
  public void testFindCharBefore() {
    String str = "a red, a yellow, a blue";
    int pos = str.indexOf("yellow");

    assertThat(Utilities.findBefore(str, pos, ',')).isEqualTo(5);
    assertThat(Utilities.findBefore(str, pos, '&')).isEqualTo(-1);
  }

  @Test
  public void testFindStringBefore() {
    String str = "this code, that code and the other code.";
    int pos = str.indexOf("and");

    assertThat(Utilities.findBefore(str, pos, "code")).isEqualTo(16);
    assertThat(Utilities.findBefore(str, pos, "purple")).isEqualTo(-1);
  }
}