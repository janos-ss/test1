/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableStandard;
import com.sonarsource.ruleapi.externalspecifications.ReportType;
import com.sonarsource.ruleapi.externalspecifications.Standard;
import com.sonarsource.ruleapi.externalspecifications.SupportedStandard;
import com.sonarsource.ruleapi.services.IntegrityEnforcementService;
import com.sonarsource.ruleapi.services.ReportService;
import com.sonarsource.ruleapi.services.RuleFilesService;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.utilities.Language;
import org.fest.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {

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

    List<Option> options = new ArrayList<>();
    for (String str : settings.option) {

      Option option = Option.fromString(str);
      if (option == null) {
        continue;
      }

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
    System.out.println(sb.toString());
  }

  protected static void doRequestedOption(Option option, Settings settings) {


    ReportService rs = new ReportService();
    Language language = Language.fromString(settings.language);

    switch (option) {
      case OUTDATED :
        new IntegrityEnforcementService(settings.login, settings.password).setCoveredLanguages();
        break;
      case INTEGRITY :
        new IntegrityEnforcementService(settings.login, settings.password).enforceIntegrity();
        break;
      case REPORTS:
        generateReports(settings, rs);
        break;
      case GENERATE:
        RuleFilesService.create(settings.directory, language).generateRuleFiles(settings.ruleKeys);
        break;
      case UPDATE:
        RuleFilesService.create(settings.directory, language).updateDescriptions();
        break;
      case DIFF:
        rs.writeOutdatedRulesReport(language, settings.instance);
        break;
      case SINGLE_REPORT:
        handleSingleReport(settings, rs);
        break;
      default:
        printHelpMessage();
        break;

    }
  }

  private static void handleSingleReport(Settings settings, ReportService rs) {

    checkSingleReportInputs(settings);

    SupportedStandard std = SupportedStandard.fromString(settings.tool);
    if (std.getStandard() instanceof AbstractReportableStandard) {
      AbstractReportableStandard ars = (AbstractReportableStandard) std.getStandard();

      rs.writeSingleReport(Language.fromString(settings.language), settings.instance, ars, ReportType.fromString(settings.report));
    }
  }

  protected static void checkSingleReportInputs(Settings settings) {

    ReportType rt = ReportType.fromString(settings.report);
    SupportedStandard std = SupportedStandard.fromString(settings.tool);
    if (std == null || ! (std.getStandard() instanceof AbstractReportableStandard)) {

      StringBuilder sb = new StringBuilder();
      sb.append("A recognized -tool must be provided: ");
      for (SupportedStandard scs : SupportedStandard.values()) {

        Standard cs = scs.getStandard();
        if (cs instanceof AbstractReportableStandard) {
          sb.append(scs.name()).append(", ");
        }
      }
      throw new RuleException(sb.toString());
    }

    AbstractReportableStandard ars = (AbstractReportableStandard) std.getStandard();
    List<ReportType> reportTypes = Arrays.asList(ars.getReportTypes());
    if (! reportTypes.contains(rt)) {
      StringBuilder sb = new StringBuilder();
      sb.append("Recognized report types for ").append(settings.tool).append(": ");
      for (ReportType type : reportTypes) {
        sb.append(type.name()).append(", ");
      }
      throw new RuleException(sb.toString());
    }

  }


  private static void generateReports(Settings settings, ReportService rs) {

    rs.writeInternalReports(settings.instance);
    rs.writeUserFacingReports();

  }

  protected static boolean credentialsProvided(Settings settings) {

    return !(Strings.isNullOrEmpty(settings.login) || Strings.isNullOrEmpty(settings.password));
  }


  public static class Settings{

    @Parameter(names = "--help", help = true)
    private boolean help;

    @Parameter(required = true)
    private List<String> option = new ArrayList<>();

    @Parameter(names = "-instance")
    private String instance = RuleManager.SONARQUBE_COM;

    @Parameter(names = "-login")
    private String login;

    @Parameter(names = "-password")
    private String password;

    @Parameter(names="-rule", variableArity = true)
    public List<String> ruleKeys = new ArrayList<>();

    @Parameter(names="-language")
    private String language;

    @Parameter(names="-report")
    private String report;

    @Parameter(names="-directory")
    private String directory;

    @Parameter(names="-tool")
    private String tool;

  }

  public enum Option {
    REPORTS(false,  "Generates all reports based on Nemo (default) or a particular -instance http:..."),
    SINGLE_REPORT(false, "Generate a single -report against -instance (defaults to Nemo), -language, and -tool."),
    OUTDATED(true,  "Marks RSpec rules outdated based on Nemo or instance specified with -instance parameter. Requires -login and -password parameters."),
    INTEGRITY(true, "RSpec internal integrity check. Requires -login and -password parameters."),
    GENERATE(false, "Generates html description and json metadata files specified by -rule and -language parameters at directory specified by -directory"),
    UPDATE(false, "Update html and json description files specified by -language found at directory specified by -directory"),
    DIFF(false, "Generates a diff report for the specified -language and -instance");

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
