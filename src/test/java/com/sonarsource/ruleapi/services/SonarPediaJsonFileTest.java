/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.fest.assertions.Assertions.assertThat;

public class SonarPediaJsonFileTest {

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  @Test
  public void shouldRunFullCycleOfUpdate() throws Exception {


    Path rulesDirPath = testFolder.getRoot().toPath().resolve("intermediate/rules");
    File rulesDir = Files.createDirectories(rulesDirPath).toFile();

    SonarPediaJsonFile sonarPediaJsonFile = SonarPediaJsonFile.create(testFolder.getRoot(), rulesDir);

    SonarPediaJsonFile underTest = SonarPediaJsonFile.findSonarPediaFile(testFolder.getRoot() );
    assertThat(underTest.getRulesMetadataFilesDir()).isEqualTo(rulesDirPath.toFile());
    assertThat(underTest.getUpdateTimeStamp()).isNull();

    // update the data
    Instant now = Instant.now();
    underTest.updateTimeStamp();
    underTest.writeToItsFile();

    SonarPediaJsonFile afterFirstWrite = SonarPediaJsonFile.findSonarPediaFile(testFolder.getRoot() );
    assertThat(afterFirstWrite.getUpdateTimeStamp().toEpochMilli()).isGreaterThanOrEqualTo(now.toEpochMilli());

    // update a second time data
    underTest.updateTimeStamp();
    underTest.writeToItsFile();

    SonarPediaJsonFile afterSecondWrite = SonarPediaJsonFile.findSonarPediaFile(testFolder.getRoot() );
    assertThat(afterSecondWrite.getUpdateTimeStamp().toEpochMilli())
        .isGreaterThan(afterFirstWrite.getUpdateTimeStamp().toEpochMilli());

  }
}
