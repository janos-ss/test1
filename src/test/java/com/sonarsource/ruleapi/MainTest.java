/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.domain.SonarPediaJsonFile;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.sonarsource.ruleapi.externalspecifications.SupportedStandard;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class MainTest {

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  @Rule
  public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

  @Test
  public void testHappyPath() {
    String[] args = {"single_report", "-report", "html", "-tool", "findbugs", "-language", "java"};

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

    String[] args = {"single_report", "-report", "html", "-tool", "cwe", "-language", "C"};

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
  public void testCredentialsProvidedBothMissing() {
    String[] args = {"single_report", "-tool", "findbugs"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    assertThat(Main.credentialsProvided(settings)).isFalse();
  }

  @Test
  public void testCredentialsProvidedBothProvided() {
    String[] args = {"single_report", "-login", "foo", "-password", "bar"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    assertThat(Main.credentialsProvided(settings)).isTrue();
  }

  @Test
  public void testCredentialsProvidedLoginProvided() {
    String[] args = {"single_report", "-login", "foo"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, null, args);

    assertThat(Main.credentialsProvided(settings)).isFalse();
  }

  @Test
  public void testCredentialsProvidedPasswdProvided() {
    String[] args = {"single_report", "-password", "bar"};

    Main.Settings settings = new Main.Settings();
    new JCommander(settings, args);

    assertThat(Main.credentialsProvided(settings)).isFalse();
  }

  @Test
  public void testInitGenerationAndUpdateOfDescriptionFile() throws Exception {

    String[] initArguments = {"init", "-language", "java", "-baseDir", testFolder.getRoot().getAbsolutePath()};
    Main.main(initArguments);
    assertThat(testFolder.getRoot().list().length).isEqualTo(2);

    File rulesDir = Stream.of(testFolder.getRoot().list())
      .map(name -> new File(testFolder.getRoot(), name))
      .filter(File::isDirectory)
      .collect(Collectors.toList())
      .get(0);

    String[] generateArguments = {"generate", "-rule", "RSPEC-4242", "-baseDir", testFolder.getRoot().getAbsolutePath()};
    Main.main(generateArguments);
    assertThat(rulesDir.list().length).isEqualTo(2);

    String[] updateArguments = {"update", "-baseDir", testFolder.getRoot().getAbsolutePath()};
    Main.main(updateArguments);
    SonarPediaJsonFile spjf = SonarPediaJsonFile.findSonarPediaFile(testFolder.getRoot());
    assertThat(spjf.getUpdateTimeStamp()).isNotNull();
  }

  public void testRequiredLanguage() throws Exception {
    String[] arguments = {"single_report", "-tool", "CHECKSTYLE", "-report", "INTERNAL_COVERAGE"};

    Main.main(arguments);

    assertThat(systemOutRule.getLog()).contains("USAGE:");
  }

  @Test
  public void testTooManyLanguage() throws Exception {
    String[] arguments = {"single_report", "-language", "JAVA", "JAVASCRIPT", "-tool", "CHECKSTYLE", "-report", "INTERNAL_COVERAGE"};

    Main.main(arguments);

    assertThat(systemOutRule.getLog()).contains("USAGE:");
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

    String[] args = {};
    Main.main(args);

    assertThat(systemOutRule.getLog()).contains("USAGE");
  }

  @Test
  public void testOption() {
    assertThat(Main.Option.fromString("single_report")).isEqualTo(Main.Option.SINGLE_REPORT);
  }


  @Test
  public void testOptionLanguageRequirement() {
    assertThat(Main.Option.LanguageRequirement.NO_LANGUAGE.isCompliant(null)).isTrue();
    assertThat(Main.Option.LanguageRequirement.NO_LANGUAGE.isCompliant(Arrays.asList())).isTrue();
    assertThat(Main.Option.LanguageRequirement.NO_LANGUAGE.isCompliant(Arrays.asList("foo"))).isFalse();

    assertThat(Main.Option.LanguageRequirement.ZERO_OR_ONE_LANGUAGE.isCompliant(null)).isTrue();
    assertThat(Main.Option.LanguageRequirement.ZERO_OR_ONE_LANGUAGE.isCompliant(Arrays.asList())).isTrue();
    assertThat(Main.Option.LanguageRequirement.ZERO_OR_ONE_LANGUAGE.isCompliant(Arrays.asList("foo"))).isTrue();
    assertThat(Main.Option.LanguageRequirement.ZERO_OR_ONE_LANGUAGE.isCompliant(Arrays.asList("foo", "bar"))).isFalse();

    assertThat(Main.Option.LanguageRequirement.ONE_AND_ONLY_ONE_LANGUAGE.isCompliant(null)).isFalse();
    assertThat(Main.Option.LanguageRequirement.ONE_AND_ONLY_ONE_LANGUAGE.isCompliant(Arrays.asList())).isFalse();
    assertThat(Main.Option.LanguageRequirement.ONE_AND_ONLY_ONE_LANGUAGE.isCompliant(Arrays.asList("foo"))).isTrue();
    assertThat(Main.Option.LanguageRequirement.ONE_AND_ONLY_ONE_LANGUAGE.isCompliant(Arrays.asList("foo", "bar"))).isFalse();

    assertThat(Main.Option.LanguageRequirement.ONE_OR_MORE_LANGUAGE.isCompliant(null)).isFalse();
    assertThat(Main.Option.LanguageRequirement.ONE_OR_MORE_LANGUAGE.isCompliant(Arrays.asList())).isFalse();
    assertThat(Main.Option.LanguageRequirement.ONE_OR_MORE_LANGUAGE.isCompliant(Arrays.asList("foo"))).isTrue();
    assertThat(Main.Option.LanguageRequirement.ONE_OR_MORE_LANGUAGE.isCompliant(Arrays.asList("foo", "bar"))).isTrue();
  }


  @Test
  public void testPrintHelpMessage() {
    String[] args = {"--help"};
    Main.main(args);

    assertThat(systemOutRule.getLog()).contains("USAGE");
  }

  @Test(expected = ParameterException.class)
  public void shouldFailToGenerateCweReportForUnknownLanguage() throws Exception {
    Main.main(new String[] {"single_report", "-tool", "cwe", "-report", "html", "-language", "polop"});
  }

  @Test
  public void shouldDoNothingOnUnrecognizedOption() throws Exception {
    Main.main(new String[] {"polop"});
  }
}
