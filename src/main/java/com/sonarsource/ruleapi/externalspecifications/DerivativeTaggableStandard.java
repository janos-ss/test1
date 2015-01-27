/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.Rule;

import java.util.Map;


/**
 * A DerivativeTaggableStandard is one that is based on another standard.
 * E.G. SANS Top 25 is derived from, and references CWE
 */
public interface DerivativeTaggableStandard extends TaggableStandard {

  void checkReferencesInSeeSection(Rule rule);

  void addTagIfMissing(Rule rule, Map<String, Object>  updates);
}
