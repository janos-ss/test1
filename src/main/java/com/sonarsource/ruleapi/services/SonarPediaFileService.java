/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SonarPediaFileService {

  List<RuleFilesService> ruleFilesServices;
  SonarPediaJsonFile sonarPediaJsonFile;

  SonarPediaFileService() {
    // NOP
    ruleFilesServices = new ArrayList<>(0);
    sonarPediaJsonFile = null;
  }

  // factory for creating a new file
  public static SonarPediaJsonFile init(List<Language> languages) {

    final File currentDir = new File(".");

    // create a rules directory
    File rulesDir = new File(currentDir, "rules");
    if (!rulesDir.exists()) {
      System.out.println("Creating " + rulesDir.toString());
      rulesDir.mkdir();
    }

    SonarPediaJsonFile sonarPediaJsonFile = SonarPediaJsonFile.create(currentDir, rulesDir, languages);
    sonarPediaJsonFile.writeToItsFile();
    System.out.println("SonarPedia file created at ./" + sonarPediaJsonFile.getFile().getName());

    return sonarPediaJsonFile;
  }

  // factory for file existing in current directory
  public static SonarPediaFileService create(boolean preserveFileNames, boolean languageInFilenames) {
    SonarPediaFileService returned = new SonarPediaFileService();
    final File currentDir = new File(".");

    try {
      returned.sonarPediaJsonFile = SonarPediaJsonFile.findSonarPediaFile(currentDir);
    } catch (FileNotFoundException exception) {
      throw new IllegalStateException("can't find sonarpedia.json file", exception);
    }

    returned.ruleFilesServices =
        returned.sonarPediaJsonFile.getLanguages()
        .stream()
        .map( language -> RuleFilesService.create(
            returned.sonarPediaJsonFile.getRulesMetadataFilesDir().getAbsolutePath(),
            language,
            preserveFileNames,
            languageInFilenames))
        .collect(Collectors.toList());

    return returned;
  }

  public void generateRuleFiles(Iterable<String> ruleKeys) {
    ruleFilesServices
      .stream()
      .forEach(ruleFilesService -> ruleFilesService.generateRuleFiles(ruleKeys));
  }

  public void updateDescriptions( ) {
    ruleFilesServices
      .stream()
      .forEach(RuleFilesService::updateDescriptions);
    sonarPediaJsonFile.updateTimeStamp().writeToItsFile();
    System.out.println("SonarPedia file updated");

  }

}
