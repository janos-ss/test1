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

public class Cwe implements TaggableStandard {

  private static final String TAG = "cwe";
  private static final String REFERENCE_PATTERN = "CWE-\\d+";
  private static final String NAME = "CWE";

  @Override
  public String getTag() {

    return TAG;
  }

  @Override
  public String getStandardName() {

    return NAME;
  }

  @Override
  public String getRSpecReferenceFieldName() {

    return NAME;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {

    return rule.getCwe();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {

    rule.setCwe(ids);
  }

  @Override
  public String getSeeSectionSearchString() {

    return NAME;
  }

  @Override
  public String getReferencePattern() {

    return REFERENCE_PATTERN;
  }

  @Override
  public boolean isFieldEntryFormatNeedUpdating(Map<String, Object> updates, Rule rule) {

    List<String> references = getRspecReferenceFieldValues(rule);

    boolean needUpdating = false;

    List<String> replacements = new ArrayList<String>();
    for (int i = 0; i < references.size(); i++) {
      String ref = references.get(i);
      if (ref.matches("\\d+")) {
        replacements.add(NAME + "-" + ref);
        needUpdating = true;
      } else if (ref.matches(REFERENCE_PATTERN)) {
        replacements.add(ref);
      } else {
        // reference in unrecognized format; bail!
        needUpdating = false;
        break;
      }
    }

    if (needUpdating) {
      setRspecReferenceFieldValues(rule, replacements);
      updates.put(getRSpecReferenceFieldName(), replacements);
    }

    return needUpdating;
  }


}
