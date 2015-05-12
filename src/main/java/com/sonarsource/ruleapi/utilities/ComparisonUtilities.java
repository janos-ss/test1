/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fest.util.Strings;

public class ComparisonUtilities {

  private ComparisonUtilities() {
    // private constructor
  }


  public static int compareTextFunctionalEquivalence(String a, String b) {
    if (a == null && b == null) {
      return 0;
    }
    if (a == null) {
      return 1;
    }
    if (b == null) {
      return -1;
    }
    if (isTextFunctionallyEquivalent(a, b)) {
      return 0;
    }
    return a.compareTo(b);
  }

  public static boolean isTextFunctionallyEquivalent(String a, String b) {

    if (a == null && b == null) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }

    return testTextFunctionalEquivalence(a, b);
  }

  private static boolean testTextFunctionalEquivalence(String a, String b) {

    String aPrime = spaceOutHtmlAndCollapseWhitespace(a);
    String bPrime = spaceOutHtmlAndCollapseWhitespace(b);

    if (aPrime.equals(bPrime)) {
      return true;
    }

    if (isTokenCheckingIndicated(aPrime, bPrime)) {
      return getFirstDifferingToken(aPrime, bPrime).length == 0;
    }

    return false;
  }

  public static String[] getFirstDifferingToken(String a, String b) {

    String rspec = spaceOutHtmlAndCollapseWhitespace(a);
    String impl = spaceOutHtmlAndCollapseWhitespace(b);

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
        implTok = Utilities.listToString(implTokens, false);
        isEquivalent = false;
      } else if (!rspecTokens.isEmpty()) {
        rspecTok = assembleExtendedRspecToken(rspecTokens, rspecTokens.remove(0), inRspecPre);
        isEquivalent = isOptional(rspecTok);

        implTok = "";
        rspecTokens.add(0, rspecTok);
        rspecTok = Utilities.listToString(rspecTokens, false);
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

      } else if (! isMatchingRuleLink(implTokens, rspecTok, implTok)) {
        isEquivalent = false;
      }
    }
    return isEquivalent;
  }


  /**
   * The goal of this method is to find these 2 strings equivalent:
   *     {rule:squid:S1234}
   *     <a href=\"/coding_rules#rule_key=squid%3AS1234\">S1234</a>"
   *
   * I.E. a rule link macro and the URL it expands to should be seen as equivalent
   *
   *
   * @param implTokens additional tokens will be popped off this list as needed to complete link string
   * @param rspecTok token from rule fetched from RSpec
   * @param implTok initial token from rule fetched from SonarQube instance
   * @return true iff rule link macro and anchor link to same rule
   */
  protected static boolean isMatchingRuleLink(List<String> implTokens, String rspecTok, String implTok) {
    if (!rspecTok.matches("\\{rule:.*") || !implTok.contains("<a")) {
      return false;
    }

    if (implTok.startsWith("c") || implTok.startsWith("C") || implTok.startsWith("Obj") || implTok.startsWith("obj")) {

      return isMatchingCFamilyRuleLink(implTokens, rspecTok, implTok);
    }

    StringBuilder sb = new StringBuilder(implTok);
    while (!sb.toString().endsWith("</a>")) {

      sb.append(" ").append(implTokens.remove(0));
    }

    String rspecLink = rspecTok.replaceAll("\\{rule:([^:]+):([^}]+)}","$1:$2");
    String implLink = sb.toString().replaceAll(".*rule_key=([^\"]+)[\"|'].*", "$1")
            .replaceAll("%3A",":")
            .trim();

    return rspecLink.equals(implLink);
  }

  private static boolean isMatchingCFamilyRuleLink(List<String> implTokens, String rspecTok, String implTok) {

    String rspecLink = rspecTok.replaceAll("\\{rule:[^:]+:([^}]+)}","$1");

    StringBuilder sb = new StringBuilder(implTok);
    while (Utilities.listToString(implTokens, false).contains("coding_rules#rule_key")){
      sb.append(" ").append(implTokens.remove(0));

      if (sb.toString().matches(".*<a href=['\"].*rule_key=.*['\"]>\\w+</a>")) {
        String implLink = sb.toString().replaceAll(".*rule_key=[a-z]+:([^\"]+)[\"|'].*", "$1").trim();

        if (! implLink.equals(rspecLink)) {
          return false;
        }

        sb = new StringBuilder();

      }
    }
    return true;
  }

  /**
   * To make comparisons easiser:
   * * Remove html whitespace (paragraph & brake tags)
   * * Convert runs of whitespace into a single space
   * * Add spaces around quotes and certain other HTML tags
   *
   * @param a  the string to be manipulated
   * @return manipulated string
   */
  protected static String spaceOutHtmlAndCollapseWhitespace(String a) {
    String unneededWhitespace = "[\\r\\n\\t]+";
    String pTags = "</?p>";
    String brTags = "<br ?/>";
    String htmlTags = "(</?(code|th|td|li|blockquote|h\\d)>)";

    return a.replaceAll(unneededWhitespace, " ").replaceAll(pTags, " ").replaceAll(brTags," ")
            .replaceAll(htmlTags, " $1 ").replaceAll("&quot;","\"").replaceAll("\"", " \" ")
            .replaceAll("[.,]", "").replaceAll(" +", " ").trim();
  }

  /**
   * There's no need to run in-depth comparison algorithms unless one or both strings
   * contains characters indicating that it's necessary.
   *
   * @param aString
   * @param bString
   * @return true if one or both params contains certain characters
   */
  private static boolean isTokenCheckingIndicated(String aString, String bString) {
    String indicatesOptionsEntities = ".*[|\\[(&\"<>].+";
    return aString.matches(indicatesOptionsEntities) || bString.matches(indicatesOptionsEntities);
  }

  private static boolean neitherListIsEmpty(List<String> rspecTokens, List<String> implTokens) {

    return !rspecTokens.isEmpty() && !implTokens.isEmpty();
  }

  private static boolean arePhraseOptionsPresent(String rspecTok) {

    return rspecTok.contains(" ") && hasPipeForAlternative(rspecTok);
  }

  private static String getImplTok(List<String> implTokens, String rspecTok) {

    String implTok = implTokens.remove(0);
    if (rspecTok.contains(" ") && !hasPipeForAlternative(rspecTok)) {
      implTok = assembleExtendedImplToken(implTokens, implTok, rspecTok);
    }
    return implTok;
  }

  /**
   * differentiates between these two:
   *   (red|blue)  // return true
   * and
   *   (<code>&</code>,<code>|</code>)  // return false
   *
   * @param rspecTok
   * @return TRUE IFF | && ! <code>|</code>
   */
  private static boolean hasPipeForAlternative(String rspecTok) {

    int pipe = rspecTok.indexOf('|');
    int openCode = -1;
    int closeCode = -1;

    if (pipe > -1) {
      openCode = Utilities.findBefore(rspecTok, pipe, "<code>");
    }

    if (openCode > -1) {
      closeCode = rspecTok.indexOf("</code>", openCode);
    }

    return pipe > -1 && closeCode < pipe;
  }

  private static boolean isEquivalentToken(String implTok, String rspecTok) {
    if (Strings.isNullOrEmpty(implTok) != Strings.isNullOrEmpty(rspecTok)) {
      return false;
    }

    return rspecTok.equals(implTok) || rspecTok.contains(implTok) || implTok.contains(rspecTok)
            || isEquivalentEntityIgnoreBrackets(rspecTok, implTok);
  }

  /**
   * Convert html-significant chars to entities & remove brackets/parens
   *
   * @param rspecTok
   * @param implTok
   * @return
   */
  protected static boolean isEquivalentEntityIgnoreBrackets(String rspecTok, String implTok) {

    String a = rspecTok;
    String b = implTok;

    a = MarkdownConverter.handleEntities(a);
    b = MarkdownConverter.handleEntities(b);

    a = a.replaceAll("[\\[\\]()]","");
    b = b.replaceAll("[\\[\\]()]","");
    return a.equalsIgnoreCase(b);
  }

  /**
   * Given a list of options, E.G. [a|the|an|one] return true if implTok matches one of the options.
   * Since options can be words or phrases, implTokens list is necessary to flesh-out implTok as necessary
   * to identify match.
   * E.G.
   *   rspecTok = [these|those|the others]
   *   implTok = the
   * The match can't be identified until the next token from implTokens ("others") is added to implTok
   *
   * @param rspecTok
   * @param implTok
   * @param implTokens
   * @return
   */
  protected static boolean isPhraseInOptions(String rspecTok, String implTok, List<String> implTokens){

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

  protected static String assembleExtendedImplToken(List<String> implTokens, String implTok, String phraseToMatch) {

    int phraseLength = phraseToMatch.split(" ").length;

    StringBuilder sb = new StringBuilder(implTok);
    for (int j = 1; j < phraseLength && !implTokens.isEmpty(); j++) {
      sb.append(" ").append(implTokens.remove(0));
    }
    return sb.toString();
  }

  protected static boolean isOptional(String rspecTok) {

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
      return a.trim().compareToIgnoreCase(b.trim());
    }
    if (a == null) {
      return -1;
    }
    return 1;
  }

  public static String stripHtml(String source) {
    return source.replaceAll("<[^>]+>", "");
  }
}
