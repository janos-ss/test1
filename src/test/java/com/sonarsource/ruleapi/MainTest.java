/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi;

import com.beust.jcommander.JCommander;
import com.sonarsource.ruleapi.domain.RuleException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import com.sonarsource.ruleapi.externalspecifications.SupportedStandard;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;


public class MainTest {

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  @Test
  public void testHappyPath(){
    String[] args = {"single_report", "-report", "html", "-tool", "findbugs"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    try {
      Main.checkSingleReportInputs(settings);
    } catch (Exception e) {
      Assert.fail();
    }
  }

  @Test
  public void testCheckSingleReportInputsNominal() {

    String[] args = {"single_report", "-report", "html", "-tool", "cwe"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    assertThat(Main.checkSingleReportInputs(settings)).isEqualTo(SupportedStandard.CWE);

  }

  @Test(expected = RuleException.class)
  public void testCheckSingleReportNonReportableTool() {

    String[] args = {"single_report", "-tool", "rules_in_language"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    Main.checkSingleReportInputs(settings);

  }

  @Test(expected = RuleException.class)
  public void testCheckSingleReportInputsBadReport() {

    String[] args = {"single_report", "-report", "red"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    Main.checkSingleReportInputs(settings);

  }

  @Test(expected = RuleException.class)
  public void testCheckSingleReportInputsGoodReportMissingDetails() {

    String[] args = {"single_report", "-report", "html"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    Main.checkSingleReportInputs(settings);

  }

  @Test(expected = RuleException.class)
  public void testCheckSingleReportInputsBadTool() {

    String[] args = {"single_report", "-tool", "red"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    Main.checkSingleReportInputs(settings);

  }

  @Test(expected = RuleException.class)
  public void testCheckSingleReportInputsGoodToolMissingDetails() {

    String[] args = {"single_report", "-tool", "findbugs"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    Main.checkSingleReportInputs(settings);
  }

  @Test
  public void testCredentialsProvidedBothMissing(){
    String[] args = {"single_report", "-tool", "findbugs"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    assertThat(Main.credentialsProvided(settings)).isFalse();
  }

  @Test
  public void testCredentialsProvidedBothProvided(){
    String[] args = {"single_report", "-login", "foo", "-password", "bar"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    assertThat(Main.credentialsProvided(settings)).isTrue();
  }

  @Test
  public void testCredentialsProvidedLoginProvided(){
    String[] args = {"single_report", "-login", "foo"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    assertThat(Main.credentialsProvided(settings)).isFalse();
  }



  @Test
  public void testGenerationDescriptionFile() throws Exception{

    File outputDir = testFolder.newFolder();

    String[] args = { "generate"
            , "-rule", "S1543"
            , "-language", "java"
            , "-directory", outputDir.getAbsolutePath()
    };
    Main.main(args);

    assertThat(outputDir.listFiles().length).isGreaterThan(0);
  }

  @Test
  public void testCredentialsProvidedPasswdProvided(){
    String[] args = {"single_report", "-password", "bar"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    assertThat(Main.credentialsProvided(settings)).isFalse();
  }

  @Test(expected = RuleException.class)
  public void testCheckGenerateInputNoRule() {

    String[] args = {"single_report", "-report", "red"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    Main.checkGenerateInput(settings);
  }

  @Test
  public void testNoOption() {

    PrintStream original = System.out;

    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));

    String[] args = { };
    Main.main(args);

    assertThat(outContent.toString()).contains("USAGE");

    System.setOut(original);
  }

  @Test
  public void testOption() {
    assertThat(Main.Option.fromString("single_report")).isEqualTo(Main.Option.SINGLE_REPORT);
  }

  @Test
  public void testPrintHelpMessage() {

    PrintStream original = System.out;

    ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    System.setOut(new PrintStream(outContent));

    Main.printHelpMessage();
    assertThat(outContent.toString()).contains("USAGE");

    System.setOut(original);
  }

  @Test(expected = NullPointerException.class)
  public void shouldFailToGenerateCweReportForUnknownLanguage() throws Exception {
    Main.main(new String[] { "single_report", "-tool", "cwe", "-report", "html", "-language", "polop" });
  }

  @Test
  public void shouldDoNothingOnUnrecognizedOption() throws Exception {
    Main.main(new String[] { "polop" });
  }
}
