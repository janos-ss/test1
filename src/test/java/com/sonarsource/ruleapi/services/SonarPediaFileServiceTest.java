/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import org.junit.contrib.java.lang.system.SystemOutRule;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.sonarsource.ruleapi.domain.SonarPediaJsonFile;
import com.sonarsource.ruleapi.utilities.Language;

import static org.assertj.core.api.Assertions.assertThat;

public class SonarPediaFileServiceTest {

  @Rule
  public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  @Test
  public void shoulCreateASonarPediaFileAndEmptyDirectory() throws Exception {

    SonarPediaJsonFile sonarPediaJsonFile = SonarPediaFileService.init(
        testFolder.getRoot(),
        Arrays.asList(Language.COBOL, Language.JS));
    assertThat(sonarPediaJsonFile.getRulesMetadataFilesDir()).exists();
    assertThat(sonarPediaJsonFile.getRulesMetadataFilesDir().list().length).isEqualTo(0);
    assertThat(sonarPediaJsonFile.getFile()).exists();
    assertThat(systemOutRule.getLog()).contains("Creating ");
    assertThat(systemOutRule.getLog()).contains("SonarPedia file created at ");

    // delete the SonarPedia file, keep the directory
    sonarPediaJsonFile.getFile().delete();

    // create again a sonarpedia file, must reuse the existing directory
    systemOutRule.clearLog();
    SonarPediaFileService.init(
        testFolder.getRoot(),Arrays.asList(Language.COBOL, Language.JS));
    assertThat(sonarPediaJsonFile.getRulesMetadataFilesDir()).exists();
    assertThat(sonarPediaJsonFile.getRulesMetadataFilesDir().list().length).isEqualTo(0);
    assertThat(sonarPediaJsonFile.getFile()).exists();
    assertThat(systemOutRule.getLog()).doesNotContain("Creating ");
    assertThat(systemOutRule.getLog()).contains("Will use ");
    assertThat(systemOutRule.getLog()).contains("SonarPedia file created at ");
  }

  @Test( expected = IllegalStateException.class)
  public void shoulNotAllowMultipleLanguageWoLanguageIntoFileName() throws Exception {
    SonarPediaFileService.init(testFolder.getRoot(), Arrays.asList(Language.CSH, Language.FLEX));
    SonarPediaFileService.create(testFolder.getRoot(), false, false);
  }

  @Test( expected = IllegalStateException.class)
  public void shouldGoBadIfNoSonarPedia() throws Exception {
    // no sonarpedia file
    SonarPediaFileService.create(testFolder.getRoot(), false, false);
  }

}
