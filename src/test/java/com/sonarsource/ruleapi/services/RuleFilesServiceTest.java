/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.Profile;
import com.sonarsource.ruleapi.utilities.Language;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class RuleFilesServiceTest {

  @Rule
  public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().mute();

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  @Test
  public void testGenerateRuleFiles() throws Exception {
    File outputDir = testFolder.newFolder();

    RuleFilesService dfs = RuleFilesService.create(outputDir.toString(), Language.fromString("java"));

    List<String> listOfKeys = new ArrayList<>(2);
    listOfKeys.add("RSPEC-1234");
    listOfKeys.add("S1111");
    dfs.generateRuleFiles(listOfKeys);

    // must be 2 files per rule
    int supposedCount = listOfKeys.size() * 2 + 1;
    assertThat(outputDir.listFiles().length).isEqualTo(supposedCount);
    assertThat(systemOutRule.getLog()).contains(String.format("(%d)", supposedCount));
  }

  @Test
  public void testGenerateRuleDescriptionsNoRule() throws Exception {
    File outputDir = testFolder.newFolder();
    RuleFilesService rfs = RuleFilesService.create(outputDir.toString(), Language.fromString("java"));
    rfs.generateRuleFiles(null);
    assertThat(outputDir.listFiles().length).isEqualTo(0);
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGenerateRuleFilesUnknownRule() throws Exception {
    File outputDir = testFolder.newFolder();

    RuleFilesService dfs = RuleFilesService.create(outputDir.toString(), Language.fromString("java"));
    List<String> listOfKeys = new ArrayList<>(1);
    listOfKeys.add("foo");
    dfs.generateRuleFiles(listOfKeys);
    assertThat(outputDir.listFiles().length).isEqualTo(0);
  }


  @Test( expected = IllegalArgumentException.class )
  public void testGenerateRuleFilesEmptyLanguage() throws Exception {
    File outputDir = testFolder.newFolder();
    RuleFilesService.create(outputDir.toString(), Language.fromString(""));
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGenerateRuleFilesNullLanguage() throws Exception {
    File outputDir = testFolder.newFolder();
    RuleFilesService.create(outputDir.toString(), null);
  }


  @Test
  public void testGenerateRuleFilesWoSeverity() throws Exception {
    File outputDir = testFolder.newFolder();

    RuleFilesService dfs = RuleFilesService.create(outputDir.toString(), Language.fromString("java"));

    List<String> listOfKeys = new ArrayList<>(1);
    // this ticket is deprecated and wo severity
    listOfKeys.add("RSPEC-2297");

    dfs.generateRuleFiles(listOfKeys);

    // must be 2 files per rule
    int supposedCount = listOfKeys.size() * 2;
    assertThat(outputDir.listFiles().length).isEqualTo(supposedCount);
    assertThat(systemOutRule.getLog()).contains(String.format("(%d)", supposedCount));
    assertThat(systemOutRule.getLog()).contains("missing severity for rule");

  }

  @Test
  public void testUpdateRuleFiles() throws Exception {
    File outputDir = testFolder.newFolder();
    RuleFilesService rfs = RuleFilesService.create(outputDir.toString(), Language.fromString("java"));

    List<String> listOfKeys = new ArrayList<>(2);
    listOfKeys.add("RSPEC-1234");
    listOfKeys.add("S1111");
    rfs.generateRuleFiles(listOfKeys);

    // record checksums
    File S1234HtmlFile = new File(outputDir.getAbsolutePath() + File.separator + "S1234_java.html");
    assertThat(S1234HtmlFile.exists()).isTrue();
    long S1234HtmlChecksum = org.apache.commons.io.FileUtils.checksumCRC32(S1234HtmlFile);
    File S1234JsonFile = new File(outputDir.getAbsolutePath() + File.separator + "S1234_java.json");
    assertThat(S1234JsonFile.exists()).isTrue();
    long S1234JsonChecksum = org.apache.commons.io.FileUtils.checksumCRC32(S1234JsonFile);
    File S1111HtmlFile = new File(outputDir.getAbsolutePath() + File.separator + "S1111_java.html");
    assertThat(S1111HtmlFile.exists()).isTrue();
    long S1111HtmlChecksum = org.apache.commons.io.FileUtils.checksumCRC32(S1111HtmlFile);
    File S1111JsonFile = new File(outputDir.getAbsolutePath() + File.separator + "S1111_java.json");
    assertThat(S1111JsonFile.exists()).isTrue();

    // overwrite content of the S1234 files
    try (PrintWriter writer = new PrintWriter(S1234HtmlFile)) {
      writer.print("Lorem ipsum dolor sit amet");
    }
    try (PrintWriter writer = new PrintWriter(S1234JsonFile)) {
      writer.print("{ \"lorem\": \"ipsum\"}");
    }


    // delete the S1111 json file, one file of html or json must be enough to trigger the generation of the rule
    S1111JsonFile.delete();

    // those two files should not be taken by the update
    File quxJson = new File(outputDir.getAbsolutePath() + File.separator + "qux.json");
    assertThat(quxJson.createNewFile()).isTrue();
    File quuxHtml = new File(outputDir.getAbsolutePath() + File.separator + "quux.html");
    assertThat(quuxHtml.createNewFile()).isTrue();
    // create a directory with a compatible name, should be ignored
    File s1000Directory = new File(outputDir.getAbsolutePath() + File.separator + "S1000_language.html");
    s1000Directory.mkdir();

    // fire
    rfs.updateDescriptions();

    // check all the files for the right content
    assertThat(outputDir.listFiles().length).isEqualTo(2 * 2 + 2 + 1 + 1);
    assertThat(org.apache.commons.io.FileUtils.checksumCRC32(S1234JsonFile)).isEqualTo(S1234JsonChecksum);
    assertThat(org.apache.commons.io.FileUtils.checksumCRC32(S1234HtmlFile)).isEqualTo(S1234HtmlChecksum);
    assertThat(org.apache.commons.io.FileUtils.checksumCRC32(S1111HtmlFile)).isEqualTo(S1111HtmlChecksum);
    File profile = new File(outputDir.getAbsolutePath() + File.separator + "Sonar_way_profile.json");
    assertThat(FileUtils.readFileToString(profile)).contains("S1234").doesNotContain("RSPEC");
    assertThat(systemOutRule.getLog()).contains("Found 2 rule(s) to update");
    assertThat(systemOutRule.getLog()).contains("Output: (4) files");
  }

  @Test
  public void testPreserveFileNamesAndNoLanguageInFilenames() throws Exception {
    File outputDir = testFolder.newFolder();
    RuleFilesService rfs = RuleFilesService.create(outputDir.toString(), Language.fromString("c"), true, false);

    rfs.generateRuleFiles(Arrays.asList(
        "S3646",
        "NamespaceName" // RSPEC-2304
    ));

    // record checksums
    File S3646HtmlFile = new File(outputDir.getAbsolutePath() + File.separator + "S3646.html");
    assertThat(S3646HtmlFile.exists()).isTrue();
    long S3646HtmlChecksum = org.apache.commons.io.FileUtils.checksumCRC32(S3646HtmlFile);
    File S3646JsonFile = new File(outputDir.getAbsolutePath() + File.separator + "S3646.json");
    assertThat(S3646JsonFile.exists()).isTrue();
    File S2304HtmlFile = new File(outputDir.getAbsolutePath() + File.separator + "NamespaceName.html");
    assertThat(S2304HtmlFile.exists()).isTrue();
    File S2304JsonFile = new File(outputDir.getAbsolutePath() + File.separator + "NamespaceName.json");
    assertThat(S2304JsonFile.exists()).isTrue();
    long S2304JsonChecksum = org.apache.commons.io.FileUtils.checksumCRC32(S2304JsonFile);

    try (PrintWriter writer = new PrintWriter(S3646HtmlFile)) {
      writer.print("Lorem ipsum dolor sit amet");
    }
    try (PrintWriter writer = new PrintWriter(S2304JsonFile)) {
      writer.print("{ \"lorem\": \"ipsum\"}");
    }

    // fire
    rfs.updateDescriptions();

    assertThat(org.apache.commons.io.FileUtils.checksumCRC32(S3646HtmlFile)).isEqualTo(S3646HtmlChecksum);
    assertThat(org.apache.commons.io.FileUtils.checksumCRC32(S2304JsonFile)).isEqualTo(S2304JsonChecksum);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateDescriptionsBadDirectory() throws Exception {
    RuleFilesService.create("/non/existing/directory", Language.fromString("ln"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateDescriptionsNoDirectory() throws Exception {
   RuleFilesService.create(null, Language.fromString("lang"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateDescriptionsBadLanguage() throws Exception {
    File outputDir = testFolder.newFolder();
    RuleFilesService.create(outputDir.toString(), Language.fromString("cheatTheRegExp|*"));
  }

  @Test
  public void testGetStandards(){
    com.sonarsource.ruleapi.domain.Rule rule = new com.sonarsource.ruleapi.domain.Rule("PHP");

    rule.getCert().add("Foo");
    rule.getMisraC04().add("misraC");

    List<String> standards = RuleFilesService.getStandards(rule);
    assertThat(standards.size()).isEqualTo(0);

    rule.getCwe().add("CWE-999");
    standards = RuleFilesService.getStandards(rule);
    assertThat(standards.size()).isEqualTo(1);

  }

  @Test
  public void testGetSquidJson( ) throws Exception{
    File outputDir = testFolder.newFolder();
    RuleFilesService rfs = RuleFilesService.create(outputDir.toString(), Language.fromString("c"), true, false);


    com.sonarsource.ruleapi.domain.Rule rule = new com.sonarsource.ruleapi.domain.Rule("foo");
    rule.setStatus(com.sonarsource.ruleapi.domain.Rule.Status.DEPRECATED);
    rule.setTitle("Lorem Ipsum");
    HashSet<Profile> defaultProfiles = new HashSet<>();
    defaultProfiles.add(new Profile("bar"));
    rule.setDefaultProfiles(defaultProfiles);
    rule.setRemediationFunction(com.sonarsource.ruleapi.domain.Rule.RemediationFunction.CONSTANT_ISSUE);
    rule.setConstantCostOrLinearThreshold("17 seconds");
    ArrayList<String> tags = new ArrayList<>(1);
    tags.add("qux");
    rule.setTags(tags);
    rule.setSeverity(com.sonarsource.ruleapi.domain.Rule.Severity.MINOR);
    rule.setKey("RSPEC-1234");

    // well formatted nice looking JSON with ordered fields
    final String expected1 = "{\n" +
            "  \"title\": \"Lorem Ipsum\",\n" +
            "  \"type\": \"CODE_SMELL\",\n" +
            "  \"status\": \"deprecated\",\n" +
            "  \"remediation\": {\n" +
            "    \"func\": \"Constant\\/Issue\",\n" +
            "    \"constantCost\": \"17 seconds\"\n" +
            "  },\n" +
            "  \"tags\": [\n" +
            "    \"qux\"\n" +
            "  ],\n" +
            "  \"defaultSeverity\": \"Minor\",\n" +
            "  \"ruleSpecification\": \"RSPEC-1234\"\n" +
            "}";

    assertThat( rfs.getSquidJson(rule)).isEqualTo(expected1);

    rule.setStatus(com.sonarsource.ruleapi.domain.Rule.Status.READY);
    rule.setRemediationFunction(com.sonarsource.ruleapi.domain.Rule.RemediationFunction.LINEAR);
    rule.setLinearArgDesc("dolor sit amet");
    rule.setLinearFactor("666");
    rule.setSeverity(com.sonarsource.ruleapi.domain.Rule.Severity.BLOCKER);
    final String expected2 = "{\n" +
            "  \"title\": \"Lorem Ipsum\",\n" +
            "  \"type\": \"CODE_SMELL\",\n" +
            "  \"status\": \"ready\",\n" +
            "  \"remediation\": {\n" +
            "    \"func\": \"Linear\",\n" +
            "    \"linearDesc\": \"dolor sit amet\",\n" +
            "    \"linearFactor\": \"666\"\n" +
            "  },\n" +
            "  \"tags\": [\n" +
            "    \"qux\"\n" +
            "  ],\n" +
            "  \"defaultSeverity\": \"Blocker\",\n" +
            "  \"ruleSpecification\": \"RSPEC-1234\"\n" +
            "}";

    assertThat( rfs.getSquidJson(rule)).isEqualTo(expected2);


    rule.setStatus(com.sonarsource.ruleapi.domain.Rule.Status.BETA);
    // without the optional fields
    rule.setSeverity(null);
    rule.setRemediationFunction(null);
    final String expected3 = "{\n" +
            "  \"title\": \"Lorem Ipsum\",\n" +
            "  \"type\": \"CODE_SMELL\",\n" +
            "  \"status\": \"beta\",\n" +
            "  \"tags\": [\n" +
            "    \"qux\"\n" +
            "  ],\n" +
            "  \"ruleSpecification\": \"RSPEC-1234\"\n" +
            "}";

    assertThat( rfs.getSquidJson(rule)).isEqualTo(expected3);

  }
}
