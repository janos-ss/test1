/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.utilities;

import java.io.StringWriter;

/**
 * From http://www.1maven.com/sources/de.jaide:courier:1.6.2/JSONWriter
 * Sample usage:
 * <pre>
 * Writer writer = new JSONWriter(); // this writer adds indentation
 * jsonobject.writeJSONString(writer);
 * System.out.println(writer.toString());
 * </pre>
 *
 * @author Elad Tabak
 * @author Maciej Komosinski, minor improvements, 2015
 * @since 28-Nov-2011
 * @version 0.2
 */
public class JSONWriter extends StringWriter {

  // define as you wish
  static final String INDENTSTRING = "  ";
  
  // use "" if you don't want space after colon
  static final String SPACEAFTERCOLON = " ";

  private int indentlevel = 0;

  @Override
  public void write(int c)
  {
    char ch = (char) c;
    if (ch == '[' || ch == '{') {
      super.write(c);
      super.write('\n');
      indentlevel++;
      writeIndentation();
    } else if (ch == ',') {
      super.write(c);
      super.write('\n');
      writeIndentation();
    } else if (ch == ']' || ch == '}') {
      super.write('\n');
      indentlevel--;
      writeIndentation();
      super.write(c);
    } else if (ch == ':') {
      super.write(c);
      super.write(SPACEAFTERCOLON);
    } else {
      super.write(c);
    }
  }

  private void writeIndentation()
  {
    for (int i = 0; i < indentlevel; i++) {
      super.write(INDENTSTRING);
    }
  }
}
