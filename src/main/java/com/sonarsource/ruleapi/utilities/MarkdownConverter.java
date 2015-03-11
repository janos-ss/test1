/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import java.util.LinkedList;

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

  private final static String BQ_OPEN = "<blockquote>";
  private final static String BQ_CLOSE = "</blockquote>";

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
          lines[i] = handleCode(hasLangCodeSample, language, lines[i], sb);

          if (!codeOpen) {

            lines[i] = handleLinebreaksInTables(lines, i);
            lines[i] = handleTable(lines[i], sb);
            lines[i] = handleHref(lines[i]);
            lines[i] = handleRuleLinks(lines[i], language);
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
      closeWhatsOpen(sb);
      return sb.toString();
    }
    return markdown;
  }

  protected String handleLinebreaksInTables(String[] lines, int i) {

    if (lines[i].startsWith("|") && !lines[i].endsWith("|")) {
      StringBuilder sb = new StringBuilder();
      sb.append(lines[i]);

      for (int j = i + 1; j < lines.length && !sb.toString().endsWith("|"); j++) {
        sb.append("<br/>");
        sb.append(lines[j]);
        lines[j] = "";
      }
      return sb.toString();
    }
    return lines[i];
  }

  protected boolean hasLanguageCodeSample(String language, String markdown) {
    return markdown.matches("(?s).*\\{code:title=" + language + "[ ,}].*");
  }

  protected void handleParagraph(StringBuilder sb, String line) {
    if (!wrongLanguage) {
      int pos = line.indexOf(BQ_OPEN);
      if (isPTagNeeded(line) &&  pos > -1) {
        pos += BQ_OPEN.length();
        sb.append(line.substring(0, pos));
        sb.append("<p>");
        sb.append(line.substring(pos));
      } else if (isPTagNeeded(line)){
        sb.append("<p>");
        sb.append(line);
      } else {
        sb.append(line);
      }

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
    boolean inCodeOrOnlyHtml = codeOpen || line.matches(htmlTag);
    return paragraph && listCloses.isEmpty() && line.length() >0
            && !inCodeOrOnlyHtml;
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
  protected void closeWhatsOpen(StringBuilder sb) {
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
      sb.append(BQ_CLOSE + "\n");
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

  protected String handleRuleLinks(String arg, String language) {

    String line = arg;

    Language lang = Language.fromString(language.toUpperCase());
    if (lang != null && line.matches(".*S\\d+(\\s|$).*")) {
      line = line.replaceAll("(S\\d+)", "<a href='/coding_rules#rule_key=" + lang.getSq() + ":$1'>$1</a>");
    }
    return line;
  }

  protected String handleHref(String arg) {
    String line = arg;
    while (line.matches("[^\\[]*\\[[^|]+\\|https?[A-Za-z0-9-._~:/?#\\\\[\\\\]@!$&'()*+,;=% ]+\\].*")) {
      int pos = line.indexOf("|http");
      if (pos == -1) {
        pos = line.indexOf("[http");
      }
      pos++;
      int hrefStart = findBefore(line, pos, '[');
      int hrefEnd = line.indexOf(']', pos+1);

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
      line = BQ_OPEN + line.substring(4) + BQ_CLOSE;
      paragraph = false;
    }
    return line;
  }

  protected String handleQuoteTag(String arg) {
    String line = arg;
    String quote = "{quote}";
    if (line.contains(quote)) {
      if (quoteOpen) {
        line = line.replace(quote, BQ_CLOSE);
        quoteOpen = false;
      } else {
        line = line.replace(quote, BQ_OPEN);
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
      line = "<tr><th>" + line.substring(2, line.lastIndexOf('|') - 1) + "</th></tr>";
      line = line.replaceAll("\\|\\|","|");
      line = line.replace("|", "</th><th>");
      paragraph = false;

    } else if (line.startsWith("|")) {
      int pos = line.lastIndexOf('|');
      if (pos <= 0) {
        pos = line.length();
      }
      line = "<tr><td>" + line.substring(1, pos) + "</td></tr>";
      line = line.replace("|", "</td><td>");
      paragraph = false;
    }

    return line;
  }

  protected String handleCode(boolean hasLanguageCodeSample, String language, String arg, StringBuilder sb) {

    // Java vs Javascript...?
    String line = arg;
    if (line.contains("{code")) {
      if (codeOpen) {
        line = line.replaceFirst("\\{code\\}", "</pre>");
        codeOpen = false;
        paragraph = false;
      }else if (isCodeLanguageMatch(hasLanguageCodeSample, language, line)) {
        closeWhatsOpen(sb);

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

    return line.matches("\\{code:title=\"?" + language + "\"?[, }].*");
  }

  protected String handleList(StringBuilder sb, String line) {
    int pos = line.indexOf(' ');

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
    int pos = line.indexOf(' ');
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

  private int findBefore(String line, int start, char ch) {
    for (int i = start - 1; i >= 0; i--) {
      if (line.charAt(i) == ch) {
        return i;
      }
    }
    return -1;
  }

}
