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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.sonar.orchestrator.Orchestrator;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.specifications.*;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.externalspecifications.*;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.domain.RuleComparison;
import com.sonarsource.ruleapi.domain.RuleException;

public class ReportService extends RuleManager {

  private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());
  private static final String COVERAGE_DIR = "target/reports/coverage/";

  private String css = "<style>\n" +
          "  * { font-family: sans-serif; }\n" +
          "  h2 {\n" +
          "    margin: 20px 0 20px 0;\n" +
          "    font-weight: 500;\n" +
          "    }\n" +
          "  table {\n" +
          "    width: 100%;\n" +
          "    border-collapse: collapse;\n" +
          "    }\n" +
          "  td {\n" +
          "    vertical-align: top;\n" +
          "    line-height: 1.5;\n" +
          "    padding: 12px 6px;\n" +
          "    }\n" +
          "  td:first-child {\n" +
          "    width: 1px;\n" +
          "    padding-right: 24px;\n" +
          "    white-space: nowrap;\n" +
          "    }\n" +
          "  td:last-child a {\n" +
          "    margin-right: 5px;\n" +
          "  }\n" +
          "  tr:nth-child(even) td { background-color: #f7f7f7; }\n" +
          "  a {\n" +
          "    border-bottom: 1px solid #cae3f2;\n" +
          "    color: #236a97;\n" +
          "    font-weight: 300;\n" +
          "    text-decoration: none;\n" +
          "  }\n" +
          "  a:hover, a:focus { color: #4b9fd5; }\n" +
          "</style>\n";


  public void generateRuleDescriptions(List<String> ruleKeys, String language) {
    if (ruleKeys != null) {
      for (String ruleKey : ruleKeys) {
        Rule rule = RuleMaker.getRuleByKey(ruleKey, language);
        writeFile(ruleKey+".html", rule.getHtmlDescription());
      }
    }
  }

  public void writeAllReports(String instance) {

    writeFindBugsDeprecationReport(instance);
    writeDetailedCoverageReports(instance);
    writeSummaryCoverageReports(instance);
    writeCweCoverageReports();
    writeOutdatedRulesReports(instance);

  }

  public void writeCweCoverageReports() {

    for (Language language : Language.values()) {

      LOGGER.info("Getting CWE coverage report for " + language.getRspec());

      writeCweCoverageReport(language);
    }
  }

  public void writeCweCoverageReport(Language language) {

    Cwe cwe = new Cwe();
    cwe.setLanguage(language);
    String report = cwe.getReport(RuleManager.NEMO);
    if (report != null) {
      report = css + report;
      writeFile(COVERAGE_DIR + language.getSq()+"_cwe_coverage.html", report);
    }
  }

  public void writeSummaryCoverageReports(String instance) {

    LOGGER.info("Getting summary coverage report  on " + instance);


    StringBuilder sb = new StringBuilder();
    for (SupportedCodingStandard supportedStandard : SupportedCodingStandard.values()) {

      CodingStandard standard = supportedStandard.getCodingStandard();
      if (standard instanceof AbstractReportableStandard) {

        sb.append(((AbstractReportableStandard)standard).getSummaryReport(instance)).append("\n\n");
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

  private void writeFile(String fileName, String content) {
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

  public void writeFindBugsDeprecationReport(String instance) {
    LOGGER.info("Getting Findbugs deprecation report on " + instance);

    writeFile("target/reports/deprecated_findBugs_ids.txt",
            ((ExternalTool) SupportedCodingStandard.FINDBUGS.getCodingStandard()).getDeprecationReport(instance));
  }

  public void writeOutdatedRulesReports(String instance) {

    for (Language language : Language.values()) {
      writeOutdatedRulesReport(language, instance);
    }

  }

  public void writeOutdatedRulesReport(Language language, String instance) {

    LOGGER.info("Getting outdated rules report for " + language.getRspec() + " on " + instance);

    String fileName = "target/reports/outdated/".concat(language.getSq()).concat("_outdated_rules.txt").toLowerCase();

    List<Rule> rspec = getCoveredRulesForLangauge(language);
    Map<String, Rule> rspecRules = mapRulesByKey(rspec);

    List<Rule> sqCovered = getImplementedRulesForLanguage(language, instance);
    List<Rule> specNotFoundForLegacyKey = standardizeKeysAndIdentifyMissingSpecs(language, sqCovered);

    int notAlike = 0;
    StringBuilder sb = new StringBuilder();
    sb.append("\nOn ").append(instance).append("\n");
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
    if (sb.length() > 0 && notAlike > 0) {
      sb.insert(0, sqCovered.size());
      sb.insert(0, " different out of ");
      sb.insert(0, notAlike);

      writeFile(fileName, sb.toString());

    } else {
      writeFile(fileName, "No differences found");
    }
  }

  public void writeReportsWithOrchestrator() {

    String url = startOrchestrator();

    writeAllReports(url);

    stopOrchestrator();
  }

}
