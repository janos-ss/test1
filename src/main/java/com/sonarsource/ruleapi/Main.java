/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.sonarsource.ruleapi.services.IntegrityEnforcementService;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.services.ReportService;
import com.sonarsource.ruleapi.services.RuleManager;
import org.fest.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  private Main() {
    // utility class private constructor
  }

  public static void main(String [] args) {

    Settings settings = new Settings();
    new JCommander(settings, args);

    List<Option> options = new ArrayList<Option>();
    for (String str : settings.option) {

      Option option = Option.fromString(str);
      if (option == null) {
        option = Option.REPORTS;
      }
      if (! options.contains(option)) {
        options.add(option);
      }
    }

    for (Option option : options) {
      doRequestedOption(option, settings);

    }
  }

  protected static void doRequestedOption(Option option, Settings settings) {


    IntegrityEnforcementService enforcer = null;
    if (option != Option.REPORTS) {

      if (Strings.isNullOrEmpty(settings.login) || Strings.isNullOrEmpty(settings.password)) {
        throw new ParameterException("-login and -password required for integrity option");
      }
      enforcer = new IntegrityEnforcementService();
    }

    try {
      switch (option) {
        case OUTDATED :
          enforcer.setCoveredAndOutdatedLanguages(settings.login, settings.password);
         break;

        case INTEGRITY :
          enforcer.enforceCwe(settings.login, settings.password);
          break;

        case REPORTS:
        default:
          ReportService rs = new ReportService();
          rs.getReports(settings.instance);
          break;

      }
    } catch (RuleException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
  }


  public static class Settings{

    @Parameter(required = true)
    private List<String> option;

    @Parameter(names = "-instance")
    private String instance = RuleManager.NEMO;

    @Parameter(names = "-login")
    private String login;

    @Parameter(names = "-password")
    private String password;

  }

  public enum Option {
    REPORTS, OUTDATED, INTEGRITY;

    public static Option fromString(String input) {

      for(Option output : Option.values()) {
        if(output.toString().equalsIgnoreCase(input)) {
          return output;
        }
      }

      return null;
    }

  }

}
