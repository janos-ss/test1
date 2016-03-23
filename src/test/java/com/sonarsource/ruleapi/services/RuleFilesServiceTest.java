/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
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

    RuleFilesService dfs = new RuleFilesService(outputDir.toString());

    List<String> listOfKeys = new ArrayList<>(2);
    listOfKeys.add("RSPEC-1234");
    listOfKeys.add("S1111");
    dfs.generateRuleFiles(listOfKeys, "java");

    // must be 2 files per rule
    int supposedCount = listOfKeys.size() * 2;
    assertThat(outputDir.listFiles().length).isEqualTo(supposedCount);
    assertThat(systemOutRule.getLog()).contains(String.format("(%d)", supposedCount));
  }

  @Test
  public void testGenerateRuleDescriptionsNoRule() throws Exception {
    File outputDir = testFolder.newFolder();
    RuleFilesService rfs = new RuleFilesService(outputDir.toString());
    rfs.generateRuleFiles(null, "java");
    assertThat(outputDir.listFiles().length).isEqualTo(0);
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGenerateRuleFilesUnknownRule() throws Exception {
    File outputDir = testFolder.newFolder();

    RuleFilesService dfs = new RuleFilesService(outputDir.toString());
    List<String> listOfKeys = new ArrayList<>(1);
    listOfKeys.add("foo");
    dfs.generateRuleFiles(listOfKeys, "bar");
    assertThat(outputDir.listFiles().length).isEqualTo(0);
  }


  @Test( expected = IllegalArgumentException.class )
  public void testGenerateRuleFilesEmptyLanguage() throws Exception {
    File outputDir = testFolder.newFolder();

    RuleFilesService dfs = new RuleFilesService(outputDir.toString());
    List<String> listOfKeys = new ArrayList<>(0);
    dfs.generateRuleFiles(listOfKeys, "");
    assertThat(outputDir.listFiles().length).isEqualTo(0);
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGenerateRuleFilesNullLanguage() throws Exception {
    File outputDir = testFolder.newFolder();

    RuleFilesService dfs = new RuleFilesService(outputDir.toString());
    List<String> listOfKeys = new ArrayList<>(0);
    dfs.generateRuleFiles(listOfKeys, null);
    assertThat(outputDir.listFiles().length).isEqualTo(0);
    assertThat(systemOutRule.getLog()).contains("no valid language");
  }


  @Test
  public void testGenerateRuleFilesWoSeverity() throws Exception {
    File outputDir = testFolder.newFolder();

    RuleFilesService dfs = new RuleFilesService(outputDir.toString());

    List<String> listOfKeys = new ArrayList<>(1);
    // this ticket is deprecated and wo severity
    listOfKeys.add("RSPEC-2297");

    dfs.generateRuleFiles(listOfKeys, "java");

    // must be 2 files per rule
    int supposedCount = listOfKeys.size() * 2;
    assertThat(outputDir.listFiles().length).isEqualTo(supposedCount);
    assertThat(systemOutRule.getLog()).contains(String.format("(%d)", supposedCount));
    assertThat(systemOutRule.getLog()).contains("missing severity for rule");

  }

  @Test
  public void testUpdateRuleFiles() throws Exception {
    File outputDir = testFolder.newFolder();
    RuleFilesService rfs = new RuleFilesService(outputDir.toString());

    List<String> listOfKeys = new ArrayList<>(2);
    listOfKeys.add("RSPEC-1234");
    listOfKeys.add("S1111");
    rfs.generateRuleFiles(listOfKeys, "java");

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
    rfs.updateDescriptions("java");

    // check all the files for the right content
    assertThat(outputDir.listFiles().length).isEqualTo(2 * 2 + 2 + 1 );
    assertThat(org.apache.commons.io.FileUtils.checksumCRC32(S1234JsonFile)).isEqualTo(S1234JsonChecksum);
    assertThat(org.apache.commons.io.FileUtils.checksumCRC32(S1234HtmlFile)).isEqualTo(S1234HtmlChecksum);
    assertThat(org.apache.commons.io.FileUtils.checksumCRC32(S1111HtmlFile)).isEqualTo(S1111HtmlChecksum);

    assertThat(systemOutRule.getLog()).contains("Found 2 rule(s) to update");
    assertThat(systemOutRule.getLog()).contains("Wrote 4 file(s)");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateDescriptionsBadDirectory() throws Exception {
    RuleFilesService rfs = new RuleFilesService("/non/existing/directory");
    rfs.updateDescriptions("language");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateDescriptionsNoDirectory() throws Exception {
    RuleFilesService rfs = new RuleFilesService(null);
    rfs.updateDescriptions("language");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUpdateDescriptionsBadLanguage() throws Exception {
    File outputDir = testFolder.newFolder();
    RuleFilesService rfs = new RuleFilesService(outputDir.toString());
    rfs.updateDescriptions("cheatTheRegExp|*");
  }
}