/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */

package com.sonarsource.ruleapi.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.specifications.*;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.externalspecifications.*;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.domain.RuleComparison;
import com.sonarsource.ruleapi.domain.RuleException;
import org.fest.util.Strings;
import org.json.simple.JSONArray;


public class ReportService extends RuleManager {

  private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());
  private static final String COVERAGE_DIR = "target/reports/coverage/";

  private String css = "";


  public ReportService() {
    java.net.URL url = this.getClass().getResource("/services");
    if (url != null) {
      try {
        css = new java.util.Scanner(new File(url.getPath() + "/report.css"), "UTF8").useDelimiter("\\Z").next();
      } catch (FileNotFoundException e) {
        LOGGER.log(Level.WARNING, "CSS file not found", e);
      }
    }
  }

  public void generateRuleDescriptions(List<String> ruleKeys, String language) {
    if (ruleKeys != null) {
      for (String ruleKey : ruleKeys) {
        Rule rule = RuleMaker.getRuleByKey(ruleKey, language);
        writeFile(ruleKey + ".html", rule.getHtmlDescription());
      }
    }
  }

  public void writeInternalReports(String instance) {

    writeDeprecationReports(instance);
    writeDetailedCoverageReports(instance);
    writeSummaryCoverageReports(instance);
    writeOutdatedRuleCountReportForWallboard(instance);

  }

  public void writeUserFacingReports() {

    for (SupportedCodingStandard supportedStandard : SupportedCodingStandard.values()) {
      CodingStandard standard = supportedStandard.getCodingStandard();

      if (standard instanceof CustomerReport) {

        CustomerReport customerReport = (CustomerReport) standard;

        LOGGER.info("Getting user-facing report for " + customerReport.getStandardName());

        String report = customerReport.getHtmlReport(RuleManager.NEMO);
        if (!Strings.isNullOrEmpty(report)) {
          report = css + report;
          writeFile(COVERAGE_DIR.concat(customerReport.getStandardName()).concat(".html").toLowerCase(), report);

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
  }

  public void writeSummaryCoverageReports(String instance) {

    LOGGER.info("Getting summary coverage report  on " + instance);


    StringBuilder sb = new StringBuilder();
    for (SupportedCodingStandard supportedStandard : SupportedCodingStandard.values()) {

      CodingStandard standard = supportedStandard.getCodingStandard();
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

    for (SupportedCodingStandard supportedStandard : SupportedCodingStandard.values()) {

      if (supportedStandard.getCodingStandard() instanceof AbstractReportableStandard) {

        AbstractReportableStandard standard = (AbstractReportableStandard) supportedStandard.getCodingStandard();

        LOGGER.info("Getting detailed coverage report for " + standard.getStandardName() + " on " + instance);

        writeFile(COVERAGE_DIR.concat(standard.getStandardName()).concat("_coverage.txt").toLowerCase(), standard.getReport(instance));
      }
    }
  }

  private static void writeFile(String fileName, String content) {
    if (content == null) {
      return;
    }

    PrintWriter writer = null;
    try {
      String path = fileName.replaceAll(" ", "_");

      File file = new File(path);
      File parent = file.getParentFile();
      if (parent != null) {
        parent.mkdirs();
      }

      writer = new PrintWriter(file, "UTF-8");
      writer.println(content);
      writer.close();

    } catch (FileNotFoundException e) {
      throw new RuleException(e);
    } catch (UnsupportedEncodingException e) {
      throw new RuleException(e);
    }

  }

  public void writeDeprecationReports(String instance) {

    for (SupportedCodingStandard supportedStandard : SupportedCodingStandard.values()) {

      if (supportedStandard.getCodingStandard() instanceof AbstractReportableExternalTool) {
        AbstractReportableExternalTool externalTool = (AbstractReportableExternalTool) supportedStandard.getCodingStandard();

        LOGGER.info("Getting deprecation report for " + externalTool.getStandardName() + " on " + instance);

        String report = externalTool.getDeprecationReport(instance);
        if (!Strings.isNullOrEmpty(report)) {
          writeFile("target/reports/deprecated_" + externalTool.getStandardName().toLowerCase() + "_ids.txt", report);
        }
      }
    }
  }

  public void writeOutdatedRuleCountReportForWallboard(String instance) {

    JSONArray results = new JSONArray();

    for (Language language : Language.values()) {
      int count = writeOutdatedRulesReport(language, instance);
      if (count > 0) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("outdated", count);
        map.put("name",language.getRspec());
        results.add(map);
      }
    }
    writeFile("target/reports/outdated/summary.json", results.toJSONString());
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

    String fileName = "target/reports/outdated/".concat(language.getSq()).concat("_outdated_rules.txt").toLowerCase();

    List<Rule> rspec = getCoveredRulesForLangauge(language);
    Map<String, Rule> rspecRules = mapRulesByKey(rspec);

    List<Rule> sqCovered = getImplementedRulesForLanguage(language, instance);
    List<Rule> specNotFoundForLegacyKey = standardizeKeysAndIdentifyMissingSpecs(language, sqCovered);

    int notAlike = 0;
    StringBuilder sb = new StringBuilder();
    for (Rule sqRule : sqCovered) {

      if (specNotFoundForLegacyKey.contains(sqRule)) {
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

  public void writeReportsWithOrchestrator() {

    String url = startOrchestrator();

    writeInternalReports(url);

    stopOrchestrator();
  }

}
