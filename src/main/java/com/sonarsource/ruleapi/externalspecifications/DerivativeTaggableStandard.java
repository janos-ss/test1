/*
 * Copyright (C) 2014-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications;

import com.sonarsource.ruleapi.domain.Rule;

import java.util.Map;
import java.util.Set;


/**
 * A DerivativeTaggableStandard is one that is based on another standard.
 *
 * E.G. SANS Top 25 is derived from, and references CWE, and because
 * SANS Top 25 is not stand-alone, different algorithms may be required
 * for it than for other standards.
 *
 * For instance the CWE standard can/should enforce that every rule
 * with a value in the CWE field has a 'cwe' tag, but SANS is also
 * based on the CWE field and should NOT add a 'sans-top25' tag
 * to every rule with a CWE value.
 */
public interface DerivativeTaggableStandard extends TaggableStandard {

  void checkReferencesInSeeSection(Rule rule);

  void addTagIfMissing(Rule rule, Map<String, Object>  updates);

  default void addOrRemoveTag(Map<String, Object> updates, String myTag, Set<String> ruleTags, boolean ruleNeedsTag, boolean ruleHasTag) {
    if (!ruleHasTag && ruleNeedsTag) {
      ruleTags.add(myTag);
      updates.put("Labels", ruleTags);
    } else if (ruleHasTag && !ruleNeedsTag) {
      ruleTags.remove(myTag);
      updates.put("Labels", ruleTags);
    }
  }
}
