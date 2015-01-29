/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
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

    if (args.length == 0) {
      printHelpMessage();
      return;
    }

    Settings settings = new Settings();
    new JCommander(settings, args);

    if (settings.help) {
      printHelpMessage();
      return;
    }

    if (settings.option != null && !settings.option.isEmpty()){

      List<Option> options = new ArrayList<Option>();
      for (String str : settings.option) {

        Option option = Option.fromString(str);
        if (! options.contains(option)) {
          options.add(option);
        }
      }

      for (Option option : options) {
        doRequestedOption(option, settings);

      }
    } else {
      printHelpMessage();
    }
  }

  protected static void printHelpMessage() {

    StringBuilder sb = new StringBuilder();
    sb.append("\nUSAGE: option [-parameter paramValue]\n\n");
    sb.append("OPTIONS:\n");
    for (Option option : Option.values()) {
      sb.append("  ").append(option.name().toLowerCase()).append(": ").append(option.description).append("\n");
    }
    LOGGER.info(sb.toString());
  }

  protected static void doRequestedOption(Option option, Settings settings) {


    IntegrityEnforcementService enforcer = null;
    ReportService rs = null;
    if (option == Option.REPORTS || option == Option.GENERATE) {

      rs = new ReportService();

    } else {

      if (Strings.isNullOrEmpty(settings.login) || Strings.isNullOrEmpty(settings.password)) {
        printHelpMessage();
        return;
      }
      enforcer = new IntegrityEnforcementService();
    }

    try {
      switch (option) {
        case OUTDATED :
          enforcer.setCoveredAndOutdatedLanguages(settings.login, settings.password);
          break;

        case INTEGRITY :
          enforcer.enforceTagReferenceIntegrity(settings.login, settings.password);
          break;

        case REPORTS:
          rs.getReports(settings.instance);
          break;

        case GENERATE:
          rs.generateRuleDescriptions(settings.ruleKeys, settings.language);
          break;

        default:
          printHelpMessage();
          break;

      }
    } catch (RuleException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
  }


  public static class Settings{

    @Parameter(names = "--help", help = true)
    private boolean help;

    @Parameter(required = true)
    private List<String> option;

    @Parameter(names = "-instance")
    private String instance = RuleManager.NEMO;

    @Parameter(names = "-login")
    private String login;

    @Parameter(names = "-password")
    private String password;

    @Parameter(names="-rule", variableArity = true)
    public List<String> ruleKeys = new ArrayList<String>();

    @Parameter(names="-langauge")
    private String language;

  }

  public enum Option {
    REPORTS("Generates all reports based on Nemo or instance specified with optional -instance parameter."),
    OUTDATED("Marks RSpec rules outdated based on Nemo or instance specified with -instance parameter. Requires -login and -password parameters."),
    INTEGRITY("RSpec internal integrity check. Requires -login and -password parameters."),
    GENERATE("Generates html description file specified by -rule and -langauge parameters.");

    private String description;

    Option(String description) {
      this.description = description;
    }

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
