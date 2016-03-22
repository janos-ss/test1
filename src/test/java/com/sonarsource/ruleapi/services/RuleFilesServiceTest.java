/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import static org.fest.assertions.Assertions.assertThat;

public class RuleFilesServiceTest {

  // log capturer see http://blog.diabol.se/?p=474
  private static Logger log = Logger.getLogger(RuleFilesService.class.getName()); // matches the logger in the affected class
  private static OutputStream logCapturingStream;
  private static StreamHandler customLogHandler;

  @Before
  public void attachLogCapturer() {
    logCapturingStream = new ByteArrayOutputStream();
    Handler[] handlers = log.getParent().getHandlers();
    customLogHandler = new StreamHandler(logCapturingStream, handlers[0].getFormatter());
    log.addHandler(customLogHandler);
  }

  public String getTestCapturedLog() throws IOException {
    customLogHandler.flush();
    return logCapturingStream.toString();
  }

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
    assertThat(getTestCapturedLog()).contains(String.format("(%d)", supposedCount));
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
    assertThat(getTestCapturedLog()).contains(String.format("(%d)", supposedCount));
    assertThat(getTestCapturedLog()).contains("Missing severity for rule");

  }
}