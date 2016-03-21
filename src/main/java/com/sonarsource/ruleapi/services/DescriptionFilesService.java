/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.get.RuleMaker;

public class DescriptionFilesService extends RuleManager {
  private static final Logger LOGGER = Logger.getLogger(DescriptionFilesService.class.getName());

  private static final String HTML_TERMINATION = ".html";
  private static final String JSON_TERMINATION = ".json";

  private String baseDir;

  public DescriptionFilesService(String baseDir) {
    this.baseDir = baseDir;
  }

  public void generateRulesDescriptions(List<String> ruleKeys, String language) {
    int countGeneratedFiles = 0;

    if (ruleKeys != null) {
      for (String ruleKey : ruleKeys) {
        Rule rule = RuleMaker.getRuleByKey(ruleKey, language);
        countGeneratedFiles += this.generateOneRuleDescriptions(rule);
      }
    }

    LOGGER.info(String.format("Output: (%d) files", countGeneratedFiles));
  }

  public int generateOneRuleDescriptions(Rule rule) {
    int countGeneratedFiles = 0;

    String htmlFilePath = String.format("%s/%s_%s%s"
      , this.baseDir
      , rule.getCanonicalKey()
      , rule.getLanguage()
      , HTML_TERMINATION);
    writeFile(htmlFilePath, rule.getHtmlDescription());
    countGeneratedFiles++;

    String squidJsonFilePath = String.format("%s/%s_%s%s"
      , this.baseDir
      , rule.getCanonicalKey()
      , rule.getLanguage()
      , JSON_TERMINATION);
    writeFile(squidJsonFilePath, rule.getSquidJson());
    countGeneratedFiles++;

    return countGeneratedFiles;
  }

  public void updateDescriptions() {

    // match string like S123456_java., the termination is matched apart
    final Pattern descriptionFileBaseNamePattern = Pattern.compile("^S(\\d+)_(\\w+)\\.");

    // establish the list of rules to update
    // HashBasedTable will make unique entry
    File[] allTheDescriptionFiles = new File(this.baseDir).listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.isFile()
          &&
          // match string like S123456_java., the termination is matched apart
          descriptionFileBaseNamePattern.matcher(file.getName()).matches()
          &&
          (file.getName().endsWith(JSON_TERMINATION) || file.getName().endsWith(HTML_TERMINATION));
      }
    });
    Table<String, String, Rule> rulesToUdate = HashBasedTable.create();
    for (File file : allTheDescriptionFiles) {
      Matcher m = descriptionFileBaseNamePattern.matcher(file.getName());
      String canonicalKey = m.group(1);
      String language = m.group(1);
      if (!rulesToUdate.contains(canonicalKey, language)) {
        rulesToUdate.put(canonicalKey, language, null);
      }
    }

    // populate the rules
    for (Table.Cell<String, String, Rule> cell : rulesToUdate.cellSet()) {
      rulesToUdate.put(
        cell.getRowKey()
        , cell.getColumnKey()
        , RuleMaker.getRuleByKey(cell.getRowKey(), cell.getColumnKey()));
    }

    // TODO loop on the rules to overwrite the files
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
