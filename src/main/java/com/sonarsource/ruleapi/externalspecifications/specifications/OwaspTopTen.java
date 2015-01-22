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

public class OwaspTopTen extends AbstractTaggableStandard {

  private static final String TAG = "owasp-top10";

  private static final String REFERENCE_FIELD_NAME = "OWASP";

  private static final String SEE_SECTION_SEARCH = "OWASP Top Ten";

  private static final String REFERENCE_PATTERN = "A\\d+";

  @Override
  public String getTag() {

    return TAG;
  }

  @Override
  public String getStandardName() {

    return SEE_SECTION_SEARCH;
  }

  @Override
  public String getRSpecReferenceFieldName() {

    return REFERENCE_FIELD_NAME;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {

    return rule.getOwasp();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {

    rule.setOwasp(ids);
  }

  @Override
  public String getSeeSectionSearchString() {

    return SEE_SECTION_SEARCH;
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
      if (ref.matches(".+"+REFERENCE_PATTERN+".+")) {
        replacements.add(ref.replaceAll(".+("+REFERENCE_PATTERN+").+","$1"));
        needUpdating = true;
      } else {
        replacements.add(ref);
      }
    }

    if (needUpdating) {
      setRspecReferenceFieldValues(rule, replacements);
      updates.put(getRSpecReferenceFieldName(), replacements);
    }

    return needUpdating;
  }
}
