/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class AbstractTaggableStandard extends AbstractCodingStandard {

  public abstract String getTag();

  public abstract String getSeeSectionSearchString();

  public abstract String getReferencePattern();

  public abstract boolean isFieldEntryFormatNeedUpdating(Map<String, Object> updates, Rule rule);

  public List<String> parseReferencesFromStrings(List<String> references) {
    List<String> refs = new ArrayList<String>();

    for (String reference : references) {
      String[] pieces = reference.split(" ");
      for (String piece : pieces) {
        if (piece.matches(getReferencePattern())) {
          refs.add(piece);
        }
      }
    }

    return refs;
  }

}
