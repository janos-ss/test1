/*
 * Copyright (C) 2014-2017 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.domain;

import com.google.gson.annotations.SerializedName;
import com.sonarsource.ruleapi.utilities.JsonUtil;
import com.sonarsource.ruleapi.utilities.Language;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;

import static com.sonarsource.ruleapi.utilities.JsonUtil.fromJson;

public class SonarPediaJsonFile {
  File file;
  SonarPediaFileData data;

  private static String sonarpediaFileName = "sonarpedia.json";

  private SonarPediaJsonFile() {
    // NOP
  }

  public static SonarPediaJsonFile create(File baseDir, File rulesDir, List<Language> languages) {
    File sonarPediaFile = new File(baseDir, sonarpediaFileName);

    SonarPediaJsonFile returned = new SonarPediaJsonFile();
    returned.file = sonarPediaFile;
    returned.data = new SonarPediaFileData();
    returned.data.ruleMetadataPath = baseDir.toPath().resolve(rulesDir.toPath().normalize()).toString();
    returned.data.languages = languages;

    try {
      try(FileOutputStream fop = new FileOutputStream(sonarPediaFile);
          OutputStreamWriter osw = new OutputStreamWriter(fop,StandardCharsets.UTF_8);
          BufferedWriter out = new BufferedWriter( osw ) ) {
        out.write(JsonUtil.toJson(returned.data));
      }
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

    try (Scanner scanner = new Scanner(file)) {
      final String content = scanner.useDelimiter("\\Z").next();
      // TODO validate incoming content against JSON schema
      SonarPediaFileData data = fromJson(content, SonarPediaFileData.class);
      SonarPediaJsonFile returned = new SonarPediaJsonFile();
      returned.file = file;
      returned.data = data;
      return returned;
    }
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

  public final File getFile() {
    return this.file;
  }

  public final List<Language> getLanguages() {
    return this.data.languages;
  }

  private static class SonarPediaFileData {
    @SerializedName("rules-metadata-path")
    String ruleMetadataPath;
    List<Language> languages;
    @SerializedName("default-profile-name")
    String defaultProfileName;
    @SerializedName("latest-update")
    Instant latestUpdate;
  }
}
