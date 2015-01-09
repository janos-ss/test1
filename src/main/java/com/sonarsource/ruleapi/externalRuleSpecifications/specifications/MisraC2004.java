/*
 * Copyright (C) 2014 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.ruleapi.externalRuleSpecifications.specifications;

import java.util.List;

import com.sonarsource.ruleapi.domain.Rule;
import com.sonarsource.ruleapi.externalRuleSpecifications.AbstractMisraSpecification;
import com.sonarsource.ruleapi.externalRuleSpecifications.CodingStandardRule;
import com.sonarsource.ruleapi.utilities.Language;
import org.fest.util.Strings;

public class MisraC2004 extends AbstractMisraSpecification {

  private String standardName = "MISRA C 2004";
  private String rspecFieldName = "MISRA C 2004";
  private Language language = Language.C;

  private int mandatoryRulesToCover = 0;
  private int optionalRulesToCover = 0;
  private int totalRulesToCover = 0;

  public enum Rules implements CodingStandardRule {
    _1POINT1 ("1.1", Boolean.TRUE, Boolean.TRUE),
    _1POINT2 ("1.2", Boolean.TRUE, Boolean.TRUE),
    _1POINT3 ("1.3", Boolean.TRUE, Boolean.TRUE),
    _1POINT4 ("1.4", Boolean.TRUE, Boolean.TRUE),
    _1POINT5 ("1.5", Boolean.FALSE, Boolean.TRUE),

    _2POINT1 ("2.1", Boolean.TRUE, Boolean.TRUE),
    _2POINT2 ("2.2", Boolean.TRUE, Boolean.TRUE),
    _2POINT3 ("2.3", Boolean.TRUE, Boolean.TRUE),
    _2POINT4 ("2.4", Boolean.FALSE, Boolean.TRUE),

    _3POINT1 ("3.1", Boolean.TRUE, Boolean.TRUE),
    _3POINT2 ("3.2", Boolean.TRUE, Boolean.TRUE),
    _3POINT3 ("3.3", Boolean.FALSE, Boolean.TRUE),
    _3POINT4 ("3.4", Boolean.TRUE, Boolean.TRUE),
    _3POINT5 ("3.5", Boolean.TRUE, Boolean.TRUE),
    _3POINT6 ("3.6", Boolean.TRUE, Boolean.TRUE),

    _4POINT1 ("4.1", Boolean.TRUE, Boolean.TRUE),
    _4POINT2 ("4.2", Boolean.TRUE, Boolean.TRUE),

    _5POINT1 ("5.1", Boolean.TRUE, Boolean.TRUE),
    _5POINT2 ("5.2", Boolean.TRUE, Boolean.TRUE),
    _5POINT3 ("5.3", Boolean.TRUE, Boolean.TRUE),
    _5POINT4 ("5.4", Boolean.TRUE, Boolean.TRUE),
    _5POINT5 ("5.5", Boolean.FALSE, Boolean.TRUE),
    _5POINT6 ("5.6", Boolean.FALSE, Boolean.TRUE),
    _5POINT7 ("5.7", Boolean.FALSE, Boolean.TRUE),

    _6POINT1 ("6.1", Boolean.TRUE, Boolean.TRUE),
    _6POINT2 ("6.2", Boolean.TRUE, Boolean.TRUE),
    _6POINT3 ("6.3", Boolean.FALSE, Boolean.TRUE),
    _6POINT4 ("6.4", Boolean.TRUE, Boolean.TRUE),
    _6POINT5 ("6.5", Boolean.TRUE, Boolean.TRUE),

    _7POINT1 ("7.1", Boolean.TRUE, Boolean.TRUE),

    _8POINT1 ("8.1", Boolean.TRUE, Boolean.TRUE),
    _8POINT2 ("8.2", Boolean.TRUE, Boolean.TRUE),
    _8POINT3 ("8.3", Boolean.TRUE, Boolean.TRUE),
    _8POINT4 ("8.4", Boolean.TRUE, Boolean.TRUE),
    _8POINT5 ("8.5", Boolean.TRUE, Boolean.TRUE),
    _8POINT6 ("8.6", Boolean.TRUE, Boolean.TRUE),
    _8POINT7 ("8.7", Boolean.TRUE, Boolean.TRUE),
    _8POINT8 ("8.8", Boolean.TRUE, Boolean.TRUE),
    _8POINT9 ("8.9", Boolean.TRUE, Boolean.TRUE),
    _8POINT10 ("8.10", Boolean.TRUE, Boolean.TRUE),
    _8POINT11 ("8.11", Boolean.TRUE, Boolean.TRUE),
    _8POINT12 ("8.12", Boolean.TRUE, Boolean.TRUE),

    _9POINT1 ("9.1", Boolean.TRUE, Boolean.TRUE),
    _9POINT2 ("9.2", Boolean.TRUE, Boolean.TRUE),
    _9POINT3 ("9.3", Boolean.TRUE, Boolean.TRUE),

    _10POINT1 ("10.1", Boolean.TRUE, Boolean.TRUE),
    _10POINT2 ("10.2", Boolean.TRUE, Boolean.TRUE),
    _10POINT3 ("10.3", Boolean.TRUE, Boolean.TRUE),
    _10POINT4 ("10.4", Boolean.TRUE, Boolean.TRUE),
    _10POINT5 ("10.5", Boolean.TRUE, Boolean.TRUE),
    _10POINT6 ("10.6", Boolean.TRUE, Boolean.TRUE),

    _11POINT1 ("11.1", Boolean.TRUE, Boolean.TRUE),
    _11POINT2 ("11.2", Boolean.TRUE, Boolean.TRUE),
    _11POINT3 ("11.3", Boolean.FALSE, Boolean.TRUE),
    _11POINT4 ("11.4", Boolean.FALSE, Boolean.TRUE),
    _11POINT5 ("11.5", Boolean.TRUE, Boolean.TRUE),

    _12POINT1 ("12.1", Boolean.FALSE, Boolean.TRUE),
    _12POINT2 ("12.2", Boolean.TRUE, Boolean.TRUE),
    _12POINT3 ("12.3", Boolean.TRUE, Boolean.TRUE),
    _12POINT4 ("12.4", Boolean.TRUE, Boolean.TRUE),
    _12POINT5 ("12.5", Boolean.TRUE, Boolean.TRUE),
    _12POINT6 ("12.6", Boolean.FALSE, Boolean.TRUE),
    _12POINT7 ("12.7", Boolean.TRUE, Boolean.TRUE),
    _12POINT8 ("12.8", Boolean.TRUE, Boolean.TRUE),
    _12POINT9 ("12.9", Boolean.TRUE, Boolean.TRUE),
    _12POINT10 ("12.10", Boolean.TRUE, Boolean.TRUE),
    _12POINT11 ("12.11", Boolean.FALSE, Boolean.TRUE),
    _12POINT12 ("12.12", Boolean.TRUE, Boolean.TRUE),
    _12POINT13 ("12.13", Boolean.FALSE, Boolean.TRUE),

    _13POINT1 ("13.1", Boolean.TRUE, Boolean.TRUE),
    _13POINT2 ("13.2", Boolean.FALSE, Boolean.TRUE),
    _13POINT3 ("13.3", Boolean.TRUE, Boolean.TRUE),
    _13POINT4 ("13.4", Boolean.TRUE, Boolean.TRUE),
    _13POINT5 ("13.5", Boolean.TRUE, Boolean.TRUE),
    _13POINT6 ("13.6", Boolean.TRUE, Boolean.TRUE),
    _13POINT7 ("13.7", Boolean.TRUE, Boolean.TRUE),

    _14POINT1 ("14.1", Boolean.TRUE, Boolean.TRUE),
    _14POINT2 ("14.2", Boolean.TRUE, Boolean.TRUE),
    _14POINT3 ("14.3", Boolean.TRUE, Boolean.TRUE),
    _14POINT4 ("14.4", Boolean.TRUE, Boolean.TRUE),
    _14POINT5 ("14.5", Boolean.TRUE, Boolean.TRUE),
    _14POINT6 ("14.6", Boolean.TRUE, Boolean.TRUE),
    _14POINT7 ("14.7", Boolean.TRUE, Boolean.TRUE),
    _14POINT8 ("14.8", Boolean.TRUE, Boolean.TRUE),
    _14POINT9 ("14.9", Boolean.TRUE, Boolean.TRUE),
    _14POINT10 ("14.10", Boolean.TRUE, Boolean.TRUE),

    _15POINT1 ("15.1", Boolean.TRUE, Boolean.TRUE),
    _15POINT2 ("15.2", Boolean.TRUE, Boolean.TRUE),
    _15POINT3 ("15.3", Boolean.TRUE, Boolean.TRUE),
    _15POINT4 ("15.4", Boolean.TRUE, Boolean.TRUE),
    _15POINT5 ("15.5", Boolean.TRUE, Boolean.TRUE),

    _16POINT1 ("16.1", Boolean.TRUE, Boolean.TRUE),
    _16POINT2 ("16.2", Boolean.TRUE, Boolean.TRUE),
    _16POINT3 ("16.3", Boolean.TRUE, Boolean.TRUE),
    _16POINT4 ("16.4", Boolean.TRUE, Boolean.TRUE),
    _16POINT5 ("16.5", Boolean.TRUE, Boolean.TRUE),
    _16POINT6 ("16.6", Boolean.TRUE, Boolean.TRUE),
    _16POINT7 ("16.7", Boolean.FALSE, Boolean.TRUE),
    _16POINT8 ("16.8", Boolean.TRUE, Boolean.TRUE),
    _16POINT9 ("16.9", Boolean.TRUE, Boolean.TRUE),
    _16POINT10 ("16.10", Boolean.TRUE, Boolean.TRUE),

    _17POINT1 ("17.1", Boolean.TRUE, Boolean.TRUE),
    _17POINT2 ("17.2", Boolean.TRUE, Boolean.TRUE),
    _17POINT3 ("17.3", Boolean.TRUE, Boolean.TRUE),
    _17POINT4 ("17.4", Boolean.TRUE, Boolean.TRUE),
    _17POINT5 ("17.5", Boolean.FALSE, Boolean.TRUE),
    _17POINT6 ("17.6", Boolean.TRUE, Boolean.TRUE),

    _18POINT1 ("18.1", Boolean.TRUE, Boolean.TRUE),
    _18POINT2 ("18.2", Boolean.TRUE, Boolean.TRUE),
    _18POINT3 ("18.3", Boolean.TRUE, Boolean.TRUE),
    _18POINT4 ("18.4", Boolean.TRUE, Boolean.TRUE),

    _19POINT1 ("19.1", Boolean.FALSE, Boolean.TRUE),
    _19POINT2 ("19.2", Boolean.FALSE, Boolean.TRUE),
    _19POINT3 ("19.3", Boolean.TRUE, Boolean.TRUE),
    _19POINT4 ("19.4", Boolean.TRUE, Boolean.TRUE),
    _19POINT5 ("19.5", Boolean.TRUE, Boolean.TRUE),
    _19POINT6 ("19.6", Boolean.TRUE, Boolean.TRUE),
    _19POINT7 ("19.7", Boolean.FALSE, Boolean.TRUE),
    _19POINT8 ("19.8", Boolean.TRUE, Boolean.TRUE),
    _19POINT9 ("19.9", Boolean.TRUE, Boolean.TRUE),
    _19POINT10 ("19.10", Boolean.TRUE, Boolean.TRUE),
    _19POINT11 ("19.11", Boolean.TRUE, Boolean.TRUE),
    _19POINT12 ("19.12", Boolean.TRUE, Boolean.TRUE),
    _19POINT13 ("19.13", Boolean.FALSE, Boolean.TRUE),
    _19POINT14 ("19.14", Boolean.TRUE, Boolean.TRUE),
    _19POINT15 ("19.15", Boolean.TRUE, Boolean.TRUE),
    _19POINT16 ("19.16", Boolean.TRUE, Boolean.TRUE),
    _19POINT17 ("19.17", Boolean.TRUE, Boolean.TRUE),

    _20POINT1 ("20.1", Boolean.TRUE, Boolean.TRUE),
    _20POINT2 ("20.2", Boolean.TRUE, Boolean.TRUE),
    _20POINT3 ("20.3", Boolean.TRUE, Boolean.TRUE),
    _20POINT4 ("20.4", Boolean.TRUE, Boolean.TRUE),
    _20POINT5 ("20.5", Boolean.TRUE, Boolean.TRUE),
    _20POINT6 ("20.6", Boolean.TRUE, Boolean.TRUE),
    _20POINT7 ("20.7", Boolean.TRUE, Boolean.TRUE),
    _20POINT8 ("20.8", Boolean.TRUE, Boolean.TRUE),
    _20POINT9 ("20.9", Boolean.TRUE, Boolean.TRUE),
    _20POINT10 ("20.10", Boolean.TRUE, Boolean.TRUE),
    _20POINT11 ("20.11", Boolean.TRUE, Boolean.TRUE),
    _20POINT12 ("20.12", Boolean.TRUE, Boolean.TRUE),

    _21POINT1 ("21.1", Boolean.TRUE, Boolean.TRUE);

    private String name;
    private Boolean isMandatory;
    private Boolean isCoverable;

    Rules (String name, Boolean isMandatory, Boolean isCoverable) {
      this.name = name;
      this.isMandatory = isMandatory;
      this.isCoverable = isCoverable;
    }

    public String getCodingStandardRuleId() {
      return name;
    }
  }

  public MisraC2004(){
    totalRulesToCover = Rules.values().length;

    for (Rules rule : Rules.values()) {
      if (rule.isMandatory) {
        mandatoryRulesToCover++;
      } else {
        optionalRulesToCover++;
      }
    }
  }

  @Override
  public CodingStandardRule[] getCodingStandardRules() {
    return Rules.values();
  }

  @Override
  public boolean isRuleMandatory(String ruleKey) {
    if (Strings.isNullOrEmpty(ruleKey)) {
      return false;
    }
    for (Rules rule : Rules.values() ) {
      if (rule.getCodingStandardRuleId().equals(ruleKey)) {
        return rule.isMandatory;
      }
    }
    return false;
  }

  @Override
  public List<String> getStandardIdsFromRSpecRule(Rule rule) {
    return rule.getMisraC04();
  }

  @Override
  public void setStandardIdsInRSpecRule(Rule rule, List<String> ids) {
    rule.setMisraC04(ids);
  }

  @Override
  public Language getLanguage() {
    return language;
  }

  @Override
  public String getStandardName() {
    return standardName;
  }

  @Override
  public String getRSpecReferenceFieldName() {
    return rspecFieldName;
  }

  @Override
  public int getOptionalRulesToCoverCount() {
    return optionalRulesToCover;
  }

  @Override
  public int getMandatoryRulesToCoverCount() {
    return mandatoryRulesToCover;
  }

}
