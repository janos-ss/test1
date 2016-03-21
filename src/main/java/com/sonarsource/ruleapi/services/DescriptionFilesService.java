/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.get.RuleMaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

public class DescriptionFilesService extends RuleManager {
  private static final Logger LOGGER = Logger.getLogger(DescriptionFilesService.class.getName());

  private static final String HTML_TERMINATION = ".html";
  private static final String JSON_TERMINATION = ".json";

  private String baseDir;

  public DescriptionFilesService(String baseDir) {
    this.baseDir = baseDir;
  }

  public void generateRuleDescriptions(List<String> ruleKeys, String language) {

    int countGeneratedFiles = 0;

    if (ruleKeys != null) {
      for (String ruleKey : ruleKeys) {
        Rule rule = RuleMaker.getRuleByKey(ruleKey, language);

        String htmlFilePath = String.format("%s/%s_%s%s", this.baseDir, rule.getCanonicalKey(), language, HTML_TERMINATION);
        writeFile(htmlFilePath, rule.getHtmlDescription());
        countGeneratedFiles++;

        String squidJsonFileePath = String.format("%s/%s_%s%s", this.baseDir, rule.getCanonicalKey(), language, JSON_TERMINATION);
        writeFile(squidJsonFileePath, rule.getSquidJson());

        countGeneratedFiles++;
      }
    }

    LOGGER.info(String.format("Output: (%d) files", countGeneratedFiles));
  }

  static private void writeFile(String fileName, String content) {

    String protectedPath = fileName.replaceAll(" ", "_");
    File file = new File(protectedPath);

    try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {

      writer.println(content);

    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      throw new RuleException(e);
    }
  }

}
