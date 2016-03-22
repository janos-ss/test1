/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

public class RuleFilesService extends RuleManager {
  private static final Logger LOGGER = Logger.getLogger(RuleFilesService.class.getName());

  private static final String HTML_TERMINATION = ".html";
  private static final String JSON_TERMINATION = ".json";

  private String baseDir;

  public RuleFilesService(String baseDir) {
    this.baseDir = baseDir;
  }

  public void generateRuleFiles(List<String> ruleKeys, String language) {

    int countGeneratedFiles = 0;

    if (ruleKeys != null) {
      for (String ruleKey : ruleKeys) {
        Rule rule = RuleMaker.getRuleByKey(ruleKey, language);
        final String denormalizedKey = Utilities.denormalizeKey(rule.getKey() );

        String htmlFilePath = String.format("%s/%s_%s%s", this.baseDir, denormalizedKey, language, HTML_TERMINATION);
        writeFile(htmlFilePath, rule.getHtmlDescription());
        countGeneratedFiles++;

        String squidJsonFilePath = String.format("%s/%s_%s%s", this.baseDir, denormalizedKey, language, JSON_TERMINATION);
        writeFile(squidJsonFilePath, rule.getSquidJson());
        countGeneratedFiles++;
      }
    }

    LOGGER.info(String.format("Output: (%d) files", countGeneratedFiles));
  }

  private static void writeFile(String fileName, String content) {

    String protectedPath = fileName.replaceAll(" ", "_");
    File file = new File(protectedPath);

    try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {

      writer.println(content);

    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      throw new RuleException(e);
    }
  }

}
