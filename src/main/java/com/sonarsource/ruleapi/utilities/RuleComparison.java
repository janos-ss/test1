/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;

import java.util.List;

/**
 * Provides nuanced comparison between rules, taking into account
 * such things as word variation e.g. "[Functions|Methods|Procedures] should not..."
 */
public class RuleComparison{

  public enum TimeUnit {
    MIN, H, D
  }

  private Rule spec;
  private Rule impl;

  /**
   * Initializes comparison with 2 rules to be compared
   * @param spec Rule fetched from RSpec
   * @param impl Rule as implemented
   */
  public RuleComparison(Rule spec, Rule impl) {
    this.spec = spec;
    this.impl = impl;
  }

  /**
   * Compares the 2 rules with which class was initialized
   *
   * @return non-zero iff meaningful differences found
   */
  public int compare() {
    int result = checkForNulls(spec, impl);
    if (result != 0 || spec == null) {
      return result;
    }

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

    result = compareDescriptionValues();
    if (result != 0) {
      return result;
    }

    result = compareSqaleValues();
    if (result != 0) {
      return result;
    }

    result = compareParameterList();
    if (result != 0) {
      return result;
    }

    result = compareTags();
    return result;
  }

  private int checkForNulls(Object spec, Object impl) {
    if (spec == null && impl == null) {
      return 0;
    }
    if (spec == null) {
      return 1;
    }
    if (impl == null) {
      return -1;
    }
    return 0;
  }

  private int compareDescriptionValues() {
    int result = compareDescription();
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

    result = compareReference();
    if (result != 0) {
      return result;
    }

    result = compareException();
    return result;
  }

  private int compareSqaleValues() {
    int result = compareSqaleCharacteristic();
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
    return result;
  }

  /**
   * Gives a simple listing of fields where differences were found.
   *
   * @return comma-delimited list of fields where meaningful differences were found
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    if (compareTitle() != 0) {
      sb.append("title, ");
    }

    if (compareSeverity() != 0) {
      sb.append("severity, ");
    }

    if (compareDefaultActive() != 0) {
      sb.append("default active, ");
    }

    if (compareTemplate() != 0) {
      sb.append("template, ");
    }

    if (compareMessage() != 0) {
      sb.append("message, ");
    }

    sb.append(toStringForDescription());

    sb.append(toStringForSqale());

    if (compareParameterList() != 0) {
      sb.append("parameter list, ");
    }

    if (compareTags() != 0) {
      sb.append("tags, ");
    }

    if (sb.length() > 0) {
      return "Differences: " + sb.toString();
    }

    return "";
  }

  private String toStringForSqale() {

    StringBuilder sb = new StringBuilder();
    if (compareSqaleCharacteristic() != 0) {
      sb.append("SQALE characteristic, ");
    }

    if (compareSqaleSubcharacertistic() != 0) {
      sb.append("SQALE sub-characteristic, ");
    }

    if (compareSqaleRemediationFunction() != 0) {
      sb.append("SQALE remediation function, ");
    }

    if (compareSqaleConstantCost() != 0) {
      sb.append("SQALE constant cost or linear threshold, ");
    }

    if (compareSqaleLinearArg() != 0) {
      sb.append("SQALE linear argument, ");
    }

    if (compareSqaleLinearFactor() != 0) {
      sb.append("SQALE linear factor, ");
    }

    if (compareSqaleLinearOffset() != 0) {
      sb.append("SQALE linear offset, ");
    }
    return sb.toString();
  }

  private String toStringForDescription() {

    StringBuilder sb = new StringBuilder();
    if (compareDescription() != 0) {
      sb.append("description text, ");
    }

    if (compareNoncompliant() != 0) {
      sb.append("noncompliant code example, ");
    }

    if (compareCompliant() != 0) {
      sb.append("compliant solution, ");
    }

    if (compareException() != 0) {
      sb.append("exceptions, ");
    }

    if (compareReference() != 0) {
      sb.append("references, ");
    }
    return sb.toString();
  }

  protected int compareSeverity() {
    int result = checkForNulls(spec.getSeverity(), impl.getSeverity());
    if (result != 0 || spec.getSeverity() == null) {
      return result;
    }
    return spec.getSeverity().compareTo(impl.getSeverity());
  }

  protected int compareDefaultActive() {
    int result = checkForNulls(spec.getDefaultActive(), impl.getDefaultActive());
    if (result != 0 || spec.getDefaultActive() == null) {
      return result;
    }
    return spec.getDefaultActive().compareTo(impl.getDefaultActive());
  }

  protected int compareTemplate() {
    return Boolean.valueOf(spec.isTemplate()).compareTo(Boolean.valueOf(impl.isTemplate()));
  }

  protected int compareTitle() {
    return compareTextFunctionalEquivalence(spec.getTitle(), impl.getTitle());
  }

  protected int compareMessage() {
    return compareTextFunctionalEquivalence(spec.getMessage(), impl.getMessage());
  }

  protected int compareDescription() {
    return compareTextFunctionalEquivalence(spec.getDescription(), impl.getDescription());
  }

  protected int compareNoncompliant() {
    return compareStrings(spec.getNonCompliant(), impl.getNonCompliant());
  }

  protected int compareCompliant() {
    return compareStrings(spec.getCompliant(), impl.getCompliant());
  }

  protected int compareException() {
    return compareStrings(spec.getExceptions(), impl.getExceptions());
  }

  protected int compareReference() {
    return compareStrings(spec.getReferences(), impl.getReferences());
  }

  protected int compareSqaleCharacteristic() {
    return compareStrings(spec.getSqaleCharac(), impl.getSqaleCharac());
  }

  protected int compareSqaleSubcharacertistic() {
    return compareStrings(spec.getSqaleSubCharac(), impl.getSqaleSubCharac());
  }

  protected int compareSqaleRemediationFunction() {
    return compareStrings(spec.getSqaleRemediationFunction(), impl.getSqaleRemediationFunction());
  }

  protected int compareSqaleLinearArg() {
    return compareStrings(spec.getSqaleLinearArg(), impl.getSqaleLinearArg());
  }

  protected int compareSqaleLinearFactor() {
    return compareStrings(spec.getSqaleLinearFactor(), impl.getSqaleLinearFactor());
  }

  protected int compareSqaleLinearOffset() {
    return compareStrings(spec.getSqaleLinearOffset(), impl.getSqaleLinearOffset());
  }

  protected int compareSqaleConstantCost() {
    String a = spec.getSqaleConstantCostOrLinearThreshold();
    String b = impl.getSqaleConstantCostOrLinearThreshold();

    int result = checkForNulls(a, b);
    if (result != 0 || a == null) {
      return result;
    }

    int aVal = Integer.valueOf(a.replaceAll("\\D",""));
    int bVal = Integer.valueOf(b.replaceAll("\\D",""));

    TimeUnit aUnit = TimeUnit.valueOf(a.replaceAll("\\d","").replaceAll("\\s","").toUpperCase().replace("MN", "MIN"));
    TimeUnit bUnit = TimeUnit.valueOf(b.replaceAll("\\d","").replaceAll("\\s","").toUpperCase().replace("MN", "MIN"));

    if (aUnit.compareTo(bUnit) == 0) {
      return Integer.valueOf(aVal).compareTo(Integer.valueOf(bVal));
    }
    return aUnit.compareTo(bUnit);
  }

  protected int compareParameterList() {
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

  protected int compareTags() {
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
    if (a == null && b == null) {
      return 0;
    }
    if (a == null) {
      return 1;
    }
    if (b == null) {
      return -1;
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
