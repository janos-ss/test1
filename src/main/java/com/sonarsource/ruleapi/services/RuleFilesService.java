/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.sonarsource.ruleapi.domain.Profile;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleFilesService {

  private static final String HTML_TERMINATION = ".html";
  private static final String JSON_TERMINATION = ".json";
  private static final String PROFILE_TERMINATION = "_profile.json";
  private final String language;
  private final File baseDir;
  private final Pattern descriptionFileBaseNamePattern;
  private int countGeneratedFiles;

  private RuleFilesService(File baseDir, Language language) {
    this.baseDir = baseDir;
    this.language = language.getRspec().toLowerCase(Locale.ENGLISH);
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
      List<Rule> updatedRules = new ArrayList<>();
      for (String ruleKey : ruleKeys) {
        Rule rule = RuleMaker.getRuleByKey(ruleKey, language);
        if( rule == null || rule.getKey() == null ) {
          throw new IllegalArgumentException("invalid rule");
        }
        generateSingleRuleFiles(rule);
        updatedRules.add(rule);
      }
      updateProfiles(updatedRules);
    }
    System.out.println(String.format("Output: (%d) files", countGeneratedFiles));
  }

  private void updateProfiles(List<Rule> updatedRules) {
    Map<String, RulesProfile> profiles = findProfiles();
    Set<RulesProfile> profilesToUpdate = new HashSet<>();
    for (Rule updatedRule : updatedRules) {
      // Add rule to profile if required
      Set<String> ruleProfileNames = new HashSet<>();
      String ruleKey = Utilities.denormalizeKey(updatedRule.getKey());
      for (Profile profile : updatedRule.getDefaultProfiles()) {
        String profileName = profile.getName();
        ruleProfileNames.add(profileName);
        if(profiles.containsKey(profileName)) {
          RulesProfile rp = profiles.get(profileName);
          if(!rp.ruleKeys.contains(ruleKey)) {
            rp.ruleKeys.add(ruleKey);
            profilesToUpdate.add(rp);
          }
        } else {
          // profile file not found, creating the profile
          RulesProfile newRP = new RulesProfile();
          newRP.name = profileName;
          newRP.ruleKeys = Lists.newArrayList(ruleKey);
          profilesToUpdate.add(newRP);
          profiles.put(profileName, newRP);
        }
      }
      // Remove rule from profile
      for (RulesProfile rulesProfile : profiles.values()) {
        if(rulesProfile.ruleKeys.contains(ruleKey) && !ruleProfileNames.contains(rulesProfile.name)) {
          rulesProfile.ruleKeys.remove(ruleKey);
          profilesToUpdate.add(rulesProfile);
        }
      }
    }
    // update required profiles
    writeProfiles(profilesToUpdate);
  }

  private Map<String, RulesProfile> findProfiles() {
    final Map<String, RulesProfile> result = new HashMap<>();
    final Gson gson = new Gson();
    baseDir.listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        if(file.getName().endsWith(PROFILE_TERMINATION)) {
          try(InputStreamReader ir = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)) {
            RulesProfile rulesProfile = gson.fromJson(ir, RulesProfile.class);
            result.put(rulesProfile.name, rulesProfile);
          } catch (IOException e) {
            throw new RuleException(e);
          }
          return true;
        }
        return false;
      }
    });
    return result;
  }

  private void writeProfiles(Set<RulesProfile> profilesToUpdate) {
    for (RulesProfile rulesProfile : profilesToUpdate) {
      System.out.println(String.format("Updating profile %s", rulesProfile.name));
      writeFile(rulesProfile.name+PROFILE_TERMINATION, new Gson().toJson(rulesProfile));
    }
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
    } catch (IOException e) {
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

  private static class RulesProfile {
    String name;
    List<String> ruleKeys;
  }
}
