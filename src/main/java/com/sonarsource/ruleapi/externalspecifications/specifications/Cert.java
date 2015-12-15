/*
 * Copyright (C) 2014-2016 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;

import java.util.List;
import java.util.logging.Logger;


public class Cert implements TaggableStandard {

  private static final Logger LOGGER = Logger.getLogger(Cert.class.getName());

  private static final String TAG = "cert";

  private static final String REFERENCE_NAME = "CERT";

  private static final String REFERENCE_PATTERN = "[A-Z]{3}\\d\\d-[A-Za-z]+";


  @Override
  public boolean isTagShared() {

    return false;
  }

  @Override
  public String getTag() {

    return TAG;
  }

  @Override
  public String getSeeSectionSearchString() {

    return REFERENCE_NAME;
  }

  @Override
  public String getReferencePattern() {

    return REFERENCE_PATTERN;
  }

  @Override
  public String getStandardName() {

    return REFERENCE_NAME;
  }

  @Override
  public String getRSpecReferenceFieldName() {

    return REFERENCE_NAME;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {

    return rule.getCert();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {

    rule.setCert(ids);
  }

  @Override
  public boolean doesReferenceNeedUpdating(String reference, List<String> replacements, String ruleKey) {
    if (reference.matches(REFERENCE_PATTERN + "\\.")) {
      replacements.add(reference.substring(0, reference.length()-1));
      return true;
    } else {
      if (!reference.matches(REFERENCE_PATTERN)) {
        LOGGER.info("Unrecognized CERT reference pattern " + reference + " in " + ruleKey);
      }

      replacements.add(reference);
    }

    return false;
  }
}
