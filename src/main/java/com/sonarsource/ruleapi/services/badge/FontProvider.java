/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services.badge;

import com.sonarsource.ruleapi.domain.RuleException;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;


/**
 * Original @author Michel Pawlak
 */
public class FontProvider {

  public static final String FONT_FAMILY = "DejaVu Sans,Verdana,Sans PT,Lucida Grande,Tahoma,Helvetica,Arial,sans-serif";
  public static final String FONT_NAME = "Verdana";
  public static final int FONT_STYLE = Font.PLAIN;
  public static final int FONT_SIZE = 12;

  private static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(new AffineTransform(), true, true);

  private Font font;


  /**
   * Constructor that loads the preferred {@link Font} or throws a {@link RuleException}.
   *
   * @throws RuleException if the preferred {@link Font} cannot be loaded.
   */
  public FontProvider() throws RuleException {
    final Font preferredFont = new Font(FONT_NAME, FONT_STYLE, FONT_SIZE);
    if (preferredFont.getFontName().equals(Font.DIALOG)) {
      throw new RuleException("Unable to load font: " + FONT_NAME);
    }
    this.font = preferredFont;
  }

  /**
   * Computes the width in pixels of a text String when using the {@link Font} held by the {@link FontProvider}.
   *
   * @param text Text to be mesured
   * @return text width in pixels
   */
  public int computeWidth(final String text) {
    final Rectangle2D stringBounds = this.font.getStringBounds(text, FONT_RENDER_CONTEXT);
    return (int) (stringBounds.getWidth());
  }

}
