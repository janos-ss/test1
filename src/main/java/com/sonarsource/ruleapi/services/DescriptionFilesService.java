/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
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

    assertBaseDir( );

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

  public void updateDescriptions(String language) {

    assertBaseDir( );

    // sanitize user input
    if (!language.matches("(?:\\w|-)+")) {
      throw new IllegalArgumentException();
    }

    // match string like S123456_java., the termination is matched apart
    final Pattern descriptionFileBaseNamePattern = Pattern.compile("^S(\\d+)_" + language + "\\.");

    // establish the list of rules to update
    // HashBasedTable will make unique entry
    File[] allTheDescriptionFiles = new File(this.baseDir).listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.isFile()
          &&
          // match string like S123456_java., the termination is matched apart
          descriptionFileBaseNamePattern.matcher(file.getName()).find()
          &&
          (file.getName().toLowerCase().endsWith(JSON_TERMINATION)
          ||
          file.getName().toLowerCase().endsWith(HTML_TERMINATION)
          );
      }
    });
    HashSet<String> rulesToUpdateKeys = new HashSet<>();
    for (File file : allTheDescriptionFiles) {
      Matcher m = descriptionFileBaseNamePattern.matcher(file.getName());
      if( m.find() ) {
        String canonicalKey = m.group(1);
        if (!rulesToUpdateKeys.contains(canonicalKey)) {
          rulesToUpdateKeys.add(canonicalKey);
        }
      } else {
        throw new IllegalStateException("Inconsistent data");
      }
    }

    LOGGER.info(String.format("Found %d rule(s) to update", rulesToUpdateKeys.size()));

    int countGeneratedFiles = 0;
    for (String canonicalKey : rulesToUpdateKeys) {
      Rule rule = RuleMaker.getRuleByKey(canonicalKey, language);
      countGeneratedFiles += generateOneRuleDescriptions(rule);
    }

    LOGGER.info(String.format("Wrote %d file(s)", countGeneratedFiles));

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

  private void assertBaseDir() {
    if( this.baseDir == null ) {
      throw new IllegalArgumentException("directory is required");
    } else {
      File baseDirFile = new File(this.baseDir);
      if( ! baseDirFile.isDirectory()) {
        throw new IllegalArgumentException("directory does not exist");
      }
    }
  }
}
