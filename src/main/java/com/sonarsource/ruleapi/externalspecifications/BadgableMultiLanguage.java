/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.ReportAndBadge;
import com.sonarsource.ruleapi.utilities.Language;
import java.util.EnumMap;
import java.util.Map;


public interface BadgableMultiLanguage extends Badgable {

  void setLanguage(Language language);

  ReportAndBadge getHtmlLanguageReport(String instance, Language language);

  default Map<Language, ReportAndBadge> getHtmlLanguageReports(String instance) {

    if (instance == null) {
      return null;
    }

    Map<Language, ReportAndBadge> reports = new EnumMap<>(Language.class);

    for (Language language : Language.values()) {

      ReportAndBadge report = getHtmlLanguageReport(instance, language);
      if (report != null) {
        reports.put(language, report);
      }
    }
    return reports;
  }
}
