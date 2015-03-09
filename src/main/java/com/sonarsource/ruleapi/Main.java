/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.sonarsource.ruleapi.services.IntegrityEnforcementService;
import com.sonarsource.ruleapi.services.ReportService;
import com.sonarsource.ruleapi.services.RuleManager;
import org.fest.util.Strings;

import java.util.ArrayList;
import java.util.List;
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

    if (settings.help || settings.option.isEmpty()) {
      printHelpMessage();
      return;
    }

    List<Option> options = new ArrayList<Option>();
    for (String str : settings.option) {

      Option option = Option.fromString(str);
      if (option.requiresCredentials && !credentialsProvided(settings)) {
        printHelpMessage();
        return;
      }

      if (! options.contains(option)) {
        options.add(option);
      }
    }

    for (Option option : options) {
      doRequestedOption(option, settings);

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


    IntegrityEnforcementService enforcer = new IntegrityEnforcementService();
    ReportService rs = new ReportService();

    switch (option) {
      case OUTDATED :
        enforcer.setCoveredLanguages(settings.login, settings.password);
        break;

      case INTEGRITY :
        enforcer.enforceTagReferenceIntegrity(settings.login, settings.password);
        break;

      case REPORTS:
        generateReports(settings, rs);
        break;

      case GENERATE:
        rs.generateRuleDescriptions(settings.ruleKeys, settings.language);
        break;

      default:
        printHelpMessage();
        break;

    }
  }

  private static void generateReports(Settings settings, ReportService rs) {

    if (settings.orchestrator) {
      rs.writeReportsWithOrchestrator();
    } else {
      rs.writeAllReports(settings.instance);
    }

  }

  private static boolean credentialsProvided(Settings settings) {

    return !(Strings.isNullOrEmpty(settings.login) || Strings.isNullOrEmpty(settings.password));
  }


  public static class Settings{

    @Parameter(names = "--help", help = true)
    private boolean help;

    @Parameter(required = true)
    private List<String> option = new ArrayList<String>();

    @Parameter(names = "-instance")
    private String instance = RuleManager.NEMO;

    @Parameter(names = "-latestSnapshot")
    private boolean orchestrator;

    @Parameter(names = "-login")
    private String login;

    @Parameter(names = "-password")
    private String password;

    @Parameter(names="-rule", variableArity = true)
    public List<String> ruleKeys = new ArrayList<String>();

    @Parameter(names="-language")
    private String language;

  }

  public enum Option {
    REPORTS(false,  "Generates all reports based on Nemo (default) or a particular -instance http:..., or -latestSnapshot."),
    OUTDATED(true,  "Marks RSpec rules outdated based on Nemo or instance specified with -instance parameter. Requires -login and -password parameters."),
    INTEGRITY(true, "RSpec internal integrity check. Requires -login and -password parameters."),
    GENERATE(false, "Generates html description file specified by -rule and -langauge parameters.");

    private String description;
    private boolean requiresCredentials;

    Option(boolean requiresCredentials, String description) {
      this.requiresCredentials = requiresCredentials;
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
