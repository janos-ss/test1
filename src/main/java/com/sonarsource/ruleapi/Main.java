/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi;

import com.sonarsource.ruleapi.utilities.IntegrityEnforcer;
import com.sonarsource.ruleapi.utilities.RuleException;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  private Main() {
    // utility class private constructor
  }

  public static void main(String [] args) {

    if (args.length < 2) {
      LOGGER.severe("Username, password required as first, second arguments.");
      System.exit(-1);
    }

    String login = args[0];
    String password = args[1];

    IntegrityEnforcer enforcer = new IntegrityEnforcer();
    try {
      enforcer.setCoveredLanguages(login, password);
      enforcer.enforceCwe(login, password);
    } catch (RuleException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
    }
  }



}
