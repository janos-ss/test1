/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sonarsource.ruleapi.domain.Profile;
import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.domain.RuleException;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.HtmlSanitizer;
import com.sonarsource.ruleapi.utilities.Language;
import com.sonarsource.ruleapi.utilities.Utilities;
import net.greypanther.natsort.CaseInsensitiveSimpleNaturalComparator;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RuleFilesService {

  private static final String HTML_TERMINATION = ".html";
  private static final String JSON_TERMINATION = ".json";
  private static final String PROFILE_TERMINATION = "_profile.json";
  private final String language;
  private boolean preserveFileNames;
  private boolean languageInFilenames;
  private final File baseDir;
  private final Pattern descriptionFileBaseNamePattern;
  private int countGeneratedFiles;

  private RuleFilesService(File baseDir, Language language, boolean preserveFileNames, boolean languageInFilenames) {
    this.baseDir = baseDir;
    this.language = language.getRspec().toLowerCase(Locale.ENGLISH);
    this.preserveFileNames = preserveFileNames;
    this.languageInFilenames = languageInFilenames;
    // match string like S123456
    descriptionFileBaseNamePattern = Pattern.compile("^S(\\d+)$");
  }

  public static RuleFilesService create(String baseDir, Language language) {
    return create(baseDir, language, false, true);
  }

  public static RuleFilesService create(String baseDir, Language language, boolean preserveFileNames, boolean languageInFilenames) {
    File baseDirFile = assertBaseDir(baseDir);
    if(language == null) {
      throw new IllegalArgumentException("no language found");
    }
    return new RuleFilesService(baseDirFile, language, preserveFileNames, languageInFilenames);
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
      String ruleKey = getKeyUsedForGeneration(updatedRule);
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
    baseDir.listFiles(file -> {
      boolean retVal = false;
      if (file.getName().endsWith(PROFILE_TERMINATION)) {
        try (InputStreamReader ir = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)) {
          RulesProfile rulesProfile = gson.fromJson(ir, RulesProfile.class);
          result.put(rulesProfile.name, rulesProfile);
        } catch (IOException e) {
          throw new RuleException(e);
        }
        retVal = true;
      }
      return retVal;
    });
    return result;
  }

  private void writeProfiles(Set<RulesProfile> profilesToUpdate) {
    for (RulesProfile rulesProfile : profilesToUpdate) {
      System.out.println(String.format("Updating profile %s", rulesProfile.name));
      // can't set a 'Comparator' to a SortedSet de-serialized by gson
      // that's why RulesProfile.ruleKeys is a List re-ordered before saving
      rulesProfile.ruleKeys.sort(CaseInsensitiveSimpleNaturalComparator.getInstance());
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      writeFile(rulesProfile.name+PROFILE_TERMINATION, gson.toJson(rulesProfile));
    }
  }

  private void generateSingleRuleFiles(Rule rule) {
    if(rule.getSeverity() == null) {
      System.out.println(String.format("WARNING: missing severity for rule %s", rule.getKey()));
    }
    String fileBaseName = computeBaseFileName(rule);
    HtmlSanitizer sanitizer = new HtmlSanitizer(2, 150);
    writeFile(fileBaseName + HTML_TERMINATION, sanitizer.format(rule.getHtmlDescription()));
    writeFile(fileBaseName + JSON_TERMINATION, rule.getSquidJson());
  }

  private String computeBaseFileName(Rule rule) {
    String key = getKeyUsedForGeneration(rule);
    return languageInFilenames ? String.format("%s_%s", key, rule.getLanguage()) : key;
  }

  private String getKeyUsedForGeneration(Rule rule) {
    if (preserveFileNames) {
      Objects.requireNonNull(rule.getLookupKey(), "Missing LookupKey in 'preserveFileNames' mode.");
      return rule.getLookupKey();
    } else {
      return Utilities.denormalizeKey(rule.getKey());
    }
  }

  public void updateDescriptions() {
    try {
      generateRuleFiles(findRulesToUpdate());
    } catch (IOException e) {
      throw new RuleException(e);
    }
  }

  private Set<String> findRulesToUpdate() throws IOException {
    final Set<String> rulesToUpdateKeys = Files.list(baseDir.toPath())
        .filter(Files::isRegularFile)
        .map(path -> path.getFileName().toString())
        .filter(RuleFilesService::matchRuleFileExtension)
        .map(FilenameUtils::removeExtension)
        .map(this::toRuleKey)
        .filter(key -> key != null)
        .collect(Collectors.toSet());
    System.out.println(String.format("Found %d rule(s) to update", rulesToUpdateKeys.size()));
    return rulesToUpdateKeys;
  }

  private static boolean matchRuleFileExtension(String fileName) {
    String fileNameLowerCase = fileName.toLowerCase();
    return !fileNameLowerCase.endsWith(PROFILE_TERMINATION) &&
        (fileNameLowerCase.endsWith(JSON_TERMINATION) || fileNameLowerCase.endsWith(HTML_TERMINATION));
  }

  @Nullable
  private String toRuleKey(String baseName) {
    String ruleKey = baseName;
    if (languageInFilenames) {
      String langSuffix = "_" + this.language;
      if (!ruleKey.endsWith(langSuffix)) {
        return null;
      }
      ruleKey = ruleKey.substring(0, ruleKey.length() - langSuffix.length());
    }
    if (preserveFileNames) {
      return ruleKey;
    }
    Matcher m = descriptionFileBaseNamePattern.matcher(ruleKey);
    return m.find() ? m.group(1) : null;
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
