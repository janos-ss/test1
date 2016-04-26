/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleFilesService {

  private static final String HTML_TERMINATION = ".html";
  private static final String JSON_TERMINATION = ".json";
  private final String language;
  private final File baseDir;
  private final Pattern descriptionFileBaseNamePattern;
  private int countGeneratedFiles;

  private RuleFilesService(File baseDir, Language language) {
    this.baseDir = baseDir;
    this.language = language.getRspec().toLowerCase();
    // match string like S123456_java., the termination is matched apart
    descriptionFileBaseNamePattern = Pattern.compile("^S(\\d+)_" + this.language + "\\.");
  }

  public static RuleFilesService create(String baseDir, Language language) {
    File baseDirFile = assertBaseDir(baseDir);
    if(language == null) {
      throw new IllegalArgumentException("no language found");
    }
    return new RuleFilesService(baseDirFile, language);
  }

  public void generateRuleFiles(Iterable<String> ruleKeys) {
    countGeneratedFiles = 0;
    if (ruleKeys != null) {
      for (String ruleKey : ruleKeys) {
        Rule rule = RuleMaker.getRuleByKey(ruleKey, language);
        if( rule == null || rule.getKey() == null ) {
          throw new IllegalArgumentException("invalid rule");
        }
        generateSingleRuleFiles(rule);
      }
    }

    System.out.println(String.format("Output: (%d) files", countGeneratedFiles));
  }

  private void generateSingleRuleFiles(Rule rule) {
    if(rule.getSeverity() == null) {
      System.out.println(String.format("WARNING: missing severity for rule %s", rule.getKey()));
    }
    writeFile(computePath(rule, HTML_TERMINATION), rule.getHtmlDescription());
    writeFile(computePath(rule, JSON_TERMINATION), rule.getSquidJson());
  }

  private static String computePath(Rule rule, String fileExtension) {
    String denormalizedKey = Utilities.denormalizeKey(rule.getKey());
    return String.format("%s_%s%s", denormalizedKey, rule.getLanguage(), fileExtension);
  }

  public void updateDescriptions() {
    generateRuleFiles(findRulesToUpdate());
  }

  private Set<String> findRulesToUpdate() {
    final Set<String> rulesToUpdateKeys = new HashSet<>();
    baseDir.listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        String fileName = file.getName();
        if(file.isFile() && (fileName.toLowerCase().endsWith(JSON_TERMINATION) || fileName.toLowerCase().endsWith(HTML_TERMINATION))) {
          Matcher m = descriptionFileBaseNamePattern.matcher(fileName);
          if (m.find()) {
            rulesToUpdateKeys.add(m.group(1));
            return true;
          }
        }
        return false;
      }
    });
    System.out.println(String.format("Found %d rule(s) to update", rulesToUpdateKeys.size()));
    return rulesToUpdateKeys;
  }


  private void writeFile(String fileName, String content) {
    String protectedPath = fileName.replaceAll(" ", "_");
    File file = new File(baseDir, protectedPath);
    try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
      writer.println(content);
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      throw new RuleException(e);
    }
    countGeneratedFiles++;
  }

  private static File assertBaseDir(String baseDir) {
    if (baseDir == null) {
      throw new IllegalArgumentException("directory is required");
    } else {
      File baseDirFile = new File(baseDir);
      if (!baseDirFile.isDirectory()) {
        throw new IllegalArgumentException("directory does not exist");
      }
      return baseDirFile;
    }
  }
}
