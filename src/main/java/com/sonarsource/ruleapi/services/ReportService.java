/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleComparison;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableExternalTool;
import com.sonarsource.ruleapi.externalspecifications.CustomerReport;
import com.sonarsource.ruleapi.externalspecifications.ReportType;
import com.sonarsource.ruleapi.externalspecifications.CleanupReport;
import com.sonarsource.ruleapi.externalspecifications.Standard;
import com.sonarsource.ruleapi.externalspecifications.SupportedStandard;
import com.sonarsource.ruleapi.externalspecifications.specifications.AbstractMultiLanguageStandard;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableStandard;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fest.util.Strings;
import org.json.simple.JSONArray;


public class ReportService extends RuleManager {

  private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());
  private static final String BASE_DIR = "target/reports/";
  private static final String COVERAGE_DIR = "coverage/";
  private static final String HTML = ".html";

  private String css = "";


  public ReportService() {
    java.net.URL url = this.getClass().getResource("/services");
    if (url != null) {
      try (Scanner scan = new java.util.Scanner(new File(url.getPath() + "/report.css"), "UTF8")){
        css = scan.useDelimiter("\\Z").next();
      } catch (FileNotFoundException e) {
        LOGGER.log(Level.WARNING, "CSS file not found", e);
      }
    }
  }
  /**
   * Writes internal reports based on passed-in SonarQube url
   *
   * @param instance SonarQube instance URL
   */
  public void writeInternalReports(String instance) {

    writeToolInternalReports(instance);
    writeDetailedCoverageReports(instance);
    writeSummaryCoverageReports(instance);
    writeOutdatedRuleCountReportForWallboard(instance);
    writeCleanupReports();

  }

  public void writeUserFacingReports() {

    for (SupportedStandard supportedStandard : SupportedStandard.values()) {
      Standard standard = supportedStandard.getStandard();

      if (standard instanceof CustomerReport) {

        CustomerReport customerReport = (CustomerReport) standard;

        LOGGER.info("Getting user-facing report for " + customerReport.getStandardName());

        String report = customerReport.getHtmlReport(RuleManager.NEMO);
        if (!Strings.isNullOrEmpty(report)) {
          report = css + report;
          writeFile(COVERAGE_DIR.concat(customerReport.getStandardName()).concat(HTML).toLowerCase(), report);

        }
      } else if (standard instanceof AbstractMultiLanguageStandard) {

        AbstractMultiLanguageStandard multiLanguageStandard = (AbstractMultiLanguageStandard)standard;

        LOGGER.info("Getting user-facing report for " + multiLanguageStandard.getStandardName());

        Map<Language, String> reports = multiLanguageStandard.getHtmlLanguageReports(RuleManager.NEMO);
        for (Map.Entry<Language, String> entry : reports.entrySet()) {

          String standardName = multiLanguageStandard.getStandardName();
          String fileName = COVERAGE_DIR.concat(standardName).concat("/").concat(entry.getKey().getSq())
                  .concat("_").concat(standardName).concat("_coverage.html").toLowerCase();

          writeFile(fileName, css + entry.getValue());
        }
      }
    }

    writeRulesInLanguagesReports(RuleManager.NEMO);
  }

  public void writeSummaryCoverageReports(String instance) {

    LOGGER.info("Getting summary coverage report  on " + instance);


    StringBuilder sb = new StringBuilder();
    for (SupportedStandard supportedStandard : SupportedStandard.values()) {

      Standard standard = supportedStandard.getStandard();
      if (standard instanceof AbstractReportableStandard) {
        String report = ((AbstractReportableStandard)standard).getSummaryReport(instance);
        if (report != null) {
          sb.append(report).append("\n\n");
        }
      }
    }
    writeFile(COVERAGE_DIR.concat("summary_coverage_reports.txt"), sb.toString());

  }

  public void writeDetailedCoverageReports(String instance) {

    for (SupportedStandard supportedStandard : SupportedStandard.values()) {

      if (supportedStandard.getStandard() instanceof AbstractReportableStandard) {

        AbstractReportableStandard standard = (AbstractReportableStandard) supportedStandard.getStandard();

        LOGGER.info("Getting detailed coverage report for " + standard.getStandardName() + " on " + instance);

        writeFile(COVERAGE_DIR.concat(standard.getStandardName()).concat("_coverage.txt").toLowerCase(), standard.getReport(instance));
      }
    }
  }

  protected static void writeFile(String fileName, String content) {

    String path = fileName.replaceAll(" ", "_");
    File file = new File(BASE_DIR + path);
    File parent = file.getParentFile();
    if (parent != null) {
      parent.mkdirs();
    }

    try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {

      writer.println(content);

      LOGGER.info("Output: " + path);

    } catch (FileNotFoundException|UnsupportedEncodingException e) {
      throw new RuleException(e);
    }
  }

  public void writeCleanupReports(){
    for (SupportedStandard supportedStandard : SupportedStandard.values()) {

      if (supportedStandard.getStandard() instanceof CleanupReport) {

        String report = ((CleanupReport)supportedStandard.getStandard()).generateCleanupReport();
        writeFile(supportedStandard.getStandard().getStandardName().toLowerCase() + "_cleanup.txt", report);
      }
    }
  }

  public void writeToolInternalReports(String instance) {

    for (SupportedStandard supportedStandard : SupportedStandard.values()) {

      if (supportedStandard.getStandard() instanceof AbstractReportableExternalTool) {
        AbstractReportableExternalTool externalTool = (AbstractReportableExternalTool) supportedStandard.getStandard();

        LOGGER.info("Getting deprecated, unspecified ids for " + externalTool.getStandardName() + " on " + instance);

        String report = externalTool.getDeprecationReport(instance);
        writeFile("deprecated_" + externalTool.getStandardName().toLowerCase() + "_ids.txt", report);

        report = externalTool.getUnspecifiedReport();
        writeFile("unspecified_" + externalTool.getStandardName().toLowerCase() + "_ids.txt", report);
      }
    }
  }

  public void writeOutdatedRuleCountReportForWallboard(String instance) {

    JSONArray results = new JSONArray();

    for (Language language : Language.values()) {
      int count = writeOutdatedRulesReport(language, instance);
      if (count > 0) {
        Map<String, Object> map = new HashMap<>();
        map.put("outdated", count);
        map.put("name",language.getRspec());
        results.add(map);
      }
    }
    writeFile("outdated/summary.json", results.toJSONString());
  }

  /**
   *
   * @param language the language to check
   * @param instance the SonarQube instance against which to run the check
   * @return Number of outdated rules
   */
  public int writeOutdatedRulesReport(Language language, String instance) {

    if (language == null || Strings.isNullOrEmpty(instance)) {
      throw new RuleException("Language and instance required to write outdated report. Received " +
              language + " " + instance);
    }

    LOGGER.info("Getting outdated rules report for " + language.getRspec() + " on " + instance);

    String fileName = "outdated/".concat(language.getSq()).concat("_outdated_rules.txt").toLowerCase();

    List<Rule> rspec = getCoveredRulesForLanguage(language);
    Map<String, Rule> rspecRules = mapRulesByKey(rspec);

    List<Rule> sqCovered = RuleMaker.getRulesFromSonarQubeForLanguage(language, instance);
    List<Rule> specNotFoundForLegacyKey = standardizeKeysAndIdentifyMissingSpecs(language, sqCovered);

    int notAlike = 0;
    StringBuilder sb = new StringBuilder();
    for (Rule sqRule : sqCovered) {

      if (specNotFoundForLegacyKey.contains(sqRule) || language.getSqCommon().equals(sqRule.getRepo())) {
        continue;
      }

      String key = sqRule.getKey();
      Rule rspecRule = rspecRules.remove(key);
      if (rspecRule == null) {
        rspecRule = RuleMaker.getRuleByKey(key, language.getRspec());
      }
      if (rspecRule != null) {
        RuleComparison rc = new RuleComparison(rspecRule, sqRule);
        if (rc.compare() != 0) {
          notAlike++;
          sb.append("\n").append(rc);
        }
      }
    }
    if (notAlike > 0) {
      sb.insert(0, "\n").insert(0, instance).insert(0,"\nOn ");
      sb.insert(0, sqCovered.size());
      sb.insert(0, " different out of ");
      sb.insert(0, notAlike);

      writeFile(fileName, sb.toString());

    } else {
      writeFile(fileName, "No differences found");
    }
    return notAlike;
  }

  public void writeRulesInLanguagesReports(String instance) {
    for (Language language : Language.values()) {
      writeRulesInLanguageReport(language, instance);
    }
  }

  public void writeRulesInLanguageReport(Language language, String instance) {

    LOGGER.info("Getting list-of-rules report for " + language.getRspec());

    List<Rule> rules = RuleMaker.getRulesFromSonarQubeForLanguage(language, instance);
    if (rules.isEmpty()) {
      return;
    }

    String fileName = COVERAGE_DIR.concat("rules_in_").concat(language.getSq()).concat(HTML).toLowerCase();

    Map<Rule.Severity, List<Rule>> severityMap = sortRulesBySeverity(rules);

    writeFile(fileName, assembleLanguageRuleReport(language, instance, severityMap));

  }

  public void writeSingleReport(Language language, String instance, AbstractReportableStandard standard, ReportType reportType) {

    String reportName = standard.getStandardName() + "_"
            + (language == null ? "" : (language.name() + "_"))
            + reportType.name()
            + (reportType.isInternal() ? ".txt" : ".html");

    if (language == null) {
      LOGGER.info("null language found. Language may be required for the requested report");
    }

    switch (reportType) {
      case INTERNAL_COVERAGE_SUMMARY:
        writeFile(reportName, standard.getSummaryReport(instance));
        break;
      case HTML:
        if (standard instanceof CustomerReport) {
          writeFile(reportName,((CustomerReport)standard).getHtmlReport(instance));
        } else {
          writeFile(reportName,((AbstractMultiLanguageStandard)standard).getHtmlLanguageReport(instance, language));
        }
        break;
      case DEPRECATION:
        writeFile(reportName, ((AbstractReportableExternalTool)standard).getDeprecationReport(instance));
        break;
      case UNSPECIFIED:
        writeFile(reportName, ((AbstractReportableExternalTool)standard).getUnspecifiedReport());
        break;
      case INTERNAL_COVERAGE:
        // fallthrough...
      default:
        writeFile(reportName, standard.getReport(instance));
        break;
    }

  }

  protected String assembleLanguageRuleReport(Language language, String instance, Map<Rule.Severity, List<Rule>> severityMap) {

    StringBuilder sb = new StringBuilder();
    sb.append(css);
    sb.append("<h2>Available ").append(language.getRspec()).append(" rules</h2>\n");

    for (Rule.Severity severity : Rule.Severity.values()) {
      List<Rule> severityList = severityMap.get(severity);
      if (severityList == null) {
        continue;
      }

      sb.append("<h3>").append(severity.name()).append("</h3><p>");
      for (Rule rule : severityList) {
        sb.append(Utilities.getNemoLinkedRuleReference(instance, rule));
      }
      sb.append("</p>");
    }
    return sb.toString();
  }

  protected Map<Rule.Severity, List<Rule>> sortRulesBySeverity(List<Rule> rules) {

    Map<Rule.Severity, List<Rule>> severityMap = new EnumMap<>(Rule.Severity.class);

    for (Rule rule : rules) {
      List<Rule> severityList = severityMap.get(rule.getSeverity());
      if (severityList == null) {
        severityList = new ArrayList<>();
        severityMap.put(rule.getSeverity(), severityList);
      }
      severityList.add(rule);
    }
    return severityMap;
  }

}
