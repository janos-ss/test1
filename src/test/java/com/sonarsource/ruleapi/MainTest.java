/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi;

import com.beust.jcommander.JCommander;
import com.sonarsource.ruleapi.domain.RuleException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.fest.assertions.Assertions.assertThat;


public class MainTest {

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  @Test
  public void testHappyPath(){
    String[] args = {"single_report", "-report", "html", "-tool", "findbugs"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, args);

    Main.checkSingleReportInputs( settings );
    assertThat(true).isTrue();

  }

  @Test(expected = RuleException.class)
  public void testCheckSingleReportInputsBadReport() {

    String[] args = {"single_report", "-report", "red"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, args);

    Main.checkSingleReportInputs(settings);

  }

  @Test(expected = RuleException.class)
  public void testCheckSingleReportInputsGoodReportMissingDetails() {

    String[] args = {"single_report", "-report", "html"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, args);

    Main.checkSingleReportInputs(settings);

  }

  @Test(expected = RuleException.class)
  public void testCheckSingleReportInputsBadTool() {

    String[] args = {"single_report", "-tool", "red"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, args);

    Main.checkSingleReportInputs(settings);

  }

  @Test(expected = RuleException.class)
  public void testCheckSingleReportInputsGoodToolMissingDetails() {

    String[] args = {"single_report", "-tool", "findbugs"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, args);

    Main.checkSingleReportInputs(settings);
  }

  @Test
  public void testCredentialsProvidedBothMissing(){
    String[] args = {"single_report", "-tool", "findbugs"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, args);

    assertThat(Main.credentialsProvided(settings)).isFalse();
  }

  @Test
  public void testCredentialsProvidedBothProvided(){
    String[] args = {"single_report", "-login", "foo", "-password", "bar"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, args);

    assertThat(Main.credentialsProvided(settings)).isTrue();
  }

  @Test
  public void testCredentialsProvidedLoginProvided(){
    String[] args = {"single_report", "-login", "foo"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, args);

    assertThat(Main.credentialsProvided(settings)).isFalse();
  }



  @Test
  public void testGenerationDescriptionFile() throws Exception{

    File outputDir = testFolder.newFolder();
    assertThat(outputDir.listFiles().length).isEqualTo(0);

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
    new JCommander(settings, args);

    assertThat(Main.credentialsProvided(settings)).isFalse();
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

}
