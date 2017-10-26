/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Strings;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableStandard;
import com.sonarsource.ruleapi.externalspecifications.ReportType;
import com.sonarsource.ruleapi.externalspecifications.Standard;
import com.sonarsource.ruleapi.externalspecifications.SupportedStandard;
import com.sonarsource.ruleapi.services.IntegrityEnforcementService;
import com.sonarsource.ruleapi.services.ReportService;
import com.sonarsource.ruleapi.services.RuleFilesService;
import com.sonarsource.ruleapi.services.RuleManager;
import com.sonarsource.ruleapi.services.SonarPediaFileService;
import com.sonarsource.ruleapi.utilities.Language;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {

  private Main() {
    // utility class private constructor
  }

  public static void main(String[] args) {

    if (args.length == 0) {
      printHelpMessage();
      return;
    }

    Settings settings = new Settings();
    new JCommander(settings, null, args);

    if (settings.help || settings.option.isEmpty()) {
      printHelpMessage();
      return;
    }

    List<Option> options = settings.option
      .stream()
      .distinct()
      .map(Option::fromString)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    long countValidOptions = options
      .stream()
      .filter(option -> !option.requiresCredentials || credentialsProvided(settings))
      .filter(option -> option.languageRequirement.isCompliant(settings.language))
      .count();

    if( countValidOptions != options.size() ) {
      printHelpMessage();
      return;
    }

    for (Option option : options) {
      doRequestedOption(option, settings);
    }
  }

  static void printHelpMessage() {

    StringBuilder sb = new StringBuilder();
    sb.append("\nUSAGE: option [-parameter paramValue]\n\n");
    sb.append("OPTIONS:\n");
    for (Option option : Option.values()) {
      sb.append("  ").append(option.name().toLowerCase()).append(": ").append(option.description).append("\n");
    }
    sb.append("\n");
    sb.append("Additional parameters can be used with 'generate' and 'update':\n");
    sb.append("  -preserve-filenames : Use the rule keys provided by \"-rule\" to construct the name of output files, this allow to use legacy keys.\n");
    sb.append("  -no-language-in-filenames : Remove language from file name format (ex: \"S123.json\" instead of \"S123_java.json\"\n");
    System.out.println(sb.toString());
  }

  private static void doRequestedOption(Option option, Settings settings) {

    final File baseDir = new File(settings.baseDir);
    ReportService rs = new ReportService();

    switch (option) {
      case OUTDATED:
        new IntegrityEnforcementService(settings.login, settings.password, settings.instance).setCoveredLanguages();
        break;
      case INTEGRITY:
        new IntegrityEnforcementService(settings.login, settings.password, settings.instance).enforceIntegrity();
        break;
      case REPORTS:
        generateReports(settings, rs);
        break;
      case INIT:
        List<Language> languages = settings.language.stream().map(Language::fromString).collect(Collectors.toList());
        SonarPediaFileService.init(baseDir, languages);
        break;
      case GENERATE:
        handleGenerate(settings);
        break;
      case UPDATE:
        handleUpdate(settings);
        break;
      case DIFF:
        rs.writeOutdatedRulesReport(Language.fromString(settings.language.get(0)), settings.instance);
        break;
      case SINGLE_REPORT:
        handleSingleReport(settings, rs);
        break;
      default:
        printHelpMessage();
        break;

    }
  }

  private static void handleGenerate(Settings settings) {

    if (settings.directory != null) {
      System.out.println("Legacy mode: -directory is a deprecated option. The use of a sonarpedia.json file is recommended instead");
      if (settings.language == null || settings.language.isEmpty() || settings.language.size() > 1 ) {
        throw new RuleException("-language required with exactly one language");
      }
      RuleFilesService.create(settings.directory, Language.fromString(settings.language.get(0)), settings.preserveFileNames, !settings.noLanguageInFilenames)
        .generateRuleFiles(settings.ruleKeys);
    } else {
      SonarPediaFileService.create(new File(settings.baseDir), settings.preserveFileNames, !settings.noLanguageInFilenames)
        .generateRuleFiles(settings.ruleKeys);
    }

  }

  private static void handleUpdate(Settings settings) {

    if (settings.directory != null) {
      System.out.println("Legacy mode: -directory is a deprecated option. The use of a sonarpedia.json file is recommended  instead");
      if (settings.language == null || settings.language.isEmpty() || settings.language.size() > 1 ) {
        throw new RuleException("-language required with exactly one language");
      }
      RuleFilesService.create(settings.directory, Language.fromString(settings.language.get(0)), settings.preserveFileNames, !settings.noLanguageInFilenames)
        .updateDescriptions();
    } else {
      SonarPediaFileService.create(new File(settings.baseDir), settings.preserveFileNames, !settings.noLanguageInFilenames)
        .updateDescriptions();
    }

  }

  static void checkGenerateInput(Settings settings) {
    if (settings.ruleKeys.isEmpty()) {
      throw new RuleException("At least one -rule must be provided to generate");
    }
  }

  private static void handleSingleReport(Settings settings, ReportService rs) {

    SupportedStandard std = checkSingleReportInputs(settings);

    AbstractReportableStandard ars = (AbstractReportableStandard) std.getStandard();

    rs.writeSingleReport(Language.fromString(settings.language.get(0)), settings.instance, ars, ReportType.fromString(settings.report));
  }

  static SupportedStandard checkSingleReportInputs(Settings settings) {

    ReportType rt = ReportType.fromString(settings.report);
    SupportedStandard std = SupportedStandard.fromString(settings.tool);
    if (std == null || !(std.getStandard() instanceof AbstractReportableStandard)) {

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
    if (!reportTypes.contains(rt)) {
      StringBuilder sb = new StringBuilder();
      sb.append("Recognized report types for ").append(settings.tool).append(": ");
      for (ReportType type : reportTypes) {
        sb.append(type.name()).append(", ");
      }
      throw new RuleException(sb.toString());
    }

    return std;
  }

  private static void generateReports(Settings settings, ReportService rs) {

    rs.writeInternalReports(settings.instance);
    rs.writeUserFacingReports();

  }

  protected static boolean credentialsProvided(Settings settings) {
    return !(Strings.isNullOrEmpty(settings.login) || Strings.isNullOrEmpty(settings.password));
  }

  public static class Settings {

    @Parameter(names = "--help", help = true)
    private boolean help;

    @Parameter(description = "option", required = true)
    private List<String> option = new ArrayList<>();

    @Parameter(names = "-instance")
    private String instance = RuleManager.SONARQUBE_COM;

    @Parameter(names = "-login")
    private String login;

    @Parameter(names = "-password")
    private String password;

    @Parameter(names = "-rule", variableArity = true)
    public List<String> ruleKeys = new ArrayList<>();

    @Parameter(names = "-language", variableArity = true)
    private List<String> language;

    @Parameter(names = "-report")
    private String report;

    @Parameter(names = "-directory")
    private String directory;

    @Parameter(names = "-tool")
    private String tool;

    @Parameter(names = "-preserve-filenames")
    private boolean preserveFileNames = false;

    @Parameter(names = "-no-language-in-filenames")
    private boolean noLanguageInFilenames = false;

    // for the purpose of testing
    @Parameter(names = "-baseDir", hidden = true)
    private String baseDir = ".";

  }


  public enum Option {
    REPORTS(false
        , LanguageRequirement.NO_LANGUAGE
        , "Generates all reports based on Next (default) or a particular -instance http:..."),
    SINGLE_REPORT(false
        , LanguageRequirement.ONE_AND_ONLY_ONE_LANGUAGE
        , "Generate a single -report against -instance (defaults to Next), -language, and -tool."),
    OUTDATED(true
        , LanguageRequirement.NO_LANGUAGE
        , "Marks RSpec rules outdated based on Next or instance specified with -instance parameter. Requires -login and -password parameters."),
    INTEGRITY(true
        , LanguageRequirement.NO_LANGUAGE
        , "RSpec internal integrity check. Requires -login and -password parameters."),
    INIT(false
        , LanguageRequirement.ONE_OR_MORE_LANGUAGE
        , "Create a sonarpedia.json file with its rules directory, the -language parameter specifies the languages, there can be more than one. "),
    GENERATE(false
        , LanguageRequirement.ZERO_OR_ONE_LANGUAGE
        , "Generates html description and json metadata files specified by -rule parameter"),
    UPDATE(false
        , LanguageRequirement.ZERO_OR_ONE_LANGUAGE
        , "Update html and json description files."),
    DIFF(false
        , LanguageRequirement.ONE_AND_ONLY_ONE_LANGUAGE
        , "Generates a diff report for the specified -language and -instance");

    private String description;
    private boolean requiresCredentials;
    private LanguageRequirement languageRequirement;

    Option(boolean requiresCredentials, LanguageRequirement languageRequirement, String description) {
      this.requiresCredentials = requiresCredentials;
      this.languageRequirement = languageRequirement;
      this.description = description;
    }

    public static Option fromString(String input) {

      for (Option output : Option.values()) {
        if (output.toString().equalsIgnoreCase(input)) {
          return output;
        }
      }

      return null;
    }

    public enum LanguageRequirement {
      NO_LANGUAGE,
      ZERO_OR_ONE_LANGUAGE,
      ONE_AND_ONLY_ONE_LANGUAGE,
      ONE_OR_MORE_LANGUAGE;

      public boolean isCompliant( List<String> languages ) {
        switch( this ) {
          case NO_LANGUAGE:
            return  languages == null || languages.isEmpty();
          case ONE_AND_ONLY_ONE_LANGUAGE:
            return languages != null && languages.size() == 1;
          case ONE_OR_MORE_LANGUAGE:
            return languages != null && ! languages.isEmpty();
          case ZERO_OR_ONE_LANGUAGE:
            return languages == null || languages.isEmpty() || languages.size() == 1;
          default:
            throw new IllegalStateException();
        }
      }
    }
  }
}
