/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;


import java.util.Arrays;
import java.util.List;

public class Profile implements Comparable<Profile> {

  private static List<String> synonyms = Arrays.asList("sonar way", "sonarqube way", "sonar c# way");

  private String key;
  private String name;

  public Profile (String name) {

    this(name, null);
  }

  public Profile(String name, String key) {
    this.name = name;
    this.key = key;
  }

  public String getKey() {

    return key;
  }

  public String getName() {

    return name;
  }

  public String getLowerCaseName() {
    return name.toLowerCase();
  }

  @Override
  public int compareTo(Profile p) {

    if (p == null) {
      return -1;
    }

    if (synonyms.contains(getLowerCaseName()) && synonyms.contains(p.getLowerCaseName())) {
      return 0;
    }

    return getLowerCaseName().compareTo(p.getLowerCaseName());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Profile profile = (Profile)o;

    return compareTo(profile) == 0;
  }

  @Override
  public int hashCode(){
    if (synonyms.contains(getLowerCaseName())) {
      return synonyms.get(0).hashCode();
    }
    return getLowerCaseName().hashCode();
  }

  @Override
  public String toString(){
    return name;
  }

}
