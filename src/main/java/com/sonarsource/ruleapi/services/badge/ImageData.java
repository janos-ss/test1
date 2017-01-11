/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services.badge;


/**
 * Container holding data needed to generate an SVG image.
 */
public class ImageData {

  private static final int X_MARGIN = 6;

  private String labelText;
  private String valueText;

  private int labelWidth;
  private int valueWidth;
  private int totalTextWidth;
  private int labelMidpoint;
  private int valueMidPoint;


  public ImageData(FontProvider fontProvider, String label, String value) {

    this.labelText = label;
    this.valueText = value;

    labelWidth = fontProvider.computeWidth(label) + (2 * X_MARGIN);
    labelMidpoint = labelWidth / 2;
    valueWidth = fontProvider.computeWidth(value) + (2 * X_MARGIN);
    valueMidPoint = labelWidth + (valueWidth / 2);

    totalTextWidth = labelWidth + valueWidth;
  }

  /**
   * Value text getter.
   *
   * @return value text
   */
  public String valueText() {

    return this.valueText;
  }

  /**
   * Label text getter.
   *
   * @return label text
   */
  public String labelText() {

    return this.labelText;
  }

  /**
   * Width in pixels of the content part of the badge.
   *
   * @return labelWidth
   */
  public String labelWidth() {

    return Integer.toString(this.labelWidth);
  }

  /**
   * Width in pixels of the value part of the badge.
   *
   * @return valueWidth
   */
  public String valueWidth() {

    return Integer.toString(this.valueWidth);
  }

  /**
   * Total width of the SVGImage from left border to right border.
   *
   * @return image width
   */
  public String totalWidth() {

    return Integer.toString(this.totalTextWidth);
  }

  /**
   * Size in pixels as a String from left border of the SVG image to the middle of the label part of the badge.
   *
   * @return labelMidpoint
   */
  public String labelMidpoint() {

    return Integer.toString(this.labelMidpoint);
  }

  /**
   * Size in pixels as a String from left border of the SVG image to the middle of the value part of the badge.
   *
   * @return valueMidPoint
   */
  public String valueMidPoint() {

    return Integer.toString(this.valueMidPoint);
  }

}
