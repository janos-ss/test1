/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;

import java.util.Map;


public interface TaggableStandard extends CodingStandard {

  public abstract String getTag();

  public abstract String getSeeSectionSearchString();

  public abstract String getReferencePattern();

  public abstract boolean isFieldEntryFormatNeedUpdating(Map<String, Object> updates, Rule rule);


}
