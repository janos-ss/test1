/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.ReportAndBadge;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.utilities.Language;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static com.sun.corba.se.spi.activation.IIOP_CLEAR_TEXT.value;
import static org.fest.assertions.Assertions.assertThat;


public class ReportServiceTest {

  private ReportService rs = new ReportService();

  @Test
  public void writeReportAndBadge(){

    String standardName = "foo";
    ReportAndBadge rab = new ReportAndBadge();

    Map<Language, ReportAndBadge> map = new HashMap<>();
    map.put(Language.ABAP, rab);
    Map.Entry<Language, ReportAndBadge> entry = map.entrySet().iterator().next();

    String baseFileName = entry.getKey().getSq().concat("_").concat(standardName);
    Path reportPath = Paths.get(ReportService.BASE_DIR.concat(ReportService.COVERAGE_DIR).concat(standardName).concat("/").concat(baseFileName).concat("_coverage.html"));
    Path badgePath = Paths.get(ReportService.BASE_DIR.concat(ReportService.BADGE_DIR).concat(baseFileName).concat(".svg").toLowerCase());


    try {
      Files.deleteIfExists(reportPath);
      Files.deleteIfExists(badgePath);
    } catch (IOException e) {
    }

    rs.writeReportAndBadge(standardName, entry);

    assertThat(Files.exists(reportPath)).isFalse();
    assertThat(Files.exists(badgePath)).isFalse();

    rab.setBadge("badge");
    rab.setReport("report");

    rs.writeReportAndBadge(standardName, entry);

    assertThat(Files.exists(reportPath)).isTrue();
    assertThat(Files.exists(badgePath)).isTrue();

  }

  @Test
  public void writeCustomerReport(){
    String standardName = "foo";

    Path reportPath = Paths.get(ReportService.BASE_DIR.concat(ReportService.COVERAGE_DIR).concat(standardName).concat(".html").toLowerCase(Locale.ENGLISH));
    try {
      Files.deleteIfExists(reportPath);
    } catch (IOException e) {
    }

    rs.writeCustomerReport(standardName, "");
    assertThat(Files.exists(reportPath)).isFalse();


    rs.writeCustomerReport(standardName, "foo");
    assertThat(Files.exists(reportPath)).isTrue();
  }

  @Test
  public void writeCustomerBadge(){
    String standardName = "foo";

    Path badgePath = Paths.get(ReportService.BASE_DIR.concat(ReportService.BADGE_DIR).concat(standardName).concat(".svg").toLowerCase(Locale.ENGLISH));
    try {
      Files.deleteIfExists(badgePath);
    } catch (IOException e) {
    }

    rs.writeCustomerBadge(standardName, null, null);
    assertThat(Files.exists(badgePath)).isFalse();

    rs.writeCustomerBadge(standardName, "label", null);
    assertThat(Files.exists(badgePath)).isFalse();

    rs.writeCustomerBadge(standardName, null, "value");
    assertThat(Files.exists(badgePath)).isFalse();

    rs.writeCustomerBadge(standardName, "label", "value");
    assertThat(Files.exists(badgePath)).isTrue();

  }

  @Test
  public void testNoOutdatedReport() {

    try {
      rs.writeOutdatedRulesReport(null, null);
      Assert.fail("An exception should have been thrown.");
    } catch (RuleException e) {

    }
    try {
      rs.writeOutdatedRulesReport(Language.C, null);
      Assert.fail("An exception should have been thrown.");
    } catch (RuleException e) {

    }
    try {
      rs.writeOutdatedRulesReport(null, "http://localhost:9000");
      Assert.fail("An exception should have been thrown.");
    } catch (RuleException e) {

    }
  }

}
