/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.specifications;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.Language;
import org.fest.util.Strings;


public class MisraC2004 extends AbstractMisraSpecification {

  private static final String NAME = "MISRA C 2004";
  private static final String SEE_SECTION_SEARCH_STRING = "MISRA C:2004,";
  private static final String REFERENCE_PATTERN = "\\d\\d?\\.\\d\\d?";

  private Map<String, CodingStandardRule> ruleMap = new HashMap<String, CodingStandardRule>();

  private Language language = Language.C;

  private int mandatoryRulesToCover = 0;
  private int optionalRulesToCover = 0;

  public enum StandardRule implements CodingStandardRule {

    MISRAC2004_1POINT1 ("1.1", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    MISRAC2004_1POINT2 ("1.2", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    MISRAC2004_1POINT3 ("1.3", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    MISRAC2004_1POINT4 ("1.4", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    MISRAC2004_1POINT5 ("1.5", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),

    MISRAC2004_2POINT1 ("2.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_2POINT2 ("2.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_2POINT3 ("2.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_2POINT4 ("2.4", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRAC2004_3POINT1 ("3.1", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    MISRAC2004_3POINT2 ("3.2", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    MISRAC2004_3POINT3 ("3.3", Boolean.FALSE, Implementability.NOT_IMPLEMENTABLE),
    MISRAC2004_3POINT4 ("3.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_3POINT5 ("3.5", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    MISRAC2004_3POINT6 ("3.6", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),

    MISRAC2004_4POINT1 ("4.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_4POINT2 ("4.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_5POINT1 ("5.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_5POINT2 ("5.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_5POINT3 ("5.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_5POINT4 ("5.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_5POINT5 ("5.5", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_5POINT6 ("5.6", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_5POINT7 ("5.7", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRAC2004_6POINT1 ("6.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_6POINT2 ("6.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_6POINT3 ("6.3", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_6POINT4 ("6.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_6POINT5 ("6.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_7POINT1 ("7.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_8POINT1 ("8.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_8POINT2 ("8.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_8POINT3 ("8.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_8POINT4 ("8.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_8POINT5 ("8.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_8POINT6 ("8.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_8POINT7 ("8.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_8POINT8 ("8.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_8POINT9 ("8.9", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_8POINT10 ("8.10", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_8POINT11 ("8.11", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_8POINT12 ("8.12", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_9POINT1 ("9.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_9POINT2 ("9.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_9POINT3 ("9.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_10POINT1 ("10.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_10POINT2 ("10.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_10POINT3 ("10.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_10POINT4 ("10.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_10POINT5 ("10.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_10POINT6 ("10.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_11POINT1 ("11.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_11POINT2 ("11.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_11POINT3 ("11.3", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_11POINT4 ("11.4", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_11POINT5 ("11.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_12POINT1 ("12.1", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_12POINT2 ("12.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_12POINT3 ("12.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_12POINT4 ("12.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_12POINT5 ("12.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_12POINT6 ("12.6", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_12POINT7 ("12.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_12POINT8 ("12.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_12POINT9 ("12.9", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_12POINT10 ("12.10", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_12POINT11 ("12.11", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_12POINT12 ("12.12", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_12POINT13 ("12.13", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRAC2004_13POINT1 ("13.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_13POINT2 ("13.2", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_13POINT3 ("13.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_13POINT4 ("13.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_13POINT5 ("13.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_13POINT6 ("13.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_13POINT7 ("13.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_14POINT1 ("14.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_14POINT2 ("14.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_14POINT3 ("14.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_14POINT4 ("14.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_14POINT5 ("14.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_14POINT6 ("14.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_14POINT7 ("14.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_14POINT8 ("14.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_14POINT9 ("14.9", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_14POINT10 ("14.10", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_15POINT0 ("15.0", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_15POINT1 ("15.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_15POINT2 ("15.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_15POINT3 ("15.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_15POINT4 ("15.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_15POINT5 ("15.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_16POINT1 ("16.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_16POINT2 ("16.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_16POINT3 ("16.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_16POINT4 ("16.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_16POINT5 ("16.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_16POINT6 ("16.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_16POINT7 ("16.7", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_16POINT8 ("16.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_16POINT9 ("16.9", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_16POINT10 ("16.10", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),

    MISRAC2004_17POINT1 ("17.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_17POINT2 ("17.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_17POINT3 ("17.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_17POINT4 ("17.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_17POINT5 ("17.5", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_17POINT6 ("17.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_18POINT1 ("18.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_18POINT2 ("18.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_18POINT3 ("18.3", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    MISRAC2004_18POINT4 ("18.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_19POINT1 ("19.1", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT2 ("19.2", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT3 ("19.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT4 ("19.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT5 ("19.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT6 ("19.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT7 ("19.7", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT8 ("19.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT9 ("19.9", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT10 ("19.10", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT11 ("19.11", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT12 ("19.12", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT13 ("19.13", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT14 ("19.14", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT15 ("19.15", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT16 ("19.16", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_19POINT17 ("19.17", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_20POINT1 ("20.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_20POINT2 ("20.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_20POINT3 ("20.3", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE),
    MISRAC2004_20POINT4 ("20.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_20POINT5 ("20.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_20POINT6 ("20.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_20POINT7 ("20.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_20POINT8 ("20.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_20POINT9 ("20.9", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_20POINT10 ("20.10", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_20POINT11 ("20.11", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2004_20POINT12 ("20.12", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2004_21POINT1 ("21.1", Boolean.TRUE, Implementability.NOT_IMPLEMENTABLE);

    private String name;
    private Boolean isMandatory;
    private Implementability implementability;

    StandardRule(String name, Boolean isMandatory, Implementability implementability) {
      this.name = name;
      this.isMandatory = isMandatory;
      this.implementability = implementability;
    }

    @Override
    public String getCodingStandardRuleId() {
      return name;
    }

    @Override
    public Implementability getImplementability() {
      return implementability;
    }
  }

  public MisraC2004(){

    for (StandardRule standardRule : StandardRule.values()) {
      ruleMap.put(standardRule.getCodingStandardRuleId(), (CodingStandardRule)standardRule);

      if (Implementability.IMPLEMENTABLE.equals(standardRule.getImplementability())) {
        if (standardRule.isMandatory) {
          mandatoryRulesToCover++;
        } else {
          optionalRulesToCover++;
        }
      }
    }
  }

  @Override
  public String getSeeSectionSearchString() {

    return SEE_SECTION_SEARCH_STRING;
  }

  @Override
  public String getReferencePattern() {

    return REFERENCE_PATTERN;
  }


  @Override
  public CodingStandardRule[] getCodingStandardRules() {
    return StandardRule.values();
  }

  @Override
  public boolean isRuleMandatory(String ruleKey) {
    if (Strings.isNullOrEmpty(ruleKey)) {
      return false;
    }
    StandardRule sr = (StandardRule) ruleMap.get(ruleKey);
    if (sr != null) {
      return sr.isMandatory;
    }
    return false;
  }

  @Override
  public List<String> getRspecReferenceFieldValues(Rule rule) {
    return rule.getMisraC04();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setMisraC04(ids);
  }

  @Override
  public Language getLanguage() {
    return language;
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
  public int getOptionalRulesToCoverCount() {
    return optionalRulesToCover;
  }

  @Override
  public CodingStandardRule getCodingStandardRuleFromId(String id) {

    if (id == null) {
      return null;
    }

    return ruleMap.get(id);
  }

  @Override
  public int getMandatoryRulesToCoverCount() {
    return mandatoryRulesToCover;
  }

}
