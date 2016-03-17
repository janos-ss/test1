/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.get.RuleMaker;

import java.io.*;
import java.util.List;
import java.util.logging.Logger;

public class DescriptionFilesService extends RuleManager {
  private static final Logger LOGGER = Logger.getLogger(DescriptionFilesService.class.getName());

  private static final String HTMLtermination = ".html";
  private static final String JSONtermination = ".json";

  private String baseDir;

  public DescriptionFilesService(String baseDir) {
    this.baseDir = baseDir;
  }

  public void generateRuleDescriptions(List<String> ruleKeys, String language) {
    if (ruleKeys != null) {
      for (String ruleKey : ruleKeys) {
        Rule rule = RuleMaker.getRuleByKey(ruleKey, language);
        String htmlFilePath = String.format("%s/%s_%s%s", this.baseDir, rule.getCanonicalKey(), language, HTMLtermination );
        String squidJsonFileePath = String.format("%s/%s_%s%s", this.baseDir, rule.getCanonicalKey(), language, JSONtermination );
        writeFile(htmlFilePath, rule.getHtmlDescription());
        try {
          writeFile(squidJsonFileePath, rule.getSquidJson());
        } catch( IOException e ) {
          throw new RuleException(e);
        }
      }
    }
  }

  private void writeFile(String fileName, String content) {

    String path = fileName.replaceAll(" ", "_");
    File file = new File(this.baseDir + "/" + path);

    try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {

      writer.println(content);

      LOGGER.info("Output: " + file.toString());

    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      throw new RuleException(e);
    }
  }

}
