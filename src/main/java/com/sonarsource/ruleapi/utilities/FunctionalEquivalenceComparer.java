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

public class FunctionalEquivalenceComparer {

  private FunctionalEquivalenceComparer() {
    // private constructor
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
    String impl  = bString.toUpperCase().replaceAll("[.,]", "");
    if (impl.matches(indicatesOptionsEntities) && !rspec.matches(indicatesOptionsEntities)) {
      rspec = bString;
      impl = aString;
    }

    List<String> rspecTokens = new ArrayList<String>(Arrays.asList(rspec.split(" ")));
    List<String> implTokens  = new ArrayList<String>(Arrays.asList(impl.split(" ")));

    boolean isEquivalent = true;
    while (!rspecTokens.isEmpty() && !implTokens.isEmpty() && isEquivalent) {
      String rspecTok = rspecTokens.remove(0);
      rspecTok = assembleExtendedRspecToken(rspecTokens, rspecTok);

      String implTok = getImplTok(implTokens, rspecTok);

      if (rspecTok.contains(" ") && rspecTok.contains("|") && ! isPhraseInOptions(rspecTok,implTok,implTokens)) {
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

}
