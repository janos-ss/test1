/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.Profile;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import static org.fest.assertions.Assertions.assertThat;

public class DescriptionFilesServiceTest {

  // log capturer see http://blog.diabol.se/?p=474
  private static Logger log = Logger.getLogger(DescriptionFilesService.class.getName()); // matches the logger in the affected class
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
  public void testGenerateRuleDescriptions() throws Exception {
    File outputDir = testFolder.newFolder();

    DescriptionFilesService dfs = new DescriptionFilesService(outputDir.toString());

    List<String> listOfKeys = new ArrayList<>(2);
    listOfKeys.add("RSPEC-1234");
    listOfKeys.add("S1111");
    dfs.generateRulesDescriptions(listOfKeys, "java");

    // must be 2 files per rule
    int supposedCount = listOfKeys.size() * 2;
    assertThat(outputDir.listFiles().length).isEqualTo(supposedCount);
    assertThat(getTestCapturedLog()).contains(String.format("(%d)", supposedCount));
  }

  @Test
  public void testUpdateDescriptions( ) throws Exception {
    File outputDir = testFolder.newFolder();
    DescriptionFilesService dfs = new DescriptionFilesService(outputDir.toString());

    // build a set of three rules

    // commons
    HashSet<Profile> profile = new HashSet<>(1);
    profile.add(new Profile("profile"));

    // rule #1
    com.sonarsource.ruleapi.domain.Rule S01_foo = new com.sonarsource.ruleapi.domain.Rule("foo");
    S01_foo.setKey("RSPEC-01");
    S01_foo.setTitle("S01 title");
    S01_foo.setDefaultProfiles(profile);
    S01_foo.setSqaleRemediationFunction(com.sonarsource.ruleapi.domain.Rule.RemediationFunction.CONSTANT_ISSUE);
    S01_foo.setSqaleConstantCostOrLinearThreshold("S01 cost");
    S01_foo.setTags(Arrays.asList("S01 tag 1", "S01 tag 2"));
    S01_foo.setSeverity(com.sonarsource.ruleapi.domain.Rule.Severity.BLOCKER);
    dfs.generateOneRuleDescriptions(S01_foo);

    // rule #2
    com.sonarsource.ruleapi.domain.Rule S02_foo = new com.sonarsource.ruleapi.domain.Rule("foo");
    S02_foo.setKey("RSPEC-02");
    S02_foo.setTitle("S02 title");
    S02_foo.setDefaultProfiles(profile);
    S02_foo.setSqaleRemediationFunction(com.sonarsource.ruleapi.domain.Rule.RemediationFunction.LINEAR);
    S02_foo.setSqaleLinearFactor("S02 factor");
    S02_foo.setTags(Arrays.asList("S02 tag"));
    S02_foo.setSeverity(com.sonarsource.ruleapi.domain.Rule.Severity.MAJOR);
    dfs.generateOneRuleDescriptions(S02_foo);

    // rule #3
    com.sonarsource.ruleapi.domain.Rule S03_bar = new com.sonarsource.ruleapi.domain.Rule("bar");
    S03_bar.setKey("RSPEC-03");
    S03_bar.setTitle("S03 title");
    S03_bar.setDefaultProfiles(profile);
    S03_bar.setSqaleRemediationFunction(com.sonarsource.ruleapi.domain.Rule.RemediationFunction.LINEAR);
    S03_bar.setSqaleRemediationFunction(com.sonarsource.ruleapi.domain.Rule.RemediationFunction.LINEAR_OFFSET );
    S03_bar.setSqaleConstantCostOrLinearThreshold("S03 cost");
    S03_bar.setSqaleLinearFactor("S03 factor");
    S03_bar.setTags(Arrays.asList("S03 tag"));
    S03_bar.setSeverity(com.sonarsource.ruleapi.domain.Rule.Severity.MINOR);
    dfs.generateOneRuleDescriptions(S03_bar);


    assertThat(outputDir.listFiles().length ).isEqualTo(3*2);

    // TODO
    // add creation of dummy json file to check the file filtering
    // add case where only the JSON is present
    // add case where only the HTML is present
    // dfs.updateDescriptions();
    // assert results

  }
}