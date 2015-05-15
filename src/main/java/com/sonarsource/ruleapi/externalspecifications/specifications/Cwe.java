/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.*;

public class Cwe extends AbstractMultiLanguageStandard implements TaggableStandard {

  private static final String TAG = "cwe";
  private static final String REFERENCE_PATTERN = "CWE-\\d+";
  private static final String NAME = "CWE";
  private Language language = null;

  @Override
  public boolean isTagShared() {

    return false;
  }

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


  @Override
  public String getReport(String instance) {

    if (language == null) {
      return null;
    }

    Map<String, List<Rule>> cweRules = initCoverage(instance);
    return generateReport(instance, cweRules);
  }

  @Override
  protected String generateReport(String instance, Map<String, List<Rule>> standardRules) {

    if (standardRules.isEmpty()) {
      return null;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<h2>").append(language.getRspec()).append(" coverage of CWE</h2>\n");
    sb.append("<table>\n");

    for (Map.Entry<String, List<Rule>> entry : standardRules.entrySet()) {

      Integer key = Integer.valueOf(entry.getKey().split("-")[1]);
      sb.append("<tr><td><a href='http://cwe.mitre.org/data/definitions/").append(key)
              .append("' target='_blank'>CWE-").append(key).append("</a></td>\n<td>");

      for (Rule rule : entry.getValue()) {
        sb.append(getLinkedRuleReference(instance, rule));
      }
      sb.append("</td></tr>\n");
    }
    sb.append("</table>");

    return sb.toString();
  }

  @Override
  public String getSummaryReport(String instance) {

    return getReport(instance);
  }

  @Override
  public Language getLanguage() {

    return language;
  }

  @Override
  public void setLanguage(Language language) {
    this.language = language;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return new CodingStandardRule[0];
  }
}
