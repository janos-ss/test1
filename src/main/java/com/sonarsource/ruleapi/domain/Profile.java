/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;


import java.util.Arrays;
import java.util.List;

public class Profile implements Comparable<Profile> {

  private static List<String> synonyms = Arrays.asList(new String[]{"Sonar way", "SonarQube Way", "Sonar C# way"});

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

  public void setKey(String key) {

    this.key = key;
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  @Override
  public int compareTo(Profile p) {

    if (p == null) {
      return -1;
    }

    if (synonyms.contains(name) && synonyms.contains(p.getName())) {
      return 0;
    }

    return name.compareTo(p.getName());
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
    if (synonyms.contains(name)) {
      return synonyms.get(0).hashCode();
    }
    return name.hashCode();
  }

  @Override
  public String toString(){
    return name;
  }

}
