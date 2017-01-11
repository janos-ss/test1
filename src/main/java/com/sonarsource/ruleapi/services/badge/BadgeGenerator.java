/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services.badge;


public class BadgeGenerator {

  private static final FontProvider FONT_PROVIDER = new FontProvider();

  /**
   * Returns a String holding the content of the generated image .
   */
  public String getBadge(final String label, final String value) {

    ImageData data = new ImageData(FONT_PROVIDER,label, value);

    return IMAGE_TEMPLATE
      .replaceAll("--fontFamily--", FontProvider.FONT_FAMILY)
      .replaceAll("--fontSize--", Integer.toString(FontProvider.FONT_SIZE))

      .replaceAll("--labelText--", data.labelText())
      .replaceAll("--labelWidth--", data.labelWidth())
      .replaceAll("--labelMidpoint--", data.labelMidpoint())

      .replaceAll("--valueText--", data.valueText())
      .replaceAll("--valueWidth--", data.valueWidth())
      .replaceAll("--valueMidPoint--", data.valueMidPoint())

      .replaceAll("--totalWidth--", data.totalWidth())
      .replaceAll(" *\n+ *", "");
  }

  private static final String IMAGE_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"--totalWidth--\" height=\"20\">\n" +
    "\n" +
    "  <linearGradient id=\"smooth\" x2=\"0\" y2=\"100%\">\n" +
    "    <stop offset=\"0\" stop-color=\"#bbb\" stop-opacity=\".1\"/>\n" +
    "    <stop offset=\"1\" stop-opacity=\".1\"/>\n" +
    "  </linearGradient>\n" +
    "\n" +
    "  <mask id=\"round\">\n" +
    "    <rect width=\"--totalWidth--\" height=\"20\" rx=\"3\" fill=\"#fff\"/>\n" +
    "  </mask>\n" +
    "\n" +
    "  <g mask=\"url(#round)\">\n" +
    "    <rect width=\"--labelWidth--\" height=\"20\" fill=\"#444\"/>\n" +
    "    <rect x=\"--labelWidth--\" width=\"--valueWidth--\" height=\"20\" fill=\"#4a1\"/>\n" +
    "    <rect width=\"--totalWidth--\" height=\"20\" fill=\"url(#smooth)\"/>\n" +
    "  </g>\n" +
    "\n" +
    "  <g fill=\"#fff\" text-anchor=\"middle\" font-family=\"--fontFamily--\" font-size=\"--fontSize--\">\n" +
    "    <text x=\"--labelMidpoint--\" y=\"15\" fill=\"#111\" fill-opacity=\".3\">--labelText--</text>\n" +
    "    <text x=\"--labelMidpoint--\" y=\"14\">--labelText--</text>\n" +
    "    <text x=\"--valueMidPoint--\" y=\"15\" fill=\"#111\" fill-opacity=\".3\">--valueText--</text>\n" +
    "    <text x=\"--valueMidPoint--\" y=\"14\">--valueText--</text>\n" +
    "  </g>\n" +
    "</svg>";

}
