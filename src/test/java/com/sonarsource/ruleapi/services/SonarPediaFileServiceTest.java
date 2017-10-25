/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;
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
      testFolder.getRoot(), Arrays.asList(Language.COBOL, Language.JS));
    assertThat(sonarPediaJsonFile.getRulesMetadataFilesDir()).exists();
    assertThat(sonarPediaJsonFile.getRulesMetadataFilesDir().list().length).isEqualTo(0);
    assertThat(sonarPediaJsonFile.getFile()).exists();
    assertThat(systemOutRule.getLog()).doesNotContain("Creating ");
    assertThat(systemOutRule.getLog()).contains("Will use ");
    assertThat(systemOutRule.getLog()).contains("SonarPedia file created at ");
  }

  @Test(expected = IllegalStateException.class)
  public void shoulNotAllowMultipleLanguageWoLanguageIntoFileName() throws Exception {
    SonarPediaFileService.init(testFolder.getRoot(), Arrays.asList(Language.CSH, Language.FLEX));
    SonarPediaFileService.create(testFolder.getRoot(), false, false);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldGoBadIfNoSonarPedia() throws Exception {
    // no sonarpedia file
    SonarPediaFileService.create(testFolder.getRoot(), false, false);
  }

  @Test
  public void shouldRunTheFullCycle() throws Exception {

    SonarPediaJsonFile newFile = SonarPediaFileService.init(testFolder.getRoot(),
      Arrays.asList(Language.PHP, Language.PY));
    final File rulesDir = newFile.getRulesMetadataFilesDir();
    List<String> rulesSet = Arrays.asList("RSPEC-2076", "RSPEC-1763", "RSPEC-4142");

    SonarPediaFileService spfs = SonarPediaFileService.create(testFolder.getRoot(), false, true);
    assertThat(rulesDir.list().length).isEqualTo(0);
    spfs.generateRuleFiles(rulesSet);
    assertThat(rulesDir.list().length).isEqualTo(rulesSet.size() * 2 * 2 + 1);
    final long maxDateBeforeUpdate = Stream.of(newFile.getRulesMetadataFilesDir().list())
        .map( fileName -> new File(rulesDir, fileName ))
        .map(File::lastModified)
        .max(Long::compareTo)
        .get();
    final long sonarpediaDateBeforeUpdate = newFile.getFile().lastModified();
    spfs.updateDescriptions();
    assertThat(systemOutRule.getLog()).contains(String.format("Found %d rule(s) to update", rulesSet.size()));
    assertThat(systemOutRule.getLog()).contains(String.format("SonarPedia file updated"));

    // read back sonarpedia to check the update
    SonarPediaJsonFile afterUpdate = SonarPediaJsonFile.findSonarPediaFile(testFolder.getRoot());
    assertThat(afterUpdate.getUpdateTimeStamp().toEpochMilli()).isGreaterThan(sonarpediaDateBeforeUpdate);
    // check modification date of rule files
    final long maxDateAfterUpdate = Stream.of(afterUpdate.getRulesMetadataFilesDir().list())
        .map( fileName -> new File(afterUpdate.getRulesMetadataFilesDir(), fileName ))
        .map(File::lastModified)
        .max(Long::compareTo)
        .get();
    assertThat(maxDateAfterUpdate).isGreaterThan(maxDateBeforeUpdate);
  }

}
