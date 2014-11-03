/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.domain;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ParameterTest extends TestCase {

  private Parameter full;

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    full = new Parameter();
    full.setKey("fullKey");
    full.setDescription("This is the description of the fully-described parameter");
    full.setType("Text");
    full.setDefaultVal("*.*");
  }

  public void testEqualsSameRef() throws Exception {

    Assert.assertTrue(full.equals(full));
  }

  public void testEqualsSameValues () throws Exception {
    Parameter copy = new Parameter();
    copy.setDefaultVal(full.getDefaultVal());
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    Assert.assertTrue(full.equals(copy));
  }

  public void testEqualsNeqKey() throws Exception {
    Parameter copy = new Parameter();
    copy.setDefaultVal("copyKey");
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    Assert.assertFalse(full.equals(copy));
  }

  public void testEqualsNeqDescription () throws Exception {
    Parameter copy = new Parameter();
    copy.setDefaultVal(full.getDefaultVal());
    copy.setKey(full.getKey());
    copy.setDescription("New description");

    Assert.assertFalse(full.equals(copy));
  }


  public void testEqualsNeqDefault () throws Exception {
    Parameter copy = new Parameter();
    copy.setDefaultVal("**/*.*");
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    Assert.assertFalse(full.equals(copy));
  }

  public void testEqualsNeqNullDefault () throws Exception {
    Parameter copy = new Parameter();
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    Assert.assertFalse(full.equals(copy));
  }

  public void testEqualsNeqOtherNullDefault () throws Exception {
    Parameter copy = new Parameter();
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    Assert.assertFalse(copy.equals(full));
  }

  public void testHashCodeDefault () throws Exception {
    Parameter copy = new Parameter();
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    Assert.assertEquals(-1130130840, full.hashCode());
  }

  public void testHashCodeNullDefault () throws Exception {
    Parameter copy = new Parameter();
    copy.setKey(full.getKey());
    copy.setDescription(full.getDescription());

    Assert.assertEquals(-1130172670, copy.hashCode());
  }


}
