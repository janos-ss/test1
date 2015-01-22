/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;

import java.util.List;


public abstract class AbstractCodingStandard {

  public abstract String getStandardName();

  public abstract String getRSpecReferenceFieldName();

  public abstract List<String> getRspecReferenceFieldValues(Rule rule);

  public abstract void setRspecReferenceFieldValues(Rule rule, List<String> ids);

}
