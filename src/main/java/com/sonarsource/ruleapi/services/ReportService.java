/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.ReportAndBadge;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleComparison;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableExternalTool;
import com.sonarsource.ruleapi.externalspecifications.AbstractReportableStandard;
import com.sonarsource.ruleapi.externalspecifications.Badgable;
import com.sonarsource.ruleapi.externalspecifications.BadgableMultiLanguage;
import com.sonarsource.ruleapi.externalspecifications.CleanupReport;
import com.sonarsource.ruleapi.externalspecifications.CustomerReport;
import com.sonarsource.ruleapi.externalspecifications.ReportType;
import com.sonarsource.ruleapi.externalspecifications.Standard;
import com.sonarsource.ruleapi.externalspecifications.SupportedStandard;
import com.sonarsource.ruleapi.externalspecifications.specifications.AbstractMultiLanguageStandard;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.services.badge.BadgeGenerator;
import com.sonarsource.ruleapi.utilities.Language;
import org.fest.util.Strings;
import org.json.simple.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;


public class ReportService extends RuleManager {

  private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());
  protected static final String BASE_DIR = "target/reports/";
  protected static final String COVERAGE_DIR = "coverage/";
  protected static final String BADGE_DIR = "badges/";
  protected static final String HTML = ".html";

  private static final BadgeGenerator BADGER = new BadgeGenerator();


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
      String standardName = standard.getStandardName();

      if (standard instanceof BadgableMultiLanguage) {
        BadgableMultiLanguage multiLanguageStandard = (BadgableMultiLanguage) standard;

        LOGGER.info("Getting user-facing report for " + standardName);

        Map<Language, ReportAndBadge> reports = multiLanguageStandard.getHtmlLanguageReports(RuleManager.SONARQUBE_COM);
        for (Map.Entry<Language, ReportAndBadge> entry : reports.entrySet()) {
          writeReportAndBadge(standardName, entry);
        }

      } else if (standard instanceof CustomerReport) {

        CustomerReport customerReport = (CustomerReport) standard;

        LOGGER.info("Getting user-facing report for " + standardName);

        writeCustomerReport(standardName, customerReport.getHtmlReport(RuleManager.SONARQUBE_COM));

        if (customerReport instanceof Badgable) {
          Badgable badgable = (Badgable) customerReport;
          writeCustomerBadge(standardName, badgable.getStandardName(), badgable.getBadgeValue(RuleManager.SONARQUBE_COM));
        }
      }
    }
  }

  protected void writeCustomerBadge(String standardName, String badgeLabel, String badgeValue) {

    if (!Strings.isNullOrEmpty(badgeLabel) && !Strings.isNullOrEmpty(badgeValue)) {
      writeFile(BADGE_DIR.concat(standardName).concat(".svg").toLowerCase(), BADGER.getBadge(badgeLabel, badgeValue));
    }
  }

  protected void writeCustomerReport(String standardName, String report) {

    if (!Strings.isNullOrEmpty(report)) {
      writeFile(COVERAGE_DIR.concat(standardName).concat(HTML).toLowerCase(Locale.ENGLISH), report);
    }
  }

  protected void writeReportAndBadge(String standardName, Map.Entry<Language, ReportAndBadge> entry) {

    String report = entry.getValue().getReport();
    String badge = entry.getValue().getBadge();

    String baseFileName = entry.getKey().getSq().concat("_").concat(standardName);

    if (!Strings.isNullOrEmpty(report)) {
      String fileName = COVERAGE_DIR.concat(standardName).concat("/").concat(baseFileName).concat("_coverage.html").toLowerCase();
      writeFile(fileName, report);
    }
    if (!Strings.isNullOrEmpty(badge)) {
      String fileName = BADGE_DIR.concat(baseFileName).concat(".svg").toLowerCase();
      writeFile(fileName, badge);
    }
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

        writeFile(COVERAGE_DIR.concat(standard.getStandardName()).concat("_coverage.txt").toLowerCase(Locale.ENGLISH), standard.getReport(instance));
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

    Map<String, Rule> rspecRules = getCoveredRulesForLanguage(language);

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
          writeFile(reportName,((AbstractMultiLanguageStandard)standard).getHtmlLanguageReport(instance, language).getReport());
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

  public static final String TABLE_OPEN = "<table class=\"table table-striped table-condensed table-hover\">\n";

  public static final String HEADER_TEMPLATE = "<!DOCTYPE html>\n" +
          "<html lang=\"en\">\n" +
          "  <head>\n" +
          "    <meta charset=\"utf-8\">\n" +
          "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
          "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
          "\n" +
          "    <title>%1$s %2$s</title>\n" +
          "\n" +
          "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" " +
          "          integrity=\"sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7\" crossorigin=\"anonymous\">\n" +
          "\n" +
          "    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->\n" +
          "    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->\n" +
          "    <!--[if lt IE 9]>\n" +
          "      <script src=\"https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js\"></script>\n" +
          "      <script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script>\n" +
          "    <![endif]-->\n" +
          "\n" +
          "<style>\n" +
          ".header-counter {\n" +
          "    font-size: 24px;\n" +
          "}\n" +
          "#checkmark {\n" +
          "    display:inline-block;\n" +
          "    width: 15px;\n" +
          "    height:15px;\n" +
          "    -ms-transform: rotate(35deg); /* IE 9 */\n" +
          "    -webkit-transform: rotate(35deg); /* Chrome, Safari, Opera */\n" +
          "    transform: rotate(35deg);\n" +
          "}\n" +
          "#checkmark:before{\n" +
          "  content:\"\"; position: absolute; width:3px; height:12px; background-color:#85bb43; left:7px; top:2px;\n" +
          "}\n" +
          "#checkmark:after{\n" +
          "  content:\"\"; position: absolute; width:5px; height:3px; background-color:#85bb43; left:4px; top:11px;\n" +
          "}\n" +
          "[class^=icon-severity-] {\n" +
          "  display:inline-block;vertical-align:top;width:16px;height:16px;background-size:14px 14px;background:no-repeat 50%%\n" +
          "}\n" +
          ".icon-severity-Blocker {\n" +
          "  background-image:url(\"data:image/svg+xml;charset=utf-8,%%3Csvg width='14' height='14' xmlns='http://www.w3.org/2000/svg' fill-rule='evenodd' clip-rule='evenodd' stroke-linejoin='round' stroke-miterlimit='1.414'%%3E%%3Cpath d='M7 1c1.09 0 2.092.268 3.012.805a5.96 5.96 0 0 1 2.183 2.183C12.732 4.908 13 5.91 13 7s-.268 2.092-.805 3.012a5.96 5.96 0 0 1-2.183 2.183C9.092 12.732 8.09 13 7 13s-2.092-.268-3.012-.805a5.96 5.96 0 0 1-2.183-2.183C1.268 9.092 1 8.09 1 7s.268-2.092.805-3.012a5.96 5.96 0 0 1 2.183-2.183A5.863 5.863 0 0 1 7 1zm1 9.742V9.258a.258.258 0 0 0-.07-.184A.226.226 0 0 0 7.758 9h-1.5a.247.247 0 0 0-.18.078.247.247 0 0 0-.078.18v1.484c0 .068.026.128.078.18a.247.247 0 0 0 .18.078h1.5a.23.23 0 0 0 .172-.074c.047-.05.07-.11.07-.184zm-.016-2.687l.14-4.852a.15.15 0 0 0-.077-.14A.284.284 0 0 0 7.86 3H6.14a.284.284 0 0 0-.187.063.152.152 0 0 0-.078.14l.133 4.852c0 .052.026.097.078.136.052.04.115.06.187.06H7.72c.072 0 .133-.02.182-.06a.17.17 0 0 0 .082-.13z' fill='%%23D43340' fill-rule='nonzero'/%%3E%%3C/svg%%3E\")\n" +
          "}\n" +
          ".icon-severity-Critical {\n" +
          "  background-image:url(\"data:image/svg+xml;charset=utf-8,%%3Csvg width='14' height='14' xmlns='http://www.w3.org/2000/svg' fill-rule='evenodd' clip-rule='evenodd' stroke-linejoin='round' stroke-miterlimit='1.414'%%3E%%3Cpath d='M11.03 6.992a.47.47 0 0 0-.14-.35L7.353 3.1a.475.475 0 0 0-.352-.14c-.14 0-.25.046-.35.14L3.11 6.64a.474.474 0 0 0-.14.35c0 .14.047.258.14.352l.71.71c.095.094.21.14.353.14a.47.47 0 0 0 .35-.14L6 6.58v3.92c0 .135.05.253.148.352.1.098.217.148.352.148h1c.135 0 .253-.05.352-.148A.48.48 0 0 0 8 10.5V6.578l1.477 1.477c.1.1.216.148.35.148.137 0 .254-.05.353-.148l.71-.71a.48.48 0 0 0 .14-.353zM13 7c0 1.09-.268 2.092-.805 3.012a5.96 5.96 0 0 1-2.183 2.183C9.092 12.732 8.09 13 7 13s-2.092-.268-3.012-.805a5.96 5.96 0 0 1-2.183-2.183C1.268 9.092 1 8.09 1 7s.268-2.092.805-3.012a5.96 5.96 0 0 1 2.183-2.183C4.908 1.268 5.91 1 7 1s2.092.268 3.012.805a5.96 5.96 0 0 1 2.183 2.183C12.732 4.908 13 5.91 13 7z' fill='%%23D43340' fill-rule='nonzero'/%%3E%%3C/svg%%3E\")\n" +
          "}\n" +
          ".icon-severity-Major {\n" +
          "  background-image:url(\"data:image/svg+xml;charset=utf-8,%%3Csvg width='14' height='14' xmlns='http://www.w3.org/2000/svg' fill-rule='evenodd' clip-rule='evenodd' stroke-linejoin='round' stroke-miterlimit='1.414'%%3E%%3Cpath d='M10.102 8.898l.796-.796c.1-.1.15-.217.15-.352a.48.48 0 0 0-.15-.352L7.352 3.852A.481.481 0 0 0 7 3.702a.48.48 0 0 0-.352.15L3.102 7.398c-.1.1-.15.217-.15.352 0 .135.05.253.15.352l.796.796c.1.1.217.15.352.15a.48.48 0 0 0 .352-.15L7 6.5l2.398 2.398c.1.1.217.15.352.15a.48.48 0 0 0 .352-.15zM13 7c0 1.09-.268 2.092-.805 3.012a5.96 5.96 0 0 1-2.183 2.183C9.092 12.732 8.09 13 7 13s-2.092-.268-3.012-.805a5.96 5.96 0 0 1-2.183-2.183C1.268 9.092 1 8.09 1 7s.268-2.092.805-3.012a5.96 5.96 0 0 1 2.183-2.183C4.908 1.268 5.91 1 7 1s2.092.268 3.012.805a5.96 5.96 0 0 1 2.183 2.183C12.732 4.908 13 5.91 13 7z' fill='%%23D43340' fill-rule='nonzero'/%%3E%%3C/svg%%3E\")\n" +
          "}\n" +
          ".icon-severity-Minor {\n" +
          "  background-image:url(\"data:image/svg+xml;charset=utf-8,%%3Csvg width='14' height='14' xmlns='http://www.w3.org/2000/svg' fill-rule='evenodd' clip-rule='evenodd' stroke-linejoin='round' stroke-miterlimit='1.414'%%3E%%3Cpath d='M7.352 10.148l3.546-3.546c.1-.1.15-.217.15-.352a.48.48 0 0 0-.15-.352l-.796-.796a.481.481 0 0 0-.352-.15.48.48 0 0 0-.352.15L7 7.5 4.602 5.102a.481.481 0 0 0-.352-.15.48.48 0 0 0-.352.15l-.796.796c-.1.1-.15.217-.15.352 0 .135.05.253.15.352l3.546 3.546c.1.1.217.15.352.15a.48.48 0 0 0 .352-.15zM13 7c0 1.09-.268 2.092-.805 3.012a5.96 5.96 0 0 1-2.183 2.183C9.092 12.732 8.09 13 7 13s-2.092-.268-3.012-.805a5.96 5.96 0 0 1-2.183-2.183C1.268 9.092 1 8.09 1 7s.268-2.092.805-3.012a5.96 5.96 0 0 1 2.183-2.183C4.908 1.268 5.91 1 7 1s2.092.268 3.012.805a5.96 5.96 0 0 1 2.183 2.183C12.732 4.908 13 5.91 13 7z' fill='%%2387BB43' fill-rule='nonzero'/%%3E%%3C/svg%%3E\")\n" +
          "}\n" +
          ".icon-severity-Info {\n" +
          "  background-image:url(\"data:image/svg+xml;charset=utf-8,%%3Csvg width='14' height='14' xmlns='http://www.w3.org/2000/svg' fill-rule='evenodd' clip-rule='evenodd' stroke-linejoin='round' stroke-miterlimit='1.414'%%3E%%3Cpath d='M11.03 7.008a.478.478 0 0 0-.14-.352l-.71-.71a.475.475 0 0 0-.352-.14.47.47 0 0 0-.35.14L8 7.42V3.5a.486.486 0 0 0-.148-.352A.48.48 0 0 0 7.5 3h-1a.486.486 0 0 0-.352.148A.48.48 0 0 0 6 3.5v3.922L4.523 5.945a.477.477 0 0 0-.35-.148.484.484 0 0 0-.353.148l-.71.71a.48.48 0 0 0-.14.353c0 .14.046.258.14.35l3.538 3.54c.094.094.21.14.352.14.14 0 .258-.046.352-.14l3.54-3.54a.478.478 0 0 0 .14-.35zM13 7c0 1.09-.268 2.092-.805 3.012a5.96 5.96 0 0 1-2.183 2.183C9.092 12.732 8.09 13 7 13s-2.092-.268-3.012-.805a5.96 5.96 0 0 1-2.183-2.183C1.268 9.092 1 8.09 1 7s.268-2.092.805-3.012a5.96 5.96 0 0 1 2.183-2.183C4.908 1.268 5.91 1 7 1s2.092.268 3.012.805a5.96 5.96 0 0 1 2.183 2.183C12.732 4.908 13 5.91 13 7z' fill='%%2387BB43' fill-rule='nonzero'/%%3E%%3C/svg%%3E\")\n" +
          "}\n" +
          "</style>\n" +
          "  </head>\n" +
          "  <body><a name='top' id='top'></a>\n" +
          "\n" +
          "<div style=\"background-color: rgb(25, 25, 25);height:66px;padding-left:6px\">\n" +
          "  <img src=\"http://dist.sonarsource.com/reports/sonarsource_white_256px.png\" width='256'/>\n" +
          "</div>\n" +
          "\n" +
          "<div class=\"container\">\n";

  public static final String FOOTER_TEMPLATE = "\n<br/>\n" +
          "<p><a href='#top'>Back to the top</a></p>" +
          "\n" +
          "    <footer class=\"footer\">\n" +
          "      <p class=\"small\">Powered by <a href=\"http://www.sonarsource.com/\">SonarSource SA</a></p>\n" +
          "    </footer>\n" +
          "  </div><!-- container -->" +
          "\n" +
          "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>\n" +
          "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\" " +
          "            integrity=\"sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS\" crossorigin=\"anonymous\"></script>\n" +
          "  </body>\n" +
          "</html>\n";

}
