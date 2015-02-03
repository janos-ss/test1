/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.Rule;

import java.util.List;


public interface CodingStandard {

  String getStandardName();

  String getRSpecReferenceFieldName();

  List<String> getRspecReferenceFieldValues(Rule rule);

  void setRspecReferenceFieldValues(Rule rule, List<String> ids);

}
