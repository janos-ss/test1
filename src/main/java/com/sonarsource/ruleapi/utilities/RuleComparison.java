/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;

import java.util.List;

public class RuleComparison{

  public enum TimeUnit {
    MIN, H, D
  }

  private Rule spec;
  private Rule impl;

  public RuleComparison(Rule a, Rule b) {
    this.spec = a;
    this.impl = b;
  }

  public int compare() {
    int result = 0;

    result = compareTitle();
    if (result != 0) {
      return result;
    }

    result = compareSeverity();
    if (result != 0) {
      return result;
    }
    result = compareDefaultActive();
    if (result != 0) {
      return result;
    }

    result = compareTemplate();
    if (result != 0) {
      return result;
    }

    result = compareMessage();
    if (result != 0) {
      return result;
    }

    result = compareDescription();
    if (result != 0) {
      return result;
    }

    result = compareNoncompliant();
    if (result != 0) {
      return result;
    }

    result = compareCompliant();
    if (result != 0) {
      return result;
    }

    result = compareException();
    if (result != 0) {
      return result;
    }

    result = compareReference();
    if (result != 0) {
      return result;
    }

    result = compareSqaleCharacteristic();
    if (result != 0) {
      return result;
    }

    result = compareSqaleSubcharacertistic();
    if (result != 0) {
      return result;
    }

    result = compareSqaleRemediationFunction();
    if (result != 0) {
      return result;
    }

    result = compareSqaleConstantCost();
    if (result != 0) {
      return result;
    }

    result = compareSqaleLinearArg();
    if (result != 0) {
      return result;
    }

    result = compareSqaleLinearFactor();
    if (result != 0) {
      return result;
    }

    result = compareSqaleLinearOffset();
    if (result != 0) {
      return result;
    }

    result = compareParameterList();
    if (result != 0) {
      return result;
    }

    result = compareTags();
    if (result != 0) {
      return result;
    }

    return result;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();




    if (sb.length() == 0) {
      sb.append("Rules are equivalent.");
    }
    return sb.toString();
  }


  public int compareSeverity() {
    return spec.getSeverity().compareTo(impl.getSeverity());
  }

  public int compareDefaultActive() {
    return spec.getDefaultActive().compareTo(impl.getDefaultActive());
  }

  public int compareTemplate() {
    return Boolean.valueOf(spec.isTemplate()).compareTo(Boolean.valueOf(impl.isTemplate()));
  }

  public int compareTitle() {
    return compareTextFunctionalEquivalence(spec.getTitle(), impl.getTitle());
  }

  public int compareMessage() {
    return compareTextFunctionalEquivalence(spec.getMessage(), impl.getMessage());
  }

  public int compareDescription() {
    return compareTextFunctionalEquivalence(spec.getDescription(), impl.getDescription());
  }

  public int compareNoncompliant() {
    return compareStrings(spec.getNonCompliant(), impl.getNonCompliant());
  }

  public int compareCompliant() {
    return compareStrings(spec.getCompliant(), impl.getCompliant());
  }

  public int compareException() {
    return compareStrings(spec.getExceptions(), impl.getExceptions());
  }

  public int compareReference() {
    return compareStrings(spec.getReferences(), impl.getReferences());
  }

  public int compareSqaleCharacteristic() {
    return compareStrings(spec.getSqaleCharac(), impl.getSqaleCharac());
  }

  public int compareSqaleSubcharacertistic() {
    return compareStrings(spec.getSqaleSubCharac(), impl.getSqaleSubCharac());
  }

  public int compareSqaleRemediationFunction() {
    return compareStrings(spec.getSqaleRemediationFunction(), impl.getSqaleRemediationFunction());
  }

  public int compareSqaleLinearArg() {
    return compareStrings(spec.getSqaleLinearArg(), impl.getSqaleLinearArg());
  }

  public int compareSqaleLinearFactor() {
    return compareStrings(spec.getSqaleLinearFactor(), impl.getSqaleLinearFactor());
  }

  public int compareSqaleLinearOffset() {
    return compareStrings(spec.getSqaleLinearOffset(), impl.getSqaleLinearOffset());
  }

  public int compareSqaleConstantCost() {
    String a = spec.getSqaleConstantCostOrLinearThreshold();
    String b = impl.getSqaleConstantCostOrLinearThreshold();

    int aVal = Integer.valueOf(a.replaceAll("\\D", ""));
    int bVal = Integer.valueOf(b.replaceAll("\\D",""));

    TimeUnit aUnit = TimeUnit.valueOf(a.replaceAll("\\W", "").replace("mn", "min").toUpperCase());
    TimeUnit bUnit = TimeUnit.valueOf(b.replaceAll("\\W","").replace("mn", "min").toUpperCase());

    if (aUnit.compareTo(bUnit) == 0) {
      return Integer.valueOf(aVal).compareTo(Integer.valueOf(bVal));
    }
    return aUnit.compareTo(bUnit);
  }

  public int compareParameterList() {
    List<Parameter> aList = spec.getParameterList();
    List<Parameter> bList = impl.getParameterList();

    if (aList.size() != bList.size()) {
      return Integer.valueOf(aList.size()).compareTo(Integer.valueOf(bList.size()));
    }

    java.util.Collections.sort(aList);
    java.util.Collections.sort(bList);

    for (int i = 0; i < aList.size(); i++) {
      int result = aList.get(i).compareTo(bList.get(i));
      if (result != 0) {
        return result;
      }
    }
    return 0;
  }

  public int compareTags() {
    List<String> aList = spec.getTags();
    List<String> bList = impl.getTags();

    if (aList.size() != bList.size()) {
      return Integer.valueOf(aList.size()).compareTo(Integer.valueOf(bList.size()));
    }

    java.util.Collections.sort(aList);
    java.util.Collections.sort(bList);

    for (int i = 0; i < aList.size(); i++) {
      int result = aList.get(i).compareTo(bList.get(i));
      if (result != 0) {
        return result;
      }
    }
    return 0;
  }

  protected static int compareTextFunctionalEquivalence(String a, String b) {
    if (isTextFunctionallyEquivalent(a, b)) {
      return 0;
    }
    return a.compareTo(b);
  }

  protected static boolean isTextFunctionallyEquivalent(String a, String b){
    if (a == null && b == null) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    if (a.equals(b)) {
      return true;
    }
    return hasEquivalentTokens(a, b);
  }

  private static boolean hasEquivalentTokens(String a, String b) {
    if (a.contains("|") || b.contains("|")){
      String [] aTokens = a.split(" ");
      String [] bTokens = b.split(" ");

      for (int i=0; i<aTokens.length && i<bTokens.length; i++) {
        String aTok = aTokens[i];
        String bTok = bTokens[i];
        if (! (aTok.equals(bTok) || aTok.contains(bTok) || bTok.contains(aTok))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  private static int compareStrings(String a, String b) {
    if (a == null && b == null){
      return 0;
    }

    if (a != null && b != null) {
      return a.compareTo(b);
    }
    if (a == null) {
      return -1;
    }
    return 1;
  }

}
