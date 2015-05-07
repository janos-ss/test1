/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import java.util.ArrayList;
import java.util.List;

import com.sonarsource.ruleapi.utilities.ComparisonUtilities;
import com.sonarsource.ruleapi.utilities.Utilities;

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

    result = compareStatus();
    if (result != 0) {
      return result;
    }

    result = compareTemplate();
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
    if (result != 0) {
      return result;
    }

    result = compareDeprecation();
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

    if (compareStatus() != 0) {
      logDifference(sb, "status", spec.getStatus(), impl.getStatus());
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
      logDifference(sb,"tags:" + getTagDifferences(),
              Utilities.listToString(spec.getTags(), true), Utilities.listToString(impl.getTags(), true));
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
      logDifference(sb, "SQALE linear argument", spec.getSqaleLinearArgDesc(), impl.getSqaleLinearArgDesc());
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

    logDifference(sb, "description text",
            ComparisonUtilities.getFirstDifferingToken(spec.getDescription(), impl.getDescription()));

    logDifference(sb, "noncompliant code example",
            ComparisonUtilities.getFirstDifferingToken(spec.getNonCompliant(), impl.getNonCompliant()));

    logDifference(sb, "compliant solution",
            ComparisonUtilities.getFirstDifferingToken(spec.getCompliant(), impl.getCompliant()));

    logDifference(sb, "exceptions",
            ComparisonUtilities.getFirstDifferingToken(spec.getExceptions(), impl.getExceptions()));

    logDifference(sb, "references",
            ComparisonUtilities.getFirstDifferingToken(spec.getReferences(), impl.getReferences()));

    logDifference(sb, "deprecation",
            ComparisonUtilities.getFirstDifferingToken(spec.getDeprecation(), impl.getDeprecation()));

    return sb.toString();
  }

  protected void logDifference(StringBuilder sb, String differenceTitle, String[] differentTokens) {
    if (differentTokens != null && differentTokens.length == 2) {
      logDifference(sb, differenceTitle, differentTokens[0], differentTokens[1]);
    }
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

  protected int compareStatus() {

    int result = checkForNulls(spec.getStatus(), impl.getStatus());
    if (result != 0 || spec.getStatus() == null) {
      return result;
    }
    return spec.getStatus().compareTo(impl.getStatus());
  }

  protected int compareSeverity() {
    int result = checkForNulls(spec.getSeverity(), impl.getSeverity());
    if (result != 0 || spec.getSeverity() == null) {
      return result;
    }
    return spec.getSeverity().compareTo(impl.getSeverity());
  }

  protected int compareTemplate() {
    return Boolean.valueOf(spec.isTemplate()).compareTo(impl.isTemplate());
  }

  protected int compareTitle() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getTitle(), impl.getTitle());
  }

  protected int compareDescription() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getDescription(), impl.getDescription());
  }

  protected int compareNoncompliant() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getNonCompliant(), impl.getNonCompliant());
  }

  protected int compareCompliant() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getCompliant(), impl.getCompliant());
  }

  protected int compareException() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getExceptions(), impl.getExceptions());
  }

  protected int compareDeprecation() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getDeprecation(), impl.getDeprecation());
  }

  protected int compareReference() {
    return ComparisonUtilities.compareTextFunctionalEquivalence(spec.getReferences(), impl.getReferences());
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
    return ComparisonUtilities.compareStrings(spec.getSqaleLinearArgDesc(), impl.getSqaleLinearArgDesc());
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

    int aVal = Integer.parseInt(a.replaceAll("\\D",""));
    int bVal = Integer.parseInt(b.replaceAll("\\D",""));

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

  protected String getTagDifferences() {

    StringBuilder sb = new StringBuilder();

    List<String> aList = spec.getTags();
    List<String> bList = impl.getTags();

    List<String> missingFromSpec = new ArrayList<String>(aList);
    missingFromSpec.removeAll(bList);

    List<String> extraInSpec = new ArrayList<String>(bList);
    extraInSpec.removeAll(aList);

    if (!missingFromSpec.isEmpty()) {
      sb.append(" +");
      sb.append(Utilities.listToString(missingFromSpec, true));
    }
    if (!extraInSpec.isEmpty()) {
      if (sb.length() > 0) {
        sb.append(";");
      }
      sb.append(" -");
      sb.append(Utilities.listToString(extraInSpec, true));
    }

    return sb.toString();
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
