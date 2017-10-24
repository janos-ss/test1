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

    System.setProperty("user.dir", testFolder.getRoot().getAbsolutePath());

    SonarPediaJsonFile underTest = SonarPediaFileService.init(Arrays.asList(Language.COBOL, Language.JS));
    assertThat(underTest.getRulesMetadataFilesDir()).exists();
    assertThat(underTest.getRulesMetadataFilesDir().list().length).isEqualTo(0);
    assertThat(underTest.getFile()).exists();
    assertThat(systemOutRule.getLog()).contains("Creating ");
    assertThat(systemOutRule.getLog()).contains("SonarPedia file created at ");

    // delete the SonarPedia file, keep the directory
    underTest.getFile().delete();

    // create again a sonarpedia file, must reuse the existing directory
    systemOutRule.clearLog();
    SonarPediaFileService.init(Arrays.asList(Language.COBOL, Language.JS));
    assertThat(underTest.getRulesMetadataFilesDir()).exists();
    assertThat(underTest.getRulesMetadataFilesDir().list().length).isEqualTo(0);
    assertThat(underTest.getFile()).exists();
    assertThat(systemOutRule.getLog()).doesNotContain("Creating ");
    assertThat(systemOutRule.getLog()).contains("Will use ");
    assertThat(systemOutRule.getLog()).contains("SonarPedia file created at ");
  }

  @Test( expected = IllegalStateException.class)
  public void shoulNotAllowMultipleLanguageWoLanguageIntoFileName() throws Exception {

    System.setProperty("user.dir", testFolder.getRoot().getAbsolutePath());

    SonarPediaFileService.init(Arrays.asList(Language.CSH, Language.FLEX));
    SonarPediaFileService.create(false, false);
  }

  @Test( expected = IllegalStateException.class)
  public void shouldGoBadIfNoSonarPedia() throws Exception {
    System.setProperty("user.dir", testFolder.getRoot().getAbsolutePath());

    // no sonarpedia file
    SonarPediaFileService.create(false, false);
  }

}
