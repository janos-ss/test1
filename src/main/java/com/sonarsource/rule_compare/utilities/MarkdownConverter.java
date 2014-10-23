package com.sonarsource.rule_compare.utilities;

import java.util.LinkedList;

/**
 * Converts Jira markdown to HTML
 */
public class MarkdownConverter {
  private boolean codeOpen = false;
  private boolean tableOpen = false;
  private boolean wrongLanguage = false;
  private boolean paragraph = true;
  private LinkedList<String> listCloses;

  public MarkdownConverter() {
  }

  public String transform(String markdown, String language) {
    codeOpen = false;
    tableOpen = false;
    wrongLanguage = false;
    paragraph = true;
    listCloses = new LinkedList<String>();
    StringBuilder sb = new StringBuilder();

    String[] lines = markdown.split("\r\n");

    for (int i = 0; i < lines.length; i++) {
      if (lines[i].length() > 0) {

        lines[i] = handleCode(lines[i].matches(language == null?"":language + "[, }]"), lines[i]);

        if (!codeOpen) {
          lines[i] = handleTable(lines[i], sb);
          lines[i] = handleHeading(lines[i]);
          lines[i] = handleBq(lines[i]);
          lines[i] = handleTt(lines[i]);
          lines[i] = handleList(sb, lines[i]);
          lines[i] = handleBold(lines[i]);
          lines[i] = handleItal(lines[i]);
        }
        if (!wrongLanguage) {
          if (paragraph && !codeOpen) {
            sb.append("<p>");
          }
          sb.append(lines[i]);
          if (paragraph && !codeOpen) {
            sb.append("</p>");
          }
          sb.append("%n");
          paragraph = true;
        }
      }
    }
    while (listCloses.size() > 0) {
      sb.append(listCloses.pop());
    }
    if (codeOpen) {
      sb.append("</pre>%n");
    }
    if (tableOpen) {
      sb.append("</table>%n");
    }

    return sb.toString();
  }

  protected String handleHeading(String line) {
    if (line.matches("^h[0-9]\\..*")) {
      char level = line.charAt(1);
      line = "<h" + level + '>' + line.substring(3).trim() + "</h" + level + ">%n";
      paragraph = false;
    }
    return line;
  }

  protected String handleHref(String line) {

    if (line.matches("[^\\[]*\\[[^|]+\\|https?[A-Za-z0-9-._~:/?#\\\\[\\\\]@!$&'()*+,;=% ]+\\].*")) {
      int pos = line.indexOf("http");
      int hrefStart = findBefore(line, pos, '[');
      int hrefEnd = findAfter(line, pos, ' ');
      if (hrefEnd == -1) {
        hrefEnd = line.length() - 1;
      }

      String href = line.substring(pos, hrefEnd - 1);
      String label = line.substring(hrefStart + 1, pos - 1);
      line = line.substring(0, hrefStart)
        + "<a href=\"" + href + "\">" + label + "</a>"
        + line.substring(hrefEnd, line.length());
    }
    return line;
  }

  protected String handleBq(String line) {
    if (line.startsWith("bq. ")) {
      line = "<blockquote>" + line.substring(4) + "</blockquote>";
      paragraph = false;
    }
    return line;
  }

  protected String handleTt(String line) {
    if (line.contains("{{")) {
      line = line.replaceAll("\\{\\{", "<code>");
    }
    if (line.contains("}}")) {
      line = line.replaceAll("}}", "</code>");
    }
    return line;
  }

  protected String handleTable(String line, StringBuilder sb) {
    if (!tableOpen && line.startsWith("|")) {
      tableOpen = true;
      sb.append("<table>%n");
    }
    else if (tableOpen) {
      tableOpen = false;
      sb.append("</table>%n");
    }

    if (line.startsWith("||")) {
      line = "<tr><th>" + line.substring(2, findLast(line, '|') - 1) + "</th></tr>";
      line = line.replace("\\|\\|", "</th><th>");
      paragraph = false;
    }
    else if (line.startsWith("|")) {
      line = "<tr><td>" + line.substring(1, findLast(line, '|')) + "</td></tr>";
      line = line.replace("|", "</td><td>");
      paragraph = false;
    }

    return line;
  }

  protected String handleCode(boolean matchesLanguage, String line) {
    // Java vs Javascript...?

    if (line.contains("{code")) {
      if (codeOpen) {
        line = line.replaceFirst("\\{code\\}", "</pre>");
        wrongLanguage = false;
        codeOpen = false;
        paragraph = false;
      } else if (line.contains("{code}") || matchesLanguage) {
        codeOpen = true;
        line = line.replaceFirst("\\{code.*\\}", "<pre>");
      } else {
        // code sample's for another language
        wrongLanguage = true;
      }
    }
    return line;
  }

  protected String handleList(StringBuilder sb, String line) {
    int pos = findFirst(line, ' ');

    if (pos > -1 && listCloses.size() > pos) {
      sb.append(listCloses.pop());
    }

    if (line.matches("[*#]+ +.*")) {
      if (listCloses.size() < pos)
      {
        if (line.charAt(pos - 1) == '*') {
          listCloses.push("</ul>%n");
          sb.append("<ul>%n");
        }
        else {
          listCloses.push("</ol>%n");
          sb.append("<ol>%n");
        }
      }

      return handleLi(line);
    }
    return line;
  }

  private String handleLi(String line) {
    int pos = findFirst(line, ' ');
    paragraph = false;
    return "<li>" + line.substring(pos) + "</li>";
  }

  protected String handleBold(String line) {
    boolean boldOpen = false;
    while (line.contains("*")) {
      if (boldOpen) {
        line = line.replaceFirst("\\*", "</strong>");
        boldOpen = false;
      } else {
        line = line.replaceFirst("\\*", "<strong>");
        boldOpen = true;
      }
    }
    return line;
  }

  protected String handleItal(String line) {
    boolean italOpen = false;
    while (line.contains("_")) {
      if (italOpen) {
        line = line.replaceFirst("_", "</em>");
        italOpen = false;
      } else {
        line = line.replaceFirst("_", "<em>");
        italOpen = true;
      }
    }
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
    for (int i = 0; i < src.length(); i++)
    {
      if (src.charAt(i) == charToFind) {
        return i;
      }
    }
    return -1;
  }

}
