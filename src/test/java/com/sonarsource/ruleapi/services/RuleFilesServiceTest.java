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
  public void testGenerateRuleFilesWoKeyList() throws Exception {
    File outputDir = testFolder.newFolder();
    RuleFilesService dfs = new RuleFilesService(outputDir.toString());
    dfs.generateRuleFiles(null, "java");
    assertThat(outputDir.listFiles().length).isEqualTo(0);

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
}