/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sonarsource.ruleapi.get.MarkdownConverter;
import org.fest.util.Strings;

public class ComparisonUtilities {

  private ComparisonUtilities() {
    // private constructor
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

    return testTextFunctionalEquivalence(a, b, ignoreWhitespace);
  }

  private static boolean testTextFunctionalEquivalence(String a, String b, boolean ignoreWhitespace) {
    String aPrime = a;
    String bPrime = b;

    if (ignoreWhitespace) {
      aPrime = spaceOutHtmlAndCollapseWhitespace(aPrime);
      bPrime = spaceOutHtmlAndCollapseWhitespace(bPrime);
    }

    if (aPrime.equals(bPrime)) {
      return true;
    }

    if (isTokenCheckingIndicated(aPrime, bPrime)) {
      return getFirstDifferingToken(aPrime, bPrime).length == 0;
    }

    return aPrime.equals(bPrime);
  }

  public static String[] getFirstDifferingToken(String rspec, String impl) {

    List<String> rspecTokens = new ArrayList<String>(Arrays.asList(rspec.split(" ")));
    List<String> implTokens  = new ArrayList<String>(Arrays.asList(impl.split(" ")));

    String rspecTok = null;
    String implTok = null;

    boolean isEquivalent = true;
    boolean inRspecPre = false;
    while (isEquivalent && neitherListIsEmpty(rspecTokens, implTokens)) {
      rspecTok = rspecTokens.remove(0);
      if (rspecTok.matches(".*</?pre>.*")) {
        inRspecPre = !inRspecPre;
      }
      rspecTok = assembleExtendedRspecToken(rspecTokens, rspecTok, inRspecPre);

      implTok = getImplTok(implTokens, rspecTok);

      isEquivalent = areTokensEquivalent(implTokens, rspecTok, implTok);
    }

    if (isEquivalent) {
      if (!implTokens.isEmpty()) {
        rspecTok = "";
        implTok = listToString(implTokens, false);
        isEquivalent = false;
      } else if (!rspecTokens.isEmpty()) {
        rspecTok = assembleExtendedRspecToken(rspecTokens, rspecTokens.remove(0), inRspecPre);
        isEquivalent = isOptional(rspecTok);

        implTok = "";
        rspecTokens.add(0, rspecTok);
        rspecTok = listToString(rspecTokens, false);
      }
    }

    if (isEquivalent) {
      return new String[0];
    }

    return new String[]{rspecTok, implTok};
  }

  protected static boolean areTokensEquivalent(List<String> implTokens, String rspecTok, String implTok) {

    boolean isEquivalent = true;
    if (arePhraseOptionsPresent(rspecTok) && !isPhraseInOptions(rspecTok, implTok, implTokens)) {
      isEquivalent = false;
    } else if (! isEquivalentToken(implTok, rspecTok)) {
      if (isOptional(rspecTok)) {
        disassembleExtendedImplToken(implTokens, implTok);
      } else {
        isEquivalent = false;
      }
    }
    return isEquivalent;
  }

  protected static String spaceOutHtmlAndCollapseWhitespace(String a) {
    String unneededWhitespace = "[\\r\\n\\t]+";
    String pTags = "</?p>";
    String brTags = "<br ?/>";
    String htmlTags = "(</?(code|th|td|li|h\\d)>)";

    return a.replaceAll(unneededWhitespace, " ").replaceAll(pTags, " ").replaceAll(brTags," ")
            .replaceAll(htmlTags, " $1 ").replaceAll("\"", " \" ")
            .replaceAll("[.,]", "").replaceAll(" +", " ").trim();
  }

  private static boolean isTokenCheckingIndicated(String aString, String bString) {
    String indicatesOptionsEntities = ".*[|\\[(&\"<>].+";

    return aString.matches(indicatesOptionsEntities) || bString.matches(indicatesOptionsEntities);
  }

  private static boolean neitherListIsEmpty(List<String> rspecTokens, List<String> implTokens) {

    return !rspecTokens.isEmpty() && !implTokens.isEmpty();
  }

  private static boolean arePhraseOptionsPresent(String rspecTok) {

    return rspecTok.contains(" ") && rspecTok.contains("|");
  }

  private static String getImplTok(List<String> implTokens, String rspecTok) {

    String implTok = implTokens.remove(0);
    if (rspecTok.contains(" ") && !rspecTok.contains("|")) {
      implTok = assembleExtendedImplToken(implTokens, implTok, rspecTok);
    }
    return implTok;
  }

  private static boolean isEquivalentToken(String implTok, String rspecTok) {
    if (Strings.isNullOrEmpty(implTok) != Strings.isNullOrEmpty(rspecTok)) {
      return false;
    }

    return rspecTok.equals(implTok) || rspecTok.contains(implTok) || implTok.contains(rspecTok)
            || isEquivalentEntityIgnoreBrackets(rspecTok, implTok);
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

    a = a.replaceAll("[\\[\\]()]","");
    b = b.replaceAll("[\\[\\]()]","");
    return a.equalsIgnoreCase(b);
  }

  private static boolean isPhraseInOptions(String rspecTok, String implTok, List<String> implTokens){

    String rTok = rspecTok;
    if (rTok.matches("^\\[.*\\]$") || rTok.matches("^\\(.*\\)$")) {
      rTok = rTok.substring(1, rTok.length() - 1);
    }

    String [] phrases = rTok.split("\\|");
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

    return rspecTok.matches("[\\[(].*[\\])]") && !rspecTok.contains("|");
  }

  private static String assembleExtendedRspecToken(List<String> rspecTokens, String rspecTok, boolean inPre) {

    String tok = rspecTok;
    if (! inPre && rspecTok.matches("^[(\\[].*") && !rspecTok.matches(".*[\\])]$")) {
      StringBuilder sb = new StringBuilder(rspecTok);
      while (!sb.toString().matches(".*[\\])]") && !rspecTokens.isEmpty()) {
        sb.append(" ").append(rspecTokens.remove(0));
      }
      tok = sb.toString();
    }
    return tok.trim();
  }

  public static int compareStrings(String a, String b) {
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

  public static String listToString(List<String> list, boolean doCommas) {

    StringBuilder sb = new StringBuilder();
    for (String str : list) {
      if (sb.length() > 0) {
        if (doCommas) {
          sb.append(",");
        }
        sb.append(" ");

      }
      sb.append(str);
    }
    return sb.toString();
  }

}
