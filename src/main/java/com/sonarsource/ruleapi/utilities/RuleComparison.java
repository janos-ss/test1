/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import com.sonarsource.ruleapi.domain.Parameter;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.get.MarkdownConverter;

import java.util.ArrayList;
import java.util.Arrays;
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
      sb.append("title").append(separator);
      if (detailedReport){
        sb.append(spc).append(spec.getTitle()).append(separator);
        sb.append(imp).append(impl.getTitle()).append(separator);
      }
    }
    if (compareSeverity() != 0) {
      sb.append("severity").append(separator);
      if (detailedReport){
        sb.append(spc).append(spec.getSeverity()).append(separator);
        sb.append(imp).append(impl.getSeverity()).append(separator);
      }
    }

    if (compareDefaultActive() != 0) {
      sb.append("default active").append(separator);
      if (detailedReport){
        sb.append(spc).append(spec.getDefaultActive()).append(separator);
        sb.append(imp).append(impl.getDefaultActive()).append(separator);
      }
    }

    if (compareTemplate() != 0) {
      sb.append("template").append(separator);
      if (detailedReport){
        sb.append(spc).append(spec.isTemplate()).append(separator);
        sb.append(imp).append(impl.isTemplate()).append(separator);
      }
    }

    sb.append(toStringForDescription());

    sb.append(toStringForSqale());

    if (compareParameterList() != 0) {
      sb.append("parameter list").append(separator);
      if (detailedReport){
        sb.append(spc).append(separator);
        for (Parameter param : spec.getParameterList()) {
          sb.append(param.toString()).append(separator);
        }
        sb.append(imp).append(separator);
        for (Parameter param : impl.getParameterList()) {
          sb.append(param.toString()).append(separator);
        }
      }
    }

    if (compareTags() != 0) {
      sb.append("tags").append(separator);
      if (detailedReport){
        sb.append(spc).append(listToString(spec.getTags())).append(separator);
        sb.append(imp).append(listToString(impl.getTags())).append(separator);
      }
    }

    if (sb.length() > 0) {
      return "Differences: " + sb.toString();
    }

    return "";
  }

  private String toStringForSqale() {

    StringBuilder sb = new StringBuilder();
    if (compareSqaleCharacteristic() != 0) {
      sb.append("SQALE characteristic").append(separator);
      if (detailedReport){
        sb.append(spc).append(spec.getSqaleCharac()).append(separator);
        sb.append(imp).append(impl.getSqaleCharac()).append(separator);
      }
    }

    if (compareSqaleSubcharacertistic() != 0) {
      sb.append("SQALE sub-characteristic").append(separator);
      if (detailedReport){
        sb.append(spc).append(spec.getSqaleSubCharac()).append(separator);
        sb.append(imp).append(impl.getSqaleSubCharac()).append(separator);
      }
    }

    if (compareSqaleRemediationFunction() != 0) {
      sb.append("SQALE remediation function").append(separator);
      if (detailedReport){
        sb.append(spc).append(spec.getSqaleRemediationFunction()).append(separator);
        sb.append(imp).append(impl.getSqaleRemediationFunction()).append(separator);
      }
    }

    if (compareSqaleConstantCost() != 0) {
      sb.append("SQALE constant cost or linear threshold").append(separator);
      if (detailedReport){
        sb.append(spc).append(spec.getSqaleConstantCostOrLinearThreshold()).append(separator);
        sb.append(imp).append(impl.getSqaleConstantCostOrLinearThreshold()).append(separator);
      }
    }

    if (compareSqaleLinearArg() != 0) {
      sb.append("SQALE linear argument").append(separator);
      if (detailedReport){
        sb.append(spc).append(spec.getSqaleLinearArg()).append(separator);
        sb.append(imp).append(impl.getSqaleLinearArg()).append(separator);
      }
    }

    if (compareSqaleLinearFactor() != 0) {
      sb.append("SQALE linear factor").append(separator);
      if (detailedReport){
        sb.append(spc).append(spec.getSqaleLinearFactor()).append(separator);
        sb.append(imp).append(impl.getSqaleLinearFactor()).append(separator);
      }
    }

    if (compareSqaleLinearOffset() != 0) {
      sb.append("SQALE linear offset").append(separator);
      if (detailedReport){
        sb.append(spc).append(spec.getSqaleLinearOffset()).append(separator);
        sb.append(imp).append(impl.getSqaleLinearOffset()).append(separator);
      }
    }
    return sb.toString();
  }

  private String toStringForDescription() {

    StringBuilder sb = new StringBuilder();
    if (compareDescription() != 0) {
      sb.append("description text").append(separator);
      if (detailedReport){
        sb.append(spc).append(separator).append(spec.getDescription()).append(separator);
        sb.append(imp).append(separator).append(impl.getDescription()).append(separator);
      }
    }

    if (compareNoncompliant() != 0) {
      sb.append("noncompliant code example").append(separator);
      if (detailedReport){
        sb.append(spc).append(separator).append(spec.getNonCompliant()).append(separator);
        sb.append(imp).append(separator).append(impl.getNonCompliant()).append(separator);
      }
    }

    if (compareCompliant() != 0) {
      sb.append("compliant solution").append(separator);
      if (detailedReport){
        sb.append(spc).append(separator).append(spec.getCompliant()).append(separator);
        sb.append(imp).append(separator).append(impl.getCompliant()).append(separator);
      }
    }

    if (compareException() != 0) {
      sb.append("exceptions").append(separator);
      if (detailedReport){
        sb.append(spc).append(separator).append(spec.getExceptions()).append(separator);
        sb.append(imp).append(separator).append(impl.getExceptions()).append(separator);
      }

    }

    if (compareReference() != 0) {
      sb.append("references").append(separator);
      if (detailedReport){
        sb.append(spc).append(separator).append(spec.getReferences()).append(separator);
        sb.append(imp).append(separator).append(impl.getReferences()).append(separator);
      }
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
    String digitsLetters = "\\d+[a-zA-Z]+";

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
    if (result != 0 || a == null) {
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
    if (isTextFunctionallyEquivalent(a, b, ignoreWhitespace)) {
      return 0;
    }
    return a.compareTo(b);
  }

  public static boolean isTextFunctionallyEquivalent(String a, String b, boolean ignoreWhitespace) {
    if (a == null && b == null) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }

    String aPrime = a;
    String bPrime = b;

    if (ignoreWhitespace) {
      String linebreaks = "[\\r\\n]+";
      String html = "(<[^>]+>)";
      String pTags = "</?p>";
      String brTags = "<br ?/>";

      aPrime = a.trim().replaceAll(linebreaks, " ").replaceAll(pTags," ").replaceAll(brTags,"").replaceAll(html, " $1 ")
              .replaceAll("\""," \" ").replaceAll(" +"," ");
      bPrime = b.trim().replaceAll(linebreaks, " ").replaceAll(pTags," ").replaceAll(brTags,"").replaceAll(html, " $1 ")
              .replaceAll("\""," \" ").replaceAll(" +"," ");
    }

    if (aPrime.equals(bPrime)) {
      return true;
    }
    return hasEquivalentTokens(aPrime, bPrime);
  }

  private static boolean hasEquivalentTokens(String aString, String bString) {

    String indicatesOptionsEntities = ".*[|\\[(&\"<>].+";
    if (! (aString.matches(indicatesOptionsEntities) || bString.matches(indicatesOptionsEntities))) {
      return aString.equals(bString);
    }

    String rspec = aString.toUpperCase().replaceAll("[.,]", "");
    String impl = bString.toUpperCase().replaceAll("[.,]", "");
    if (impl.matches(indicatesOptionsEntities) && !rspec.matches(indicatesOptionsEntities)) {
      rspec = bString;
      impl = aString;
    }

    List<String> rspecTokens = new ArrayList<String>(Arrays.asList(rspec.split(" ")));
    List<String> implTokens = new ArrayList<String>(Arrays.asList(impl.split(" ")));

    while (!rspecTokens.isEmpty() && !implTokens.isEmpty()) {
      boolean optional = false;
      String rspecTok = rspecTokens.remove(0);
      String implTok = implTokens.remove(0);

      rspecTok = assembleExtendedRspecToken(rspecTokens, rspecTok);
      if (rspecTok.contains(" ") && !rspecTok.contains("|")) {
        implTok = assembleExtendedImplToken(implTokens, implTok, rspecTok);
      }

      if (rspecTok.contains("|") && rspecTok.contains(" ") && ! isPhraseInOptions(rspecTok,implTok,implTokens)) {
        return false;
      } else if (! (rspecTok.equals(implTok) || rspecTok.contains(implTok) || implTok.contains(rspecTok)
              || isEquivalentEntityIgnoreBrackets(rspecTok, implTok))) {
        if (isOptional(rspecTok)) {
          disassembleExtendedImplToken(implTokens, implTok);
        } else {
          return false;
        }
      }
    }

    if (! implTokens.isEmpty()) {
      return false;
    } else if (! rspecTokens.isEmpty()){
      String rspecTok = assembleExtendedRspecToken(rspecTokens, "");
       return isOptional(rspecTok);
    }

    return true;
  }

  private static boolean isEquivalentEntityIgnoreBrackets(String rspecTok, String implTok) {

    String a = rspecTok;
    String b = implTok;
    String hasEntities = ".*&\\w+;.*";

    if (!a.matches(hasEntities)) {
      a = MarkdownConverter.handleEntities(a);
    }
    if (!b.matches(hasEntities)) {
      b = MarkdownConverter.handleEntities(b);
    }
    a = a.replaceAll("[\\[\\]]","");
    b = b.replaceAll("[\\[\\]]","");
    return a.equalsIgnoreCase(b);
  }

  private static boolean isPhraseInOptions(String rspecTok, String implTok, List<String> implTokens){
    String [] phrases = rspecTok.split("\\|");
    String tok = implTok;

    for(String phrase : phrases) {
      phrase = phrase.trim();
      tok = assembleExtendedImplToken(implTokens, implTok , phrase);
      if (tok.equals(phrase)) {
        return true;
      } else {
        disassembleExtendedImplToken(implTokens, tok);
        tok = implTokens.remove(0);
      }
    }
    return false;
  }

  private static void disassembleExtendedImplToken(List<String> implTokens, String implTok) {

    String[] pieces = implTok.split(" ");
    for (int i = pieces.length-1; i >=0; i--) {
      implTokens.add(0, pieces[i]);
    }
  }

  private static String assembleExtendedImplToken(List<String> implTokens, String implTok, String phraseToMatch) {

    int phraseLength = phraseToMatch.split(" ").length;

    StringBuilder sb = new StringBuilder(implTok);
    for (int j = 1; j < phraseLength && !implTokens.isEmpty(); j++) {
      sb.append(" ").append(implTokens.remove(0));
    }
    return sb.toString();
  }

  private static boolean isOptional(String rspecTok) {

    return !rspecTok.contains("|");
  }

  private static String assembleExtendedRspecToken(List<String> rspecTokens, String rspecTok) {

    if (rspecTok.matches("^[(\\[].*") && !rspecTok.matches(".*[\\])]$")) {
      StringBuilder sb = new StringBuilder(rspecTok);
      while (!sb.toString().contains("]") && !rspecTokens.isEmpty()) {
        sb.append(" ").append(rspecTokens.remove(0));
      }
      rspecTok = sb.toString();
    }

    if (rspecTok.matches("^\\[.*\\]$") || rspecTok.matches("^\\(.*\\)$")){
      rspecTok = rspecTok.substring(1, rspecTok.length() - 1);
    }
    return rspecTok.trim();
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
