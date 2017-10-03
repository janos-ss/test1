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

public class SonarPediaFileService {

  RuleFilesService ruleFilesService;
  SonarPediaJsonFile sonarPediaJsonFile;

  SonarPediaFileService() {
    // NOP
    ruleFilesService = null;
    sonarPediaJsonFile = null;
  }

  public static SonarPediaJsonFile init(String baseDirString, Language language) {

    File baseDir = Utilities.assertBaseDir(baseDirString);

    // create a rules directory
    File rulesDir = new File(baseDir, "rules");
    if (!rulesDir.exists()) {
      System.out.println("Creating " + rulesDir.toString());
      rulesDir.mkdir();
    }

    SonarPediaJsonFile sonarPediaJsonFile = SonarPediaJsonFile.create(baseDir, rulesDir);
    System.out.println("SonarPedia file created at " + sonarPediaJsonFile.toString());

    return sonarPediaJsonFile;
  }

  public static SonarPediaFileService create(boolean preserveFileNames, boolean languageInFilenames) {
    SonarPediaFileService returned = new SonarPediaFileService();

    try {
      final File currentDir = new File(".");
      returned.sonarPediaJsonFile = SonarPediaJsonFile.findSonarPediaFile(currentDir);
    } catch( FileNotFoundException exception ) {

    }

    returned.ruleFilesService = RuleFilesService.create(
      returned.sonarPediaJsonFile.getRulesMetadataFilesDir().getAbsolutePath(),
      returned.sonarPediaJsonFile.getLanguage(),
      preserveFileNames, languageInFilenames);

    return returned;
  }

  public void generateRuleFiles( Iterable<String> ruleKeys ) {
    ruleFilesService.generateRuleFiles(ruleKeys);
  }

  public void updateDescriptions( ) {
    ruleFilesService.updateDescriptions();
    sonarPediaJsonFile.updateTimeStamp().writeToItsFile();
  }


}
