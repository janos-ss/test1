/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class ProfileTest {

  private Profile p0 = null;
  private Profile p1 = new Profile("Sonar way", "sonar-way-java-4579");
  private Profile p2 = new Profile("Warrior's way");
  private Profile p3 = new Profile("SonarQube Way");

  @Test
  public void testCompareToAndEquals() {

    assertThat(p1.compareTo(p0)).isEqualTo(-1);
    assertThat(p1.compareTo(p2)).isEqualTo(-4);
    assertThat(p1.compareTo(p3)).isEqualTo(0);
  }

  @Test
  public void testEquals(){
    Parameter param = new Parameter();

    assertThat(p1.equals(p1)).isTrue();
    assertThat(p1.equals(p0)).isFalse();
    assertThat(p1.equals(p2)).isFalse();
    assertThat(p1.equals(p3)).isTrue();
    assertThat(p1.equals(param)).isFalse();
  }

  @Test
  public void testHashCode(){

    assertThat(p1.hashCode()).isEqualTo(p3.hashCode());
    assertThat(p1.hashCode()).isNotEqualTo(p2.hashCode());
  }


}
