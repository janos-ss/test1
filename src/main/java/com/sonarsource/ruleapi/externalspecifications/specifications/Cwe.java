/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.TaggableStandard;
import com.sonarsource.ruleapi.get.RuleMaker;
import com.sonarsource.ruleapi.utilities.Language;

import java.util.*;

public class Cwe extends AbstractReportableStandard implements TaggableStandard {

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


  private Map<Integer, ArrayList<Rule>> initCoverage(String instance) {

    if (language == null) {
      return null;
    }

    TreeMap<Integer, ArrayList<Rule>> cweRules = new TreeMap<Integer, ArrayList<Rule>>();

    List<Rule> sqImplemented = RuleMaker.getRulesFromSonarQubeForLanguage(language, instance);
    for (Rule sq : sqImplemented) {

      Rule rspec = RuleMaker.getRuleByKey(sq.getKey(), language.getRspec());
      for (String cwe : rspec.getCwe()) {
        Integer num = Integer.valueOf(cwe.split("-")[1]);

        ArrayList<Rule> rules = cweRules.get(num);
        if (rules == null) {
          rules = new ArrayList<Rule>();
          cweRules.put(num, rules);
        }
        rules.add(sq);
      }
    }

    return cweRules;
  }

  @Override
  public String getReport(String instance) {

    if (language == null) {
      return null;
    }

    Map<Integer, ArrayList<Rule>> cweRules = initCoverage(instance);
    if (cweRules.isEmpty()) {
      return null;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<h2>").append(language.getRspec()).append(" coverage of CWE</h2>\n");
    sb.append("<table>\n");

    for (Map.Entry<Integer, ArrayList<Rule>> entry : cweRules.entrySet()) {

      Integer key = entry.getKey();
      sb.append("<tr><td><a href='http://cwe.mitre.org/data/definitions/").append(key)
              .append("' target='_blank'>CWE-").append(key).append("</a></td>\n<td>");

      for (Rule rule : entry.getValue()) {

        String ruleKey = rule.getKey();
        if (RuleMaker.isKeyNormal(ruleKey)) {
          ruleKey = ruleKey.replace("RSPEC-", "S");
        }

        // http://nemo.sonarqube.org/coding_rules#rule_key=squid%3AS2066
        sb.append("<a href='").append(instance).append("/coding_rules#rule_key=")
                .append(language.getSq()).append("%3A").append(ruleKey).append("'>")
                .append(ruleKey).append("</a> ").append(rule.getTitle()).append("<br/>\n");
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

  public void setLanguage(Language language){

    this.language = language;
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {

    return new CodingStandardRule[0];
  }
}
