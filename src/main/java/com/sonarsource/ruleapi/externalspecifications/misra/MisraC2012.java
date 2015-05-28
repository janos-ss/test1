/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalspecifications.misra;

import java.util.List;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalspecifications.AbstractMisraSpecification;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRequirableRule;
import com.sonarsource.ruleapi.externalspecifications.CodingStandardRule;
import com.sonarsource.ruleapi.externalspecifications.Implementability;
import com.sonarsource.ruleapi.utilities.Language;

public class MisraC2012 extends AbstractMisraSpecification {

  private static final String NAME = "MISRA C 2012";
  private static final String SEE_SECTION_SEARCH_STRING = "MISRA C:2012,";
  private static final String REFERENCE_PATTERN = "\\d\\d?\\.\\d\\d?";

  private Language language = Language.C;

  public enum StandardRule implements CodingStandardRequirableRule {

    MISRAC2012_1POINT1 ("1.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_1POINT2 ("1.2", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_1POINT3 ("1.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_2POINT1 ("2.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_2POINT2 ("2.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_2POINT3 ("2.3", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_2POINT4 ("2.4", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_2POINT5 ("2.5", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_2POINT6 ("2.6", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_2POINT7 ("2.7", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRAC2012_3POINT1 ("3.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_3POINT2 ("3.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_4POINT1 ("4.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_4POINT2 ("4.2", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRAC2012_5POINT1 ("5.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_5POINT2 ("5.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_5POINT3 ("5.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_5POINT4 ("5.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_5POINT5 ("5.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_5POINT6 ("5.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_5POINT7 ("5.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_5POINT8 ("5.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_5POINT9 ("5.9", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRAC2012_6POINT1 ("6.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_6POINT2 ("6.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_7POINT1 ("7.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_7POINT2 ("7.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_7POINT3 ("7.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_7POINT4 ("7.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_8POINT1 ("8.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT2 ("8.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT3 ("8.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT4 ("8.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT5 ("8.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT6 ("8.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT7 ("8.7", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT8 ("8.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT9 ("8.9", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT10 ("8.10", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT11 ("8.11", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT12 ("8.12", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT13 ("8.13", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_8POINT14 ("8.14", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_9POINT1 ("9.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_9POINT2 ("9.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_9POINT3 ("9.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_9POINT4 ("9.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_9POINT5 ("9.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_10POINT1 ("10.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_10POINT2 ("10.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_10POINT3 ("10.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_10POINT4 ("10.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_10POINT5 ("10.5", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_10POINT6 ("10.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_10POINT7 ("10.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_10POINT8 ("10.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_11POINT1 ("11.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_11POINT2 ("11.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_11POINT3 ("11.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_11POINT4 ("11.4", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_11POINT5 ("11.5", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_11POINT6 ("11.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_11POINT7 ("11.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_11POINT8 ("11.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_11POINT9 ("11.9", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_12POINT1 ("12.1", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_12POINT2 ("12.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_12POINT3 ("12.3", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_12POINT4 ("12.4", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRAC2012_13POINT1 ("13.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_13POINT2 ("13.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_13POINT3 ("13.3", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_13POINT4 ("13.4", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_13POINT5 ("13.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_13POINT6 ("13.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_14POINT1 ("14.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_14POINT2 ("14.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_14POINT3 ("14.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_14POINT4 ("14.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_15POINT1 ("15.1", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_15POINT2 ("15.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_15POINT3 ("15.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_15POINT4 ("15.4", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_15POINT5 ("15.5", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_15POINT6 ("15.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_15POINT7 ("15.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_16POINT1 ("16.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_16POINT2 ("16.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_16POINT3 ("16.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_16POINT4 ("16.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_16POINT5 ("16.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_16POINT6 ("16.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_16POINT7 ("16.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_17POINT1 ("17.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_17POINT2 ("17.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_17POINT3 ("17.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_17POINT4 ("17.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_17POINT5 ("17.5", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_17POINT6 ("17.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_17POINT7 ("17.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_17POINT8 ("17.8", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRAC2012_18POINT1 ("18.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_18POINT2 ("18.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_18POINT3 ("18.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_18POINT4 ("18.4", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_18POINT5 ("18.5", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_18POINT6 ("18.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_18POINT7 ("18.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_18POINT8 ("18.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_19POINT1 ("19.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_19POINT2 ("19.2", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRAC2012_20POINT1 ("20.1", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT2 ("20.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT3 ("20.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT4 ("20.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT5 ("20.5", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT6 ("20.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT7 ("20.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT8 ("20.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT9 ("20.9", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT10 ("20.10", Boolean.FALSE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT11 ("20.11", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT12 ("20.12", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT13 ("20.13", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_20POINT14 ("20.14", Boolean.TRUE, Implementability.IMPLEMENTABLE),

    MISRAC2012_21POINT1 ("21.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_21POINT2 ("21.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_21POINT3 ("21.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_21POINT4 ("21.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_21POINT5 ("21.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_21POINT6 ("21.6", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_21POINT7 ("21.7", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_21POINT8 ("21.8", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_21POINT9 ("21.9", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_21POINT10 ("21.10", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_21POINT11 ("21.11", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_21POINT12 ("21.12", Boolean.FALSE, Implementability.IMPLEMENTABLE),

    MISRAC2012_22POINT1 ("22.1", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_22POINT2 ("22.2", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_22POINT3 ("22.3", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_22POINT4 ("22.4", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_22POINT5 ("22.5", Boolean.TRUE, Implementability.IMPLEMENTABLE),
    MISRAC2012_22POINT6 ("22.6", Boolean.TRUE, Implementability.IMPLEMENTABLE);

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

    @Override
    public boolean isRuleRequired(){
      return isMandatory;
    }
  }

  public MisraC2012() {

    super(StandardRule.values());
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
  public List<String> getRspecReferenceFieldValues(Rule rule) {
    return rule.getMisraC12();
  }

  @Override
  public void setRspecReferenceFieldValues(Rule rule, List<String> ids) {
    rule.setMisraC12(ids);
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

}
