/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.services;

import com.sonarsource.ruleapi.utilities.JsonUtil;
import com.sonarsource.ruleapi.utilities.Language;
import java.io.BufferedWriter;
import java.io.File;
import com.google.gson.annotations.SerializedName;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Scanner;

import static com.sonarsource.ruleapi.utilities.JsonUtil.fromJson;

public class SonarPediaJsonFile {
  File file;
  SonarPediaFileData data;

  private static String sonarpediaFileName = "sonarpedia.json";

  private SonarPediaJsonFile() {
    // NOP
  }

  public static SonarPediaJsonFile create(File baseDir, File rulesDir) {
      File sonarPediaFile = new File(baseDir, sonarpediaFileName);

    SonarPediaJsonFile returned = new SonarPediaJsonFile();
    returned.file = sonarPediaFile;
    returned.data = new SonarPediaFileData();
    returned.data.ruleMetadataPath = baseDir.toPath().resolve(rulesDir.toPath()).toString();

    try (BufferedWriter out = new BufferedWriter(new FileWriter(sonarPediaFile))) {
      out.write(JsonUtil.toJson(returned.data));
    } catch (IOException exception) {
      throw new IllegalStateException(exception);
    }

    return returned;
  }

  public static SonarPediaJsonFile findSonarPediaFile(File baseDir) throws FileNotFoundException {
    File sonarPediaFile = new File(baseDir, sonarpediaFileName);
    return deserialize(sonarPediaFile);
  }

  public static SonarPediaJsonFile deserialize(File file) throws FileNotFoundException {
    final String content = new Scanner(file).useDelimiter("\\Z").next();
    SonarPediaFileData data = fromJson(content, SonarPediaFileData.class);
    SonarPediaJsonFile returned = new SonarPediaJsonFile();
    returned.file = file;
    returned.data = data;

    return returned;
  }

  public void writeToItsFile() {
    try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
      out.write(JsonUtil.toJson(this.data));
    } catch (IOException exception) {
      throw new IllegalStateException(exception);
    }
  }

  public final File getRulesMetadataFilesDir() {
    Path rulesBaseDir = file.toPath().getParent().resolve(this.data.ruleMetadataPath);
    return rulesBaseDir.toFile();
  }

  public SonarPediaJsonFile updateTimeStamp() {
    data.latestUpdate = Instant.now();
    return this;
  }

  public Instant getUpdateTimeStamp() {
    return data.latestUpdate;
  }

  public final String getFileName() {
    return this.file.getName();
  }

  public final Language getLanguage() {
    return this.data.language;
  }

  private static class SonarPediaFileData {
    @SerializedName("rules-metadata-path")
    String ruleMetadataPath;
    Language language;
    @SerializedName("default-profile-name")
    String defaultProfileName;
    @SerializedName("latest-update")
    Instant latestUpdate;
  }

  public static FilenameFilter getSonarPediaJsonFileFilter() {
    return (dir, name) -> sonarpediaFileName.equals(name);
  }
}
