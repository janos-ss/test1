/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import com.sonarsource.ruleapi.get.JiraHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    copy.setDefaultVal(full.getDefaultVal());
    copy.setKey("copyKey");
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

    assertThat(copy.equals(full)).isFalse();
  }

  @Test
  public void testEqualsBothDefaultNull() {
    Parameter copy = new Parameter();
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    Parameter p2 = new Parameter();
    p2.setKey(copy.getKey());
    p2.setDescription(copy.getDescription());

    assertThat(copy.equals(p2)).isTrue();

  }

  @Test
  public void NeqObject() {

    assertThat(full.equals(null)).isFalse();

    Object obj = new Object();
    assertThat(full.equals(obj)).isFalse();

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

  @Test
  public void testToStringNulls() {
    Parameter param = new Parameter();
    param.setKey("key");
    param.setType("boolean");

    assertThat(param.toString()).isEqualTo("* key = key\n* type = boolean");
  }

  @Test
  public void testKeyCompare() {

    String key = "Key";
    Parameter param1 = new Parameter();
    param1.setKey(key);

    Parameter param2 = new Parameter();
    param2.setKey(key);

    assertThat(param1.compareTo(param2)).isEqualTo(0);

    param2.setKey(key.toLowerCase());
    assertThat(param1.compareTo(param2)).isEqualTo(0);

  }

  @Test
  public void testAllEqualButKeySubstring() {

    List<Parameter> params1 = JiraHelper.handleParameterList("* key = Max\r\n" +
            "* description = Maximum authorized lines in a file.\r\n" +
            "* default = 1000", "JavaScript");
    List<Parameter> params2 = JiraHelper.handleParameterList("* key = maximum\r\n" +
            "* description = Maximum authorized lines in a file.\r\n" +
            "* default = 1000\r\n" +
            "* type = INTEGER", "JavaScript");

    assertThat(params1.get(0).compareTo(params2.get(0))).isEqualTo(0);
  }

  @Test
  public void testAllEqualButKeysWordMissing() {

    List<Parameter> params1 = JiraHelper.handleParameterList("* key = legalTrailingCommentPattern\r\n" +
            "* description = Pattern for text of trailing comments that are allowed.\r\n" +
            "* default = ^\\s*+[^\\s]++$", "JavaScript");
    List<Parameter> params2 = JiraHelper.handleParameterList("* key = legalCommentPattern\r\n" +
            "* description = Pattern for text of trailing comments that are allowed.\r\n" +
            "* default = ^\\s*+[^\\s]++$\r\n" +
            "* type = STRING", "JavaScript");
    assertThat(params1.get(0).compareTo(params2.get(0))).isEqualTo(0);
  }

}
