/*
 * Copyright (C) 2014-2018 SonarSource SA
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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.annotation.CheckForNull;

import static com.sonarsource.ruleapi.utilities.JsonUtil.fromJson;

public class SonarPediaJsonFile {
  File file;
  SonarPediaFileData data;

  private static String sonarpediaFileName = "sonarpedia.json";

  private SonarPediaJsonFile() {
    // NOP
  }

  public static SonarPediaJsonFile create(File baseDir, File rulesDir, List<Language> languages, boolean preserveFilenames, boolean noLanguageInFilenames) {
    File sonarPediaFile = new File(baseDir, sonarpediaFileName);

    SonarPediaJsonFile returned = new SonarPediaJsonFile();
    returned.file = sonarPediaFile;
    returned.data = new SonarPediaFileData();
    returned.data.ruleMetadataPath = baseDir.toPath().resolve(rulesDir.toPath().normalize()).toString();
    returned.data.languages = languages;
    returned.data.options = SonarPediaFileData.create(preserveFilenames, noLanguageInFilenames);

    returned.data.validate();

    try (
      FileOutputStream fop = new FileOutputStream(sonarPediaFile);
      OutputStreamWriter osw = new OutputStreamWriter(fop, StandardCharsets.UTF_8);
      BufferedWriter out = new BufferedWriter(osw)) {

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

    try (Scanner scanner = new Scanner(file, "UTF-8")) {
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
    try (FileOutputStream fop = new FileOutputStream(this.file);
      OutputStreamWriter osw = new OutputStreamWriter(fop, StandardCharsets.UTF_8);
      BufferedWriter out = new BufferedWriter(osw)) {

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

  public boolean noLanguageInFileName() {
    return this.data.noLanguageInFileNames();
  }

  public boolean preserveFilenames() {
    return this.data.preserveFilenames();
  }

  private static class SonarPediaFileData {
    @SerializedName("rules-metadata-path")
    String ruleMetadataPath;
    List<Language> languages;
    @SerializedName("default-profile-name")
    String defaultProfileName;
    @SerializedName("latest-update")
    Instant latestUpdate;
    Map<String, Boolean> options;

    private static final String NO_LANGUAGE_IN_FILENAMES = "no-language-in-filenames";
    private static final String PRESERVE_FILENAMES = "preserve-filenames";

    @CheckForNull
    private static HashMap<String, Boolean> create(boolean preserveFilenames, boolean noLanguageInFilenames) {
      if (preserveFilenames || noLanguageInFilenames) {
        HashMap<String, Boolean> returned = new HashMap<>();
        if (preserveFilenames) {
          returned.put(PRESERVE_FILENAMES, true);
        }
        if (noLanguageInFilenames) {
          returned.put(NO_LANGUAGE_IN_FILENAMES, true);
        }
        return returned;
      } else {
        return null;
      }
    }

    private void validate() {
      if (languages.size() > 1 && !noLanguageInFileNames()) {
        throw new IllegalArgumentException("more than one language requires the option no-language-in-filemanes");
      }
    }

    private boolean noLanguageInFileNames() {
      return options != null && options.getOrDefault(NO_LANGUAGE_IN_FILENAMES, false);
    }

    private boolean preserveFilenames() {
      return options != null && options.getOrDefault(PRESERVE_FILENAMES, false);
    }

  }
}
