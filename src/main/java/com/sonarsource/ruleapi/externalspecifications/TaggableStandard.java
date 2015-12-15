/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import java.util.List;


public interface TaggableStandard extends CodingStandard {

  boolean isTagShared();

  String getTag();

  String getSeeSectionSearchString();

  String getReferencePattern();

  boolean doesReferenceNeedUpdating(String reference, List<String> replacements, String ruleKey);


}
