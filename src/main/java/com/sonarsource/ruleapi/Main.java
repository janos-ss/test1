/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi;

import com.sonarsource.ruleapi.domain.Rule;

public class Main
{
  public static void main(String[] args)
  {
    if (args.length < 2) {

      System.out.println("Usage: language ruleKey");
      return;
    }

    String language = args[0];
    String ruleKey = args[1];

    RuleMaker maker = new RuleMaker();

    maker.makeRule(ruleKey, language);

  }
}
