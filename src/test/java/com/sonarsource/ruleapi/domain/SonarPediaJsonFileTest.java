/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import com.sonarsource.ruleapi.utilities.Language;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class SonarPediaJsonFileTest {

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  @Test
  public void shouldRunFullCycleOfUpdate() throws Exception {

    Path rulesDirPath = testFolder.getRoot().toPath().resolve("intermediate/rules");
    File rulesDir = Files.createDirectories(rulesDirPath).toFile();

    // create a file
    SonarPediaJsonFile.create(
      testFolder.getRoot(),
        rulesDir, Arrays.asList(Language.C, Language.JAVA));

    // find the file
    SonarPediaJsonFile underTest = SonarPediaJsonFile.findSonarPediaFile(testFolder.getRoot());
    assertThat(underTest.getRulesMetadataFilesDir()).isEqualTo(rulesDirPath.toFile());
    assertThat(underTest.getUpdateTimeStamp()).isNull();
    assertThat(underTest.getLanguages()).containsExactlyInAnyOrder(Language.C, Language.JAVA);

    // update the data
    Instant now = Instant.now();
    underTest.updateTimeStamp();
    underTest.writeToItsFile();

    SonarPediaJsonFile afterFirstWrite = SonarPediaJsonFile.findSonarPediaFile(testFolder.getRoot());
    assertThat(afterFirstWrite.getUpdateTimeStamp().toEpochMilli()).isGreaterThanOrEqualTo(now.toEpochMilli());

    // update a second time data
    underTest.updateTimeStamp();
    underTest.writeToItsFile();

    SonarPediaJsonFile afterSecondWrite = SonarPediaJsonFile.findSonarPediaFile(testFolder.getRoot());
    assertThat(afterSecondWrite.getUpdateTimeStamp().toEpochMilli())
      .isGreaterThan(afterFirstWrite.getUpdateTimeStamp().toEpochMilli());

  }
}
