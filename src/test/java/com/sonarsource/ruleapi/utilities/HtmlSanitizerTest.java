/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlSanitizerTest {

  private static HtmlSanitizer san() {
    return san(80);
  }

  private static HtmlSanitizer san(int wrapWidth) {
    return new HtmlSanitizer(2, wrapWidth);
  }

  @Test
  public void formatList() {
    assertThat(san().format(
        "<ul><li>A</li></ul>")
    ).isEqualTo("" +
        "<ul>\n" +
        "  <li>A</li>\n" +
        "</ul>\n");
  }

  @Test
  public void wrapLine30() {
    assertThat(san(30).format(
        "one two three four five six seven eight nine ten eleven twelve thirteen fourteen")
    ).isEqualTo("" +
        "one two three four five six\n" +
        "seven eight nine ten eleven\n" +
        "twelve thirteen fourteen\n");
  }

  @Test
  public void keepIndentWhileWrapLine30() {
    assertThat(san(30).format(
        "<table><tr><td>one two three four five six <em>seven eight</em> nine ten eleven twelve thirteen fourteen</td></tr></table>")
    ).isEqualTo("" +
        "<table>\n" +
        "  <tbody>\n" +
        "    <tr>\n" +
        "      <td>one two three four\n" +
        "      five six <em>seven\n" +
        "      eight</em> nine ten\n" +
        "      eleven twelve thirteen\n" +
        "      fourteen</td>\n" +
        "    </tr>\n" +
        "  </tbody>\n" +
        "</table>\n");
  }

  @Test
  public void doNotWrapInsidePre() {
    assertThat(san(30).format("" +
        "<p>one two three <code>four five</code> six seven eight seven eight nine ten</p>\n" +
        "<pre>\n" +
        "one two three four five six seven eight seven eight nine ten\n" +
        "one two three four five six seven eight seven eight nine ten\n" +
        "</pre> " +
        "<p>one two</p>\n")
    ).isEqualTo("" +
        "<p>one two three <code>four\n" +
        "five</code> six seven eight\n" +
        "seven eight nine ten</p>\n" +
        "<pre>\n" +
        "one two three four five six seven eight seven eight nine ten\n" +
        "one two three four five six seven eight seven eight nine ten\n" +
        "</pre>\n" +
        "<p>one two</p>\n");
  }

  @Test
  public void doNotWrapUnbreakable() {
    assertThat(san(30).format("" +
        "<p>________________good_luck_to_wrap_this________________</p>  \n")
    ).isEqualTo("" +
        "<p>________________good_luck_to_wrap_this________________</p>\n");
  }

}
