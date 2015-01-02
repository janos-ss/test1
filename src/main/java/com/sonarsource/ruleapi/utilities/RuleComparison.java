/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import java.util.List;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;

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
  private String separator = "\n";

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
      logDifference(sb, "title", spec.getTitle(), impl.getTitle());
    }
    if (compareSeverity() != 0) {
      logDifference(sb, "severity", spec.getSeverity(), impl.getSeverity());
    }

    if (compareDefaultActive() != 0) {
      logDifference(sb, "default active", spec.getDefaultActive(),impl.getDefaultActive());
    }

    if (compareTemplate() != 0) {
      logDifference(sb, "template", spec.isTemplate(),impl.isTemplate());
    }

    sb.append(toStringForDescription());

    sb.append(toStringForSqale());

    if (compareParameterList() != 0) {
      logDifference(sb, "parameter list",
              parameterListToString(spec.getParameterList()), parameterListToString(impl.getParameterList()));
    }

    if (compareTags() != 0) {
      logDifference(sb,"tags",
              ComparisonUtilities.listToString(spec.getTags()), ComparisonUtilities.listToString(impl.getTags()));
    }

    if (sb.length() > 0) {
      sb.insert(0,separator);
      List<String> legacyKeys = impl.getLegacyKeys();
      if (legacyKeys != null && !legacyKeys.isEmpty()) {
        sb.insert(0, legacyKeys.get(0)).insert(0," - legacy key: ");
      }
      sb.insert(0, spec.getKey());

      return sb.toString();
    }

    return "";
  }

  protected String parameterListToString(List<Parameter> list) {

    StringBuilder sb = new StringBuilder();
    for (Parameter param : list) {
      sb.append(param.toString()).append(separator);
    }
    return sb.toString();
  }

  private String toStringForSqale() {

    StringBuilder sb = new StringBuilder();
    if (compareSqaleCharacteristic() != 0) {
      logDifference(sb, "SQALE characteristic", spec.getSqaleCharac(), impl.getSqaleCharac());
    }

    if (compareSqaleSubcharacertistic() != 0) {
      logDifference(sb, "SQALE sub-characteristic", spec.getSqaleSubCharac(), impl.getSqaleSubCharac());
    }

    if (compareSqaleRemediationFunction() != 0) {
      logDifference(sb, "SQALE remediation function", spec.getSqaleRemediationFunction(), impl.getSqaleRemediationFunction());
    }

    if (compareSqaleConstantCost() != 0) {
      logDifference(sb, "SQALE constant cost or linear threshold", spec.getSqaleConstantCostOrLinearThreshold(), impl.getSqaleConstantCostOrLinearThreshold());
    }

    if (compareSqaleLinearArg() != 0) {
      logDifference(sb, "SQALE linear argument", spec.getSqaleLinearArg(), impl.getSqaleLinearArg());
    }

    if (compareSqaleLinearFactor() != 0) {
      logDifference(sb, "SQALE linear factor", spec.getSqaleLinearFactor(), impl.getSqaleLinearFactor());
    }

    if (compareSqaleLinearOffset() != 0) {
      logDifference(sb, "SQALE linear offset", spec.getSqaleLinearOffset(), impl.getSqaleLinearOffset());
    }
    return sb.toString();
  }

  private String toStringForDescription() {

    StringBuilder sb = new StringBuilder();
    if (compareDescription() != 0) {
      logDifference(sb, "description text", null, null);
    }

    if (compareNoncompliant() != 0) {
      logDifference(sb, "noncompliant code example", null, null);
    }

    if (compareCompliant() != 0) {
      logDifference(sb, "compliant solution", null, null);
    }

    if (compareException() != 0) {
      logDifference(sb, "exceptions", null, null);

    }

    if (compareReference() != 0) {
      logDifference(sb, "references", null, null);
    }
    return sb.toString();
  }

  protected void logDifference(StringBuilder sb, String differenceTitle, Object specValue, Object implValue) {
    String specTitle = "    spec: ";
    String implTitle = "    impl: ";

    sb.append("  ").append(differenceTitle).append(separator);
    if (specValue != null || implValue != null){
      sb.append(specTitle).append(specValue).append(separator);
      sb.append(implTitle).append(implValue).append(separator);
    }

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
    return Boolean.valueOf(spec.isTemplate()).compareTo(impl.isTemplate());
  }

  protected int compareTitle() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getTitle(), impl.getTitle(), true);
  }

  protected int compareMessage() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getMessage(), impl.getMessage(), false);
  }

  protected int compareDescription() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getDescription(), impl.getDescription(), true);
  }

  protected int compareNoncompliant() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getNonCompliant(), impl.getNonCompliant(), true);
  }

  protected int compareCompliant() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getCompliant(), impl.getCompliant(), true);
  }

  protected int compareException() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getExceptions(), impl.getExceptions(), true);
  }

  protected int compareReference() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getReferences(), impl.getReferences(), true);
  }

  protected int compareSqaleCharacteristic() {
    return ComparisonUtilities.compareStrings(spec.getSqaleCharac(), impl.getSqaleCharac());
  }

  protected int compareSqaleSubcharacertistic() {
    int result = checkForNulls(spec.getSqaleSubCharac(), impl.getSqaleSubCharac());
    if (result != 0 || spec.getSqaleSubCharac() == null) {
      return result;
    }
    return spec.getSqaleSubCharac().compareTo(impl.getSqaleSubCharac());
  }

  protected int compareSqaleRemediationFunction() {
    int result = checkForNulls(spec.getSqaleRemediationFunction(), impl.getSqaleRemediationFunction());
    if (result != 0 || spec.getSqaleRemediationFunction() == null) {
      return result;
    }
    return spec.getSqaleRemediationFunction().compareTo(impl.getSqaleRemediationFunction());
  }

  protected int compareSqaleLinearArg() {
    return ComparisonUtilities.compareStrings(spec.getSqaleLinearArg(), impl.getSqaleLinearArg());
  }

  protected int compareSqaleLinearFactor() {
    return compareSqaleTimeValue(spec.getSqaleLinearFactor(), impl.getSqaleLinearFactor());
  }

  protected int compareSqaleLinearOffset() {
    return compareSqaleTimeValue(spec.getSqaleLinearOffset(), impl.getSqaleLinearOffset());
  }

  protected int compareSqaleConstantCost() {
    return compareSqaleTimeValue(spec.getSqaleConstantCostOrLinearThreshold(), impl.getSqaleConstantCostOrLinearThreshold());
  }

  protected int compareSqaleTimeValue(String a, String b) {

    int result = checkForNulls(a, b);
    if (result != 0 || a == null) {
      return result;
    }

    int aVal = Integer.valueOf(a.replaceAll("\\D",""));
    int bVal = Integer.valueOf(b.replaceAll("\\D",""));

    TimeUnit aUnit = null;
    String tmp = a.replaceAll("\\d","").replaceAll("\\s", "").toUpperCase().replace("MN", "MIN");
    if (tmp.length()>0) {
      aUnit = TimeUnit.valueOf(tmp);
    }
    tmp = b.replaceAll("\\d","").replaceAll("\\s", "").toUpperCase().replace("MN", "MIN");
    TimeUnit bUnit = TimeUnit.valueOf(tmp);

    result = checkForNulls(aUnit, bUnit);
    if (result != 0 || aUnit == null) {
      return result;
    }

    if (aUnit.compareTo(bUnit) == 0) {
      return Integer.valueOf(aVal).compareTo(bVal);
    }
    return aUnit.compareTo(bUnit);
  }

  protected int compareParameterList() {
    List<Parameter> aList = spec.getParameterList();
    List<Parameter> bList = impl.getParameterList();

    if (aList.size() != bList.size()) {
      return Integer.valueOf(aList.size()).compareTo(bList.size());
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
      return Integer.valueOf(aList.size()).compareTo(bList.size());
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

}
