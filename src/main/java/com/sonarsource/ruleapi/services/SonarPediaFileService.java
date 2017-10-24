/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.SonarPediaJsonFile;
import com.sonarsource.ruleapi.utilities.Language;
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

  // initialize SonarPedia file and directory
  public static SonarPediaJsonFile init(List<Language> languages) {

    final File currentDir = new File(".");

    // create a rules directory
    File rulesDir = new File(currentDir, "rules");
    if (rulesDir.exists()) {
      System.out.println("Will use " + rulesDir.toString() + " to store metadata");
    } else {
      System.out.println("Creating directory " + rulesDir.toString() + " to store the metadata");
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

    if (!languageInFilenames && returned.sonarPediaJsonFile.getLanguages().size() > 1) {
      throw new IllegalStateException("Multiple languages requires language in filenames");
    }

    returned.ruleFilesServices = returned.sonarPediaJsonFile.getLanguages()
      .stream()
      .map(language -> RuleFilesService.create(
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

  public void updateDescriptions() {
    ruleFilesServices
      .stream()
      .forEach(RuleFilesService::updateDescriptions);
    sonarPediaJsonFile.updateTimeStamp().writeToItsFile();
    System.out.println("SonarPedia file updated");

  }

}
