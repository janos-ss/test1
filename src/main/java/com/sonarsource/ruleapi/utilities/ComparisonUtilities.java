/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;


import com.sonarsource.ruleapi.get.MarkdownConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComparisonUtilities {

  private static String INICATES_OPTIONS_ENTITIES = ".*[|\\[(&\"<>].+";


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
    if (ComparisonUtilities.isTextFunctionallyEquivalent(a, b, ignoreWhitespace)) {
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

    if (!isTokenCheckingIndicated(aString, bString)) {
      return aString.equals(bString);
    }

    String rspec = aString.toUpperCase().replaceAll("[.,]", "");
    String impl  = bString.toUpperCase().replaceAll("[.,]", "");

    if (shouldStringsBeSwapped(rspec, impl)) {
      rspec = bString;
      impl = aString;
    }

    List<String> rspecTokens = new ArrayList<String>(Arrays.asList(rspec.split(" ")));
    List<String> implTokens  = new ArrayList<String>(Arrays.asList(impl.split(" ")));

    boolean isEquivalent = true;
    while (isEquivalent && neitherListIsEmpty(rspecTokens, implTokens)) {
      String rspecTok = rspecTokens.remove(0);
      rspecTok = assembleExtendedRspecToken(rspecTokens, rspecTok);

      String implTok = getImplTok(implTokens, rspecTok);

      if (arePhraseOptionsPresent(rspecTok) && !isPhraseInOptions(rspecTok, implTok, implTokens)) {
        isEquivalent = false;
      } else if (! isEquivalent(implTok,rspecTok)) {
        if (isOptional(rspecTok)) {
          disassembleExtendedImplToken(implTokens, implTok);
        } else {
          isEquivalent = false;
        }
      }
    }

    if (! implTokens.isEmpty()) {
      isEquivalent = false;
    } else if (! rspecTokens.isEmpty()){
      String rspecTok = assembleExtendedRspecToken(rspecTokens, "");
      isEquivalent = isOptional(rspecTok);
    }

    return isEquivalent;
  }

  private static boolean shouldStringsBeSwapped(String rspec, String impl) {

    return impl.matches(INICATES_OPTIONS_ENTITIES) && !rspec.matches(INICATES_OPTIONS_ENTITIES);
  }

  private static boolean isTokenCheckingIndicated(String aString, String bString) {

    return aString.matches(INICATES_OPTIONS_ENTITIES) || bString.matches(INICATES_OPTIONS_ENTITIES);
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

  private static boolean isEquivalent(String implTok, String rspecTok) {
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

    String tok = rspecTok;
    if (rspecTok.matches("^[(\\[].*") && !rspecTok.matches(".*[\\])]$")) {
      StringBuilder sb = new StringBuilder(rspecTok);
      while (!sb.toString().contains("]") && !rspecTokens.isEmpty()) {
        sb.append(" ").append(rspecTokens.remove(0));
      }
      tok = sb.toString();
    }

    if (tok.matches("^\\[.*\\]$") || tok.matches("^\\(.*\\)$")){
      tok = tok.substring(1, tok.length() - 1);
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

}
