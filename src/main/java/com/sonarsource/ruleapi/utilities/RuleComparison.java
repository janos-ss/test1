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
  private boolean detailedReport = false;
  private String spc = "  spec: ";
  private String imp = "  impl: ";

  private String separator = ", ";

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
      logDifference(sb,"tags", listToString(spec.getTags()), listToString(impl.getTags()));
    }

    if (sb.length() > 0) {
      return "Differences: " + sb.toString();
    }

    return "";
  }

  private String parameterListToString(List<Parameter> list) {

    StringBuilder sb = new StringBuilder();
    for (Parameter param : spec.getParameterList()) {
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
      logDifference(sb, "description text", spec.getDescription(), impl.getDescription());
    }

    if (compareNoncompliant() != 0) {
      logDifference(sb, "noncompliant code example", spec.getNonCompliant(), impl.getNonCompliant());
    }

    if (compareCompliant() != 0) {
      logDifference(sb, "compliant solution", spec.getCompliant(), impl.getCompliant());
    }

    if (compareException() != 0) {
      logDifference(sb, "exceptions", spec.getExceptions(), impl.getExceptions());

    }

    if (compareReference() != 0) {
      logDifference(sb, "references", spec.getReferences(), impl.getReferences());
    }
    return sb.toString();
  }

  private void logDifference(StringBuilder sb, String differenceTitle, Object specValue, Object implValue) {
    sb.append(differenceTitle).append(separator);
    if (detailedReport){
      sb.append(spc).append(specValue).append(separator);
      sb.append(imp).append(implValue).append(separator);
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
    return Boolean.valueOf(spec.isTemplate()).compareTo(Boolean.valueOf(impl.isTemplate()));
  }

  protected int compareTitle() {
    return compareTextFunctionalEquivalence(spec.getTitle(), impl.getTitle(), true);
  }

  protected int compareMessage() {
    return compareTextFunctionalEquivalence(spec.getMessage(), impl.getMessage(), false);
  }

  protected int compareDescription() {
    return compareTextFunctionalEquivalence(spec.getDescription(), impl.getDescription(), true);
  }

  protected int compareNoncompliant() {
    return compareTextFunctionalEquivalence(spec.getNonCompliant(), impl.getNonCompliant(), true);
  }

  protected int compareCompliant() {
    return compareTextFunctionalEquivalence(spec.getCompliant(), impl.getCompliant(), true);
  }

  protected int compareException() {
    return compareTextFunctionalEquivalence(spec.getExceptions(), impl.getExceptions(), true);
  }

  protected int compareReference() {
    return compareTextFunctionalEquivalence(spec.getReferences(), impl.getReferences(), true);
  }

  protected int compareSqaleCharacteristic() {
    return compareStrings(spec.getSqaleCharac(), impl.getSqaleCharac());
  }

  protected int compareSqaleSubcharacertistic() {
    int result = checkForNulls(spec.getSqaleSubCharac(), impl.getSqaleSubCharac());
    if (result != 0 || spec.getSqaleSubCharac() == null) {
      return result;
    }
    return spec.getSqaleSubCharac().compareTo(impl.getSqaleSubCharac());
  }

  protected int compareSqaleRemediationFunction() {
    return compareStrings(spec.getSqaleRemediationFunction(), impl.getSqaleRemediationFunction());
  }

  protected int compareSqaleLinearArg() {
    return compareStrings(spec.getSqaleLinearArg(), impl.getSqaleLinearArg());
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

  public static int compareTextFunctionalEquivalence(String a, String b, boolean ignoreWhitespace) {
    if (a == null && b == null) {
      return 0;
    }
    if (a == null) {
      return 1;
    }
    if (b == null) {
      return -1;
    }
    if (FunctionalEquivalenceComparer.isTextFunctionallyEquivalent(a, b, ignoreWhitespace)) {
      return 0;
    }
    return a.compareTo(b);
  }


  private static int compareStrings(String a, String b) {
    if (a == null && b == null){
      return 0;
    }

    if (a != null && b != null) {
      return a.compareToIgnoreCase(b);
    }
    if (a == null) {
      return -1;
    }
    return 1;
  }

  public static String listToString(List<String> list) {

    StringBuilder sb = new StringBuilder();
    for (String str : list) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(str);
    }
    return sb.toString();
  }

  public boolean isDetailedReport() {

    return detailedReport;
  }

  public void setDetailedReport(boolean detailedReport) {

    this.detailedReport = detailedReport;
    if (detailedReport) {
      separator = "\n";
    }
  }

}
