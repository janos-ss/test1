/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;

/**
 * Fix Html issue, indent formatting, wrap long lines
 */
public class HtmlSanitizer {

  private int indentAmount;
  private int wrapWidth;

  /**
   * @param indentAmount indent size used between html tag
   * @param wrapWidth    maximum line width
   */
  public HtmlSanitizer(int indentAmount, int wrapWidth) {
    this.indentAmount = indentAmount;
    this.wrapWidth = wrapWidth;
  }

  /**
   * fix, indent and wrap html source code
   *
   * @param html to sanitize
   * @return sanitized html
   */
  public String format(String html) {
    Document doc = Jsoup.parseBodyFragment(html);
    doc.outputSettings().indentAmount(indentAmount).prettyPrint(true);
    String normalizedHtml = doc.body().html();
    return wrapLines(normalizedHtml);
  }

  private String wrapLines(String html) {
    StringBuilder out = new StringBuilder();
    String[] lines = html.split("\n");
    boolean inPreTag = false;
    for (String line : lines) {
      line = trimRight(line);
      if (!isImpactedByPreTagStatus(inPreTag, line)) {
        writeWrapped(out, line);
      } else {
        out.append(line).append('\n');
      }
      inPreTag = updateInPreTagStatus(inPreTag, line);
    }
    return out.toString();
  }

  private void writeWrapped(StringBuilder out, String line) {
    String remaining = line;
    if (remaining.length() <= wrapWidth) {
      out.append(remaining).append('\n');
      return;
    }
    String padding = StringUtil.padding(indentSize(remaining));
    while (remaining.length() > 0) {
      int spacePos = -1;
      if (remaining.length() > wrapWidth) {
        spacePos = findBestSpacePosToSplit(remaining);
      }
      if (spacePos != -1 && spacePos > padding.length()) {
        out.append(remaining, 0, spacePos).append('\n');
        remaining = padding + remaining.substring(spacePos + 1);
      } else {
        out.append(remaining).append('\n');
        remaining = "";
      }
    }
  }

  private static String trimRight(String line) {
    if (line.endsWith(" ")) {
      int lineEnd = line.length();
      while (lineEnd > 0 && line.charAt(lineEnd - 1) == ' ') {
        lineEnd--;
      }
      return line.substring(0, lineEnd);
    } else {
      return line;
    }
  }

  private int findBestSpacePosToSplit(String line) {
    int spacePos = line.lastIndexOf(' ', wrapWidth);
    if (spacePos == -1) {
      spacePos = line.indexOf(' ', wrapWidth + 1);
    }
    return spacePos;
  }

  private static boolean isImpactedByPreTagStatus(boolean previousInPreTag, String line) {
    return previousInPreTag || line.contains("<pre>") || line.contains("</pre>");
  }

  private static boolean updateInPreTagStatus(boolean previousInPreTag, String line) {
    int openPrePos = line.indexOf("<pre");
    int closePrePos = line.indexOf("</pre>");
    boolean openPreTag = openPrePos != -1 && openPrePos > closePrePos;
    boolean closePreTag = closePrePos != -1 && closePrePos > openPrePos;
    return openPreTag || (previousInPreTag && !closePreTag);
  }

  private static int indentSize(String text) {
    int indent = 0;
    while (indent < text.length() && text.charAt(indent) == ' ') {
      indent++;
    }
    return indent;
  }

}
