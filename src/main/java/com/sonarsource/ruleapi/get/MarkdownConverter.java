/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.get;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts Jira markdown to HTML
 */
public class MarkdownConverter {
  private boolean codeOpen = false;
  private boolean tableOpen = false;
  private boolean wrongLanguage = false;
  private boolean paragraph = true;
  private boolean quoteOpen = false;
  private LinkedList<String> listCloses;

  public MarkdownConverter() {
  }

  /**
   * Converts text in Jira markdown format to HTML, additionally
   * choosing the correct variant if the markdown contains
   * multiple, language-specific {code} sections.
   *
   * @param markdown the text to be converted
   * @param language when multiple, language-specific {code} blocks are present, choose the one for this language
   * @return the HTML version of the passed-in text
   */
  public String transform(String markdown, String language) {
    if (markdown != null && markdown.length() > 0) {
      codeOpen = false;
      tableOpen = false;
      quoteOpen = false;
      wrongLanguage = false;
      paragraph = true;
      listCloses = new LinkedList<String>();
      StringBuilder sb = new StringBuilder();

      boolean hasLangCodeSample = hasLanguageCodeSample(language, markdown);

      String[] lines;
      if (markdown.contains("\r\n")) {
        lines = markdown.split("\r\n");
      } else {
        lines = markdown.split("\n");
      }

      for (int i = 0; i < lines.length; i++) {
        if (lines[i].length() > 0) {

          lines[i] = handleCodeTags(lines[i]);
          lines[i] = handleEntities(lines[i]);
          lines[i] = handleCode(hasLangCodeSample, language, lines[i]);

          if (!codeOpen) {
            lines[i] = handleTable(lines[i], sb);
            lines[i] = handleHref(lines[i]);
            lines[i] = handleHeading(lines[i]);
            lines[i] = handleBq(lines[i]);
            lines[i] = handleDoubleCurly(lines[i]);
            lines[i] = handleList(sb, lines[i]);
            lines[i] = handleBold(lines[i]);
            lines[i] = handleItal(lines[i]);
            lines[i] = handleQuoteTag(lines[i]);
          }
        }
        handleParagraph(sb, lines[i]);
      }
      closeWhatsOpenEOD(sb);
      return sb.toString();
    }
    return markdown;
  }

  protected boolean hasLanguageCodeSample(String language, String markdown) {
    return markdown.matches("(?s).*\\{code:title=" + language + "[ ,}].*");
  }

  protected void handleParagraph(StringBuilder sb, String line) {
    if (!wrongLanguage) {
      if (isPTagNeeded(line)) {
        sb.append("<p>");
      }
      sb.append(line);
      if (isPTagNeeded(line)) {
        sb.append("</p>");
      }
      if (line.length() > 0 || codeOpen) {
        sb.append("\n");
      }
      paragraph = true;
    }
  }

  protected boolean isPTagNeeded(String line) {
    String htmlTag = "<[^>]+>";
    return paragraph && listCloses.isEmpty() && line.length() >0
            && !(codeOpen || line.matches(htmlTag));
  }

  /**
   * Some markdown contains <code></code> tags. (Grrr..)
   * @return <code></code> converted to markdown
   */
  protected String handleCodeTags(String line) {
    return line.replaceAll("<code>","{{").replaceAll("</code>","}}");
  }

  public static String handleEntities(String line) {
    String l2 = line.replaceAll("&","&amp;");
    l2 = l2.replaceAll("<","&lt;");
    l2 = l2.replaceAll(">","&gt;");
    return l2;
  }

  /**
   * Should only be called at End Of Document
   */
  protected void closeWhatsOpenEOD(StringBuilder sb) {
    while ( ! listCloses.isEmpty()) {
      sb.append(listCloses.pop());
    }
    if (codeOpen) {
      sb.append("</pre>\n");
    }
    if (tableOpen) {
      sb.append("</table>\n");
    }
    if (quoteOpen) {
      sb.append("</blockquote>\n");
    }

  }

  protected String handleHeading(String arg) {
    String line = arg;
    if (line.matches("^h[0-9]\\..*")) {
      char level = line.charAt(1);
      line = "<h" + level + '>' + line.substring(3).trim() + "</h" + level + ">\n";
      paragraph = false;
    }
    return line;
  }

  protected String handleHref(String arg) {
    String line = arg;
    if (line.matches("[^\\[]*\\[[^|]+\\|https?[A-Za-z0-9-._~:/?#\\\\[\\\\]@!$&'()*+,;=% ]+\\].*")) {
      int pos = line.indexOf("http");
      int hrefStart = findBefore(line, pos, '[');
      int hrefEnd = findAfter(line, pos, ']');

      String href = line.substring(pos, hrefEnd);
      String label = line.substring(hrefStart + 1, pos - 1);
      String lineBegin = line.substring(0, hrefStart);
      String lineEnd = "";
      if (hrefEnd +1 < line.length()) {
        lineEnd = line.substring(hrefEnd+1, line.length());
      }
      line = lineBegin
        + "<a href=\"" + href + "\">" + label + "</a>"
        + lineEnd;
    }
    return line;
  }

  protected String handleBq(String arg) {
    String line = arg;
    if (line.startsWith("bq. ")) {
      line = "<blockquote>" + line.substring(4) + "</blockquote>";
      paragraph = false;
    }
    return line;
  }

  protected String handleQuoteTag(String arg) {
    String line = arg;
    if (line.contains("{quote}")) {
      if (quoteOpen) {
        line = line.replace("{quote}", "</blockquote>");
        quoteOpen = false;
      } else {
        line = line.replace("{quote}", "<blockquote>");
        quoteOpen = true;
      }
    }
    return line;
  }

  protected String handleDoubleCurly(String arg) {
    String line = arg;
    if (line.contains("{{")) {
      line = line.replaceAll("\\{\\{", "<code>");
    }
    if (line.contains("}}")) {
      line = line.replaceAll("}}", "</code>");
    }
    return line;
  }

  protected String handleTable(String arg, StringBuilder sb) {
    String line = arg;
    if (!tableOpen && line.startsWith("|")) {
      tableOpen = true;
      sb.append("<table>\n");
    }else if (tableOpen  && ! line.startsWith("|")) {
      tableOpen = false;
      sb.append("</table>\n");
    }

    if (line.startsWith("||")) {
      line = "<tr><th>" + line.substring(2, findLast(line, '|') - 1) + "</th></tr>";
      line = line.replaceAll("\\|\\|","|");
      line = line.replace("|", "</th><th>");
      paragraph = false;

    } else if (line.startsWith("|")) {
      line = "<tr><td>" + line.substring(1, findLast(line, '|')) + "</td></tr>";
      line = line.replace("|", "</td><td>");
      paragraph = false;
    }

    return line;
  }

  protected String handleCode(boolean hasLanguageCodeSample, String language, String arg) {
    // Java vs Javascript...?
    String line = arg;
    if (line.contains("{code")) {
      if (codeOpen) {
        line = line.replaceFirst("\\{code\\}", "</pre>");
        codeOpen = false;
        paragraph = false;
      }else if (isCodeLanguageMatch(hasLanguageCodeSample, language, line)) {
        wrongLanguage = false;
        codeOpen = true;
        line = line.replaceFirst("\\{code.*\\}", "<pre>");
      } else {
        // code sample's for another language
        wrongLanguage = true;
        codeOpen = true;
      }
    }
    return line;
  }

  protected boolean isCodeLanguageMatch(boolean hasLanguageCodeSample, String language, String line){
    if (line.contains("{code}")) {
      return ! hasLanguageCodeSample;
    }

    return line.matches("\\{code:title=" + language + "[, }].*");
  }

  protected String handleList(StringBuilder sb, String line) {
    int pos = findFirst(line, ' ');

    if (line.matches("[*#]+ +.*")) {

      if (pos > -1 && listCloses.size() > pos) {
        sb.append(listCloses.pop());
      }

      if (listCloses.size() < pos) {
        if (line.charAt(pos - 1) == '*') {
          listCloses.push("</ul>\n");
          sb.append("<ul>\n");
        } else {
          listCloses.push("</ol>\n");
          sb.append("<ol>\n");
        }
      }

      return handleLi(line);
    } else if (! listCloses.isEmpty()) {
      sb.append(listCloses.pop());
    }

    return line;
  }

  private String handleLi(String line) {
    int pos = findFirst(line, ' ');
    paragraph = false;
    return "<li>" + line.substring(pos) + "</li>";
  }

  protected String handleBold(String arg) {
    String line = arg;
    boolean boldOpen = false;
    while (line.matches(".*(\\b\\*|\\*\\b).*")) {
      if (boldOpen) {
        line = line.replaceFirst("(\\b)\\*", "</strong>$1");
        boldOpen = false;
      } else {
        line = line.replaceFirst("\\*(\\b)", "$1<strong>");
        boldOpen = true;
      }
    }
    return line;
  }

  protected String handleItal(String arg) {
    String line = arg;
    boolean italOpen = false;

    while (line.matches(".*([ .>]_|_[ .<]).*")) {
      if (italOpen) {
        line = line.replaceFirst("_([ .<])", "</em>$1");
        italOpen = false;
      } else {
        line = line.replaceFirst("([ .>])_", "$1<em>");
        italOpen = true;
      }
    }

    line = line.replaceAll("^_", "<em>");
    line = line.replaceAll("_$", "</em>");

    return line;
  }

  private int findAfter(String line, int start, char ch) {
    for (int i = start + 1; i < line.length(); i++) {
      if (line.charAt(i) == ch) {
        return i;
      }
    }
    return -1;
  }

  private int findBefore(String line, int start, char ch) {
    for (int i = start - 1; i >= 0; i--) {
      if (line.charAt(i) == ch) {
        return i;
      }
    }
    return -1;
  }

  private int findLast(String line, char ch) {
    for (int i = line.length() - 1; i > -1; i--) {
      if (line.charAt(i) == ch) {
        return i;
      }
    }
    return -1;
  }

  private int findFirst(String src, char charToFind) {
    for (int i = 0; i < src.length(); i++) {
      if (src.charAt(i) == charToFind) {
        return i;
      }
    }
    return -1;
  }

}
