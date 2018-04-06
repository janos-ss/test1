/*
 * Copyright (C) 2014-2018 SonarSource SA
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

  public SonarPediaFileService() {
    ruleFilesServices = new ArrayList<>(0);
    sonarPediaJsonFile = null;
  }

  // initialize SonarPedia file and directory
  public static SonarPediaJsonFile init(File baseDir, List<Language> languages, boolean preserveFileNames, boolean noLanguageInFilenames) {

    // create a rules directory
    File rulesDir = new File(baseDir, "rules");
    if (rulesDir.exists()) {
      System.out.println("Will use " + rulesDir.toString() + " to store metadata");
    } else {
      System.out.println("Creating directory " + rulesDir.toString() + " to store the metadata");
      rulesDir.mkdir();
    }

    SonarPediaJsonFile sonarPediaJsonFile = SonarPediaJsonFile.create(baseDir, rulesDir, languages, preserveFileNames, noLanguageInFilenames);
    sonarPediaJsonFile.writeToItsFile();
    System.out.println("SonarPedia file created at ./" + sonarPediaJsonFile.getFile().getName());

    return sonarPediaJsonFile;
  }

  // factory for file existing in current directory
  public static SonarPediaFileService create(File baseDir) {
    SonarPediaFileService returned = new SonarPediaFileService();
    try {
      returned.sonarPediaJsonFile = SonarPediaJsonFile.findSonarPediaFile(baseDir);
    } catch (FileNotFoundException exception) {
      throw new IllegalStateException("can't find sonarpedia.json file", exception);
    }

    returned.ruleFilesServices = returned.sonarPediaJsonFile.getLanguages()
      .stream()
      .map(language -> RuleFilesService.create(
        returned.sonarPediaJsonFile.getRulesMetadataFilesDir().getAbsolutePath(),
        language,
        returned.sonarPediaJsonFile.preserveFilenames(),
        !returned.sonarPediaJsonFile.noLanguageInFileName()))
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
