/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandard;

import java.util.Map;


public interface TaggableStandard extends CodingStandard {

  boolean isTagShared();

  String getTag();

  String getSeeSectionSearchString();

  String getReferencePattern();

  boolean isFieldEntryFormatNeedUpdating(Map<String, Object> updates, Rule rule);


}
