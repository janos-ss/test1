/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.Rule;

import java.util.List;


/**
 * A set of coding rules
 * Can be as set of abstract rules, such as MISRA or CWE
 * or a concrete tool, such as FindBugs.
 */
public interface CodingStandard extends Standard {

  String getRSpecReferenceFieldName();

  List<String> getRspecReferenceFieldValues(Rule rule);

  void setRspecReferenceFieldValues(Rule rule, List<String> ids);

}
